package com.academiabaile.backend.service;

import com.academiabaile.backend.entidades.InscripcionDTO;

public interface InscripcionService {
    Integer registrar(InscripcionDTO dto);
    void completarPagoDiferencia(Integer inscripcionId, String metodo, String comprobanteUrl);
}
