package com.academiabaile.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.academiabaile.backend.entidades.*;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByNombreUsuario(String nombreUsuario);
    Usuario findByNombreUsuario(String nombreUsuario);
}


