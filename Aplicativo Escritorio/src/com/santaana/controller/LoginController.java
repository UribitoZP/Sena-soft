package com.santaana.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import com.santaana.dao.UsuarioDAO;
import com.santaana.db.DatabaseException;
import com.santaana.model.Usuario;
import com.santaana.util.ErrorUtil;
import com.santaana.view.LoginFrame;
import com.santaana.view.MainFrame;

public class LoginController {
    private LoginFrame view;
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public LoginController(LoginFrame view) {
        this.view = view;
        this.view.addLoginListener(new LoginListener());
    }

    private class LoginListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                ejecutarLogin();
            } catch (DatabaseException ex) {
                ErrorUtil.mostrarError(view, "iniciar sesión", ex);
            }
        }

        private void ejecutarLogin() {
            String rol      = view.getSelectedRole();
            String username = view.getUsername().trim();
            String password = view.getPassword();

            if (rol.equals("Seleccionar...")) {
                view.showMessage("Por favor, seleccione un rol antes de ingresar.",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (username.isEmpty() || password.isEmpty()) {
                view.showMessage("Por favor, complete todos los campos.",
                        "Campos vacíos", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Usuario usuario = usuarioDAO.autenticar(username, password, rol);

            if (usuario == null) {
                view.showMessage("Credenciales incorrectas o rol no coincide.",
                        "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                return;
            }

            view.clearPassword();

            String bienvenida = "Bienvenido al sistema, " + usuario.getNombre() + ".";
            MainFrame mainFrame = new MainFrame(usuario.getRol(), bienvenida, usuario.getId());
            mainFrame.setVisible(true);
            view.dispose();
        }
    }
}
