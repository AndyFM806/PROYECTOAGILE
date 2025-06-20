package com.academiabaile.backend.service;

import com.academiabaile.backend.entidades.AuditoriaEvento;
import com.academiabaile.backend.entidades.ModuloAcceso;

import com.academiabaile.backend.repository.AuditoriaEventoRepository;


import java.time.LocalDateTime;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditoriaServiceImpl implements AuditoriaService {

    @Autowired
    private AuditoriaEventoRepository auditoriaEventoRepository;



// Sobrecarga con usuario explícito
@Override
public void registrar(String tipoEvento, String descripcion, ModuloAcceso modulo) {
    AuditoriaEvento evento = new AuditoriaEvento();
    evento.setDescripcion(descripcion);
    evento.setFecha(LocalDateTime.now());
    evento.setTipoEvento(tipoEvento);
    evento.setModulo(modulo.getNombre());
     // puede ser null si no está logueado
    auditoriaEventoRepository.save(evento);
}


}
