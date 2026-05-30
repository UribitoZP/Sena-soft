package com.santaana.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.santaana.dao.HabitacionDAO;
import com.santaana.model.Habitacion;
import java.io.IOException;
import java.util.List;

public class HabitacionesHandler implements HttpHandler {

    private final HabitacionDAO dao = new HabitacionDAO();

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

        List<Habitacion> lista = dao.listarTodas();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            Habitacion h = lista.get(i);
            sb.append(String.format(
                "{\"id\":%d,\"numero\":\"%s\",\"tipo\":\"%s\",\"precio\":%.0f,\"estado\":\"%s\"}",
                h.getId(),
                JsonUtil.escapar(h.getNumero()),
                JsonUtil.escapar(h.getTipo()),
                h.getPrecio(),
                JsonUtil.escapar(h.getEstado())
            ));
            if (i < lista.size() - 1) sb.append(",");
        }
        sb.append("]");
        JsonUtil.enviar(ex, 200, sb.toString());
    }
}
