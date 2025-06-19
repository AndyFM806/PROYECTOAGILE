package com.academiabaile.backend.service;

import com.academiabaile.backend.entidades.FiltroReporteDTO;

public interface ReporteService {
    byte[] generarReporteAuditoria(FiltroReporteDTO filtros);
}
