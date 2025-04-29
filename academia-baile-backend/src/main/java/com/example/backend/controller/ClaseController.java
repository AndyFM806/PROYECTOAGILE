package com.example.backend.controller;

import com.example.backend.entidades.Clase;
import com.example.backend.service.ClaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/clases")
@CrossOrigin(origins = "*") // Para permitir solicitudes del frontend
public class ClaseController {

    @Autowired
    private ClaseService claseService;

    @GetMapping
    public List<Clase> listarClases() {
        return claseService.listarClases();
    }
}
