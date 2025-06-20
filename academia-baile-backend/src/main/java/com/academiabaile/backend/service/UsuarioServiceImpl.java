package com.academiabaile.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.academiabaile.backend.config.UsuarioUtil;
import com.academiabaile.backend.entidades.ModuloAcceso;
import com.academiabaile.backend.entidades.Usuario;
import com.academiabaile.backend.repository.ModuloAccesoRepository;
import com.academiabaile.backend.repository.UsuarioRepository;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    @Autowired
    private ModuloAccesoRepository moduloAccesoRepository;

    @Autowired
    private AuditoriaService auditoriaService;

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
        ModuloAcceso modulo = moduloAccesoRepository.findByNombre("USUARIOS");
        auditoriaService.registrar(
        UsuarioUtil.obtenerUsuarioActual(),
        "USUARIO_CREADO",
        "Usuario creado: " + usuario.getNombreUsuario() + " - Rol: " + usuario.getRol(),
        modulo
    );

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
        existente.setModulos(nuevo.getModulos());
        ModuloAcceso modulo = moduloAccesoRepository.findByNombre("USUARIOS");
        auditoriaService.registrar(
            UsuarioUtil.obtenerUsuarioActual(),
            "USUARIO_EDITADO",
            "Usuario editado: " + nuevo.getNombreUsuario() + " - Rol: " + nuevo.getRol(),
            modulo
        );

        return usuarioRepository.save(existente);

    }

    @Override
    public void eliminarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        usuarioRepository.deleteById(id);
        ModuloAcceso modulo = moduloAccesoRepository.findByNombre("USUARIOS");
        auditoriaService.registrar(
            UsuarioUtil.obtenerUsuarioActual(),
            "USUARIO_ELIMINADO",
            "Usuario eliminado: " + usuario.getNombreUsuario(),
            modulo
        );
    }

    @Override
    public Usuario obtenerPorNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
