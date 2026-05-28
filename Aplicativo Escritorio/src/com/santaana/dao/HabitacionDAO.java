package com.santaana.dao;

import com.santaana.db.DatabaseConnection;
import com.santaana.model.Habitacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HabitacionDAO {

    public List<Habitacion> listarTodas() {
        List<Habitacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM habitaciones ORDER BY numero";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error listando habitaciones: " + e.getMessage());
        }
        return lista;
    }

    public List<Habitacion> listarDisponibles() {
        List<Habitacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM habitaciones WHERE estado = 'Disponible' ORDER BY numero";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error listando disponibles: " + e.getMessage());
        }
        return lista;
    }

    /**
     * @param desdeDateTime  "yyyy-MM-dd HH:mm" — inicio de la nueva reserva
     * @param hastaDateTime  "yyyy-MM-dd HH:mm" — fin de la nueva reserva
     */
    public List<Habitacion> listarDisponiblesEnFechas(String desdeDateTime, String hastaDateTime) {
        List<Habitacion> lista = new ArrayList<>();
        String sql =
            "SELECT * FROM habitaciones WHERE estado IN ('Disponible','Limpieza') AND id NOT IN (" +
            "  SELECT id_habitacion FROM reservas WHERE estado = 'Activa'" +
            "  AND (fecha_entrada || ' ' || COALESCE(hora_entrada,'12:00')) < ?" +
            "  AND (fecha_salida  || ' ' || COALESCE(hora_salida, '12:00')) > ?" +
            ") ORDER BY numero";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hastaDateTime);
            ps.setString(2, desdeDateTime);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error listando disponibles por fechas: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizarEstado(int id, String nuevoEstado) {
        String sql = "UPDATE habitaciones SET estado = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error actualizando estado: " + e.getMessage());
            return false;
        }
    }

    public boolean agregar(String numero, String tipo, double precio) {
        String sql = "INSERT INTO habitaciones (numero, tipo, precio) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, numero);
            ps.setString(2, tipo);
            ps.setDouble(3, precio);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error agregando habitacion: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarDatos(int id, String tipo, double precio) {
        String sql = "UPDATE habitaciones SET tipo = ?, precio = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tipo);
            ps.setDouble(2, precio);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizando habitacion: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM habitaciones WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error eliminando habitacion: " + e.getMessage());
            return false;
        }
    }

    private Habitacion mapear(ResultSet rs) throws SQLException {
        return new Habitacion(
            rs.getInt("id"),
            rs.getString("numero"),
            rs.getString("tipo"),
            rs.getDouble("precio"),
            rs.getString("estado")
        );
    }
}
