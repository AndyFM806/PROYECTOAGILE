package com.academiabaile.backend.controller;

import com.academiabaile.backend.entidades.SolicitudCambio;
import com.academiabaile.backend.service.SolicitudCambioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudCambioController {

    @Autowired
    private SolicitudCambioService solicitudCambioService;

    @PostMapping
    public SolicitudCambio registrar(@RequestBody SolicitudCambio solicitud) {
        return solicitudCambioService.registrarSolicitud(solicitud);
    }

    @GetMapping("/pendientes")
    public List<SolicitudCambio> listarPendientes() {
        return solicitudCambioService.listarPendientes();
    }

    @GetMapping("/usuario/{id}")
    public List<SolicitudCambio> listarPorUsuario(@PathVariable Long id) {
        return solicitudCambioService.listarPorUsuario(id);
    }

    @PutMapping("/{id}/atender")
    public SolicitudCambio atender(
        @PathVariable Long id,
        @RequestParam("respuesta") String respuesta,
        @RequestParam("aprobar") boolean aprobar
    ) {
        return solicitudCambioService.atenderSolicitud(id, respuesta, aprobar);
    }
}
