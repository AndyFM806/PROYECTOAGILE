package com.academiabaile.backend.service;

import com.academiabaile.backend.entidades.InscripcionDTO;
import com.academiabaile.backend.entidades.MovimientoClienteDTO;

public interface InscripcionService {
    Integer registrar(InscripcionDTO dto);
    void completarPagoDiferencia(Integer inscripcionId, String metodo, String comprobanteUrl);
    Integer registrarManual(InscripcionDTO dto);
    void moverCliente(MovimientoClienteDTO dto);


}
