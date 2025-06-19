package com.academiabaile.backend.controller;

import com.academiabaile.backend.repository.AuditoriaEventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    @Autowired
    private AuditoriaEventoRepository repo;

    @GetMapping
    public Object listarEventos() {
        return repo.findAll();
    }
    
}
