package com.academiabaile.backend.controller;

import com.academiabaile.backend.entidades.Usuario;
import com.academiabaile.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Usuario login(@RequestBody Usuario login) {
        Usuario user = usuarioRepository.findByNombreUsuario(login.getNombreUsuario());
        if (user != null && passwordEncoder.matches(login.getContrasena(), user.getContrasena())) {
            return user;
        } else {
            throw new RuntimeException("Credenciales inválidas");
        }
    }
}
