package com.santaana.dao;

import com.santaana.db.DatabaseConnection;
import com.santaana.db.DatabaseException;
import com.santaana.model.Actividad;

import java.sql.*;
import java.util.*;

public class NotificacionDAO {

    private static final List<String> IMPORTANTES   = Arrays.asList("Checkout", "Cancelacion", "Sistema");
    private static final List<String> RECORDATORIOS = Arrays.asList("Reserva", "Checkin");

    public static List<Actividad> listar(String filtro) {
        List<String> tipos = null;
        if ("Importantes".equals(filtro))        tipos = IMPORTANTES;
        else if ("Recordatorios".equals(filtro)) tipos = RECORDATORIOS;

        StringBuilder sql = new StringBuilder(
            "SELECT id, tipo, titulo, descripcion, fecha_hora FROM historial");
        if (tipos != null) {
            sql.append(" WHERE tipo IN (");
            for (int i = 0; i < tipos.size(); i++) sql.append(i == 0 ? "?" : ",?");
            sql.append(")");
        }
        sql.append(" ORDER BY fecha_hora DESC LIMIT 50");

        List<Actividad> lista = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (tipos != null)
                for (int i = 0; i < tipos.size(); i++) ps.setString(i + 1, tipos.get(i));
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                lista.add(new Actividad(
                    rs.getInt("id"),
                    rs.getString("tipo"),
                    rs.getString("titulo"),
                    rs.getString("descripcion"),
                    rs.getString("fecha_hora")
                ));
        } catch (SQLException e) {
            throw new DatabaseException("listar notificaciones", e);
        }
        return lista;
    }
}
