package com.santaana.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.santaana.dao.HabitacionDAO;
import com.santaana.dao.ReservaDAO;
import com.santaana.model.Habitacion;
import com.santaana.model.Reserva;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatsHandler implements HttpHandler {

    private final HabitacionDAO habitacionDAO = new HabitacionDAO();
    private final ReservaDAO    reservaDAO    = new ReservaDAO();

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
            JsonUtil.enviar(ex, 204, "");
            return;
        }

        if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) {
            JsonUtil.enviar(ex, 405, "{\"error\":\"Metodo no permitido\"}");
            return;
        }

        String auth = ex.getRequestHeaders().getFirst("Authorization");
        if (!TokenManager.verificar(auth)) {
            JsonUtil.enviar(ex, 401, "{\"error\":\"Token requerido\"}");
            return;
        }

        List<Habitacion> habitaciones = habitacionDAO.listarTodas();
        long disponibles   = habitaciones.stream().filter(h -> h.getEstado().equals("Disponible")).count();
        long ocupadas      = habitaciones.stream().filter(h -> h.getEstado().equals("Ocupada")).count();
        long limpieza      = habitaciones.stream().filter(h -> h.getEstado().equals("Limpieza")).count();
        long mantenimiento = habitaciones.stream().filter(h -> h.getEstado().equals("Mantenimiento")).count();
        long total         = habitaciones.size();

        Map<Integer, Double> preciosPorId = new HashMap<>();
        for (Habitacion h : habitaciones) {
            preciosPorId.put(h.getId(), h.getPrecio());
        }

        List<Reserva> reservas = reservaDAO.listarTodas();
        long activas     = reservas.stream().filter(r -> r.getEstado().equals("Activa")).count();
        long completadas = reservas.stream().filter(r -> r.getEstado().equals("Completada")).count();
        long canceladas  = reservas.stream().filter(r -> r.getEstado().equals("Cancelada")).count();

        String hoy = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        double ingresosHoy   = 0;
        double ingresosMes   = 0;
        double ingresosTotal = 0;
        String mesActual     = hoy.substring(0, 7);

        for (Reserva r : reservas) {
            if (r.getEstado().equals("Cancelada")) continue;

            double precioNoche = preciosPorId.getOrDefault(r.getIdHabitacion(), 0.0);
            double ingreso     = 0;

            try {
                LocalDate entrada = LocalDate.parse(r.getFechaEntrada());
                LocalDate salida  = LocalDate.parse(r.getFechaSalida());
                long noches = ChronoUnit.DAYS.between(entrada, salida);
                if (noches <= 0) noches = 1;

                ingreso = precioNoche * noches;

                ingresosTotal += ingreso;

                if (r.getFechaEntrada().startsWith(mesActual) ||
                    r.getFechaSalida().startsWith(mesActual)) {
                    ingresosMes += ingreso;
                }

                LocalDate hoyDate = LocalDate.now();
                if (!hoyDate.isBefore(entrada) && hoyDate.isBefore(salida)) {
                    ingresosHoy += precioNoche;
                }

            } catch (Exception e) {
                ingreso = r.getAnticipo();
                ingresosTotal += ingreso;
            }
        }

        String ingresosHoyStr   = formatPrecio(ingresosHoy);
        String ingresosMesStr   = formatPrecio(ingresosMes);
        String ingresosTotalStr = formatPrecio(ingresosTotal);

        String json = String.format(
            Locale.US,
            "{" +
            "\"habitaciones\":{" +
                "\"total\":%d," +
                "\"disponibles\":%d," +
                "\"ocupadas\":%d," +
                "\"limpieza\":%d," +
                "\"mantenimiento\":%d" +
            "}," +
            "\"reservas\":{" +
                "\"activas\":%d," +
                "\"completadas\":%d," +
                "\"canceladas\":%d" +
            "}," +
            "\"ingresos\":{" +
                "\"hoy\":%.2f," +
                "\"hoyFormato\":\"%s\"," +
                "\"mes\":%.2f," +
                "\"mesFormato\":\"%s\"," +
                "\"total\":%.2f," +
                "\"totalFormato\":\"%s\"" +
            "}" +
            "}",
            total, disponibles, ocupadas, limpieza, mantenimiento,
            activas, completadas, canceladas,
            ingresosHoy,   ingresosHoyStr,
            ingresosMes,   ingresosMesStr,
            ingresosTotal, ingresosTotalStr
        );

        JsonUtil.enviar(ex, 200, json);
    }

    private String formatPrecio(double valor) {
        if (valor >= 1_000_000) {
            return String.format(Locale.US, "$%.1fM", valor / 1_000_000);
        } else if (valor >= 1_000) {
            return String.format(Locale.US, "$%.1fk", valor / 1_000);
        } else {
            return String.format(Locale.US, "$%.0f", valor);
        }
    }
}
