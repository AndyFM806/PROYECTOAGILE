package com.academiabaile.backend.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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
    ClaseNivel claseNivel = claseNivelRepository.findById(dto.getClaseNivelId())
        .orElseThrow(() -> new RuntimeException("ClaseNivel no encontrada"));

    // Validar aforo
    int inscritos = inscripcionRepository.countByClaseNivelAndEstado(claseNivel, "aprobada");
    if (inscritos >= claseNivel.getAforo()) {
        throw new RuntimeException("Clase llena");
    }

    // Validar duplicado
    boolean yaInscrito = clienteRepository.existsByDniAndClaseNivel_Id(dto.getDni(), dto.getClaseNivelId());
    if (yaInscrito) {
        throw new RuntimeException("Ya inscrito en esta clase");
    }

    // Crear cliente
    Cliente cliente = new Cliente();
    cliente.setNombres(dto.getNombres());
    cliente.setApellidos(dto.getApellidos());
    cliente.setCorreo(dto.getCorreo());
    cliente.setDireccion(dto.getDireccion());
    cliente.setDni(dto.getDni());
    cliente = clienteRepository.save(cliente);

    // Crear inscripción
    Inscripcion inscripcion = new Inscripcion();
    inscripcion.setCliente(cliente);
    inscripcion.setClaseNivel(claseNivel);
    inscripcion.setEstado("pendiente");

    if ("pasarela".equalsIgnoreCase(dto.getMetodoPago())) {
        // Simulación de validación exitosa de pasarela (puedes reemplazar por lógica real)
        inscripcion.setEstado("aprobada");
    }

    inscripcion = inscripcionRepository.save(inscripcion);
    return inscripcion.getId();
}


}
