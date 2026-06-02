package com.santaana.model;

public class Usuario {
    private int id;
    private String nombre;
    private String usuario;
    private String clave;
    private String rol;
    private String telefono;
    private String correo;

    public Usuario(int id, String nombre, String usuario, String clave, String rol, String telefono, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.usuario = usuario;
        this.clave = clave;
        this.rol = rol;
        this.telefono = telefono;
        this.correo = correo;
    }

    public int getId()            { return id; }
    public String getNombre()     { return nombre; }
    public String getUsuario()    { return usuario; }
    public String getClave()      { return clave; }
    public String getRol()        { return rol; }
    public String getTelefono()   { return telefono; }
    public String getCorreo()     { return correo; }

    public void setId(int id)                   { this.id = id; }
    public void setNombre(String nombre)        { this.nombre = nombre; }
    public void setUsuario(String usuario)      { this.usuario = usuario; }
    public void setClave(String clave)          { this.clave = clave; }
    public void setRol(String rol)              { this.rol = rol; }
    public void setTelefono(String telefono)    { this.telefono = telefono; }
    public void setCorreo(String correo)        { this.correo = correo; }
}
