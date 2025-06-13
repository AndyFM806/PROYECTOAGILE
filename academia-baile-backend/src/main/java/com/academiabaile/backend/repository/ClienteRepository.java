package com.academiabaile.backend.repository;


import com.academiabaile.backend.entidades.Cliente;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    boolean existsByDniAndClaseNivel_Id(String dni, Integer claseNivelId);
    List<Cliente> findByClaseNivel_Id(Integer claseNivelId);
    void deleteById(Integer id);
}
