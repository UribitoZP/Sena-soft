package com.santaana.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.santaana.dao.HabitacionDAO;
import com.santaana.dao.ReservaDAO;
import com.santaana.model.Habitacion;
import com.santaana.model.Reserva;
import java.io.IOException;
import java.util.List;

public class StatsHandler implements HttpHandler {

    private final HabitacionDAO habitacionDAO = new HabitacionDAO();
    private final ReservaDAO    reservaDAO    = new ReservaDAO();

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (!ex.getRequestMethod().equalsIgnoreCase("GET")) {
            JsonUtil.enviar(ex, 405, "{\"error\":\"Metodo no permitido\"}");
            return;
        }

        List<Habitacion> habitaciones = habitacionDAO.listarTodas();
        long disponibles   = habitaciones.stream().filter(h -> h.getEstado().equals("Disponible")).count();
        long ocupadas      = habitaciones.stream().filter(h -> h.getEstado().equals("Ocupada")).count();
        long limpieza      = habitaciones.stream().filter(h -> h.getEstado().equals("Limpieza")).count();
        long mantenimiento = habitaciones.stream().filter(h -> h.getEstado().equals("Mantenimiento")).count();
        long total         = habitaciones.size();

        List<Reserva> reservas = reservaDAO.listarTodas();
        long activas     = reservas.stream().filter(r -> r.getEstado().equals("Activa")).count();
        long completadas = reservas.stream().filter(r -> r.getEstado().equals("Completada")).count();
        long canceladas  = reservas.stream().filter(r -> r.getEstado().equals("Cancelada")).count();

        String json = String.format(
            "{\"habitaciones\":{\"total\":%d,\"disponibles\":%d,\"ocupadas\":%d," +
            "\"limpieza\":%d,\"mantenimiento\":%d}," +
            "\"reservas\":{\"activas\":%d,\"completadas\":%d,\"canceladas\":%d}}",
            total, disponibles, ocupadas, limpieza, mantenimiento,
            activas, completadas, canceladas
        );
        JsonUtil.enviar(ex, 200, json);
    }
}
