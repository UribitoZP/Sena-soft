package com.santaana.dao;

import com.santaana.db.DatabaseConnection;
import com.santaana.model.Actividad;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistorialDAO {

    public static void registrar(String tipo, String titulo, String descripcion) {
        String sql = "INSERT INTO historial (tipo, titulo, descripcion) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tipo);
            ps.setString(2, titulo);
            ps.setString(3, descripcion);
            ps.executeUpdate();
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
