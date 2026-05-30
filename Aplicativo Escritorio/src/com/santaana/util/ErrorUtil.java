package com.santaana.util;

import com.santaana.db.DatabaseException;
import javax.swing.JOptionPane;
import java.awt.Component;

public class ErrorUtil {

    public static void mostrarError(Component parent, String accion, DatabaseException e) {
        String msg = String.format(
            "Error al %s.\n\nDetalle: %s\n\nLa aplicación continuará funcionando, pero algunos datos podrían no estar disponibles.",
            accion, e.getMessage()
        );
        System.err.println("[DatabaseException] " + accion + ": " + e.getMessage());
        e.getCause().printStackTrace();
        JOptionPane.showMessageDialog(parent, msg, "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
    }

    public static void mostrarError(Component parent, String accion, Exception e) {
        String msg = String.format(
            "Error al %s.\n\nDetalle: %s",
            accion, e.getMessage()
        );
        System.err.println("[Error] " + accion + ": " + e.getMessage());
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
