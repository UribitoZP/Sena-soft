package com.santaana.model;

public class Habitacion {
    private int id;
    private String numero;
    private String tipo;
    private double precio;
    private double precioBloque;
    private String estado;

    public Habitacion(int id, String numero, String tipo, double precio, String estado) {
        this(id, numero, tipo, precio, 0, estado);
    }

    public Habitacion(int id, String numero, String tipo, double precio, double precioBloque, String estado) {
        this.id = id;
        this.numero = numero;
        this.tipo = tipo;
        this.precio = precio;
        this.precioBloque = precioBloque;
        this.estado = estado;
    }

    public int getId()            { return id; }
    public String getNumero()     { return numero; }
    public String getTipo()       { return tipo; }
    public double getPrecio()     { return precio; }
    public double getPrecioBloque() { return precioBloque; }
    public String getEstado()     { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setPrecioBloque(double precioBloque) { this.precioBloque = precioBloque; }
}
