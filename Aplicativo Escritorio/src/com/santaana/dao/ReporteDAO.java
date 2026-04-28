package com.santaana.dao;

import com.santaana.db.DatabaseConnection;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReporteDAO {

    public double getTotalIngresos() {
        String sql = "SELECT COALESCE(SUM(anticipo), 0) FROM reservas WHERE estado = 'Completada'";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s  = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return 0;
    }

    public double getTotalAnticipos() {
        String sql = "SELECT COALESCE(SUM(anticipo), 0) FROM reservas";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s  = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return 0;
    }

    public int getTotalReservas() {
        String sql = "SELECT COUNT(*) FROM reservas";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s  = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return 0;
    }

    /** Conteo por estado: Activa, Completada, Cancelada */
    public Map<String, Integer> getReservasPorEstado() {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("Activa", 0);
        map.put("Completada", 0);
        map.put("Cancelada", 0);
        String sql = "SELECT estado, COUNT(*) FROM reservas GROUP BY estado";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s  = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) map.put(rs.getString(1), rs.getInt(2));
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return map;
    }

    /** Ingresos (anticipo) por mes, últimos 6 meses */
    public Map<String, Double> getIngresosPorMes() {
        Map<String, Double> map = new LinkedHashMap<>();
        String sql =
            "SELECT strftime('%Y-%m', fecha_entrada) AS mes, COALESCE(SUM(anticipo), 0) AS total " +
            "FROM reservas " +
            "WHERE fecha_entrada >= date('now', '-5 months', 'start of month') " +
            "GROUP BY mes ORDER BY mes ASC";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s  = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) map.put(rs.getString("mes"), rs.getDouble("total"));
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return map;
    }

    /** Reservas por mes, últimos 6 meses */
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
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return map;
    }

    /** Top 5 habitaciones más reservadas */
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
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return map;
    }

}
