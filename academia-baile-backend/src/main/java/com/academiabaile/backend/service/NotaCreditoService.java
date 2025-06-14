package com.academiabaile.backend.service;

import com.academiabaile.backend.entidades.ClaseNivel;
import com.academiabaile.backend.entidades.Cliente;
import com.academiabaile.backend.entidades.NotaCredito;

public interface NotaCreditoService {
    NotaCredito generarNotaCredito(Cliente cliente, Double valor, ClaseNivel claseCancelada);
    void marcarComoUsada(NotaCredito notaCredito);
    public NotaCredito validarNota(String codigo);
}
