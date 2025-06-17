package com.academiabaile.backend.controller;

import com.academiabaile.backend.entidades.ClaseNivel;
import com.academiabaile.backend.entidades.Clase;
import com.academiabaile.backend.entidades.Nivel;
import com.academiabaile.backend.entidades.Horario;
import com.academiabaile.backend.entidades.CrearClaseNivelDTO;
import com.academiabaile.backend.repository.ClaseNivelRepository;
import com.academiabaile.backend.repository.ClaseRepository;
import com.academiabaile.backend.repository.NivelRepository;
import com.academiabaile.backend.repository.HorarioRepository;
import com.academiabaile.backend.service.AuditoriaService;
import com.academiabaile.backend.service.ClaseNivelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clase-nivel")
@CrossOrigin(origins = {"https://timbatumbao-front.onrender.com", "http://localhost:5500"})
public class ClaseNivelController {

    @Autowired
    private ClaseNivelRepository claseNivelRepository;

    @Autowired
    private ClaseRepository claseRepository;

    @Autowired
    private NivelRepository nivelRepository;

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private ClaseNivelService claseNivelService;

    @Autowired
    private AuditoriaService auditoriaService;

    // Listar todas las combinaciones clase-nivel
    @GetMapping
    public List<ClaseNivel> listarClaseNiveles() {
        return claseNivelRepository.findAll();
    }

    // Obtener una clase nivel por ID
    @GetMapping("/{id}")
    public ResponseEntity<ClaseNivel> obtenerClaseNivelPorId(@PathVariable Integer id) {
        return claseNivelRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear clase nivel usando el DTO
    @PostMapping("/crear")
    public ResponseEntity<?> crearClaseNivel(@RequestBody CrearClaseNivelDTO dto) {
        try {
            Clase clase = claseRepository.findById(dto.getClaseId()).orElseThrow(() -> new RuntimeException("Clase no encontrada"));
            Nivel nivel = nivelRepository.findById(dto.getNivelId()).orElseThrow(() -> new RuntimeException("Nivel no encontrado"));
            Horario horario = horarioRepository.findById(dto.getHorarioId()).orElseThrow(() -> new RuntimeException("Horario no encontrado"));

            ClaseNivel nueva = new ClaseNivel();
            nueva.setClase(clase);
            nueva.setNivel(nivel);
            nueva.setHorario(horario);
            nueva.setPrecio(dto.getPrecio());
            nueva.setAforo(dto.getAforo() != null ? dto.getAforo() : 20);
            nueva.setEstado(dto.getEstado());

            ClaseNivel guardada = claseNivelRepository.save(nueva);

            auditoriaService.registrar("ADMIN", "CREACIÓN", "Se creó una clase nivel: ID " + guardada.getId());
            return ResponseEntity.ok(guardada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear ClaseNivel: " + e.getMessage());
        }
    }

    // Editar clase nivel
    @PutMapping("/{id}")
    public ResponseEntity<?> editarClaseNivel(@PathVariable Integer id, @RequestBody CrearClaseNivelDTO dto) {
        try {
            ClaseNivel existente = claseNivelRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("ClaseNivel no encontrada"));

            Clase clase = claseRepository.findById(dto.getClaseId()).orElseThrow(() -> new RuntimeException("Clase no encontrada"));
            Nivel nivel = nivelRepository.findById(dto.getNivelId()).orElseThrow(() -> new RuntimeException("Nivel no encontrado"));
            Horario horario = horarioRepository.findById(dto.getHorarioId()).orElseThrow(() -> new RuntimeException("Horario no encontrado"));

            existente.setClase(clase);
            existente.setNivel(nivel);
            existente.setHorario(horario);
            existente.setPrecio(dto.getPrecio());
            existente.setAforo(dto.getAforo());
            existente.setEstado(dto.getEstado());

            ClaseNivel actualizada = claseNivelRepository.save(existente);

            auditoriaService.registrar("ADMIN", "MODIFICACIÓN", "Se modificó clase nivel ID " + id);
            return ResponseEntity.ok(actualizada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al modificar ClaseNivel: " + e.getMessage());
        }
    }

    // Eliminar clase nivel
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarClaseNivel(@PathVariable Integer id) {
        if (!claseNivelRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        claseNivelRepository.deleteById(id);
        auditoriaService.registrar("ADMIN", "ELIMINACIÓN", "Se eliminó clase nivel ID " + id);
        return ResponseEntity.ok().build();
    }
}
