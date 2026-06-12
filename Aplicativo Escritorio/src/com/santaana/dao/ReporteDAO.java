package com.santaana.dao;

import com.santaana.db.DatabaseConnection;
import com.santaana.db.DatabaseException;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReporteDAO {

    public double getTotalIngresos() {
        String sql = "SELECT COALESCE(SUM(total_pagar), 0) FROM reservas WHERE estado = 'Finalizada'";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s  = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            throw new DatabaseException("obtener total de ingresos", e);
        }
        return 0;
    }

    public double getTotalAnticipos() {
        String sql = "SELECT COALESCE(SUM(anticipo), 0) FROM reservas";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s  = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            throw new DatabaseException("obtener total de anticipos", e);
        }
        return 0;
    }

    public int getTotalReservas() {
        String sql = "SELECT COUNT(*) FROM reservas";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s  = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new DatabaseException("obtener total de reservas", e);
        }
        return 0;
    }

    public Map<String, Integer> getReservasPorEstado() {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("Activa", 0);
        map.put("Finalizada", 0);
        map.put("Cancelada", 0);
        String sql = "SELECT estado, COUNT(*) FROM reservas GROUP BY estado";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s  = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) map.put(rs.getString(1), rs.getInt(2));
        } catch (SQLException e) {
            throw new DatabaseException("obtener reservas por estado", e);
        }
        return map;
    }

    public Map<String, Double> getIngresosPorMes() {
        Map<String, Double> map = new LinkedHashMap<>();
        String sql =
            "SELECT strftime('%Y-%m', fecha_entrada) AS mes, COALESCE(SUM(total_pagar), 0) AS total " +
            "FROM reservas " +
            "WHERE estado = 'Finalizada' AND fecha_entrada >= date('now', '-5 months', 'start of month') " +
            "GROUP BY mes ORDER BY mes ASC";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s  = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) map.put(rs.getString("mes"), rs.getDouble("total"));
        } catch (SQLException e) {
            throw new DatabaseException("obtener ingresos por mes", e);
        }
        return map;
    }

    public Map<String, Integer> getReservasPorMes() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql =
            "SELECT strftime('%Y-%m', fecha_entrada) AS mes, COUNT(*) AS total " +
            "FROM reservas " +
            "WHERE fecha_entrada >= date('now', '-5 months', 'start of month') " +
            "GROUP BY mes ORDER BY mes ASC";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s  = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) map.put(rs.getString("mes"), rs.getInt("total"));
        } catch (SQLException e) {
            throw new DatabaseException("obtener reservas por mes", e);
        }
        return map;
    }

    public Map<String, Integer> getTopHabitaciones() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql =
            "SELECT h.numero, COUNT(r.id) AS total " +
            "FROM reservas r JOIN habitaciones h ON r.id_habitacion = h.id " +
            "GROUP BY r.id_habitacion ORDER BY total DESC LIMIT 5";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s  = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) map.put("Hab. " + rs.getString(1), rs.getInt(2));
        } catch (SQLException e) {
            throw new DatabaseException("obtener top habitaciones", e);
        }
        return map;
    }

}
