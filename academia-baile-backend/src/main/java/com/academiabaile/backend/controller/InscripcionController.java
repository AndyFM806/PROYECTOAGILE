package com.academiabaile.backend.controller;

import com.academiabaile.backend.entidades.*;
import com.academiabaile.backend.repository.ClaseNivelRepository;
import com.academiabaile.backend.repository.ClienteRepository;
import com.academiabaile.backend.repository.InscripcionRepository;
import com.academiabaile.backend.service.AlmacenamientoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private ClaseNivelRepository claseNivelRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private AlmacenamientoService almacenamientoService;

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody InscripcionDTO dto) {
        // Obtener claseNivel
        ClaseNivel claseNivel = claseNivelRepository.findById(dto.getClaseNivelId())
            .orElseThrow(() -> new RuntimeException("ClaseNivel no encontrada con ID: " + dto.getClaseNivelId()));

        // Crear cliente
        Cliente cliente = new Cliente();
        cliente.setNombres(dto.getNombres());
        cliente.setApellidos(dto.getApellidos());
        cliente.setCorreo(dto.getCorreo());
        cliente.setDireccion(dto.getDireccion());
        cliente.setDni(dto.getDni());
        cliente.setClaseNivel(claseNivel);
        cliente = clienteRepository.save(cliente);

        // Crear inscripción
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setCliente(cliente);
        inscripcion.setClaseNivel(claseNivel);
        inscripcion.setEstado(dto.getEstado() != null ? dto.getEstado() : "pendiente");

        inscripcion = inscripcionRepository.save(inscripcion);

        return ResponseEntity.ok(inscripcion.getId());
    }

    @PostMapping("/comprobante/{id}")
    public ResponseEntity<?> subirComprobante(@PathVariable Integer id, @RequestParam("file") MultipartFile file) {
        String url = almacenamientoService.guardar(file);

        Inscripcion insc = inscripcionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Inscripción no encontrAda"));

        insc.setComprobanteUrl(url);
        inscripcionRepository.save(insc);

        return ResponseEntity.ok("Comprobante subido");
    }
}