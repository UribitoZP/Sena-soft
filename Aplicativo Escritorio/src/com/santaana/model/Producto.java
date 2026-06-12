package com.santaana.model;

public class Producto {
    private int id;
    private String nombre;
    private int stock;
    private double precioCompra;
    private double precioVenta;

    public Producto(int id, String nombre, int stock, double precioCompra, double precioVenta) {
        this.id = id;
        this.nombre = nombre;
        this.stock = stock;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }
    @Override
    public String toString() {
        return nombre + " - $" + precioVenta;
    }
}
