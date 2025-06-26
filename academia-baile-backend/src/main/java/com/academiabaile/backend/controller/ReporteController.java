package com.academiabaile.backend.controller;

import com.academiabaile.backend.entidades.ClaseNivel;
import com.academiabaile.backend.entidades.Inscripcion;
import com.academiabaile.backend.repository.ClaseNivelRepository;
import com.academiabaile.backend.repository.InscripcionRepository;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private ClaseNivelRepository claseNivelRepository;

    @GetMapping("/reporte-general")
    public ResponseEntity<byte[]> generarReportePdf(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4.rotate());

            // Encabezado
            document.add(new Paragraph("📊 Reporte General - Timba Tumbao")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Fecha de generación: " + LocalDate.now()));
            document.add(new Paragraph("Rango: " + (desde != null ? desde : "Inicio") + " al " + (hasta != null ? hasta : "Actual")));
            document.add(new Paragraph("\n"));

            // Convertir fechas a LocalDateTime
            LocalDateTime desdeFecha = (desde != null) ? desde.atStartOfDay() : null;
            LocalDateTime hastaFecha = (hasta != null) ? hasta.atTime(23, 59, 59) : null;

            // CLIENTES INSCRITOS
            List<Inscripcion> inscripciones = inscripcionRepository.findByFechaEntre(desdeFecha, hastaFecha);
            document.add(new Paragraph("👥 Clientes Inscritos").setFontSize(14).setBold());

            Table tablaInscritos = new Table(UnitValue.createPercentArray(6)).useAllAvailableWidth();
            String[] headers = {"Alumno", "DNI", "Clase", "Nivel", "Estado", "Fecha"};
            for (String h : headers) {
                tablaInscritos.addHeaderCell(new Cell().add(new Paragraph(h).setBold()));
            }

            for (Inscripcion i : inscripciones) {
                tablaInscritos.addCell(i.getCliente().getNombres() + " " + i.getCliente().getApellidos());
                tablaInscritos.addCell(i.getCliente().getDni());
                tablaInscritos.addCell(i.getClaseNivel().getClase().getNombre());
                tablaInscritos.addCell(i.getClaseNivel().getNivel().getNombre());
                tablaInscritos.addCell(i.getEstado());
                tablaInscritos.addCell(String.valueOf(i.getFechaInscripcion()));
            }
            document.add(tablaInscritos);
            document.add(new Paragraph("\n"));

            // VENTAS POR CLASE
            List<ClaseNivel> clases = claseNivelRepository.findByFechaEntre(desde, hasta);
            document.add(new Paragraph("💰 Ventas por ClaseNivel").setFontSize(14).setBold());

            Table tablaVentas = new Table(UnitValue.createPercentArray(5)).useAllAvailableWidth();
            String[] headersVentas = {"Clase", "Nivel", "Precio", "Inscritos", "Total"};
            for (String h : headersVentas) {
                tablaVentas.addHeaderCell(new Cell().add(new Paragraph(h).setBold()));
            }

            for (ClaseNivel cn : clases) {
                long inscritos = inscripcionRepository.countByClaseNivelId(cn.getId());
                double total = inscritos * cn.getPrecio();

                tablaVentas.addCell(cn.getClase().getNombre());
                tablaVentas.addCell(cn.getNivel().getNombre());
                tablaVentas.addCell("S/. " + cn.getPrecio());
                tablaVentas.addCell(String.valueOf(inscritos));
                tablaVentas.addCell("S/. " + total);
            }

            document.add(tablaVentas);
            document.close();

            HttpHeaders headersResp = new HttpHeaders();
            headersResp.setContentType(MediaType.APPLICATION_PDF);
            headersResp.setContentDisposition(ContentDisposition.inline().filename("reporte_general.pdf").build());

            return new ResponseEntity<>(baos.toByteArray(), headersResp, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
