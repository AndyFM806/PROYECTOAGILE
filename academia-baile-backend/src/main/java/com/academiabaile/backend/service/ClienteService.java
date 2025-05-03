package com.academiabaile.backend.service;

import com.academiabaile.backend.entidades.Cliente;
import java.util.List;

public interface ClienteService {
    Cliente guardarCliente(Cliente cliente);
    List<Cliente> listarClientes();
}
