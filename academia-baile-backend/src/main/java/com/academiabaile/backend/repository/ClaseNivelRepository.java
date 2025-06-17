package com.academiabaile.backend.repository;

import com.academiabaile.backend.entidades.ClaseNivel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ClaseNivelRepository extends JpaRepository<ClaseNivel, Integer> {
    List<ClaseNivel> findByFechaCierreAndEstado(LocalDate fechaCierre, String estado);
    List<ClaseNivel> findByClaseId(Integer claseId);
    List<ClaseNivel> findByClaseIdAndEstado(Integer claseId, String estado);
    List<ClaseNivel> findByEstado(String estado);


}

