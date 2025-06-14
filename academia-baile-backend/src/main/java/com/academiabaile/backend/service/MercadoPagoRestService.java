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

        // Obtener datos de clase para la inscripción
        Inscripcion insc = inscripcionRepository.findById(inscripcionId).orElseThrow();
        ClaseNivel claseNivel = insc.getClaseNivel();
        Integer claseNivelId = claseNivel.getId();
        String nombreNivel = claseNivel.getNivel().getNombre();
        Double precioClase = claseNivel.getPrecio();

        // Construcción segura de URLs de retorno
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

        // Headers con token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Datos del producto
        Map<String, Object> item = Map.of(
                "title", "Inscripción: " + nombreClase,
                "quantity", 1,
                "unit_price", precio
        );

        // Construcción del cuerpo de la solicitud
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

        // Enviar POST a Mercado Pago
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.mercadopago.com/checkout/preferences",
                entity,
                Map.class
        );

        return response.getBody().get("sandbox_init_point").toString();
    }

    public boolean pagoEsAprobado(String paymentId) {
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

        String status = resp.getBody().get("status").toString();
        Map metadata = (Map) resp.getBody().get("metadata");
        Integer inscripcionId = (Integer) metadata.get("inscripcion_id");

        if ("approved".equals(status)) {
            Inscripcion insc = inscripcionRepository.findById(inscripcionId).orElseThrow();
            insc.setEstado("aprobada");
            inscripcionRepository.save(insc);
            auditoriaService.registrar("sistema", "PAGO_APROBADO", "Pago aprobado para inscripción ID " + inscripcionId);
            return true;
        }

        auditoriaService.registrar("sistema", "PAGO_RECHAZADO", "Pago rechazado. ID " + paymentId);
        return false;
    }
}
