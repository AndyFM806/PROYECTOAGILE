package com.academiabaile.backend.service;

import com.academiabaile.backend.entidades.ClaseNivel;
import com.academiabaile.backend.entidades.Inscripcion;
import com.academiabaile.backend.entidades.NotaCredito;
import com.academiabaile.backend.repository.InscripcionRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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

    public String crearPreferencia(Double precioOriginal, String nombreClase, Integer inscripcionId) {
        RestTemplate restTemplate = new RestTemplate();

        // Obtener inscripción y datos relacionados
        Inscripcion insc = inscripcionRepository.findById(inscripcionId).orElseThrow();
        ClaseNivel claseNivel = insc.getClaseNivel();
        Integer claseNivelId = claseNivel.getId();
        String nivel = claseNivel.getNivel().getNombre();
        Double precioClase = claseNivel.getPrecio();

        // Si hay nota de crédito y monto pendiente < precio total, usar ese monto
        Double montoFinal = precioClase;
        NotaCredito nota = insc.getNotaCredito();
        if (nota != null && insc.getMontoPendiente() != null && insc.getMontoPendiente() > 0) {
            montoFinal = insc.getMontoPendiente();
        }

        // URLs de retorno codificadas con parámetros
        String baseUrl = "https://timbatumbao-front.onrender.com/html/registro.html";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("id", claseNivelId)
                .queryParam("nivel", nivel)
                .queryParam("precio", precioClase); // mostrar el precio completo

        String successUrl = builder.cloneBuilder().queryParam("estado", "exito").toUriString();
        String failureUrl = builder.cloneBuilder().queryParam("estado", "fallo").toUriString();
        String pendingUrl = builder.cloneBuilder().queryParam("estado", "pendiente").toUriString();

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Item
        Map<String, Object> item = Map.of(
                "title", "Inscripción: " + nombreClase,
                "quantity", 1,
                "unit_price", montoFinal
        );

        // Payer
        Map<String, Object> payer = Map.of(
                "name", insc.getCliente().getNombres(),
                "surname", insc.getCliente().getApellidos() != null ? insc.getCliente().getApellidos() : "",
                "email", insc.getCliente().getCorreo(),
                "identification", Map.of(
                        "type", "DNI",
                        "number", insc.getCliente().getDni()
                )
        );

        // Body
        Map<String, Object> body = new HashMap<>();
        body.put("items", List.of(item));
        body.put("metadata", Map.of("inscripcion_id", inscripcionId));
        body.put("notification_url", "https://timbatumbao-back.onrender.com/api/pagos/webhook");
        body.put("back_urls", Map.of(
                "success", successUrl,
                "failure", failureUrl,
                "pending", pendingUrl
        ));
        body.put("auto_return", "approved");
        body.put("payer", payer);

        // POST a Mercado Pago
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.mercadopago.com/checkout/preferences",
                entity,
                Map.class
        );

        // Retornar URL de pago en sandbox
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
