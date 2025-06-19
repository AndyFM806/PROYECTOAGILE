package com.academiabaile.backend.service;

import com.academiabaile.backend.config.UsuarioUtil;
import com.academiabaile.backend.entidades.Clase;
import com.academiabaile.backend.entidades.ClaseNivel;
import com.academiabaile.backend.entidades.ClaseNivelDTO;
import com.academiabaile.backend.entidades.CrearClaseNivelDTO;
import com.academiabaile.backend.entidades.Horario;
import com.academiabaile.backend.entidades.Inscripcion;
import com.academiabaile.backend.entidades.ModuloAcceso;
import com.academiabaile.backend.entidades.Nivel;
import com.academiabaile.backend.entidades.NotaCredito;
import com.academiabaile.backend.repository.ClaseNivelRepository;
import com.academiabaile.backend.repository.InscripcionRepository;
import com.academiabaile.backend.repository.ModuloAccesoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import com.academiabaile.backend.repository.ClaseRepository;
import com.academiabaile.backend.repository.NivelRepository;

import com.academiabaile.backend.repository.HorarioRepository;

@Service
public class ClaseNivelServiceImpl implements ClaseNivelService {

    @Autowired
    private ModuloAccesoRepository moduloAccesoRepository;

    @Autowired
    private ClaseRepository claseRepository;

    @Autowired
    private NivelRepository nivelRepository;

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private AuditoriaService auditoriaService;

    @Autowired
    private ClaseNivelRepository claseNivelRepository;


    @Override
    public List<ClaseNivelDTO> obtenerNivelesPorClase(Integer claseId) {
        
    List<ClaseNivel> lista = claseNivelRepository.findByClaseId(claseId);
    for (ClaseNivel cn : lista) {
        System.out.println("Nivel: " + (cn.getNivel() != null ? cn.getNivel().getNombre() : "null"));
        System.out.println("Horario: " + (cn.getHorario() != null ? cn.getHorario().getDias() : "null"));
    }
    
    return lista.stream().map(cn -> new ClaseNivelDTO(
    cn.getNivel().getNombre(),
    cn.getHorario().getDias(),
    cn.getHorario().getHora(),
    cn.getPrecio(),
    cn.getAforo() // 👈 NUEVO CAMPO
)).collect(Collectors.toList());

    
    }
     @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private NotaCreditoService notaCreditoService;

    @Autowired
    private EmailService emailService;
    

    @Override
public void cerrarClaseSiNoLlegaAlMinimo(ClaseNivel claseNivel) {
    List<Inscripcion> inscritos = inscripcionRepository.findByClaseNivelIn(List.of(claseNivel));

    if (inscritos.size() < 10 && "abierta".equalsIgnoreCase(claseNivel.getEstado())) {
        claseNivel.setEstado("cancelada");
        claseNivel.setMotivoCancelacion("Clase cancelada por no alcanzar el mínimo de 10 inscritos.");
        claseNivelRepository.save(claseNivel);

        for (Inscripcion insc : inscritos) {
            NotaCredito nota = notaCreditoService.generarNotaCredito(
                insc.getCliente(),
                claseNivel.getPrecio(),
                claseNivel
            );

            emailService.enviarCorreo(
                insc.getCliente().getCorreo(),
                "Clase cancelada: se ha emitido una nota de crédito",
                "Estimado/a " + insc.getCliente().getNombres() +
                ", la clase \"" + claseNivel.getClase().getNombre() + "\" ha sido cancelada por no alcanzar el mínimo de inscritos." +
                "\n\nSe le ha emitido una nota de crédito válida por 6 meses." +
                "\nCódigo: " + nota.getCodigo() +
                "\nMonto: S/ " + nota.getValor()
            );
        }
    }
}
@Override
public ClaseNivel crearClaseNivel(CrearClaseNivelDTO dto) {
    Clase clase = claseRepository.findById(dto.getClaseId())
        .orElseThrow(() -> new RuntimeException("Clase no encontrada"));
    Nivel nivel = nivelRepository.findById(dto.getNivelId())
        .orElseThrow(() -> new RuntimeException("Nivel no encontrado"));
    Horario horario = horarioRepository.findById(dto.getHorarioId())
        .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

    ClaseNivel claseNivel = new ClaseNivel();
    claseNivel.setClase(clase);
    claseNivel.setNivel(nivel);
    claseNivel.setHorario(horario);
    claseNivel.setPrecio(dto.getPrecio());
    claseNivel.setAforo(dto.getAforo());
    claseNivel.setEstado("abierta");
    if (dto.getFechaCierre() != null) {
    claseNivel.setFechaCierre(dto.getFechaCierre());
}

    claseNivelRepository.save(claseNivel);

    ModuloAcceso modulo = moduloAccesoRepository.findByNombre("CLASES");
    auditoriaService.registrar(
        UsuarioUtil.obtenerUsuarioActual(),
        "CLASE_NIVEL_CREADA",
        "Clase nivel creado: " + clase.getNombre() + " - Nivel: " + nivel.getNombre() +
        ", Aforo: " + dto.getAforo() + ", Precio: S/ " + dto.getPrecio(),
        modulo
    );


    return claseNivel;
}

    @Override
public void cerrarClaseNivel(Integer id) {
    ClaseNivel claseNivel = claseNivelRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("ClaseNivel no encontrada"));

    if ("cerrada".equalsIgnoreCase(claseNivel.getEstado())) {
        throw new RuntimeException("Clase ya cerrada");
    }

    claseNivel.setEstado("cerrada");
    claseNivelRepository.save(claseNivel);

    List<Inscripcion> inscripciones = inscripcionRepository
        .findByClaseNivelAndEstado(claseNivel, "aprobada");

    for (Inscripcion insc : inscripciones) {
        NotaCredito nota = notaCreditoService.generarNotaCredito(
        insc.getCliente(),
        claseNivel.getPrecio(),
        claseNivel
    );

        emailService.enviarCorreo(
            insc.getCliente().getCorreo(),
            "Clase cancelada",
            "La clase fue cancelada. Puedes usar el código " + nota.getCodigo() +
            " por S/ " + nota.getValor() + " hasta el " + nota.getFechaExpiracion() + ".");
        ModuloAcceso modulo = moduloAccesoRepository.findByNombre("CLASES");
        auditoriaService.registrar(
            UsuarioUtil.obtenerUsuarioActual(),
            "CLASE_CANCELADA_CLIENTE",
            "Se notificó a cliente " + insc.getCliente().getNombres() +
            " por cierre de clase. Nota código: " + nota.getCodigo() + ", Monto: S/ " + nota.getValor(),
            modulo
);

    }

    ModuloAcceso modulo = moduloAccesoRepository.findByNombre("CLASES");
    auditoriaService.registrar(
    UsuarioUtil.obtenerUsuarioActual(),
    "CLASE_NIVEL_CERRADA",
    "Clase nivel ID " + claseNivel.getId() + " cerrada manualmente.",
    modulo
);

}

@Override
public void reabrirClaseNivel(Integer id) {
    ClaseNivel claseNivel = claseNivelRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("ClaseNivel no encontrada"));

    if (!"cerrada".equalsIgnoreCase(claseNivel.getEstado())) {
        throw new RuntimeException("Solo se pueden reabrir clases que estén en estado 'cerrada'");
    }

    claseNivel.setEstado("abierta");
    claseNivel.setMotivoCancelacion(null);
    claseNivel.setFechaCierre(null); // opcional: si quieres limpiar la fecha
    claseNivelRepository.save(claseNivel);

    ModuloAcceso modulo = moduloAccesoRepository.findByNombre("CLASES");
    auditoriaService.registrar(
    UsuarioUtil.obtenerUsuarioActual(),
    "CLASE_REABIERTA",
    "Clase nivel ID " + claseNivel.getId() + " reabierta.",
    modulo
);

}
@Override
public List<ClaseNivel> findByClaseIdAndEstado(Integer claseId, String estado) {
    return claseNivelRepository.findByClaseIdAndEstado(claseId, estado);
}

    
}

