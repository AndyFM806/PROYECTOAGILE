package com.academiabaile.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.academiabaile.backend.entidades.ClaseNivel;
import com.academiabaile.backend.entidades.Cliente;
import com.academiabaile.backend.entidades.Inscripcion;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer>{
    int countByClaseNivelAndEstado(ClaseNivel claseNivel, String estado); // para validar aforo
    List<Inscripcion> findByEstadoAndComprobanteUrlIsNotNull(String estado);
    boolean existsByClienteAndClaseNivel(Cliente cliente, ClaseNivel claseNivel);
    List<Inscripcion> findByClaseNivel_IdInAndEstado(List<Integer> claseNivelIds, String estado);
    List<Inscripcion> findByClaseNivelIn(List<ClaseNivel> niveles);
    List<Inscripcion> findByClaseNivel(ClaseNivel claseNivel);
    List<Inscripcion> findByEstadoAndNotaCreditoIsNotNullAndMontoPendienteGreaterThan(String estado, Double montoMinimo);
    Inscripcion findByClienteAndClaseNivel(Cliente cliente, ClaseNivel claseNivel);
    List<Inscripcion> findByClaseNivelAndEstado(ClaseNivel claseNivel, String estado);
    List<Inscripcion> findByClienteId(Integer clienteId);
    boolean existsByClienteIdAndClaseNivelId(Integer clienteId, Integer claseNivelId);
    java.util.Optional<Inscripcion> findByClienteIdAndClaseNivelId(Integer clienteId, Integer claseNivelId);
    
}
