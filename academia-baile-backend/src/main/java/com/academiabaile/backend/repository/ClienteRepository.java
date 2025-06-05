package com.academiabaile.backend.repository;

import com.academiabaile.backend.entidades.ClaseNivel;
import com.academiabaile.backend.entidades.Cliente;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    int countByClaseNivel(ClaseNivel claseNivel);
    List<Cliente> findByClaseNivel_Id(Integer claseNivelId);

}
