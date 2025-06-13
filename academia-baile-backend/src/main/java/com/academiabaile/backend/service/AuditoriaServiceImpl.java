package com.academiabaile.backend.service;

import com.academiabaile.backend.entidades.AuditoriaEvento;
import com.academiabaile.backend.repository.AuditoriaEventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditoriaServiceImpl implements AuditoriaService {

    @Autowired
    private AuditoriaEventoRepository auditoriaEventoRepository;

    @Override
    public void registrar(String usuario, String tipoEvento, String descripcion) {
        AuditoriaEvento evento = new AuditoriaEvento();
        evento.setUsuario(usuario);
        evento.setTipoEvento(tipoEvento);
        evento.setDescripcion(descripcion);
        auditoriaEventoRepository.save(evento);
    }
}
