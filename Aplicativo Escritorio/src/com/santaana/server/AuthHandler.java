package com.santaana.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.santaana.dao.UsuarioDAO;
import com.santaana.model.Usuario;
import java.io.IOException;

public class AuthHandler implements HttpHandler {

    private final UsuarioDAO dao = new UsuarioDAO();

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (!ex.getRequestMethod().equalsIgnoreCase("POST")) {
            JsonUtil.enviar(ex, 405, "{\"error\":\"Metodo no permitido\"}");
            return;
        }

        String body = JsonUtil.leerCuerpo(ex);
        String usuario = extraer(body, "usuario");
        String clave   = extraer(body, "clave");
        String rol     = extraer(body, "rol");

        if (usuario.isEmpty() || clave.isEmpty()) {
            JsonUtil.enviar(ex, 400, "{\"error\":\"Campos requeridos: usuario, clave\"}");
            return;
        }

        Usuario u = rol.isEmpty()
            ? dao.autenticarSinRol(usuario, clave)
            : dao.autenticar(usuario, clave, rol);
        if (u == null) {
            JsonUtil.enviar(ex, 401, "{\"error\":\"Credenciales incorrectas\"}");
            return;
        }

        String json = String.format(
            "{\"id\":%d,\"nombre\":\"%s\",\"usuario\":\"%s\",\"rol\":\"%s\"}",
            u.getId(),
            JsonUtil.escapar(u.getNombre()),
            JsonUtil.escapar(u.getUsuario()),
            JsonUtil.escapar(u.getRol())
        );
        JsonUtil.enviar(ex, 200, json);
    }

    /** Extrae un valor de un JSON simple clave:valor */
    private String extraer(String json, String clave) {
        String buscar = "\"" + clave + "\"";
        int i = json.indexOf(buscar);
        if (i < 0) return "";
        i = json.indexOf(":", i) + 1;
        while (i < json.length() && (json.charAt(i) == ' ' || json.charAt(i) == '"')) i++;
        int fin = i;
        while (fin < json.length() && json.charAt(fin) != '"' && json.charAt(fin) != '}' && json.charAt(fin) != ',') fin++;
        return json.substring(i, fin).trim();
    }
}
