package com.santaana.server;

import com.santaana.model.Usuario;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TokenManager {

    private static final Map<String, TokenInfo> tokens = new ConcurrentHashMap<>();

    public static String crear(Usuario u) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, new TokenInfo(u.getId(), u.getNombre(), u.getUsuario(), u.getRol()));
        return token;
    }

    public static boolean verificar(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return false;
        return tokens.containsKey(authHeader.substring(7));
    }

    @SuppressWarnings("unused")
    public static TokenInfo get(String token) {
        return tokens.get(token);
    }

    public static class TokenInfo {
        private final int id;
        private final String nombre;
        private final String usuario;
        private final String rol;

        public TokenInfo(int id, String nombre, String usuario, String rol) {
            this.id = id;
            this.nombre = nombre;
            this.usuario = usuario;
            this.rol = rol;
        }

        public int getId() { return id; }
        public String getNombre() { return nombre; }
        public String getUsuario() { return usuario; }
        public String getRol() { return rol; }
    }
}
