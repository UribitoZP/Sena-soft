package com.santaana.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.santaana.dao.UsuarioDAO;
import com.santaana.model.Usuario;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuthHandler implements HttpHandler {

    private final UsuarioDAO dao = new UsuarioDAO();
    private static final Map<String, Intentos> intentos = new ConcurrentHashMap<>();
    private static final int MAX_INTENTOS = 5;
    private static final long BLOQUEO_MS = 30_000;

    private static class Intentos {
        int count;
        long ultimo;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
            JsonUtil.enviar(ex, 204, "");
            return;
        }

        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
            JsonUtil.enviar(ex, 405, "{\"error\":\"Metodo no permitido\"}");
            return;
        }

        String ip = ex.getRemoteAddress().getAddress().getHostAddress();
        if (!permitirIntento(ip)) {
            JsonUtil.enviar(ex, 429, "{\"error\":\"Demasiados intentos. Espere 30 segundos.\"}");
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
            registrarFallo(ip);
            JsonUtil.enviar(ex, 401, "{\"error\":\"Credenciales incorrectas\"}");
            return;
        }

        intentos.remove(ip);
        String token = TokenManager.crear(u);
        String json = String.format(
            "{\"token\":\"%s\",\"id\":%d,\"nombre\":\"%s\",\"usuario\":\"%s\",\"rol\":\"%s\"}",
            token,
            u.getId(),
            JsonUtil.escapar(u.getNombre()),
            JsonUtil.escapar(u.getUsuario()),
            JsonUtil.escapar(u.getRol())
        );
        JsonUtil.enviar(ex, 200, json);
    }

    private boolean permitirIntento(String ip) {
        Intentos i = intentos.get(ip);
        if (i == null) return true;
        long ahora = System.currentTimeMillis();
        if (ahora - i.ultimo > BLOQUEO_MS) {
            intentos.remove(ip);
            return true;
        }
        return i.count < MAX_INTENTOS;
    }

    private void registrarFallo(String ip) {
        Intentos i = intentos.computeIfAbsent(ip, k -> new Intentos());
        i.count++;
        i.ultimo = System.currentTimeMillis();
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
