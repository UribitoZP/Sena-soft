package com.santaana.main;

import com.santaana.db.SchemaManager;
import com.santaana.db.SeedData;
import com.santaana.server.RestServer;
import com.santaana.view.LoginFrame;
import javax.swing.SwingUtilities;

public class ApplicationMain {
    public static void main(String[] args) {
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
