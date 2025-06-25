package com.academiabaile.backend.service;

import com.academiabaile.backend.entidades.Aula;
import com.academiabaile.backend.entidades.CeldaHorarioDTO;
import com.academiabaile.backend.entidades.Clase;
import com.academiabaile.backend.entidades.ClaseNivel;
import com.academiabaile.backend.entidades.ClaseNivelDTO;
import com.academiabaile.backend.entidades.CrearClaseNivelDTO;
import com.academiabaile.backend.entidades.Horario;
import com.academiabaile.backend.entidades.Inscripcion;
import com.academiabaile.backend.entidades.Nivel;
import com.academiabaile.backend.entidades.NotaCredito;
import com.academiabaile.backend.repository.ClaseNivelRepository;
import com.academiabaile.backend.repository.InscripcionRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.academiabaile.backend.repository.ClaseRepository;
import com.academiabaile.backend.repository.NivelRepository;

import com.academiabaile.backend.repository.HorarioRepository;

@Service
public class ClaseNivelServiceImpl implements ClaseNivelService {



    @Autowired
    private ClaseRepository claseRepository;

    @Autowired
    private NivelRepository nivelRepository;

    @Autowired
    private HorarioRepository horarioRepository;



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

    // 🔍 VALIDACIÓN 1: No más de 3 clases por día+hora
    int conteo = claseNivelRepository.countByHorarioId(horario.getId());
    if (conteo >= 3) {
        throw new RuntimeException("Ya existen 3 clases en este horario. No se puede asignar más.");
    }

    // 🔍 VALIDACIÓN 2: Aula no debe estar ocupada en ese horario
    boolean aulaOcupada = claseNivelRepository.existsByHorarioIdAndAulaId(
        horario.getId(), dto.getAulaId());
    if (aulaOcupada) {
        throw new RuntimeException("El aula ya está ocupada en ese horario.");
    }

    // 🔍 VALIDACIÓN 3: Fecha de cierre no vencida
    if (dto.getFechaCierre() != null && dto.getFechaCierre().isBefore(java.time.LocalDate.now())) {
        throw new RuntimeException("La fecha de cierre ya pasó. No se puede crear esta clase.");
    }

    // 🧱 Crear la clase normalmente
    ClaseNivel claseNivel = new ClaseNivel();
    claseNivel.setClase(clase);
    claseNivel.setNivel(nivel);
    claseNivel.setHorario(horario);
    claseNivel.setPrecio(dto.getPrecio());
    claseNivel.setAforo(dto.getAforo());
    claseNivel.setEstado("abierta");

    // ✅ Setear aula
    Aula aula = new Aula();
    aula.setId(dto.getAulaId());
    claseNivel.setAula(aula);

    // ✅ Setear fecha de cierre si aplica
    if (dto.getFechaCierre() != null) {
        claseNivel.setFechaCierre(dto.getFechaCierre());
    }

    claseNivelRepository.save(claseNivel);
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

    }
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

}
@Override
public List<ClaseNivel> findByClaseIdAndEstado(Integer claseId, String estado) {
    return claseNivelRepository.findByClaseIdAndEstado(claseId, estado);
}
@Autowired
private com.academiabaile.backend.repository.AulaRepository aulaRepository;

@Override
public List<CeldaHorarioDTO> obtenerMapaHorarioDisponible() {
    List<Aula> aulas = aulaRepository.findAll();
    List<Horario> horarios = horarioRepository.findAll();
    List<ClaseNivel> clases = claseNivelRepository.findAll();

    List<CeldaHorarioDTO> resultado = new ArrayList<>();

    for (Horario horario : horarios) {
        for (Aula aula : aulas) {
            Optional<ClaseNivel> ocupado = clases.stream()
                .filter(cn -> cn.getHorario().getId().equals(horario.getId())
                        && cn.getAula().getId().equals(aula.getId()))
                .findFirst();

            if (ocupado.isPresent()) {
                ClaseNivel cn = ocupado.get();
                resultado.add(new CeldaHorarioDTO(
                    horario.getDias(),
                    horario.getHora(),
                    aula.getCodigo(),
                    true,
                    cn.getClase().getNombre() + " - " + cn.getNivel().getNombre(),
                    cn.getEstado(),
                    horario.getId(), // nuevo
                    aula.getId()     // nuevo
                ));
            } else {
                resultado.add(new CeldaHorarioDTO(
                    horario.getDias(),
                    horario.getHora(),
                    aula.getCodigo(),
                    false,
                    null,
                    null,
                    horario.getId(), // nuevo
                    aula.getId()     // nuevo
                ));
            }
        }
    }

    return resultado;
}


}

