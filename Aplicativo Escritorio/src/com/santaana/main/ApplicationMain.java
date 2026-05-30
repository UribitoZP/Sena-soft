package com.santaana.main;

import com.santaana.db.DatabaseException;
import com.santaana.db.SchemaManager;
import com.santaana.db.SeedData;
import com.santaana.server.RestServer;
import com.santaana.view.LoginFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class ApplicationMain {
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            if (e instanceof DatabaseException) {
                System.err.println("[DatabaseException no capturado] " + e.getMessage());
                e.getCause().printStackTrace();
                SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(null,
                        "Error inesperado de base de datos:\n" + e.getMessage() +
                        "\n\nLa aplicación se cerrará.",
                        "Error Crítico", JOptionPane.ERROR_MESSAGE)
                );
            } else {
                System.err.println("[Error no capturado] " + e.getMessage());
                e.printStackTrace();
            }
        });
        SchemaManager.inicializar();
        SeedData.insertar();

        RestServer.iniciar();
        Runtime.getRuntime().addShutdownHook(new Thread(RestServer::detener));

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
