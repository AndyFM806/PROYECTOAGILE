package com.academiabaile.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import com.academiabaile.backend.entidades.Usuario;

import com.academiabaile.backend.repository.UsuarioRepository;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {



    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario crearUsuario(Usuario usuario) {
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));

        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario editarUsuario(Long id, Usuario nuevo) {
        Usuario existente = usuarioRepository.findById(id).orElseThrow();
        existente.setNombreUsuario(nuevo.getNombreUsuario());
        if (nuevo.getContrasena() != null && !nuevo.getContrasena().isBlank()) {
        existente.setContrasena(passwordEncoder.encode(nuevo.getContrasena()));
        }
        existente.setRol(nuevo.getRol());
        existente.getModulos().clear(); // Limpia los módulos actuales
        existente.getModulos().addAll(nuevo.getModulos()); // Agrega los nuevos

        return usuarioRepository.save(existente);

    }

    @Override
    public void eliminarUsuario(Long id) {

        usuarioRepository.deleteById(id);

    }

    @Override
    public Usuario obtenerPorNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
