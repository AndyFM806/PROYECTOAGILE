package com.academiabaile.backend.controller;

import com.academiabaile.backend.entidades.*;
import com.academiabaile.backend.repository.InscripcionRepository;
import com.academiabaile.backend.service.AlmacenamientoService;
import com.academiabaile.backend.service.AuditoriaService;
import com.academiabaile.backend.service.EmailService;
import com.academiabaile.backend.service.InscripcionService;
import com.academiabaile.backend.service.MercadoPagoRestService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {
    @Autowired
    private EmailService emailService;

    @Autowired
    private InscripcionService inscripcionService;

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private AlmacenamientoService almacenamientoService;

    @Autowired
    private AuditoriaService auditoriaService;

    @Autowired
    private MercadoPagoRestService mpService;

    // Registrar una nueva inscripción
    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody InscripcionDTO dto) {
        try {
            Integer id = inscripcionService.registrar(dto);

            auditoriaService.registrar(
                "sistema",
                "NUEVA_INSCRIPCION",
                "Se registró una inscripción pendiente con ID " + id + " para cliente DNI: " + dto.getDni()
            );
            emailService.enviarCorreo(
            dto.getCorreo(),
            "Registro de inscripción recibido",
            "Hola " + dto.getNombres() + ", hemos recibido tu solicitud de inscripción en la clase seleccionada. Te confirmaremos pronto."
        );


            return ResponseEntity.ok("Inscripción registrada con ID: " + id);
        } catch (RuntimeException e) {
            auditoriaService.registrar(
                "sistema",
                "ERROR_INSCRIPCION",
                "Error al intentar registrar inscripción para DNI: " + dto.getDni() + ". Motivo: " + e.getMessage()
            );
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Aprobar inscripción (automático)
    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<?> aprobar(@PathVariable Integer id) {
        try {
            Inscripcion insc = inscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));

            insc.setEstado("aprobada");
            inscripcionRepository.save(insc);

            auditoriaService.registrar(
                "admin",
                "APROBACION_INSCRIPCION",
                "Se aprobó la inscripción ID " + id + " del cliente DNI: " + insc.getCliente().getDni()
            );
            emailService.enviarCorreo(
            insc.getCliente().getCorreo(),
            "Inscripción aprobada",
            "Hola " + insc.getCliente().getNombres() + ", tu inscripción ha sido aprobada exitosamente. ¡Bienvenido a la academia Timba Tumbao!"
    );

            return ResponseEntity.ok("Inscripción aprobada");
        } catch (RuntimeException e) {
            auditoriaService.registrar(
                "admin",
                "ERROR_APROBACION",
                "Error al aprobar inscripción ID " + id + ": " + e.getMessage()
            );
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Aprobar inscripción (manual) — se cambió la ruta para evitar conflico
    @PatchMapping("/{id}/aprobar-manual")
public ResponseEntity<?> aprobarInscripcion(@PathVariable Integer id) {
    Inscripcion insc = inscripcionRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));

    if (!"pendiente".equalsIgnoreCase(insc.getEstado())) {
        return ResponseEntity.badRequest().body("Esta inscripción ya fue procesada");
    }

    insc.setEstado("aprobada");
    inscripcionRepository.save(insc);

    auditoriaService.registrar("admin", "APROBACION_MANUAL", "Admin aprobó inscripción ID " + id);

    // Enviar correo al cliente
    String correo = insc.getCliente().getCorreo();
    String asunto = "Inscripción aprobada - Timba Tumbao";
    String mensaje = String.format("Hola %s,\n\nTu inscripción en la clase '%s - %s' ha sido aprobada. ¡Te esperamos!",
        insc.getCliente().getNombres(),
        insc.getClaseNivel().getClase().getNombre(),
        insc.getClaseNivel().getNivel().getNombre());

    emailService.enviarCorreo(correo, asunto, mensaje);

    return ResponseEntity.ok("Inscripción aprobada");
}
    // Rechazar inscripción
    @PatchMapping("/{id}/rechazar")
public ResponseEntity<?> rechazarInscripcion(@PathVariable Integer id) {
    Inscripcion insc = inscripcionRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));

    if (!"pendiente".equalsIgnoreCase(insc.getEstado())) {
        return ResponseEntity.badRequest().body("Esta inscripción ya fue procesada");
    }

    insc.setEstado("rechazada");
    inscripcionRepository.save(insc);

    auditoriaService.registrar("admin", "RECHAZO_MANUAL", "Admin rechazó inscripción ID " + id);

    // Enviar correo al cliente
    String correo = insc.getCliente().getCorreo();
    String asunto = "Inscripción rechazada - Timba Tumbao";
    String mensaje = String.format("Hola %s,\n\nLamentamos informarte que tu inscripción en la clase '%s - %s' ha sido rechazada. Puedes intentarlo nuevamente o contactarnos para más información.",
        insc.getCliente().getNombres(),
        insc.getClaseNivel().getClase().getNombre(),
        insc.getClaseNivel().getNivel().getNombre());

    emailService.enviarCorreo(correo, asunto, mensaje);

    return ResponseEntity.ok("Inscripción rechazada");
}


    // Subir comprobante de pago
    @PostMapping("/comprobante/{id}")
    public ResponseEntity<?> subirComprobante(@PathVariable Integer id, @RequestParam("file") MultipartFile file) {
        String url = almacenamientoService.guardar(file);

        Inscripcion insc = inscripcionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));

        insc.setComprobanteUrl(url);
        inscripcionRepository.save(insc);

        return ResponseEntity.ok("Comprobante subido");
    }

    // Generar pago con Mercado Pago
    @PostMapping("/generar-pago/{inscripcionId}")
    public ResponseEntity<?> generarPago(@PathVariable Integer inscripcionId) {
        Inscripcion insc = inscripcionRepository.findById(inscripcionId)
            .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));

        String nombre = insc.getClaseNivel().getClase().getNombre() + " - " + insc.getClaseNivel().getNivel().getNombre();
        Double precio = insc.getClaseNivel().getPrecio();

        String urlPago = mpService.crearPreferencia(precio, nombre, inscripcionId);

        return ResponseEntity.ok(urlPago);
    }

    // Listar inscripciones pendientes con comprobante
    @GetMapping("/pendientes-con-comprobante")
    public ResponseEntity<?> listarPendientesConComprobante() {
        List<Inscripcion> lista = inscripcionRepository.findByEstadoAndComprobanteUrlIsNotNull("pendiente");

        auditoriaService.registrar(
            "admin",
            "CONSULTA_INSCRIPCIONES_PENDIENTES",
            "Admin consultó lista de inscripciones con comprobante pendiente"
        );

        return ResponseEntity.ok(lista);
    }
    @GetMapping
public ResponseEntity<?> listarTodas() {
    List<Inscripcion> lista = inscripcionRepository.findAll();
    return ResponseEntity.ok(lista);
}
@PostMapping("/completar-pago-diferencia")
public ResponseEntity<?> completarPagoDiferencia(@RequestParam Integer id,
                                                 @RequestParam String metodo,
                                                 @RequestParam(required = false) String comprobanteUrl) {
    try {
        inscripcionService.completarPagoDiferencia(id, metodo, comprobanteUrl);
        return ResponseEntity.ok().body("Pago completado");
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

    // En InscripcionController.java

@GetMapping("/pendientes-diferencia")
public ResponseEntity<?> listarPendientesDiferencia() {
    List<Inscripcion> lista = inscripcionRepository
        .findByEstadoAndNotaCreditoIsNotNullAndMontoPendienteGreaterThan("pendiente_pago_diferencia", 0.0);

    return ResponseEntity.ok(lista);
}

}
