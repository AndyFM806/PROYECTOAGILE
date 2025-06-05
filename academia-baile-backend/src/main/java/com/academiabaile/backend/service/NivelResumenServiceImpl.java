package com.academiabaile.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.academiabaile.backend.entidades.ClaseNivel;
import com.academiabaile.backend.entidades.NivelResumenDTO;
import com.academiabaile.backend.repository.ClaseNivelRepository;
import com.academiabaile.backend.repository.ClienteRepository;

@Service
public class NivelResumenServiceImpl implements NivelResumenService {
   @Autowired
private ClaseNivelRepository ClaseNivelRepository;

@Autowired
private ClienteRepository ClienteRepository;


    @Override
public List<NivelResumenDTO> obtenerResumenNivelesPorClase(Integer claseId) {
    List<ClaseNivel> niveles = ClaseNivelRepository.findByClaseId(claseId);

    return niveles.stream().map(nivel -> {
        NivelResumenDTO dto = new NivelResumenDTO();
        dto.setNivel(nivel.getNivel().getNombre());
        dto.setHorario(nivel.getHorario().getHora());
        dto.setInscritos(ClienteRepository.countByClaseNivel(nivel));
        return dto;
    }).collect(Collectors.toList());
}

}
