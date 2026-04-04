package com.santaana.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaManager {

    public static void inicializar() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS usuarios (" +
                "  id       INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  nombre   TEXT    NOT NULL," +
                "  usuario  TEXT    NOT NULL UNIQUE," +
                "  clave    TEXT    NOT NULL," +
                "  rol      TEXT    NOT NULL CHECK(rol IN ('Administrador','Recepcionista'))" +
                ")"
            );

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS habitaciones (" +
                "  id       INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  numero   TEXT    NOT NULL UNIQUE," +
                "  tipo     TEXT    NOT NULL," +
                "  precio   REAL    NOT NULL," +
                "  estado   TEXT    NOT NULL DEFAULT 'Disponible'" +
                "       CHECK(estado IN ('Disponible','Ocupada','Mantenimiento'))" +
                ")"
            );

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS reservas (" +
                "  id              INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  id_habitacion   INTEGER NOT NULL REFERENCES habitaciones(id)," +
                "  id_usuario      INTEGER NOT NULL REFERENCES usuarios(id)," +
                "  cliente_nombre  TEXT    NOT NULL," +
                "  cliente_doc     TEXT    NOT NULL," +
                "  fecha_entrada   TEXT    NOT NULL," +
                "  fecha_salida    TEXT    NOT NULL," +
                "  estado          TEXT    NOT NULL DEFAULT 'Activa'" +
                "       CHECK(estado IN ('Activa','Completada','Cancelada'))" +
                ")"
            );

            System.out.println("Esquema inicializado correctamente.");

        } catch (SQLException e) {
            System.err.println("Error inicializando esquema: " + e.getMessage());
        }
    }
}
