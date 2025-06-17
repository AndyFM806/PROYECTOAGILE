package com.academiabaile.backend.entidades;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ClaseNivel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clase_id")
    private Clase clase;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nivel_id")
    private Nivel nivel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "horario_id")
    private Horario horario;

    @Column(nullable = false)
    private int aforo;

    private Double precio;

    @Column(nullable = false)
    private String estado;

    @Column(name = "motivo_cancelacion")
    private String motivoCancelacion;
    @Column(name = "fecha_cierre")
    private LocalDate fechaCierre;
    
    }





