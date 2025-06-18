package com.academiabaile.backend.service;

import com.academiabaile.backend.entidades.SolicitudCambio;
import com.academiabaile.backend.entidades.SolicitudCambio.EstadoSolicitud;
import com.academiabaile.backend.repository.SolicitudCambioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SolicitudCambioServiceImpl implements SolicitudCambioService {

    @Autowired
    private SolicitudCambioRepository solicitudCambioRepository;

    @Override
    public SolicitudCambio registrarSolicitud(SolicitudCambio solicitud) {
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);
        solicitud.setFechaCreacion(LocalDateTime.now());
        return solicitudCambioRepository.save(solicitud);
    }

    @Override
    public List<SolicitudCambio> listarPendientes() {
        return solicitudCambioRepository.findByEstado(EstadoSolicitud.PENDIENTE);
    }

    @Override
    public List<SolicitudCambio> listarPorUsuario(Long usuarioId) {
        return solicitudCambioRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public SolicitudCambio atenderSolicitud(Long id, String respuesta, boolean aprobar) {
        SolicitudCambio solicitud = solicitudCambioRepository.findById(id).orElseThrow();
        solicitud.setRespuesta(respuesta);
        solicitud.setFechaRespuesta(LocalDateTime.now());
        solicitud.setEstado(aprobar ? EstadoSolicitud.ATENDIDA : EstadoSolicitud.RECHAZADA);
        return solicitudCambioRepository.save(solicitud);
    }
    @Override
    public List<SolicitudCambio> listarTodas() {
    return solicitudCambioRepository.findAll();
}

}
