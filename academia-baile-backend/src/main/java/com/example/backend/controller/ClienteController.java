package com.example.backend.controller;

import com.example.backend.entidades.Cliente;
import com.example.backend.entidades.ClaseNivel;
import com.example.backend.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.backend.repository.ClaseNivelRepository;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClaseNivelRepository claseNivelRepository;
    @PostMapping
public Cliente registrarCliente(@RequestBody Cliente cliente) {
    Integer claseNivelId = cliente.getClaseNivel().getId();

    ClaseNivel claseNivel = claseNivelRepository.findById(claseNivelId)
    .orElseThrow(() -> new RuntimeException("ClaseNivel no encontrada con ID: " + claseNivelId));

    cliente.setClaseNivel(claseNivel); // ✅ establecer la relación correctamente

    return clienteService.guardarCliente(cliente);
}
    @GetMapping
    public List<Cliente> listarClientes() {
        return clienteService.listarClientes();
    }
}
