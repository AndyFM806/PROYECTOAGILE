package com.academiabaile.backend.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.academiabaile.backend.entidades.InscripcionDTO;
import com.academiabaile.backend.entidades.Cliente;
import com.academiabaile.backend.entidades.Inscripcion;
import com.academiabaile.backend.entidades.ClaseNivel;
import com.academiabaile.backend.repository.ClienteRepository;
import com.academiabaile.backend.repository.ClaseNivelRepository;
import com.academiabaile.backend.repository.InscripcionRepository;

    @Service
public class InscripcionServiceImpl implements InscripcionService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ClaseNivelRepository claseNivelRepository;

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Override
public Integer registrar(InscripcionDTO dto) {
    // Crear cliente
    Cliente cliente = new Cliente();
    cliente.setNombres(dto.getNombres());
    cliente.setApellidos(dto.getApellidos());
    cliente.setCorreo(dto.getCorreo());
    cliente.setDireccion(dto.getDireccion());
    cliente.setDni(dto.getDni());
    cliente = clienteRepository.save(cliente); 
    // Buscar ClaseNivel
    List<ClaseNivel> resultados = claseNivelRepository.findByClaseId(dto.getClaseNivelId());
    if (resultados.isEmpty()) {
        throw new RuntimeException("No se encontró ClaseNivel con ID " + dto.getClaseNivelId());
    }
    ClaseNivel claseNivel = resultados.get(0);

    // Crear inscripción
    Inscripcion inscripcion = new Inscripcion();
    inscripcion.setCliente(cliente);
    inscripcion.setClaseNivel(claseNivel);
    inscripcion.setEstado("pendiente");
    
    inscripcion = inscripcionRepository.save(inscripcion);
    return inscripcion.getId();

}}
