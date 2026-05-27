package com.santaana.dao;

import com.santaana.db.DatabaseConnection;
import com.santaana.model.Actividad;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistorialDAO {

    private static volatile int pendingCount = 0;

    public static int getPendingCount()  { return pendingCount; }
    public static void resetPendingCount() { pendingCount = 0; }

    public static void registrar(String tipo, String titulo, String descripcion) {
        registrar(tipo, titulo, descripcion, 0);
    }

    public static void registrar(String tipo, String titulo, String descripcion, int idUsuario) {
        String sql = "INSERT INTO historial (tipo, titulo, descripcion, id_usuario) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tipo);
            ps.setString(2, titulo);
            ps.setString(3, descripcion);
            ps.setInt   (4, idUsuario);
            ps.executeUpdate();
            pendingCount++;
        } catch (SQLException e) {
            System.err.println("Error registrando historial: " + e.getMessage());
        }
    }

    public List<Actividad> listarTodas() {
        return buscar(null, null, null);
    }

    public List<Actividad> buscar(String texto, String desde, String hasta) {
        List<Actividad> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT id, tipo, titulo, descripcion, fecha_hora FROM historial WHERE 1=1");

        boolean hayTexto = texto != null && !texto.isEmpty();
        boolean hayDesde = desde != null && !desde.isEmpty();
        boolean hayHasta = hasta != null && !hasta.isEmpty();

        if (hayTexto)  sql.append(" AND (titulo LIKE ? OR descripcion LIKE ?)");
        if (hayDesde)  sql.append(" AND DATE(fecha_hora) >= ?");
        if (hayHasta)  sql.append(" AND DATE(fecha_hora) <= ?");
        sql.append(" ORDER BY fecha_hora DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            if (hayTexto) {
                String like = "%" + texto + "%";
                ps.setString(idx++, like);
                ps.setString(idx++, like);
            }
            if (hayDesde) ps.setString(idx++, desde);
            if (hayHasta) ps.setString(idx++, hasta);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Actividad(
                    rs.getInt("id"),
                    rs.getString("tipo"),
                    rs.getString("titulo"),
                    rs.getString("descripcion"),
                    rs.getString("fecha_hora")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error buscando historial: " + e.getMessage());
        }
        return lista;
    }
}
