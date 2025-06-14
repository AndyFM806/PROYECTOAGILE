package com.academiabaile.backend.service;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.academiabaile.backend.entidades.InscripcionDTO;
import com.academiabaile.backend.entidades.NotaCredito;
import com.academiabaile.backend.entidades.Cliente;
import com.academiabaile.backend.entidades.Inscripcion;
import com.academiabaile.backend.entidades.ClaseNivel;
import com.academiabaile.backend.repository.ClienteRepository;
import com.academiabaile.backend.repository.ClaseNivelRepository;
import com.academiabaile.backend.repository.InscripcionRepository;

    @Service
public class InscripcionServiceImpl implements InscripcionService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ClaseNivelRepository claseNivelRepository;

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private NotaCreditoService notaCreditoService;

    @Autowired
    private EmailService emailService;
    @Autowired
    private AuditoriaService auditoriaService;
@Override
public Integer registrar(InscripcionDTO dto) {
    ClaseNivel claseNivel = claseNivelRepository.findById(dto.getClaseNivelId())
        .orElseThrow(() -> new RuntimeException("ClaseNivel no encontrada"));

    // Validar aforo
    int inscritos = inscripcionRepository.countByClaseNivelAndEstado(claseNivel, "aprobada");
    if (inscritos >= claseNivel.getAforo()) {
        throw new RuntimeException("Clase llena");
    }

    // Obtener o crear cliente
    Cliente cliente = clienteRepository.findByDni(dto.getDni())
        .orElseGet(() -> {
            Cliente nuevo = new Cliente();
            nuevo.setDni(dto.getDni());
            nuevo.setNombres(dto.getNombres());
            nuevo.setApellidos(dto.getApellidos()); // ✅ Agregado correctamente
            nuevo.setCorreo(dto.getCorreo());
            return clienteRepository.save(nuevo);
        });

    // Verificar si ya está inscrito en esta clase
    if (inscripcionRepository.existsByClienteAndClaseNivel(cliente, claseNivel)) {
        throw new RuntimeException("El cliente ya está inscrito en esta clase");
    }

    // Crear nueva inscripción
    Inscripcion inscripcion = new Inscripcion();
    inscripcion.setCliente(cliente);
    inscripcion.setClaseNivel(claseNivel);
    inscripcion.setFechaInscripcion(LocalDate.now().atStartOfDay());

    // Lógica con nota de crédito
    if (dto.getCodigoNotaCredito() != null && !dto.getCodigoNotaCredito().isEmpty()) {
        NotaCredito nota = notaCreditoService.validarNota(dto.getCodigoNotaCredito());

        double precioClase = claseNivel.getPrecio();

        if (nota.getValor() >= precioClase) {
            // Nota cubre todo el precio
            notaCreditoService.marcarComoUsada(nota);
            inscripcion.setEstado("aprobada");
            inscripcion.setNotaCredito(nota);

            emailService.enviarCorreo(cliente.getCorreo(),
                "Inscripción completada con nota de crédito",
                "Tu inscripción a la clase " + claseNivel.getClase().getNombre() +
                " fue completada usando el código: " + nota.getCodigo() +
                ". No necesitas realizar ningún pago adicional.");
        } else {
            // Nota cubre parcialmente
            double diferencia = precioClase - nota.getValor();
            inscripcion.setEstado("pendiente_pago_diferencia");
            inscripcion.setMontoPendiente(diferencia);
            inscripcion.setNotaCredito(nota);
        }
    } else {
        // Sin nota de crédito → pago completo pendiente
        inscripcion.setEstado("pendiente");
    }

    inscripcionRepository.save(inscripcion);
    return inscripcion.getId();
}

    @Override
    public void completarPagoDiferencia(Integer inscripcionId, String metodo, String comprobanteUrl) {
    Inscripcion inscripcion = inscripcionRepository.findById(inscripcionId)
        .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));

    if (!"pendiente_pago_diferencia".equalsIgnoreCase(inscripcion.getEstado())) {
        throw new RuntimeException("La inscripción no está pendiente de pago de diferencia");
    }

    if ("comprobante".equalsIgnoreCase(metodo)) {
        inscripcion.setComprobanteUrl(comprobanteUrl);
        inscripcion.setEstado("pendiente_aprobacion_comprobante");

        auditoriaService.registrar("sistema", "PAGO_DIFERENCIA_COMPROBANTE",
            "Pago de diferencia registrado con comprobante para inscripción ID " + inscripcionId);

    } else if ("mercado_pago".equalsIgnoreCase(metodo)) {
        inscripcion.setEstado("aprobada");

        if (inscripcion.getNotaCredito() != null && !inscripcion.getNotaCredito().getUsada()) {
            notaCreditoService.marcarComoUsada(inscripcion.getNotaCredito());
        }

        emailService.enviarCorreo(
            inscripcion.getCliente().getCorreo(),
            "Inscripción completada",
            "Tu inscripción a la clase " + inscripcion.getClaseNivel().getClase().getNombre() +
            " fue aprobada tras completar el pago de diferencia con Mercado Pago."
        );

        auditoriaService.registrar("sistema", "PAGO_DIFERENCIA_MERCADO_PAGO",
            "Pago de diferencia completado con Mercado Pago para inscripción ID " + inscripcionId);
    }

    inscripcionRepository.save(inscripcion);
}



}
