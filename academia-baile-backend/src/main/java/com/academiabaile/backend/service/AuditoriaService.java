package com.academiabaile.backend.service;

import com.academiabaile.backend.entidades.ModuloAcceso;

public interface AuditoriaService {
    public void registrar(String usuario, String tipoEvento, String descripcion,ModuloAcceso modulo);
}
