package com.academiabaile.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.academiabaile.backend.entidades.Cliente;
import com.academiabaile.backend.entidades.ClienteDTO;
import com.academiabaile.backend.repository.ClienteRepository;

@Service
public class AlumnosPorClaseServiceImpl implements AlumnosPorClaseService {
    @Autowired
    private ClienteRepository ClienteRepository;


    @Override
    public List<ClienteDTO> obtenerAlumnosPorClaseNivel(Integer claseNivelId) {
        List<Cliente> alumnos = ClienteRepository.findByClaseNivel_Id(claseNivelId);

    return alumnos.stream().map(c -> {
        ClienteDTO dto = new ClienteDTO();
        dto.setNombres(c.getNombres());
        dto.setApellidos(c.getApellidos());
        dto.setDni(c.getDni());
        dto.setCorreo(c.getCorreo());
        return dto;
    }).collect(Collectors.toList());
}
}
