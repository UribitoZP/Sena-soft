package com.santaana.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import com.santaana.view.LoginFrame;
import com.santaana.view.MainFrame;
import com.santaana.view.NotificacionFrame;
import com.santaana.view.ReservaFrame;
import com.santaana.view.TableroFrame;


public class LoginController {
    private LoginFrame view;

    public LoginController(LoginFrame view) {
        this.view = view;
        this.view.addLoginListener(new LoginListener());
    }

    private class LoginListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String role = view.getSelectedRole();
            String username = view.getUsername();
            String password = view.getPassword();

            // 1. Validacion de Seleccion
            if (role.equals("Seleccionar...")) {
                view.showMessage("Por favor, seleccione un rol antes de ingresar.", "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (username.isEmpty() || password.isEmpty()) {
                view.showMessage("Por favor, complete todos los campos.", "Campos vacíos", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. Captura de Perfil y Despacho de Vistas
            processLogin(role, username);
        }
    }

    private void processLogin(String role, String username) {
        String welcomeMessage = "Bienvenido al sistema, " + username + ".";
        
        // Siempre abrir MainFrame, pero con el rol correspondiente
        MainFrame mainFrame = new MainFrame(role, welcomeMessage);
        mainFrame.setVisible(true);

        view.dispose(); // Cerrar ventana de login
    }
}
