package com.santaana.server;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class RestServer {

    private static final int PORT = 8080;
    private static HttpServer server;

    public static void iniciar() {
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext("/auth/login",    new AuthHandler());
            server.createContext("/habitaciones",  new HabitacionesHandler());
            server.createContext("/reservas",      new ReservasHandler());
            server.createContext("/stats",         new StatsHandler());
            server.createContext("/reportes",      new ReportesHandler());
            server.setExecutor(null);
            server.start();
            System.out.println("Servidor REST iniciado en puerto " + PORT);
        } catch (IOException e) {
            System.err.println("Error iniciando servidor REST: " + e.getMessage());
        }
    }

    public static void detener() {
        if (server != null) server.stop(0);
    }
}
