package com.santaana.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DB_URL;
    private static Connection instance;

    static {
        DB_URL = inicializarDbUrl();
    }

    private DatabaseConnection() {}

    private static String inicializarDbUrl() {
        try {
            File dirClases = new File(
                DatabaseConnection.class
                    .getProtectionDomain().getCodeSource().getLocation()
                    .toURI()
            ).getAbsoluteFile();

            // Si los .class están en bin/, la DB va en la raíz del proyecto
            File dbFile;
            if (dirClases.isDirectory() && dirClases.getName().equalsIgnoreCase("bin")) {
                dbFile = new File(dirClases.getParentFile(), "santaana.db");
            } else {
                dbFile = new File(dirClases.getParentFile(), "santaana.db");
            }

            return "jdbc:sqlite:" + dbFile.getAbsolutePath();
        } catch (Exception e) {
            // Fallback: directorio actual
            return "jdbc:sqlite:santaana.db";
        }
    }

    public static synchronized Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
                instance = DriverManager.getConnection(DB_URL);
                instance.createStatement().execute("PRAGMA foreign_keys = ON");
                instance.setAutoCommit(true);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver SQLite no encontrado", e);
            }
        }
        return instance;
    }

    public static void cerrar() {
        try {
            if (instance != null && !instance.isClosed()) {
                instance.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar conexion: " + e.getMessage());
        }
    }
}
