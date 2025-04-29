package com.example.backend.service;

import com.example.backend.entidades.Clase;
import com.example.backend.repository.ClaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClaseServiceImpl implements ClaseService {

    @Autowired
    private ClaseRepository claseRepository;

    @Override
    public List<Clase> listarClases() {
        return claseRepository.findAll();
    }
}
