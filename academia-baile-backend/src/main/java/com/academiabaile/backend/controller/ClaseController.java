package com.academiabaile.backend.controller;

import com.academiabaile.backend.entidades.Clase;
import com.academiabaile.backend.entidades.ClaseNivelDTO;
import com.academiabaile.backend.entidades.ClienteDTO;
import com.academiabaile.backend.entidades.NivelResumenDTO;
import com.academiabaile.backend.service.ClaseService;
import com.academiabaile.backend.service.NivelResumenService;
import com.academiabaile.backend.service.AlumnosPorClaseService;
import com.academiabaile.backend.service.ClaseNivelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clases")
@CrossOrigin(origins = {"https://timbatumbao-front.onrender.com", "http://localhost:5500"})
public class ClaseController {

    @Autowired
    private NivelResumenService nivelResumenService;

    @Autowired
    private AlumnosPorClaseService alumnosPorClaseService;

    @Autowired
    private ClaseService claseService;

    @Autowired
    private ClaseNivelService claseNivelService;

    @GetMapping
    public List<Clase> listarClases() {
        return claseService.listarClases();
    }

    @GetMapping("/{id}/niveles")
    public ResponseEntity<List<ClaseNivelDTO>> obtenerNivelesPorClase(@PathVariable Integer id) {
        return ResponseEntity.ok(claseNivelService.obtenerNivelesPorClase(id));
    }

    @GetMapping("/{id}/niveles-resumen")
    public ResponseEntity<List<NivelResumenDTO>> obtenerResumenNiveles(@PathVariable Integer id) {
        return ResponseEntity.ok(nivelResumenService.obtenerResumenNivelesPorClase(id));
    }

    @GetMapping("/{id}/alumnos")
    public ResponseEntity<List<ClienteDTO>> listarAlumnosPorClaseNivel(@PathVariable Integer id) {
        return ResponseEntity.ok(alumnosPorClaseService.obtenerAlumnosPorClaseNivel(id));
    }
}
