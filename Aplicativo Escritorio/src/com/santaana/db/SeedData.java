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

            // Usuarios - CORREGIDO: Se cerró la comilla de 'admin' y se añadieron telefono y correo
            stmt.executeUpdate("INSERT INTO usuarios (nombre, usuario, clave, rol, telefono, correo) VALUES " +
                "('---- Admin',    'admin',    '1234', 'Administrador', '3000000000', 'admin@hotelsantaana.com')," +
                "('---- Recepcion', 'recepcion', '1234', 'Recepcionista',  '3000000001', 'recepcion@hotelsantaana.com')");

            // Habitaciones
            stmt.executeUpdate("INSERT INTO habitaciones (numero, tipo, precio, estado) VALUES " +
                "('101', 'Simple', 120000, 'Disponible')," +
                "('102', 'Simple', 120000, 'Disponible')," +
                "('103', 'Simple', 120000, 'Disponible')," +
                "('104', 'Simple', 120000, 'Disponible')," +
                "('105', 'Simple', 120000, 'Disponible')," +
                "('106', 'Simple', 120000, 'Disponible')," +
                "('207', 'Doble',  200000, 'Disponible')," +
                "('208', 'Doble',  200000, 'Disponible')," +
                "('209', 'Doble',  200000, 'Disponible')," +
                "('210', 'Doble',  200000, 'Disponible')," +
                "('211', 'Doble',  200000, 'Disponible')," +
                "('212', 'Doble',  200000, 'Disponible')," +
                "('213', 'Doble',  200000, 'Disponible')," +
                "('214', 'Doble',  200000, 'Disponible')," +
                "('215', 'Doble',  200000, 'Disponible')");

            System.out.println("Datos iniciales insertados con éxito.");

        } catch (SQLException e) {
            System.err.println("Error insertando seed: " + e.getMessage());
        }
    }
}
