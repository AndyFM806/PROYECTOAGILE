package com.academiabaile.backend.controller;

import com.academiabaile.backend.entidades.Clase;
import com.academiabaile.backend.entidades.ClaseNivelDTO;
import com.academiabaile.backend.service.ClaseService;
import com.academiabaile.backend.service.ClaseNivelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clases")
@CrossOrigin(origins = "*")
public class ClaseController {

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


}

