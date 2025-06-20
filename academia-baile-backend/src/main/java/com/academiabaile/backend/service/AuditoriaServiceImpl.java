package com.academiabaile.backend.service;

import com.academiabaile.backend.config.UsuarioUtil;
import com.academiabaile.backend.entidades.AuditoriaEvento;
import com.academiabaile.backend.entidades.ModuloAcceso;
import com.academiabaile.backend.entidades.Usuario;
import com.academiabaile.backend.repository.AuditoriaEventoRepository;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditoriaServiceImpl implements AuditoriaService {

    @Autowired
    private AuditoriaEventoRepository auditoriaEventoRepository;

    @Override
    public void registrar(Usuario usuario, String tipoEvento, String descripcion,ModuloAcceso modulo) {
        
         AuditoriaEvento evento = new AuditoriaEvento();
        evento.setUsuario(UsuarioUtil.obtenerUsuarioActual());
        evento.setModulo(modulo.getNombre()); // Usa bien esta columna en tus reportes
        evento.setTipoEvento(tipoEvento);
        evento.setDescripcion(descripcion);
        evento.setFecha(LocalDateTime.now());
        auditoriaEventoRepository.save(evento);
        
        auditoriaEventoRepository.save(evento);
    }
}
