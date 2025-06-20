package com.academiabaile.backend.service;

import com.academiabaile.backend.config.UsuarioUtil;
import com.academiabaile.backend.entidades.AuditoriaEvento;
import com.academiabaile.backend.entidades.ModuloAcceso;
import com.academiabaile.backend.entidades.Usuario;
import com.academiabaile.backend.repository.AuditoriaEventoRepository;
import com.academiabaile.backend.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditoriaServiceImpl implements AuditoriaService {

    @Autowired
    private AuditoriaEventoRepository auditoriaEventoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;



// Sobrecarga con usuario explícito
@Override
public void registrar(Usuario usuario, String tipoEvento, String descripcion, ModuloAcceso modulo) {
    AuditoriaEvento evento = new AuditoriaEvento();
    evento.setUsuario(usuario);
    evento.setDescripcion(descripcion);
    evento.setFecha(LocalDateTime.now());
    evento.setTipoEvento(tipoEvento);
    evento.setModulo(modulo.getNombre());
     // puede ser null si no está logueado
    auditoriaEventoRepository.save(evento);
}


}
