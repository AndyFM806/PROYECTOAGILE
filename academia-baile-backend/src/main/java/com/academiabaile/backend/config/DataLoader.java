package com.academiabaile.backend.config;

import com.academiabaile.backend.entidades.ModuloAcceso;
import com.academiabaile.backend.entidades.Usuario;
import com.academiabaile.backend.entidades.Rol;
import com.academiabaile.backend.repository.ModuloAccesoRepository;
import com.academiabaile.backend.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModuloAccesoRepository moduloAccesoRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (!usuarioRepository.existsByNombreUsuario("admin")) {

            // Crear módulos si no existen
            ModuloAcceso insc = new ModuloAcceso(); insc.setNombre("Inscripciones");
            ModuloAcceso clases = new ModuloAcceso(); clases.setNombre("Clases");
            ModuloAcceso alumnos = new ModuloAcceso(); alumnos.setNombre("Alumnos");
            ModuloAcceso reportes = new ModuloAcceso(); reportes.setNombre("Reportes");

            moduloAccesoRepository.saveAll(List.of(insc, clases, alumnos, reportes));

            // Crear admin
            Usuario admin = new Usuario();
            admin.setNombreUsuario("admin");
            admin.setContrasena(encoder.encode("admin123"));
            admin.setCorreoRecuperacion("timbatumbaomail@gmail.com");
            admin.setRol(Rol.ADMIN);
            admin.setModulos(Set.of(insc, clases, alumnos, reportes));
            usuarioRepository.save(admin);
        }
    }
}
