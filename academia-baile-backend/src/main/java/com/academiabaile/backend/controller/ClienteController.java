package com.academiabaile.backend.controller;

import com.academiabaile.backend.entidades.Cliente;
import com.academiabaile.backend.entidades.ClaseNivel;
import com.academiabaile.backend.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.academiabaile.backend.repository.ClaseNivelRepository;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = {"https://timbatumbao-front.onrender.com", "http://localhost:5500"})
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
    @PutMapping("/{id}/anotacion")
    public ResponseEntity<Cliente> actualizarAnotacion(@PathVariable Integer id, @RequestBody String nuevaAnotacion) {
    Cliente cliente = clienteService.findById(id);
    if (cliente == null) {
        return ResponseEntity.notFound().build();
    }
    cliente.setAnotacion(nuevaAnotacion);
    return ResponseEntity.ok(clienteService.guardarCliente(cliente));
}
    @GetMapping("/{id}/anotacion")
public ResponseEntity<String> obtenerAnotacion(@PathVariable Integer id) {
    Cliente cliente = clienteService.findById(id);
    if (cliente == null) {
        return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(cliente.getAnotacion());
}
    @PostMapping("/api/clientes")
    public Cliente registrarClienteManual(@RequestBody Cliente cliente) {
    Integer claseNivelId = cliente.getClaseNivel().getId();
    ClaseNivel claseNivel = claseNivelRepository.findById(claseNivelId)
        .orElseThrow(() -> new RuntimeException("ClaseNivel no encontrada"));

    cliente.setClaseNivel(claseNivel);
    return clienteService.guardarCliente(cliente);
}
    @Autowired
    private com.academiabaile.backend.repository.ClienteRepository clienteRepository;
    
    @DeleteMapping("/api/clientes/{id}")
    public ResponseEntity<?> eliminarCliente(@PathVariable Integer id) {
    clienteRepository.deleteById(id);
    return ResponseEntity.ok().build();
}


}
