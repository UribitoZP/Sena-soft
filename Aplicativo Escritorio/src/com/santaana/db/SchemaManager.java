package com.santaana.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaManager {

    private static final int SCHEMA_VERSION = 4;

    public static void inicializar() {
        Connection conn = null;
        Statement  stmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();

            int version = 0;
            ResultSet rv = stmt.executeQuery("PRAGMA user_version");
            if (rv.next()) version = rv.getInt(1);
            rv.close();

            if (version < SCHEMA_VERSION) {
                migrar(conn, stmt, version);
                stmt.executeUpdate("PRAGMA user_version = " + SCHEMA_VERSION);
            }

            // Garantizar tabla historial siempre presente (idempotente)
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS historial (" +
                "  id          INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  tipo        TEXT    NOT NULL," +
                "  titulo      TEXT    NOT NULL," +
                "  descripcion TEXT    NOT NULL," +
                "  fecha_hora  TEXT    NOT NULL DEFAULT (datetime('now','localtime'))" +
                ")"
            );

            System.out.println("Esquema v" + SCHEMA_VERSION + " listo.");

        } catch (SQLException e) {
            System.err.println("Error inicializando esquema: " + e.getMessage());
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }
    }

    private static void migrar(Connection conn, Statement stmt, int fromVersion) throws SQLException {
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

        stmt.executeUpdate("DROP TABLE IF EXISTS habitaciones_old");

        // Verificar si habitaciones existe
        ResultSet rs = stmt.executeQuery(
            "SELECT name FROM sqlite_master WHERE type='table' AND name='habitaciones'"
        );
        boolean existe = rs.next();
        rs.close();

        if (existe) {
            stmt.executeUpdate("ALTER TABLE habitaciones RENAME TO habitaciones_old");
        }

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

        if (existe) {
            stmt.executeUpdate(
                "INSERT INTO habitaciones (id, numero, tipo, precio, estado) " +
                "SELECT id, numero, tipo, precio, " +
                "CASE WHEN estado IN ('Disponible','Ocupada','Mantenimiento','Limpieza') " +
                "     THEN estado ELSE 'Disponible' END " +
                "FROM habitaciones_old"
            );
            stmt.executeUpdate("DROP TABLE habitaciones_old");
            System.out.println("Migración: tabla habitaciones actualizada con estado Limpieza.");
        }

        // v4: hora y tipo de estadía en reservas
        boolean tieneHoraEnt = false;
        ResultSet cols = stmt.executeQuery("PRAGMA table_info(reservas)");
        while (cols.next()) {
            if ("hora_entrada".equals(cols.getString("name"))) { tieneHoraEnt = true; }
        }
        cols.close();
        if (!tieneHoraEnt) {
            stmt.executeUpdate("ALTER TABLE reservas ADD COLUMN hora_entrada  TEXT DEFAULT '12:00'");
            stmt.executeUpdate("ALTER TABLE reservas ADD COLUMN hora_salida   TEXT DEFAULT '12:00'");
            stmt.executeUpdate("ALTER TABLE reservas ADD COLUMN tipo_estadia  TEXT DEFAULT 'Noche'");
            System.out.println("Migración v4: columnas hora y tipo_estadia añadidas a reservas.");
        }
    }
}
