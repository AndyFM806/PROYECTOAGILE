package com.academiabaile.backend.service;

import com.academiabaile.backend.config.UsuarioUtil;
import com.academiabaile.backend.entidades.ModuloAcceso;
import com.academiabaile.backend.entidades.SolicitudCambio;
import com.academiabaile.backend.entidades.SolicitudCambio.EstadoSolicitud;
import com.academiabaile.backend.entidades.Usuario;
import com.academiabaile.backend.repository.ModuloAccesoRepository;
import com.academiabaile.backend.repository.SolicitudCambioRepository;
import com.academiabaile.backend.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SolicitudCambioServiceImpl implements SolicitudCambioService {

    @Autowired
    private SolicitudCambioRepository solicitudCambioRepository;

    @Autowired
    private AuditoriaService auditoriaService;

    @Autowired
    private ModuloAccesoRepository moduloAccesoRepository;


   @Override
    public SolicitudCambio registrarSolicitud(SolicitudCambio solicitud) {
    solicitud.setEstado(EstadoSolicitud.PENDIENTE);
    solicitud.setFechaCreacion(LocalDateTime.now());
    
    // Guardar solicitud primero
    SolicitudCambio guardada = solicitudCambioRepository.save(solicitud);

    // Obtener el usuario completo desde su ID (ya que solo se envía el ID desde el frontend)
    solicitudCambioRepository.save(solicitud);

        return guardada;
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
        SolicitudCambio actualizada = solicitudCambioRepository.save(solicitud);

        ModuloAcceso modulo = moduloAccesoRepository.findByNombre("USUARIOS");
        String tipo = aprobar ? "SOLICITUD_CAMBIO_APROBADA" : "SOLICITUD_CAMBIO_RECHAZADA";
        String descripcion = "Solicitud ID " + id + " fue " + (aprobar ? "aprobada" : "rechazada") +
                " por el admin. Respuesta: " + respuesta;

        auditoriaService.registrar(
            UsuarioUtil.obtenerUsuarioActual(),
            tipo,
            descripcion,
            modulo
        );

        return actualizada;
    }

    @Override
    public List<SolicitudCambio> listarTodas() {
        return solicitudCambioRepository.findAll();
    }
}
