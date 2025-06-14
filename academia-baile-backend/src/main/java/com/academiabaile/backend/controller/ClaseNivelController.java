package com.academiabaile.backend.controller;

import com.academiabaile.backend.entidades.ClaseNivel;
import com.academiabaile.backend.repository.ClaseNivelRepository;
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

    // Crear nueva clase nivel
    @PostMapping
    public ResponseEntity<ClaseNivel> crearClaseNivel(@RequestBody ClaseNivel claseNivel) {
        claseNivel.setAforo(20); // aforo predeterminado
        ClaseNivel nueva = claseNivelRepository.save(claseNivel);
        return ResponseEntity.ok(nueva);
    }

    // Listar todas las combinaciones clase-nivel
    @GetMapping
    public List<ClaseNivel> listarClaseNiveles() {
        return claseNivelRepository.findAll();
    }

    // Obtener claseNivel por ID
    @GetMapping("/{id}")
    public ResponseEntity<ClaseNivel> obtenerClaseNivelPorId(@PathVariable Integer id) {
        return claseNivelRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Editar clase nivel
    @PutMapping("/{id}")
    public ResponseEntity<ClaseNivel> editarClaseNivel(@PathVariable Integer id, @RequestBody ClaseNivel actualizada) {
        return claseNivelRepository.findById(id).map(existente -> {
            existente.setPrecio(actualizada.getPrecio());
            existente.setNivel(actualizada.getNivel());
            existente.setHorario(actualizada.getHorario());
            existente.setClase(actualizada.getClase());
            existente.setAforo(actualizada.getAforo());
            return ResponseEntity.ok(claseNivelRepository.save(existente));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Eliminar clase nivel
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarClaseNivel(@PathVariable Integer id) {
        if (!claseNivelRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        claseNivelRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
