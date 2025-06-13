package com.academiabaile.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.academiabaile.backend.entidades.ClaseNivel;
import com.academiabaile.backend.entidades.Inscripcion;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer>{
    int countByClaseNivelAndEstado(ClaseNivel claseNivel, String estado); // para validar aforo
    List<Inscripcion> findByEstadoAndComprobanteUrlIsNotNull(String estado);

}
