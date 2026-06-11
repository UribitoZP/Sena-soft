package com.santaana.model;

public class ReservaProducto {

    private int idReserva;
    private int idProducto;
    private int cantidad;
    private double precio;

    public ReservaProducto(int idReserva, int idProducto, int cantidad, double precio) {
        this.idReserva = idReserva;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}