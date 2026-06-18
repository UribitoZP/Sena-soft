package com.santaana.db;

import com.santaana.util.PasswordUtil;
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

            String hashAdmin = PasswordUtil.hash("1234");
            String hashRecep = PasswordUtil.hash("1234");
            stmt.executeUpdate("INSERT INTO usuarios (nombre, usuario, clave, rol, telefono, correo) VALUES " +
                "('---- Admin',    'admin',    '" + hashAdmin + "', 'Administrador', '3000000000', 'admin@hotelsantaana.com')," +
                "('---- Recepcion', 'recepcion', '" + hashRecep + "', 'Recepcionista',  '3000000001', 'recepcion@hotelsantaana.com')");

            // Habitaciones
            stmt.executeUpdate("INSERT INTO habitaciones (numero, tipo, precio, precio_bloque, estado) VALUES " +
                "('101', 'Simple', 120000, 15000, 'Disponible')," +
                "('102', 'Simple', 120000, 15000, 'Disponible')," +
                "('103', 'Simple', 120000, 15000, 'Disponible')," +
                "('104', 'Simple', 120000, 15000, 'Disponible')," +
                "('105', 'Simple', 120000, 15000, 'Disponible')," +
                "('106', 'Simple', 120000, 15000, 'Disponible')," +
                "('207', 'Doble',  200000, 25000, 'Disponible')," +
                "('208', 'Doble',  200000, 25000, 'Disponible')," +
                "('209', 'Doble',  200000, 25000, 'Disponible')," +
                "('210', 'Doble',  200000, 25000, 'Disponible')," +
                "('211', 'Doble',  200000, 25000, 'Disponible')," +
                "('212', 'Doble',  200000, 25000, 'Disponible')," +
                "('213', 'Doble',  200000, 25000, 'Disponible')," +
                "('214', 'Doble',  200000, 25000, 'Disponible')," +
                "('215', 'Doble',  200000, 25000, 'Disponible')");

            System.out.println("Datos iniciales insertados con éxito.");

        } catch (SQLException e) {
            System.err.println("Error insertando seed: " + e.getMessage());
        }
    }
}
