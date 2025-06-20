package com.academiabaile.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.academiabaile.backend.entidades.AuditoriaEvento;
import com.academiabaile.backend.entidades.FiltroReporteDTO;
import com.academiabaile.backend.repository.AuditoriaEventoRepository;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

@Service
public class ReporteServiceImpl implements ReporteService {

    @Autowired
    private AuditoriaEventoRepository auditoriaRepo;

    @Override
public byte[] generarReporteAuditoria(FiltroReporteDTO filtros) {
    LocalDateTime inicio = filtros.getFechaInicio().atStartOfDay();
    LocalDateTime fin = filtros.getFechaFin().atTime(23, 59, 59);

    List<AuditoriaEvento> eventos = auditoriaRepo.findByFechaBetween(inicio, fin);
    if (filtros.getTipoEvento() != null) {
        eventos = eventos.stream()
                .filter(e -> e.getTipoEvento().equalsIgnoreCase(filtros.getTipoEvento()))
                .toList();
    }
    if (filtros.getModulo() != null) {
        eventos = eventos.stream()
                .filter(e -> e.getModulo().equalsIgnoreCase(filtros.getModulo()))
                .toList();
    }

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PdfWriter writer = new PdfWriter(outputStream);
    PdfDocument pdf = new PdfDocument(writer);
    Document document = new Document(pdf);

    document.add(new Paragraph("REPORTE DE AUDITORÍA").setBold().setFontSize(16));
    document.add(new Paragraph("Rango: " + filtros.getFechaInicio() + " a " + filtros.getFechaFin()));

    Table table = new Table(4);
    table.addHeaderCell("Fecha");
    table.addHeaderCell("Usuario");
    table.addHeaderCell("Módulo");
    table.addHeaderCell("Descripción");

    for (AuditoriaEvento e : eventos) {
        table.addCell(e.getFecha().toString());
        table.addCell(e.getUsuario().getNombreUsuario());

        table.addCell(e.getModulo());
        table.addCell(e.getDescripcion());
    }

    document.add(table);
    document.close();

    return outputStream.toByteArray();
}

}
