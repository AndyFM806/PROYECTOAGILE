package com.example.backend.service;

import com.example.backend.entidades.ClaseNivel;
import java.util.List;

public interface ClaseNivelService {
    List<ClaseNivel> obtenerNivelesPorClase(Integer claseId);
}
