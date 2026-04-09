package com.santaana.model;

public class Reserva {
    private int id;
    private int idHabitacion;
    private int idUsuario;
    private String clienteNombre;
    private String clienteDoc;
    private String fechaEntrada;
    private String fechaSalida;
    private String estado;

    public Reserva(int id, int idHabitacion, int idUsuario,
                   String clienteNombre, String clienteDoc,
                   String fechaEntrada, String fechaSalida, String estado) {
        this.id = id;
        this.idHabitacion = idHabitacion;
        this.idUsuario = idUsuario;
        this.clienteNombre = clienteNombre;
        this.clienteDoc = clienteDoc;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.estado = estado;
    }

    public int getId()               { return id; }
    public int getIdHabitacion()     { return idHabitacion; }
    public int getIdUsuario()        { return idUsuario; }
    public String getClienteNombre() { return clienteNombre; }
    public String getClienteDoc()    { return clienteDoc; }
    public String getFechaEntrada()  { return fechaEntrada; }
    public String getFechaSalida()   { return fechaSalida; }
    public String getEstado()        { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
