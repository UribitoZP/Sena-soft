package com.santaana.model;

public class Reserva {
    private int id;
    private int idHabitacion;
    private int idUsuario;
    private String clienteNombre;
    private String clienteDoc;
    private String fechaEntrada;
    private String horaEntrada;
    private String fechaSalida;
    private String horaSalida;
    private String tipoEstadia;
    private String estado;
    private double anticipo;
    private double totalPagar;

    public Reserva(int id, int idHabitacion, int idUsuario,
                   String clienteNombre, String clienteDoc,
                   String fechaEntrada, String horaEntrada,
                   String fechaSalida, String horaSalida,
                   String tipoEstadia, String estado, double anticipo) {
        this(id, idHabitacion, idUsuario, clienteNombre, clienteDoc,
             fechaEntrada, horaEntrada, fechaSalida, horaSalida,
             tipoEstadia, estado, anticipo, 0);
    }

    public Reserva(int id, int idHabitacion, int idUsuario,
                   String clienteNombre, String clienteDoc,
                   String fechaEntrada, String horaEntrada,
                   String fechaSalida, String horaSalida,
                   String tipoEstadia, String estado, double anticipo,
                   double totalPagar) {
        this.id            = id;
        this.idHabitacion  = idHabitacion;
        this.idUsuario     = idUsuario;
        this.clienteNombre = clienteNombre;
        this.clienteDoc    = clienteDoc;
        this.fechaEntrada  = fechaEntrada;
        this.horaEntrada   = horaEntrada != null ? horaEntrada : "12:00";
        this.fechaSalida   = fechaSalida;
        this.horaSalida    = horaSalida  != null ? horaSalida  : "12:00";
        this.tipoEstadia   = tipoEstadia != null ? tipoEstadia : "Noche";
        this.estado        = estado;
        this.anticipo      = anticipo;
        this.totalPagar    = totalPagar;
    }

    public int    getId()               { return id; }
    public int    getIdHabitacion()     { return idHabitacion; }
    public int    getIdUsuario()        { return idUsuario; }
    public String getClienteNombre()    { return clienteNombre; }
    public String getClienteDoc()       { return clienteDoc; }
    public String getFechaEntrada()     { return fechaEntrada; }
    public String getHoraEntrada()      { return horaEntrada; }
    public String getFechaSalida()      { return fechaSalida; }
    public String getHoraSalida()       { return horaSalida; }
    public String getTipoEstadia()      { return tipoEstadia; }
    public String getEstado()           { return estado; }
    public double getAnticipo()         { return anticipo; }
    public double getTotalPagar()       { return totalPagar; }
    public void   setEstado(String e)   { this.estado = e; }
}
