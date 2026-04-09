package com.santaana.dao;

import com.santaana.db.DatabaseConnection;
import com.santaana.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public Usuario autenticar(String usuario, String clave, String rol) {
        String sql = "SELECT * FROM usuarios WHERE usuario = ? AND clave = ? AND rol = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, clave);
            ps.setString(3, rol);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Usuario(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("usuario"),
                    rs.getString("clave"),
                    rs.getString("rol")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error autenticando usuario: " + e.getMessage());
        }
        return null;
    }

    public Usuario autenticarSinRol(String usuario, String clave) {
        String sql = "SELECT * FROM usuarios WHERE usuario = ? AND clave = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, clave);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Usuario(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("usuario"),
                    rs.getString("clave"),
                    rs.getString("rol")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error autenticando usuario: " + e.getMessage());
        }
        return null;
    }
}
