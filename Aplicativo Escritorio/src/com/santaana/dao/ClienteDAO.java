package com.santaana.dao;

import com.santaana.db.DatabaseConnection;
import com.santaana.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public Cliente buscarPorDocumento(String doc) {
        String sql = "SELECT * FROM clientes WHERE documento = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, doc);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Cliente(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("documento"),
                        rs.getString("telefono"),
                        rs.getString("correo")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error buscando cliente por documento: " + e.getMessage());
        }
        return null;
    }

    public int crear(Cliente cliente) {
        String sql = "INSERT INTO clientes (nombre, documento, telefono, correo) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getDocumento());
            ps.setString(3, cliente.getTelefono());
            ps.setString(4, cliente.getCorreo());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet gk = ps.getGeneratedKeys()) {
                    if (gk.next()) {
                        return gk.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creando cliente: " + e.getMessage());
        }
        return -1;
    }

    public boolean actualizar(Cliente cliente) {
        String sql = "UPDATE clientes SET nombre = ?, telefono = ?, correo = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getTelefono());
            ps.setString(3, cliente.getCorreo());
            ps.setInt(4, cliente.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizando cliente: " + e.getMessage());
        }
        return false;
    }

    public List<Cliente> listarTodos() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes ORDER BY nombre ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Cliente(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("documento"),
                    rs.getString("telefono"),
                    rs.getString("correo")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error listando clientes: " + e.getMessage());
        }
        return lista;
    }
}
