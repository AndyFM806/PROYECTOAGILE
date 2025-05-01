package com.example.backend.repository;

import com.example.backend.entidades.ClaseNivel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClaseNivelRepository extends JpaRepository<ClaseNivel, Integer> {
    List<ClaseNivel> findByClaseId(Integer claseId);
}

