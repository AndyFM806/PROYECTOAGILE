// HorarioRepository.java
package com.example.backend.repository;

import com.example.backend.entidades.Horario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HorarioRepository extends JpaRepository<Horario, Integer> {}
