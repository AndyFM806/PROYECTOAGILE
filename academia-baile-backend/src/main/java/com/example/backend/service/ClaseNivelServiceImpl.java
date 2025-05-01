package com.example.backend.service;

import com.example.backend.entidades.ClaseNivel;
import com.example.backend.entidades.ClaseNivelDTO;
import com.example.backend.repository.ClaseNivelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClaseNivelServiceImpl implements ClaseNivelService {

    @Autowired
    private ClaseNivelRepository claseNivelRepository;


    @Override
    public List<ClaseNivelDTO> obtenerNivelesPorClase(Integer claseId) {
        
    List<ClaseNivel> lista = claseNivelRepository.findByClaseId(claseId);
    for (ClaseNivel cn : lista) {
        System.out.println("Nivel: " + (cn.getNivel() != null ? cn.getNivel().getNombre() : "null"));
        System.out.println("Horario: " + (cn.getHorario() != null ? cn.getHorario().getDias() : "null"));
    }
    
    return lista.stream().map(cn -> new ClaseNivelDTO(
        
        cn.getNivel().getNombre(),
        cn.getHorario().getDias(),
        cn.getHorario().getHora(),
        cn.getPrecio()
    )).collect(Collectors.toList());
    
    }


}

