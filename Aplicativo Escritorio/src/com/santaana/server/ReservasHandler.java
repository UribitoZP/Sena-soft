package com.santaana.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.santaana.dao.ReservaDAO;
import com.santaana.dao.HabitacionDAO;
import com.santaana.model.Reserva;
import com.santaana.model.Habitacion;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservasHandler implements HttpHandler {

    private final ReservaDAO     reservaDAO    = new ReservaDAO();
    private final HabitacionDAO  habitacionDAO = new HabitacionDAO();

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

        Map<Integer, String> numHab = new HashMap<>();
        for (Habitacion h : habitacionDAO.listarTodas()) {
            numHab.put(h.getId(), h.getNumero());
        }

        List<Reserva> lista = reservaDAO.listarTodas();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            Reserva r = lista.get(i);
            String numH = numHab.getOrDefault(r.getIdHabitacion(), "?");
            sb.append(String.format(
                "{\"id\":%d,\"habitacion\":\"%s\",\"cliente\":\"%s\",\"doc\":\"%s\"," +
                "\"entrada\":\"%s\",\"salida\":\"%s\",\"estado\":\"%s\"}",
                r.getId(),
                JsonUtil.escapar(numH),
                JsonUtil.escapar(r.getClienteNombre()),
                JsonUtil.escapar(r.getClienteDoc()),
                JsonUtil.escapar(r.getFechaEntrada()),
                JsonUtil.escapar(r.getFechaSalida()),
                JsonUtil.escapar(r.getEstado())
            ));
            if (i < lista.size() - 1) sb.append(",");
        }
        sb.append("]");
        JsonUtil.enviar(ex, 200, sb.toString());
    }
}
