package com.academiabaile.backend.service;

import com.academiabaile.backend.entidades.ModuloAcceso;
import com.academiabaile.backend.entidades.Usuario;

public interface AuditoriaService {
    public void registrar(Usuario usuario, String tipoEvento, String descripcion,ModuloAcceso modulo);
}
