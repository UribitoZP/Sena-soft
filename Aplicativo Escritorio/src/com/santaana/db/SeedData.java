package com.santaana.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SeedData {

    public static void insertar() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM usuarios");
            if (rs.next() && rs.getInt(1) > 0) return; // ya tiene datos

            // Usuarios
            stmt.executeUpdate("INSERT INTO usuarios (nombre, usuario, clave, rol) VALUES " +
                "('Carlos Admin',    'admin',       '1234', 'Administrador')," +
                "('Laura Recepcion', 'recepcion',   '1234', 'Recepcionista')");

            // Habitaciones
            stmt.executeUpdate("INSERT INTO habitaciones (numero, tipo, precio, estado) VALUES " +
                "('101', 'Simple',    120000, 'Disponible')," +
                "('102', 'Simple',    120000, 'Ocupada')," +
                "('201', 'Doble',     200000, 'Disponible')," +
                "('202', 'Doble',     200000, 'Mantenimiento')," +
                "('301', 'Suite',     350000, 'Disponible')," +
                "('302', 'Suite',     350000, 'Disponible')");

            System.out.println("Datos iniciales insertados.");

        } catch (SQLException e) {
            System.err.println("Error insertando seed: " + e.getMessage());
        }
    }
}
