package com.academiabaile.backend.service;

import com.academiabaile.backend.config.UsuarioUtil;
import com.academiabaile.backend.entidades.*;
import com.academiabaile.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AlumnoServiceImpl implements AlumnoService {
    @Autowired
    private ModuloAccesoRepository moduloAccesoRepository;


    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ClaseNivelRepository claseNivelRepository;

    @Autowired
    private AuditoriaService auditoriaService;

    @Autowired
    private NotaCreditoRepository notaCreditoRepository;

    @Override
    public List<ClaseNivel> listarClasesDeAlumno(Integer clienteId) {
        return inscripcionRepository.findByClienteId(clienteId).stream()
                .map(Inscripcion::getClaseNivel)
                .distinct()
                .toList();
    }

    @Override
    public void inscribirAlumnoEnClase(Integer clienteId, Integer claseNivelId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        ClaseNivel claseNivel = claseNivelRepository.findById(claseNivelId)
                .orElseThrow(() -> new RuntimeException("ClaseNivel no encontrada"));

        boolean yaInscrito = inscripcionRepository.existsByClienteIdAndClaseNivelId(clienteId, claseNivelId);
        if (yaInscrito) throw new RuntimeException("Este alumno ya está inscrito en esta clase");

        Inscripcion insc = new Inscripcion();
        insc.setCliente(cliente);
        insc.setClaseNivel(claseNivel);
        insc.setEstado("aprobada");
        insc.setFechaInscripcion(LocalDateTime.now());

        inscripcionRepository.save(insc);
        ModuloAcceso modulo = moduloAccesoRepository.findByNombre("ALUMNOS");
    auditoriaService.registrar(
    "INSCRIPCION_MANUAL",
    "Cliente " + cliente.getNombres() + " inscrito manualmente en clase nivel " +
    claseNivel.getClase().getNombre() + " - Nivel: " + claseNivel.getNivel().getNombre() +
    ". Precio: S/ " + claseNivel.getPrecio(),
    modulo
);


    }

    @Override
    public void eliminarAlumnoDeClase(Integer clienteId, Integer claseNivelId) {
        Inscripcion insc = inscripcionRepository.findByClienteIdAndClaseNivelId(clienteId, claseNivelId)
        .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));
        ClaseNivel claseNivel = claseNivelRepository.findById(claseNivelId)
            .orElseThrow(() -> new RuntimeException("ClaseNivel no encontrada"));

        inscripcionRepository.delete(insc);

        ModuloAcceso modulo = moduloAccesoRepository.findByNombre("ALUMNOS");
        auditoriaService.registrar(
            "ELIMINAR_ALUMNO_AULA",
            "Alumno " + insc.getCliente().getNombres() + " eliminado de clase nivel " +
            claseNivel.getClase().getNombre() + " - Nivel: " + claseNivel.getNivel().getNombre(),
            modulo
        );

    }

    @Override
    public void moverAlumno(MovimientoAlumnoDTO dto) {
        moverAlumnoDeClase(dto.getClienteId(), dto.getOrigenClaseNivelId(), dto.getDestinoClaseNivelId());
        ModuloAcceso modulo = moduloAccesoRepository.findByNombre("ALUMNOS");
        auditoriaService.registrar(
            "CAMBIO_CLASE_ALUMNO",
            "Alumno con ID " + dto.getClienteId() + " movido de clase nivel ID " +
            dto.getOrigenClaseNivelId() + " a clase nivel ID " + dto.getDestinoClaseNivelId(),
            modulo
        );

    }

    @Override
    public NotaCredito generarManualNotaCredito(Integer clienteId, Double valor) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        NotaCredito nota = new NotaCredito();
        nota.setCliente(cliente);
        nota.setValor(valor);
        nota.setCodigo(UUID.randomUUID().toString().substring(0, 10));
        nota.setFechaEmision(LocalDate.now());
        nota.setFechaExpiracion(LocalDate.now().plusDays(1));
        nota.setUsada(false);

        ModuloAcceso modulo = moduloAccesoRepository.findByNombre("ALUMNOS");
        auditoriaService.registrar(
            "NOTA_CREDITO_MANUAL",
            "Nota de crédito generada manualmente para " + cliente.getNombres() +
            " por S/ " + valor + ". Validez: 1 día.",
            modulo
        );
        return notaCreditoRepository.save(nota);
    }

    @Override
    public ClienteDTO obtenerDatosDelAlumno(Integer clienteId) {
        Cliente c = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        ClienteDTO dto = new ClienteDTO();
        dto.setId(c.getId());
        dto.setNombres(c.getNombres());
        dto.setApellidos(c.getApellidos());
        dto.setDni(c.getDni());
        dto.setCorreo(c.getCorreo());
        dto.setDireccion(c.getDireccion());
        return dto;
    }

    @Override
    public void actualizarDatosCliente(ClienteDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        cliente.setNombres(dto.getNombres());
        cliente.setApellidos(dto.getApellidos());
        cliente.setCorreo(dto.getCorreo());
        cliente.setDireccion(dto.getDireccion());
        cliente.setDni(dto.getDni());

        clienteRepository.save(cliente);

        ModuloAcceso modulo = moduloAccesoRepository.findByNombre("ALUMNOS");
        auditoriaService.registrar(
        "ACTUALIZAR_DATOS_CLIENTE",
        "Datos del cliente " + cliente.getNombres() + " (DNI: " + cliente.getDni() + ") actualizados.",
        modulo
    );
    }

    @Override
public void moverAlumnoDeClase(Integer clienteId, Integer origenClaseNivelId, Integer destinoClaseNivelId) {
    if (origenClaseNivelId.equals(destinoClaseNivelId)) {
        throw new RuntimeException("Clase de origen y destino no pueden ser iguales");
    }

    Inscripcion inscOrigen = inscripcionRepository
        .findByClienteIdAndClaseNivelId(clienteId, origenClaseNivelId)
        .orElseThrow(() -> new RuntimeException("No se encontró inscripción en la clase origen"));

    inscripcionRepository.delete(inscOrigen); // ✅ No debe marcar error aquí

    Cliente cliente = inscOrigen.getCliente();
    ClaseNivel claseDestino = claseNivelRepository.findById(destinoClaseNivelId)
        .orElseThrow(() -> new RuntimeException("Clase destino no encontrada"));

    Inscripcion nueva = new Inscripcion();
    nueva.setCliente(cliente);
    nueva.setClaseNivel(claseDestino);
    nueva.setEstado("aprobada");
    nueva.setFechaInscripcion(LocalDateTime.now());

    inscripcionRepository.save(nueva);
}
        @Override
        public List<ClaseNivel> listarClasesNoInscritas(Integer clienteId) {
        List<ClaseNivel> inscritas = listarClasesDeAlumno(clienteId);
        List<ClaseNivel> todas = claseNivelRepository.findAll();
        return todas.stream()
                .filter(cn -> !inscritas.contains(cn))
                .collect(Collectors.toList());
}
    @Override
    public List<ClienteDTO> listarAlumnosPorClaseNivel(Integer claseNivelId) {
        List<Inscripcion> inscripciones = inscripcionRepository.findByClaseNivelId(claseNivelId);

        return inscripciones.stream()
                .map(insc -> {
                    Cliente c = insc.getCliente();
                    ClienteDTO dto = new ClienteDTO();
                    dto.setId(c.getId());
                    dto.setNombres(c.getNombres());
                    dto.setApellidos(c.getApellidos());
                    dto.setDni(c.getDni());
                    return dto;
                })
                .collect(Collectors.toList());
    }

}
