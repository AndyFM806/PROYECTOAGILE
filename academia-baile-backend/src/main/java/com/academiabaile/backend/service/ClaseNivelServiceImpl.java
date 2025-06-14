package com.academiabaile.backend.service;

import com.academiabaile.backend.entidades.ClaseNivel;
import com.academiabaile.backend.entidades.ClaseNivelDTO;
import com.academiabaile.backend.entidades.Inscripcion;
import com.academiabaile.backend.entidades.NotaCredito;
import com.academiabaile.backend.repository.ClaseNivelRepository;
import com.academiabaile.backend.repository.InscripcionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClaseNivelServiceImpl implements ClaseNivelService {

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

}

