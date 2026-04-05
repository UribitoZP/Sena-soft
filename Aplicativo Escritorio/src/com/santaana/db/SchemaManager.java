package com.santaana.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaManager {

    // Incrementar cuando cambie el esquema
    private static final int SCHEMA_VERSION = 2;

    public static void inicializar() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            int version = getUserVersion(conn);

            if (version < SCHEMA_VERSION) {
                migrar(stmt, version);
                setUserVersion(conn, SCHEMA_VERSION);
            }

            System.out.println("Esquema v" + SCHEMA_VERSION + " listo.");

        } catch (SQLException e) {
            System.err.println("Error inicializando esquema: " + e.getMessage());
        }
    }

    private static void migrar(Statement stmt, int desde) throws SQLException {
        if (desde < 1) {
            // Creacion inicial
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
                "       CHECK(estado IN ('Disponible','Ocupada','Mantenimiento','Limpieza'))" +
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
        }

        if (desde == 1) {
            // v1 → v2: agregar estado Limpieza a habitaciones
            // SQLite no permite ALTER TABLE para cambiar CHECK, se recrea la tabla
            stmt.executeUpdate("ALTER TABLE habitaciones RENAME TO habitaciones_old");
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
            stmt.executeUpdate(
                "INSERT INTO habitaciones SELECT id, numero, tipo, precio, estado " +
                "FROM habitaciones_old"
            );
            stmt.executeUpdate("DROP TABLE habitaciones_old");
            System.out.println("Migración v1→v2: estado Limpieza agregado.");
        }
    }

    private static int getUserVersion(Connection conn) throws SQLException {
        ResultSet rs = conn.createStatement().executeQuery("PRAGMA user_version");
        return rs.next() ? rs.getInt(1) : 0;
    }

    private static void setUserVersion(Connection conn, int v) throws SQLException {
        conn.createStatement().executeUpdate("PRAGMA user_version = " + v);
    }
}
