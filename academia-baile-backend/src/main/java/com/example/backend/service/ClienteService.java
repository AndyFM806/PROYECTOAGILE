package com.example.backend.service;

import com.example.backend.entidades.Cliente;
import java.util.List;

public interface ClienteService {
    Cliente guardarCliente(Cliente cliente);
    List<Cliente> listarClientes();
}
