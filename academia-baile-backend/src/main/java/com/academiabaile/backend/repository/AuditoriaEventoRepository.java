package com.academiabaile.backend.repository;

import com.academiabaile.backend.entidades.AuditoriaEvento;
import java.util.List;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriaEventoRepository extends JpaRepository<AuditoriaEvento, Long> {
    List<AuditoriaEvento> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
}
