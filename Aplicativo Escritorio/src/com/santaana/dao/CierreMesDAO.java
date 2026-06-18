package com.santaana.dao;

import com.santaana.db.DatabaseConnection;
import com.santaana.db.DatabaseException;
import com.santaana.model.CierreMes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CierreMesDAO {

    public boolean esCerrado(String mes) {
        String sql = "SELECT COUNT(*) FROM cierres_mes WHERE mes = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, mes);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new DatabaseException("verificar si mes está cerrado", e);
        }
        return false;
    }

    public boolean cerrarMes(String mes, int idUsuario, String notas) {
        double ingresos    = calcularIngresos(mes);
        int[]  conteos     = calcularConteos(mes);

        String sql =
            "INSERT INTO cierres_mes " +
            "(mes, id_usuario, total_ingresos, total_reservas, total_completadas, total_canceladas, notas) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, mes);
            ps.setInt   (2, idUsuario);
            ps.setDouble(3, ingresos);
            ps.setInt   (4, conteos[0]);
            ps.setInt   (5, conteos[1]);
            ps.setInt   (6, conteos[2]);
            ps.setString(7, notas != null ? notas : "");
            boolean ok = ps.executeUpdate() > 0;
            if (ok) HistorialDAO.registrar("Sistema",
                    "Cierre de mes: " + mes,
                    "Cierre contable registrado. Ingresos: $" + (long) ingresos +
                    " | Reservas: " + conteos[0], idUsuario);
            return ok;
        } catch (SQLException e) {
            throw new DatabaseException("cerrar mes contable", e);
        }
    }

    public List<CierreMes> listarCierres() {
        List<CierreMes> lista = new ArrayList<>();
        String sql =
            "SELECT cm.*, u.nombre FROM cierres_mes cm " +
            "LEFT JOIN usuarios u ON cm.id_usuario = u.id " +
            "ORDER BY cm.mes DESC";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s  = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new DatabaseException("listar cierres de mes", e);
        }
        return lista;
    }

    public CierreMes getCierre(String mes) {
        String sql =
            "SELECT cm.*, u.nombre FROM cierres_mes cm " +
            "LEFT JOIN usuarios u ON cm.id_usuario = u.id " +
            "WHERE cm.mes = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, mes);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            throw new DatabaseException("obtener cierre de mes", e);
        }
        return null;
    }

    private double calcularIngresos(String mes) {
        String sql =
            "SELECT COALESCE(SUM(total_pagar), 0) FROM reservas " +
            "WHERE strftime('%Y-%m', fecha_entrada) = ? AND estado = 'Finalizada'";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, mes);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            throw new DatabaseException("calcular ingresos del mes", e);
        }
        return 0;
    }

    private int[] calcularConteos(String mes) {
        int[] r = {0, 0, 0};
        String sql =
            "SELECT COUNT(*), " +
            "SUM(CASE WHEN estado='Finalizada' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN estado='Cancelada'  THEN 1 ELSE 0 END) " +
            "FROM reservas WHERE strftime('%Y-%m', fecha_entrada) = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, mes);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) { r[0] = rs.getInt(1); r[1] = rs.getInt(2); r[2] = rs.getInt(3); }
        } catch (SQLException e) {
            throw new DatabaseException("calcular conteos del mes", e);
        }
        return r;
    }

    private CierreMes mapear(ResultSet rs) throws SQLException {
        return new CierreMes(
            rs.getInt("id"),
            rs.getString("mes"),
            rs.getInt("id_usuario"),
            rs.getString("nombre"),
            rs.getString("fecha_cierre"),
            rs.getDouble("total_ingresos"),
            rs.getInt("total_reservas"),
            rs.getInt("total_completadas"),
            rs.getInt("total_canceladas"),
            rs.getString("notas")
        );
    }
}
