package com.academiabaile.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.academiabaile.backend.entidades.ClaseNivel;
import com.academiabaile.backend.entidades.Inscripcion;
import com.academiabaile.backend.repository.InscripcionRepository;

import java.util.*;

@Service
public class MercadoPagoRestService {

    @Value("${mercadopago.access.token}")
    private String accessToken;

    private final InscripcionRepository inscripcionRepository;
    private final AuditoriaService auditoriaService;

    public MercadoPagoRestService(InscripcionRepository inscripcionRepository, AuditoriaService auditoriaService) {
        this.inscripcionRepository = inscripcionRepository;
        this.auditoriaService = auditoriaService;
    }

    public String crearPreferencia(Double precio, String nombreClase, Integer inscripcionId) {
        RestTemplate restTemplate = new RestTemplate();

        // Validación básica
        if (precio == null || inscripcionId == null) {
            throw new IllegalArgumentException("Datos de inscripción inválidos");
        }

        // Obtener datos de la inscripción
        Inscripcion insc = inscripcionRepository.findById(inscripcionId)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));

        ClaseNivel claseNivel = insc.getClaseNivel();
        if (claseNivel == null || claseNivel.getNivel() == null || claseNivel.getClase() == null) {
            throw new RuntimeException("Clase o nivel no configurado correctamente para esta inscripción.");
        }

        Integer claseNivelId = claseNivel.getId();
        String nombreNivel = claseNivel.getNivel().getNombre();
        Double precioClase = claseNivel.getPrecio();

        // ✅ Construcción segura de URLs
        String baseUrl = "https://timbatumbao-front.onrender.com/html/registro.html";

        String successUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("id", claseNivelId)
                .queryParam("nivel", nombreNivel)
                .queryParam("precio", precioClase)
                .queryParam("estado", "exito")
                .build()
                .toUriString();

        String failureUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("id", claseNivelId)
                .queryParam("nivel", nombreNivel)
                .queryParam("precio", precioClase)
                .queryParam("estado", "fallo")
                .build()
                .toUriString();

        String pendingUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("id", claseNivelId)
                .queryParam("nivel", nombreNivel)
                .queryParam("precio", precioClase)
                .queryParam("estado", "pendiente")
                .build()
                .toUriString();

        // ✅ Headers con token de acceso
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // ✅ Datos del producto
        Map<String, Object> item = Map.of(
                "title", "Inscripción: " + nombreClase,
                "quantity", 1,
                "unit_price", precio
        );

        // ✅ Estructura del cuerpo de la solicitud
        Map<String, Object> body = new HashMap<>();
        body.put("items", List.of(item));
        body.put("metadata", Map.of("inscripcion_id", inscripcionId));
        body.put("notification_url", "https://timbatumbao-back.onrender.com/api/pagos/webhook");
        body.put("auto_return", "approved");
        body.put("back_urls", Map.of(
                "success", successUrl,
                "failure", failureUrl,
                "pending", pendingUrl
        ));

        // ✅ Datos del comprador (payer)
        if (insc.getCliente() != null) {
            body.put("payer", Map.of(
                    "name", Optional.ofNullable(insc.getCliente().getNombres()).orElse(""),
                    "surname", Optional.ofNullable(insc.getCliente().getApellidos()).orElse(""),
                    "email", Optional.ofNullable(insc.getCliente().getCorreo()).orElse(""),
                    "identification", Map.of(
                            "type", "DNI",
                            "number", Optional.ofNullable(insc.getCliente().getDni()).orElse("00000000")
                    )
            ));
        }

        // ✅ Enviar POST a Mercado Pago
        try {
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.mercadopago.com/checkout/preferences",
                    entity,
                    Map.class
            );

            Object sandboxUrl = response.getBody().get("sandbox_init_point");
            if (sandboxUrl == null) {
                throw new RuntimeException("No se obtuvo el enlace de pago.");
            }

            return sandboxUrl.toString();
        } catch (Exception e) {
            auditoriaService.registrar("sistema", "ERROR_MERCADOPAGO", "Error al crear preferencia: " + e.getMessage());
            throw new RuntimeException("Error al crear preferencia en Mercado Pago", e);
        }
    }

    public boolean pagoEsAprobado(String paymentId) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> resp = restTemplate.exchange(
                    "https://api.mercadopago.com/v1/payments/" + paymentId,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            String status = (String) resp.getBody().get("status");
            Map metadata = (Map) resp.getBody().get("metadata");
            Integer inscripcionId = (Integer) metadata.get("inscripcion_id");

            if ("approved".equalsIgnoreCase(status)) {
                Inscripcion insc = inscripcionRepository.findById(inscripcionId).orElseThrow();
                insc.setEstado("aprobada");
                inscripcionRepository.save(insc);
                auditoriaService.registrar("sistema", "PAGO_APROBADO", "Pago aprobado para inscripción ID " + inscripcionId);
                return true;
            } else {
                auditoriaService.registrar("sistema", "PAGO_NO_APROBADO", "Estado: " + status + " para inscripción ID " + inscripcionId);
                return false;
            }

        } catch (Exception e) {
            auditoriaService.registrar("sistema", "ERROR_VERIFICACION_PAGO", "Error al verificar pago ID " + paymentId + ": " + e.getMessage());
            return false;
        }
    }
}
