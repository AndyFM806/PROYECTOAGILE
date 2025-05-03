package com.academiabaile.backend.repository;

import com.academiabaile.backend.entidades.ClaseNivel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClaseNivelRepository extends JpaRepository<ClaseNivel, Integer> {
    List<ClaseNivel> findByClaseId(Integer claseId);
}

