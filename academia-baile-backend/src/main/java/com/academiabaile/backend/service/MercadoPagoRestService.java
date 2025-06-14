package com.academiabaile.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.academiabaile.backend.entidades.*;
import com.academiabaile.backend.repository.InscripcionRepository;

import java.util.*;

@Service
public class MercadoPagoRestService {

    @Value("${mercadopago.access.token}")
    private String accessToken;

    private final InscripcionRepository inscripcionRepository;
    private final AuditoriaService auditoriaService;
    private final EmailService emailService;

    public MercadoPagoRestService(InscripcionRepository inscripcionRepository, AuditoriaService auditoriaService) {
        this.inscripcionRepository = inscripcionRepository;
        this.auditoriaService = auditoriaService;
        this.emailService = new EmailService();
    }

    public String crearPreferencia(String nombreClase, Integer inscripcionId){
        try {
            RestTemplate restTemplate = new RestTemplate();

            Inscripcion insc = inscripcionRepository.findById(inscripcionId)
                    .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));
            ClaseNivel claseNivel = insc.getClaseNivel();
            Integer claseNivelId = claseNivel.getId();
            String nivel = claseNivel.getNivel().getNombre();
            Double precioClase = claseNivel.getPrecio();

            // 🔍 Determinar monto a pagar
            double montoFinal = precioClase;
            NotaCredito nota = insc.getNotaCredito();
            if (nota != null && insc.getMontoPendiente() != null) {
                montoFinal = insc.getMontoPendiente();
            }

            // ❌ Verifica monto válido
            if (montoFinal <= 0) {
                throw new RuntimeException("Monto inválido: No se puede generar un pago por 0 o monto negativo.");
            }

            // 🔗 Construcción de URLs de retorno
            String baseUrl = "https://timbatumbao-front.onrender.com/html/registro.html";
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .queryParam("id", claseNivelId)
                    .queryParam("nivel", nivel)
                    .queryParam("precio", precioClase);

            String successUrl = builder.cloneBuilder().queryParam("estado", "exito").toUriString();
            String failureUrl = builder.cloneBuilder().queryParam("estado", "fallo").toUriString();
            String pendingUrl = builder.cloneBuilder().queryParam("estado", "pendiente").toUriString();

            // Headers con autorización
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Datos del producto
            Map<String, Object> item = Map.of(
                    "title", "Inscripción: " + nombreClase,
                    "quantity", 1,
                    "unit_price", montoFinal
            );

            // Cuerpo completo
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

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.mercadopago.com/checkout/preferences",
                    entity,
                    Map.class
            );

            Object sandboxUrl = response.getBody().get("sandbox_init_point");
            if (sandboxUrl == null) {
                throw new RuntimeException("No se pudo obtener la URL de pago");
            }

            return sandboxUrl.toString();

        } catch (Exception e) {
            auditoriaService.registrar("sistema", "ERROR_MP_PREFERENCIA",
                    "Error al generar preferencia para inscripción ID " + inscripcionId + ": " + e.getMessage());
            throw new RuntimeException("Error al generar preferencia de pago: " + e.getMessage());
        }
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
        Integer inscripcionId = Integer.parseInt(metadata.get("inscripcion_id").toString());


        if ("approved".equals(status)) {
    Inscripcion insc = inscripcionRepository.findById(inscripcionId).orElseThrow();
    insc.setEstado("aprobada");
    inscripcionRepository.save(insc);

    auditoriaService.registrar("sistema", "PAGO_APROBADO", "Pago aprobado para inscripción ID " + inscripcionId);

    // Enviar correo de confirmación
    emailService.enviarCorreo(
        insc.getCliente().getCorreo(),
        "Inscripción confirmada",
        "¡Hola " + insc.getCliente().getNombres() + "!\n\n" +
        "Tu inscripción a la clase " + insc.getClaseNivel().getClase().getNombre() +
        " - " + insc.getClaseNivel().getNivel().getNombre() +
        " ha sido aprobada exitosamente mediante Mercado Pago.\n\n¡Te esperamos!"
    );

    return true;
}


        auditoriaService.registrar("sistema", "PAGO_RECHAZADO", "Pago rechazado. ID " + paymentId);
        return false;
    }
}
