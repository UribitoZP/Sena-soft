package com.santaana.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaManager {

    private static final int SCHEMA_VERSION = 3;

    public static void inicializar() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            int version = getUserVersion(conn);

            if (version < SCHEMA_VERSION) {
                migrar(conn, stmt);
                setUserVersion(conn, SCHEMA_VERSION);
            }

            System.out.println("Esquema v" + SCHEMA_VERSION + " listo.");

        } catch (SQLException e) {
            System.err.println("Error inicializando esquema: " + e.getMessage());
        }
    }

    private static void migrar(Connection conn, Statement stmt) throws SQLException {
        // Tablas que no cambian: crear si no existen
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

        // Habitaciones: recrear con constraint correcto usando el mismo stmt
        stmt.executeUpdate("DROP TABLE IF EXISTS habitaciones_old");

        // Verificar si la tabla existe usando el mismo statement
        ResultSet rs = stmt.executeQuery(
            "SELECT name FROM sqlite_master WHERE type='table' AND name='habitaciones'"
        );
        boolean existe = rs.next();
        rs.close();

        if (existe) {
            stmt.executeUpdate("ALTER TABLE habitaciones RENAME TO habitaciones_old");
            crearTablaHabitaciones(stmt);
            stmt.executeUpdate(
                "INSERT INTO habitaciones (id, numero, tipo, precio, estado) " +
                "SELECT id, numero, tipo, precio, " +
                "CASE WHEN estado IN ('Disponible','Ocupada','Mantenimiento','Limpieza') " +
                "     THEN estado ELSE 'Disponible' END " +
                "FROM habitaciones_old"
            );
            stmt.executeUpdate("DROP TABLE habitaciones_old");
            System.out.println("Migración: tabla habitaciones actualizada con estado Limpieza.");
        } else {
            crearTablaHabitaciones(stmt);
        }
    }

    private static void crearTablaHabitaciones(Statement stmt) throws SQLException {
        stmt.executeUpdate(
            "CREATE TABLE habitaciones (" +
            "  id       INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  numero   TEXT    NOT NULL UNIQUE," +
            "  tipo     TEXT    NOT NULL," +
            "  precio   REAL    NOT NULL," +
            "  estado   TEXT    NOT NULL DEFAULT 'Disponible'" +
            "       CHECK(estado IN ('Disponible','Ocupada','Mantenimiento','Limpieza'))" +
            ")"
        );
    }

    private static int getUserVersion(Connection conn) throws SQLException {
        ResultSet rs = conn.createStatement().executeQuery("PRAGMA user_version");
        return rs.next() ? rs.getInt(1) : 0;
    }

    private static void setUserVersion(Connection conn, int v) throws SQLException {
        conn.createStatement().executeUpdate("PRAGMA user_version = " + v);
    }
}
