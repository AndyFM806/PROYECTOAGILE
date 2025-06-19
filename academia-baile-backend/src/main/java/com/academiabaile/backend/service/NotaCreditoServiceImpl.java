package com.academiabaile.backend.service;

import com.academiabaile.backend.config.UsuarioUtil;
import com.academiabaile.backend.entidades.*;
import com.academiabaile.backend.repository.ModuloAccesoRepository;
import com.academiabaile.backend.repository.NotaCreditoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class NotaCreditoServiceImpl implements NotaCreditoService {
    @Autowired
    private ModuloAccesoRepository moduloAccesoRepository;

    @Autowired
    private AuditoriaService auditoriaService;

    @Autowired
    private NotaCreditoRepository notaCreditoRepository;

    @Override
    public NotaCredito generarNotaCredito(Cliente cliente, Double valor, ClaseNivel claseCancelada) {
        NotaCredito nota = new NotaCredito();
        nota.setCliente(cliente);
        nota.setValor(valor);
        nota.setCodigo(UUID.randomUUID().toString().substring(0, 10));
        nota.setFechaEmision(LocalDate.now());
        nota.setFechaExpiracion(LocalDate.now().plusMonths(6));
        nota.setClaseCancelada(claseCancelada);
        ModuloAcceso modulo = moduloAccesoRepository.findByNombre("INSCRIPCIONES");
        auditoriaService.registrar(
            UsuarioUtil.obtenerUsuarioActual(),
            "NOTA_GENERADA",
            "Nota de crédito emitida para cliente " + cliente.getNombres() +
            " por S/ " + valor + ", Clase cancelada: " + claseCancelada.getClase().getNombre(),
            modulo
        );

        return notaCreditoRepository.save(nota);
    }

    @Override
public void marcarComoUsada(NotaCredito notaCredito) {
    NotaCredito nota = notaCreditoRepository.findById(notaCredito.getId())
            .orElseThrow(() -> new IllegalArgumentException("Nota de crédito no encontrada"));
    ModuloAcceso modulo = moduloAccesoRepository.findByNombre("INSCRIPCIONES");
auditoriaService.registrar(
    UsuarioUtil.obtenerUsuarioActual(),
    "NOTA_USADA",
    "Nota de crédito usada: código " + nota.getCodigo() + " por cliente " +
    nota.getCliente().getNombres(),
    modulo
);


    nota.setUsada(true);
    notaCreditoRepository.save(nota);
}
public NotaCredito validarNota(String codigo) {
    NotaCredito nota = notaCreditoRepository.findByCodigo(codigo)
            .orElseThrow(() -> new RuntimeException("Código de nota de crédito inválido"));

    if (nota.getUsada()) {
        throw new RuntimeException("La nota de crédito ya fue usada");
    }

    if (nota.getFechaExpiracion().isBefore(LocalDate.now())) {
        throw new RuntimeException("La nota de crédito está vencida");
    }

    return nota;
}
    @Override
    public NotaCredito crearNotaCreditoNueva(Cliente cliente, Double valor, LocalDate fechaEmision, LocalDate fechaExpiracion, ClaseNivel claseCancelada) {
    NotaCredito nota = new NotaCredito();
    nota.setCliente(cliente);
    nota.setValor(valor);
    nota.setFechaEmision(fechaEmision);
    nota.setFechaExpiracion(fechaExpiracion);
    nota.setCodigo(UUID.randomUUID().toString().substring(0, 10));
    nota.setClaseCancelada(claseCancelada); // ✅ ¡Esto es lo que faltaba!
    ModuloAcceso modulo = moduloAccesoRepository.findByNombre("INSCRIPCIONES");
    auditoriaService.registrar(
        UsuarioUtil.obtenerUsuarioActual(),
        "NOTA_MANUAL",
        "Nota de crédito creada manualmente para cliente " + cliente.getNombres() +
        " por S/ " + valor + ", Clase cancelada: " + (claseCancelada != null ? claseCancelada.getClase().getNombre() : "N/A"),
        modulo
    );
    return notaCreditoRepository.save(nota);
}


    
}
