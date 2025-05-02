package com.example.backend.entidades;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "inscripcion")
public class Inscripcion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String comprobanteUrl;

    private String estado = "pendiente";

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "clase_nivel_id")
    private ClaseNivel claseNivel;

    public String getComprobanteUrl() {
        return comprobanteUrl;
    }

    public void setComprobanteUrl(String comprobanteUrl) {
        this.comprobanteUrl = comprobanteUrl;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public ClaseNivel getClaseNivel() {
        return claseNivel;
    }

    public void setClaseNivel(ClaseNivel claseNivel) {
        this.claseNivel = claseNivel;
    }
    public Integer getId() {
        return id;
    }
    @Column(name = "fecha_inscripcion", updatable = false)
    private LocalDateTime fechaInscripcion;

    @PrePersist
    protected void onCreate() {
    this.fechaInscripcion = LocalDateTime.now();
}
}
