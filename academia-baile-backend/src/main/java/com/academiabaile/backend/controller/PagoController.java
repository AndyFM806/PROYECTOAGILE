package com.academiabaile.backend.controller;

import com.academiabaile.backend.repository.InscripcionRepository;
import com.academiabaile.backend.service.AuditoriaService;
import com.academiabaile.backend.service.MercadoPagoRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private AuditoriaService auditoriaService;

    @Autowired
    private MercadoPagoRestService mercadoPagoRestService;

    @PostMapping("/webhook")
    public ResponseEntity<?> recibirNotificacion(@RequestBody Map<String, Object> payload) {
        try {
            String tipo = (String) payload.get("type");
            if (!"payment".equals(tipo)) {
                return ResponseEntity.ok("Evento no relacionado a pago. Ignorado.");
            }

            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            String paymentId = data.get("id").toString();

            boolean aprobado = mercadoPagoRestService.pagoEsAprobado(paymentId);

            if (aprobado) {
                return ResponseEntity.ok("Pago aprobado y procesado");
            } else {
                return ResponseEntity.ok("Pago recibido pero no aprobado");
            }

        } catch (Exception e) {
            auditoriaService.registrar("sistema", "ERROR_PAGO", "Error al procesar notificación: " + e.getMessage());
            return ResponseEntity.badRequest().body("Error");
        }
    }

}

