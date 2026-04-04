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

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error listando reservas: " + e.getMessage());
        }
        return lista;
    }

    public boolean crear(int idHabitacion, int idUsuario, String clienteNombre,
                         String clienteDoc, String fechaEntrada, String fechaSalida) {
        String sql = "INSERT INTO reservas (id_habitacion, id_usuario, cliente_nombre, " +
                     "cliente_doc, fecha_entrada, fecha_salida) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idHabitacion);
            ps.setInt(2, idUsuario);
            ps.setString(3, clienteNombre);
            ps.setString(4, clienteDoc);
            ps.setString(5, fechaEntrada);
            ps.setString(6, fechaSalida);
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

    private Reserva mapear(ResultSet rs) throws SQLException {
        return new Reserva(
            rs.getInt("id"),
            rs.getInt("id_habitacion"),
            rs.getInt("id_usuario"),
            rs.getString("cliente_nombre"),
            rs.getString("cliente_doc"),
            rs.getString("fecha_entrada"),
            rs.getString("fecha_salida"),
            rs.getString("estado")
        );
    }
}
