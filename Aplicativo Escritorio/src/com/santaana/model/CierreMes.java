package com.santaana.model;

public class CierreMes {
    private int    id;
    private String mes;
    private int    idUsuario;
    private String nombreUsuario;
    private String fechaCierre;
    private double totalIngresos;
    private int    totalReservas;
    private int    totalCompletadas;
    private int    totalCanceladas;
    private String notas;

    public CierreMes(int id, String mes, int idUsuario, String nombreUsuario,
                     String fechaCierre, double totalIngresos,
                     int totalReservas, int totalCompletadas,
                     int totalCanceladas, String notas) {
        this.id               = id;
        this.mes              = mes;
        this.idUsuario        = idUsuario;
        this.nombreUsuario    = nombreUsuario;
        this.fechaCierre      = fechaCierre;
        this.totalIngresos    = totalIngresos;
        this.totalReservas    = totalReservas;
        this.totalCompletadas = totalCompletadas;
        this.totalCanceladas  = totalCanceladas;
        this.notas            = notas;
    }

    public int    getId()               { return id; }
    public String getMes()              { return mes; }
    public int    getIdUsuario()        { return idUsuario; }
    public String getNombreUsuario()    { return nombreUsuario; }
    public String getFechaCierre()      { return fechaCierre; }
    public double getTotalIngresos()    { return totalIngresos; }
    public int    getTotalReservas()    { return totalReservas; }
    public int    getTotalCompletadas() { return totalCompletadas; }
    public int    getTotalCanceladas()  { return totalCanceladas; }
    public String getNotas()            { return notas; }
}
