package com.academiabaile.backend.entidades;

public class ClaseNivelDTO {
    private String nivel;
    private String dias;
    private String hora;
    private Double precio;
    private int aforo;
    private String estado;

    // Constructor con estado
    public ClaseNivelDTO(String nivel, String dias, String hora, Double precio, int aforo, String estado) {
        this.nivel = nivel;
        this.dias = dias;
        this.hora = hora;
        this.precio = precio;
        this.aforo = aforo;
        this.estado = estado;
    }

    // Constructor sin estado (opcional, si aún se necesita por compatibilidad)
    public ClaseNivelDTO(String nivel, String dias, String hora, Double precio, int aforo) {
        this(nivel, dias, hora, precio, aforo, null);
    }



    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getDias() {
        return dias;
    }

    public void setDias(String dias) {
        this.dias = dias;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public int getAforo() {
        return aforo;
    }

    public void setAforo(int aforo) {
        this.aforo = aforo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
