package com.santaana.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.santaana.dao.ReporteDAO;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

public class ReportesHandler implements HttpHandler {

    private final ReporteDAO dao = new ReporteDAO();

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (!ex.getRequestMethod().equalsIgnoreCase("GET")) {
            JsonUtil.enviar(ex, 405, "{\"error\":\"Metodo no permitido\"}");
            return;
        }

        // ── KPIs ──────────────────────────────────────────────────
        double totalIngresos  = dao.getTotalIngresos();
        double totalAnticipos = dao.getTotalAnticipos();
        int    totalReservas  = dao.getTotalReservas();

        Map<String, Integer> porEstado   = dao.getReservasPorEstado();
        Map<String, Double>  ingresosMes = dao.getIngresosPorMes();
        Map<String, Integer> reservasMes = dao.getReservasPorMes();
        Map<String, Integer> topHabs     = dao.getTopHabitaciones();

        int completadas = porEstado.getOrDefault("Completada", 0);
        int canceladas  = porEstado.getOrDefault("Cancelada",  0);
        int activas     = porEstado.getOrDefault("Activa",     0);

        double tasaCancelacion = totalReservas > 0
                ? (100.0 * canceladas / totalReservas) : 0;

        // ── Ingresos por mes → JSON array ─────────────────────────
        StringBuilder ingresosMesJson = new StringBuilder("[");
        int idx = 0;
        for (Map.Entry<String, Double> e : ingresosMes.entrySet()) {
            if (idx > 0) ingresosMesJson.append(",");
            ingresosMesJson.append(String.format(Locale.US,
                "{\"mes\":\"%s\",\"valor\":%.2f,\"etiqueta\":\"%s\"}",
                e.getKey(), e.getValue(), formatPrecio(e.getValue())
            ));
            idx++;
        }
        ingresosMesJson.append("]");

        // ── Reservas por mes → JSON array ─────────────────────────
        StringBuilder reservasMesJson = new StringBuilder("[");
        idx = 0;
        for (Map.Entry<String, Integer> e : reservasMes.entrySet()) {
            if (idx > 0) reservasMesJson.append(",");
            reservasMesJson.append(String.format(Locale.US,
                "{\"mes\":\"%s\",\"valor\":%d}",
                e.getKey(), e.getValue()
            ));
            idx++;
        }
        reservasMesJson.append("]");

        // ── Top habitaciones → JSON array ──────────────────────────
        StringBuilder topHabsJson = new StringBuilder("[");
        idx = 0;
        for (Map.Entry<String, Integer> e : topHabs.entrySet()) {
            if (idx > 0) topHabsJson.append(",");
            topHabsJson.append(String.format(Locale.US,
                "{\"habitacion\":\"%s\",\"reservas\":%d}",
                e.getKey(), e.getValue()
            ));
            idx++;
        }
        topHabsJson.append("]");

        // ── JSON final ─────────────────────────────────────────────
        String json = String.format(Locale.US,
            "{" +
            "\"kpis\":{" +
                "\"ingresosConfirmados\":%.2f," +
                "\"ingresosConfirmadosFormato\":\"%s\"," +
                "\"anticiposTotales\":%.2f," +
                "\"anticiposTotalesFormato\":\"%s\"," +
                "\"totalReservas\":%d," +
                "\"activas\":%d," +
                "\"completadas\":%d," +
                "\"canceladas\":%d," +
                "\"tasaCancelacion\":%.1f" +
            "}," +
            "\"ingresosPorMes\":%s," +
            "\"reservasPorMes\":%s," +
            "\"topHabitaciones\":%s" +
            "}",
            totalIngresos,  formatPrecio(totalIngresos),
            totalAnticipos, formatPrecio(totalAnticipos),
            totalReservas, activas, completadas, canceladas, tasaCancelacion,
            ingresosMesJson,
            reservasMesJson,
            topHabsJson
        );

        JsonUtil.enviar(ex, 200, json);
    }

    private String formatPrecio(double valor) {
        if (valor >= 1_000_000) return String.format(Locale.US, "$%.1fM", valor / 1_000_000);
        if (valor >= 1_000)     return String.format(Locale.US, "$%.1fk", valor / 1_000);
        return String.format(Locale.US, "$%.0f", valor);
    }
}
