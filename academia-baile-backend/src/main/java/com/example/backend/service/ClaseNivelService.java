package com.example.backend.service;

import com.example.backend.entidades.ClaseNivel;
import com.example.backend.entidades.ClaseNivelDTO;

import java.util.List;

public interface ClaseNivelService {
        List<ClaseNivelDTO> obtenerNivelesPorClase(Integer claseId);
}
