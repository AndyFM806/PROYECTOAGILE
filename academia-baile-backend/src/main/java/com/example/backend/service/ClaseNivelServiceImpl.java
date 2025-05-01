package com.example.backend.service;

import com.example.backend.entidades.ClaseNivel;
import com.example.backend.repository.ClaseNivelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClaseNivelServiceImpl implements ClaseNivelService {

    @Autowired
    private ClaseNivelRepository claseNivelRepository;

    @Override
    public List<ClaseNivel> obtenerNivelesPorClase(Integer claseId) {
        return claseNivelRepository.findByClase_Id(claseId);
    }
}

