package com.santaana.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:santaana.db";
    private static Connection instance;

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
                instance = DriverManager.getConnection(DB_URL);
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
