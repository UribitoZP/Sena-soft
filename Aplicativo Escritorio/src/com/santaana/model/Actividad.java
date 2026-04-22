package com.santaana.model;

public class Actividad {
    private int id;
    private String tipo;
    private String titulo;
    private String descripcion;
    private String fechaHora;

    public Actividad(int id, String tipo, String titulo, String descripcion, String fechaHora) {
        this.id = id;
        this.tipo = tipo;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaHora = fechaHora;
    }

    public int getId()           { return id; }
    public String getTipo()      { return tipo; }
    public String getTitulo()    { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getFechaHora() { return fechaHora; }
}
