package com.academiabaile.backend.service;

import com.academiabaile.backend.entidades.ClaseNivel;
import com.academiabaile.backend.entidades.ClaseNivelDTO;
import com.academiabaile.backend.entidades.CrearClaseNivelDTO;

import java.util.List;

public interface ClaseNivelService {
        List<ClaseNivelDTO> obtenerNivelesPorClase(Integer claseId);
        void cerrarClaseSiNoLlegaAlMinimo(ClaseNivel claseNivel);
        void cerrarClaseNivel(Integer id);
        ClaseNivel crearClaseNivel(CrearClaseNivelDTO dto);

}
