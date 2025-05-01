package com.example.backend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AlmacenamientoServiceImpl implements AlmacenamientoService {

    private final String RUTA_BASE = "uploads/";

    @Override
    public String guardar(MultipartFile file) {
        try {
            String nombreArchivo = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path destino = Paths.get(RUTA_BASE + nombreArchivo);
            Files.createDirectories(destino.getParent());
            Files.write(destino, file.getBytes());
            return nombreArchivo;
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar archivo", e);
        }
    }
}
