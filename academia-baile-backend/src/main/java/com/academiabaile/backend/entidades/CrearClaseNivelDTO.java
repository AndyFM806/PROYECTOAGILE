package com.academiabaile.backend.entidades;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearClaseNivelDTO {

    private Integer claseId;
    private Integer nivelId;
    private List<Integer> horariosIds; // Antes era horarioId
    private Double precio;
    private Integer aforo;
    private LocalDate fechaCierre;
    private String estado;
    private Integer aulaId;


    public LocalDate getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDate fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

}
