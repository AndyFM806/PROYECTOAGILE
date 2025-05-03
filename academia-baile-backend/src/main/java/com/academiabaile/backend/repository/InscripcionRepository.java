package com.academiabaile.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.academiabaile.backend.entidades.Inscripcion;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer>{
    
}
