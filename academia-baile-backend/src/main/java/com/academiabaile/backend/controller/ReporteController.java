package com.academiabaile.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ContentDisposition;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.academiabaile.backend.entidades.FiltroReporteDTO;
import com.academiabaile.backend.service.ReporteService;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @PostMapping("/auditoria")
    public ResponseEntity<byte[]> generarReporteAuditoria(@RequestBody FiltroReporteDTO filtro) {
        byte[] pdfBytes = reporteService.generarReporteAuditoria(filtro);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("reporte_auditoria.pdf").build());

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }
}
