package com.santaana.dao;

import com.santaana.db.DatabaseConnection;
import com.santaana.db.DatabaseException;
import com.santaana.model.Reserva;
import com.santaana.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {

    public List<Reserva> listarTodas() {
        List<Reserva> lista = new ArrayList<>();
        String sql = "SELECT r.*, c.nombre AS cliente_nombre, c.documento AS cliente_doc " +
                     "FROM reservas r " +
                     "LEFT JOIN clientes c ON r.id_cliente = c.id " +
                     "ORDER BY r.fecha_entrada DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new DatabaseException("listar reservas", e);
        }
        return lista;
    }

    public List<Reserva> listarActivas() {
        List<Reserva> lista = new ArrayList<>();
        String sql = "SELECT r.*, c.nombre AS cliente_nombre, c.documento AS cliente_doc " +
                     "FROM reservas r " +
                     "LEFT JOIN clientes c ON r.id_cliente = c.id " +
                     "WHERE r.estado = 'Activa' ORDER BY r.fecha_entrada ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new DatabaseException("listar reservas activas", e);
        }
        return lista;
    }

    public boolean crear(int idHabitacion, int idUsuario, String clienteNombre,
                         String clienteDoc, String clienteTelefono, String clienteCorreo,
                         String fechaEntrada, String horaEntrada,
                         String fechaSalida, String horaSalida, String tipoEstadia,
                         double anticipo) {
        ClienteDAO clienteDAO = new ClienteDAO();
        Cliente cliente = clienteDAO.buscarPorDocumento(clienteDoc);
        int idCliente;
        if (cliente == null) {
            cliente = new Cliente(0, clienteNombre, clienteDoc, clienteTelefono, clienteCorreo);
            idCliente = clienteDAO.crear(cliente);
            if (idCliente == -1) {
                throw new DatabaseException("crear cliente durante reserva", new SQLException("No se pudo crear el cliente"));
            }
        } else {
            idCliente = cliente.getId();
        }

        String sql = "INSERT INTO reservas (id_habitacion, id_usuario, id_cliente, fecha_entrada, hora_entrada, fecha_salida, hora_salida, tipo_estadia, anticipo) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idHabitacion);
            ps.setInt(2, idUsuario);
            ps.setInt(3, idCliente);
            ps.setString(4, fechaEntrada);
            ps.setString(5, horaEntrada);
            ps.setString(6, fechaSalida);
            ps.setString(7, horaSalida);
            ps.setString(8, tipoEstadia);
            ps.setDouble(9, anticipo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("crear reserva", e);
        }
    }

    public boolean actualizarAnticipo(int id, double nuevoAnticipo) {
        String sql = "UPDATE reservas SET anticipo = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, nuevoAnticipo);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("actualizar anticipo de reserva", e);
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
            throw new DatabaseException("actualizar estado de reserva", e);
        }
    }

    public Reserva buscarActivaPorHabitacion(int idHabitacion) {
        String sql = "SELECT r.*, c.nombre AS cliente_nombre, c.documento AS cliente_doc " +
                     "FROM reservas r " +
                     "LEFT JOIN clientes c ON r.id_cliente = c.id " +
                     "WHERE r.id_habitacion = ? AND r.estado = 'Activa'" +
                     " AND r.fecha_entrada <= date('now') LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idHabitacion);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            throw new DatabaseException("buscar reserva activa por habitación", e);
        }
        return null;
    }

    public Reserva buscarProximaPorHabitacion(int idHabitacion) {
        String sql = "SELECT r.*, c.nombre AS cliente_nombre, c.documento AS cliente_doc " +
                     "FROM reservas r " +
                     "LEFT JOIN clientes c ON r.id_cliente = c.id " +
                     "WHERE r.id_habitacion = ? AND r.estado = 'Activa'" +
                     " AND r.fecha_entrada > date('now') ORDER BY r.fecha_entrada ASC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idHabitacion);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            throw new DatabaseException("buscar próxima reserva por habitación", e);
        }
        return null;
    }

    public List<Reserva> listarUltimasPorHabitacion(int idHabitacion, int limite) {
        List<Reserva> lista = new ArrayList<>();
        String sql = "SELECT r.*, c.nombre AS cliente_nombre, c.documento AS cliente_doc " +
                     "FROM reservas r " +
                     "LEFT JOIN clientes c ON r.id_cliente = c.id " +
                     "WHERE r.id_habitacion = ? ORDER BY r.id DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idHabitacion);
            ps.setInt(2, limite);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new DatabaseException("listar últimas reservas por habitación", e);
        }
        return lista;
    }

    private Reserva mapear(ResultSet rs) throws SQLException {
        String horaEnt = "12:00", horaSal = "12:00", tipo = "Noche";
        double anticipo = 0;
        try { String v = rs.getString("hora_entrada"); if (v != null) horaEnt = v; } catch (SQLException ignored) {}
        try { String v = rs.getString("hora_salida");  if (v != null) horaSal = v; } catch (SQLException ignored) {}
        try { String v = rs.getString("tipo_estadia"); if (v != null) tipo    = v; } catch (SQLException ignored) {}
        try { anticipo = rs.getDouble("anticipo"); } catch (SQLException ignored) {}
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
            rs.getString("estado"),
            anticipo
        );
    }
}
