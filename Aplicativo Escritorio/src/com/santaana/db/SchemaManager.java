package com.santaana.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaManager {

    private static final int SCHEMA_VERSION = 6;

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

            // v5: anticipo en reservas (idempotente)
            boolean tieneAnticipo = false;
            ResultSet ca = stmt.executeQuery("PRAGMA table_info(reservas)");
            while (ca.next()) if ("anticipo".equals(ca.getString("name"))) tieneAnticipo = true;
            ca.close();
            if (!tieneAnticipo) {
                stmt.executeUpdate("ALTER TABLE reservas ADD COLUMN anticipo REAL DEFAULT 0");
                System.out.println("Migración v5: columna anticipo añadida a reservas.");
            }

            // v6a: id_usuario en historial (idempotente)
            boolean tieneIdUsuario = false;
            ResultSet ch = stmt.executeQuery("PRAGMA table_info(historial)");
            while (ch.next()) if ("id_usuario".equals(ch.getString("name"))) tieneIdUsuario = true;
            ch.close();
            if (!tieneIdUsuario) {
                stmt.executeUpdate("ALTER TABLE historial ADD COLUMN id_usuario INTEGER DEFAULT 0");
                System.out.println("Migración v6a: columna id_usuario añadida a historial.");
            }

            // v6b: tabla cierres_mes
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS cierres_mes (" +
                "  id               INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  mes              TEXT    NOT NULL UNIQUE," +
                "  id_usuario       INTEGER NOT NULL REFERENCES usuarios(id)," +
                "  fecha_cierre     TEXT    NOT NULL DEFAULT (datetime('now','localtime'))," +
                "  total_ingresos   REAL    NOT NULL DEFAULT 0," +
                "  total_reservas   INTEGER NOT NULL DEFAULT 0," +
                "  total_completadas INTEGER NOT NULL DEFAULT 0," +
                "  total_canceladas INTEGER NOT NULL DEFAULT 0," +
                "  notas            TEXT    DEFAULT ''" +
                ")"
            );

            // v6c: tabla clientes (idempotente para casos sin migración completa)
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS clientes (" +
                "  id          INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  nombre      TEXT    NOT NULL," +
                "  documento   TEXT    UNIQUE NOT NULL," +
                "  telefono    TEXT," +
                "  correo      TEXT" +
                ")"
            );

            // v6d: migrar reservas del esquema antiguo (cliente_nombre/cliente_doc) al nuevo (id_cliente)
            boolean tieneClienteNombre = false;
            ResultSet crn = stmt.executeQuery("PRAGMA table_info(reservas)");
            while (crn.next()) if ("cliente_nombre".equals(crn.getString("name"))) tieneClienteNombre = true;
            crn.close();
            if (tieneClienteNombre) {
                stmt.executeUpdate(
                    "INSERT OR IGNORE INTO clientes (nombre, documento) " +
                    "SELECT DISTINCT cliente_nombre, cliente_doc FROM reservas " +
                    "WHERE cliente_nombre IS NOT NULL AND cliente_doc IS NOT NULL"
                );
                stmt.executeUpdate("CREATE TABLE reservas_v2 (" +
                    "  id              INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "  id_habitacion   INTEGER NOT NULL REFERENCES habitaciones(id)," +
                    "  id_usuario      INTEGER NOT NULL REFERENCES usuarios(id)," +
                    "  id_cliente      INTEGER NOT NULL REFERENCES clientes(id)," +
                    "  fecha_entrada   TEXT    NOT NULL," +
                    "  hora_entrada    TEXT    DEFAULT '12:00'," +
                    "  fecha_salida    TEXT    NOT NULL," +
                    "  hora_salida     TEXT    DEFAULT '12:00'," +
                    "  tipo_estadia    TEXT    DEFAULT 'Noche'," +
                    "  anticipo        REAL    DEFAULT 0," +
                    "  estado          TEXT    NOT NULL DEFAULT 'Activa' " +
                    "       CHECK(estado IN ('Activa','Completada','Cancelada'))" +
                    ")"
                );
                stmt.executeUpdate(
                    "INSERT INTO reservas_v2 " +
                    "SELECT r.id, r.id_habitacion, r.id_usuario, " +
                    "       COALESCE((SELECT c.id FROM clientes c WHERE c.documento = r.cliente_doc), 0), " +
                    "       r.fecha_entrada, COALESCE(r.hora_entrada, '12:00'), " +
                    "       r.fecha_salida, COALESCE(r.hora_salida, '12:00'), " +
                    "       COALESCE(r.tipo_estadia, 'Noche'), COALESCE(r.anticipo, 0), r.estado " +
                    "FROM reservas r"
                );
                stmt.executeUpdate("DROP TABLE reservas");
                stmt.executeUpdate("ALTER TABLE reservas_v2 RENAME TO reservas");
                System.out.println("Migración v6d: tabla reservas migrada a nuevo esquema con id_cliente.");
            } else {
                boolean tieneIdCliente = false;
                ResultSet cr = stmt.executeQuery("PRAGMA table_info(reservas)");
                while (cr.next()) if ("id_cliente".equals(cr.getString("name"))) tieneIdCliente = true;
                cr.close();
                if (!tieneIdCliente) {
                    stmt.executeUpdate("ALTER TABLE reservas ADD COLUMN id_cliente INTEGER REFERENCES clientes(id)");
                    System.out.println("Migración v6d: columna id_cliente añadida a reservas.");
                }
            }

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

        // v5: campo anticipo en reservas
        boolean tieneAnticipo = false;
        ResultSet cols5 = stmt.executeQuery("PRAGMA table_info(reservas)");
        while (cols5.next()) {
            if ("anticipo".equals(cols5.getString("name"))) { tieneAnticipo = true; }
        }
        cols5.close();
        if (!tieneAnticipo) {
            stmt.executeUpdate("ALTER TABLE reservas ADD COLUMN anticipo REAL DEFAULT 0");
            System.out.println("Migración v5: columna anticipo añadida a reservas.");
        }

        // v6: normalización de base de datos (clientes y reservas)
        if (fromVersion < 6) {
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS clientes (" +
                "  id          INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  nombre      TEXT    NOT NULL," +
                "  documento   TEXT    UNIQUE NOT NULL," +
                "  telefono    TEXT," +
                "  correo      TEXT" +
                ")"
            );

            // Recreamos la tabla reservas
            stmt.executeUpdate("DROP TABLE IF EXISTS reservas");
            stmt.executeUpdate(
                "CREATE TABLE reservas (" +
                "  id              INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  id_habitacion   INTEGER NOT NULL REFERENCES habitaciones(id)," +
                "  id_usuario      INTEGER NOT NULL REFERENCES usuarios(id)," +
                "  id_cliente      INTEGER NOT NULL REFERENCES clientes(id)," +
                "  fecha_entrada   TEXT    NOT NULL," +
                "  hora_entrada    TEXT    DEFAULT '12:00'," +
                "  fecha_salida    TEXT    NOT NULL," +
                "  hora_salida     TEXT    DEFAULT '12:00'," +
                "  tipo_estadia    TEXT    DEFAULT 'Noche'," +
                "  anticipo        REAL    DEFAULT 0," +
                "  estado          TEXT    NOT NULL DEFAULT 'Activa' " +
                "       CHECK(estado IN ('Activa','Completada','Cancelada'))" +
                ")"
            );
            System.out.println("Migración v6: tabla clientes creada y tabla reservas normalizada.");
        }
    }
}
