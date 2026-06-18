package com.santaana.dao;

import com.santaana.db.DatabaseConnection;
import com.santaana.db.DatabaseException;
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
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new DatabaseException("listar habitaciones", e);
        }
        return lista;
    }

    public Habitacion buscarPorId(int id) {
        String sql = "SELECT * FROM habitaciones WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            throw new DatabaseException("buscar habitación por id", e);
        }
        return null;
    }

    public List<Habitacion> listarDisponibles() {
        List<Habitacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM habitaciones WHERE estado = 'Disponible' ORDER BY numero";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new DatabaseException("listar habitaciones disponibles", e);
        }
        return lista;
    }

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
            throw new DatabaseException("listar habitaciones disponibles por fechas", e);
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
            throw new DatabaseException("actualizar estado de habitación", e);
        }
    }

    public boolean agregar(String numero, String tipo, double precio, double precioBloque) {
        String sql = "INSERT INTO habitaciones (numero, tipo, precio, precio_bloque) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numero);
            ps.setString(2, tipo);
            ps.setDouble(3, precio);
            ps.setDouble(4, precioBloque);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("agregar habitación", e);
        }
    }

    public boolean actualizarDatos(int id, String tipo, double precio, double precioBloque) {
        String sql = "UPDATE habitaciones SET tipo = ?, precio = ?, precio_bloque = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tipo);
            ps.setDouble(2, precio);
            ps.setDouble(3, precioBloque);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("actualizar datos de habitación", e);
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM habitaciones WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("eliminar habitación", e);
        }
    }

    private Habitacion mapear(ResultSet rs) throws SQLException {
        double pb = 0;
        try { pb = rs.getDouble("precio_bloque"); } catch (SQLException ignored) {}
        return new Habitacion(
            rs.getInt("id"),
            rs.getString("numero"),
            rs.getString("tipo"),
            rs.getDouble("precio"),
            pb,
            rs.getString("estado")
        );
    }
}
