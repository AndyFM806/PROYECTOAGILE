package com.example.backend.entidades;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "clases")
public class Clase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_clase;

    private String nombre;

    private String descripcion;

    private String horario;

    private Double precio;


    public Integer getId_clase() {
        return id_clase;
    }

    public void setId_clase(Integer id_clase) {
        this.id_clase = id_clase;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getHorario() {
        return horario;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


}
