package com.example.backend.controller;

import com.example.backend.entidades.Clase;
import com.example.backend.entidades.ClaseNivel;
import com.example.backend.service.ClaseService;
import com.example.backend.service.ClaseNivelService;

import org.springframework.beans.factory.annotation.Autowired;
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
    public List<ClaseNivel> obtenerNivelesPorClase(@PathVariable Integer id) {
        return claseNivelService.obtenerNivelesPorClase(id);
    }
}

