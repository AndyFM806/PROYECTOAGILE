package com.example.backend.controller;

import com.example.backend.entidades.Cliente;
import com.example.backend.entidades.Clase;
import com.example.backend.repository.ClaseRepository;
import com.example.backend.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClaseRepository claseRepository;

    @PostMapping
    public Cliente registrarCliente(@RequestBody Cliente cliente) {
        Integer claseId = cliente.getClase().getId();

        Clase clase = claseRepository.findById(claseId)
            .orElseThrow(() -> new RuntimeException("Clase no encontrada con ID: " + claseId));

        cliente.setClase(clase);

        return clienteService.guardarCliente(cliente);
    }

    @GetMapping
    public List<Cliente> listarClientes() {
        return clienteService.listarClientes();
    }
}
