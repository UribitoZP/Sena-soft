package com.santaana.dao;

import com.santaana.db.DatabaseConnection;
import com.santaana.db.DatabaseException;
import com.santaana.model.Usuario;
import com.santaana.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public Usuario autenticar(String usuario, String clave, String rol) {
        String sql = "SELECT * FROM usuarios WHERE usuario = ? AND rol = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, rol);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String stored = rs.getString("clave");
                if (stored != null) {
                    boolean ok = PasswordUtil.esHash(stored)
                        ? PasswordUtil.verify(clave, stored)
                        : stored.equals(clave);
                    if (!ok) return null;
                }
                return mapear(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseException("autenticar usuario", e);
        }
        return null;
    }

    public Usuario autenticarSinRol(String usuario, String clave) {
        String sql = "SELECT * FROM usuarios WHERE usuario = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String stored = rs.getString("clave");
                if (stored != null) {
                    boolean ok = PasswordUtil.esHash(stored)
                        ? PasswordUtil.verify(clave, stored)
                        : stored.equals(clave);
                    if (!ok) return null;
                }
                return mapear(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseException("autenticar usuario sin rol", e);
        }
        return null;
    }

    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nombre ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new DatabaseException("listar usuarios", e);
        }
        return lista;
    }

    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseException("buscar usuario por ID", e);
        }
        return null;
    }

    public boolean crear(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre, usuario, clave, rol, telefono, correo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getUsuario());
            ps.setString(3, PasswordUtil.hash(usuario.getClave()));
            ps.setString(4, usuario.getRol());
            ps.setString(5, usuario.getTelefono());
            ps.setString(6, usuario.getCorreo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("crear usuario", e);
        }
    }

    public boolean actualizar(Usuario usuario) {
        boolean cambiarClave = usuario.getClave() != null && !usuario.getClave().isEmpty();
        String sql = cambiarClave
            ? "UPDATE usuarios SET nombre = ?, usuario = ?, clave = ?, rol = ?, telefono = ?, correo = ? WHERE id = ?"
            : "UPDATE usuarios SET nombre = ?, usuario = ?, rol = ?, telefono = ?, correo = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getUsuario());
            if (cambiarClave) {
                ps.setString(3, PasswordUtil.hash(usuario.getClave()));
                ps.setString(4, usuario.getRol());
                ps.setString(5, usuario.getTelefono());
                ps.setString(6, usuario.getCorreo());
                ps.setInt(7, usuario.getId());
            } else {
                ps.setString(3, usuario.getRol());
                ps.setString(4, usuario.getTelefono());
                ps.setString(5, usuario.getCorreo());
                ps.setInt(6, usuario.getId());
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("actualizar usuario", e);
        }
    }

    public int contarPorRol(String rol) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE rol = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rol);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("contar usuarios por rol", e);
        }
        return 0;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("eliminar usuario", e);
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("usuario"),
            rs.getString("clave"),
            rs.getString("rol"),
            rs.getString("telefono"),
            rs.getString("correo")
        );
    }
}
