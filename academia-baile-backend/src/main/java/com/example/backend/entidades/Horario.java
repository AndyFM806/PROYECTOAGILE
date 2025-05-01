package com.example.backend.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Horario {

    @Id
    @Column(name = "id_horario") // Esto es lo que falta
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String dias;
    private String hora;
}


