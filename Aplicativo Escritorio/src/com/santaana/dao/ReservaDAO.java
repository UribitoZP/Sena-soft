package com.santaana.dao;

import com.santaana.db.DatabaseConnection;
import com.santaana.model.Reserva;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {

    public List<Reserva> listarTodas() {
        List<Reserva> lista = new ArrayList<>();
        String sql = "SELECT * FROM reservas ORDER BY fecha_entrada DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error listando reservas: " + e.getMessage());
        }
        return lista;
    }

    public List<Reserva> listarActivas() {
        List<Reserva> lista = new ArrayList<>();
        String sql = "SELECT * FROM reservas WHERE estado = 'Activa' ORDER BY fecha_entrada ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error listando activas: " + e.getMessage());
        }
        return lista;
    }

    public boolean crear(int idHabitacion, int idUsuario, String clienteNombre,
                         String clienteDoc, String fechaEntrada, String horaEntrada,
                         String fechaSalida, String horaSalida, String tipoEstadia) {
        String sql = "INSERT INTO reservas (id_habitacion, id_usuario, cliente_nombre, " +
                     "cliente_doc, fecha_entrada, hora_entrada, fecha_salida, hora_salida, tipo_estadia) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idHabitacion);
            ps.setInt(2, idUsuario);
            ps.setString(3, clienteNombre);
            ps.setString(4, clienteDoc);
            ps.setString(5, fechaEntrada);
            ps.setString(6, horaEntrada);
            ps.setString(7, fechaSalida);
            ps.setString(8, horaSalida);
            ps.setString(9, tipoEstadia);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error creando reserva: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarEstado(int id, String nuevoEstado) {
        String sql = "UPDATE reservas SET estado = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizando reserva: " + e.getMessage());
            return false;
        }
    }

    public Reserva buscarActivaPorHabitacion(int idHabitacion) {
        // Reserva activa cuya fecha_entrada ya pasó o es hoy (huésped presente)
        String sql = "SELECT * FROM reservas WHERE id_habitacion = ? AND estado = 'Activa'" +
                     " AND fecha_entrada <= date('now') LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idHabitacion);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error buscando reserva activa: " + e.getMessage());
        }
        return null;
    }

    public Reserva buscarProximaPorHabitacion(int idHabitacion) {
        // Reserva activa cuya fecha_entrada es futura (todavía no llega)
        String sql = "SELECT * FROM reservas WHERE id_habitacion = ? AND estado = 'Activa'" +
                     " AND fecha_entrada > date('now') ORDER BY fecha_entrada ASC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idHabitacion);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error buscando próxima reserva: " + e.getMessage());
        }
        return null;
    }

    public List<Reserva> listarUltimasPorHabitacion(int idHabitacion, int limite) {
        List<Reserva> lista = new ArrayList<>();
        String sql = "SELECT * FROM reservas WHERE id_habitacion = ? ORDER BY id DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idHabitacion);
            ps.setInt(2, limite);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error listando historial: " + e.getMessage());
        }
        return lista;
    }

    private Reserva mapear(ResultSet rs) throws SQLException {
        String horaEnt = "12:00", horaSal = "12:00", tipo = "Noche";
        try { String v = rs.getString("hora_entrada"); if (v != null) horaEnt = v; } catch (SQLException ignored) {}
        try { String v = rs.getString("hora_salida");  if (v != null) horaSal = v; } catch (SQLException ignored) {}
        try { String v = rs.getString("tipo_estadia"); if (v != null) tipo    = v; } catch (SQLException ignored) {}
        return new Reserva(
            rs.getInt("id"),
            rs.getInt("id_habitacion"),
            rs.getInt("id_usuario"),
            rs.getString("cliente_nombre"),
            rs.getString("cliente_doc"),
            rs.getString("fecha_entrada"),
            horaEnt,
            rs.getString("fecha_salida"),
            horaSal,
            tipo,
            rs.getString("estado")
        );
    }
}
