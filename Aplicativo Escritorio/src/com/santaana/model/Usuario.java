package com.santaana.model;

public class Usuario {
    private int id;
    private String nombre;
    private String usuario;
    private String clave;
    private String rol;

    public Usuario(int id, String nombre, String usuario, String clave, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.usuario = usuario;
        this.clave = clave;
        this.rol = rol;
    }

    public int getId()         { return id; }
    public String getNombre()  { return nombre; }
    public String getUsuario() { return usuario; }
    public String getClave()   { return clave; }
    public String getRol()     { return rol; }
}
