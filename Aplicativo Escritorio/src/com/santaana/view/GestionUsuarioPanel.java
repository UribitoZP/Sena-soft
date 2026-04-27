package com.santaana.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.MatteBorder;

import com.santaana.util.BackupManager;
import com.santaana.util.ThemeManager;

public class GestionUsuarioPanel extends JPanel implements ThemeManager.ThemeListener {

    private String role;

    private Color getPrimario() { return ThemeManager.getPrimary(); }
    private Color getFondo() { return ThemeManager.getBackground(); }
    private Color getPanelCol() { return ThemeManager.getPanelBackground(); }
    private Color getBorde() { return ThemeManager.getBorder(); }
    private Color getTextCol() { return ThemeManager.getTextPrimary(); }
    private Color getLabelCol() { return ThemeManager.getTextSecondary(); }

    public GestionUsuarioPanel(String role) {
        this.role = role;
        ThemeManager.addListener(this);
        setLayout(new BorderLayout());
        refreshUI();
    }

    private void refreshUI() {
        removeAll();
        setBackground(getFondo());
        add(crearNavbar(), BorderLayout.NORTH);
        add(listaUsuarios(), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    @Override
    public void onThemeChanged() {
        refreshUI();
    }

    //  NAVBAR
    private JPanel crearNavbar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(getPanelCol());
        navbar.setPreferredSize(new Dimension(0, 60));
        navbar.setBorder(new MatteBorder(0, 0, 1, 0, getBorde()));

        JLabel title = new JLabel("  GESTIÓN DE USUARIOS");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(getTextCol());

        JButton btnNuevo = new JButton("+ Nuevo usuario");
        btnNuevo.setBackground(getPrimario());
        btnNuevo.setForeground(Color.WHITE);
        btnNuevo.setFocusPainted(false);
        btnNuevo.setBorderPainted(false);
        btnNuevo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));


        JButton btnBackup = new JButton(" Backup");
        estilizarBotonSecundario(btnBackup);

        btnBackup.addActionListener(e -> {
            try {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Guardar Backup");

                int opcion = fileChooser.showSaveDialog(this);

                if (opcion == JFileChooser.APPROVE_OPTION) {
                    String ruta = fileChooser.getSelectedFile().getAbsolutePath() + ".csv";

                    BackupManager.exportarReservasCSV(ruta);

                    JOptionPane.showMessageDialog(this,
                            "✅ Backup generado correctamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "❌ Error al generar backup",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        right.setOpaque(false);
        right.add(btnNuevo);
        right.add(btnBackup);

        navbar.add(title, BorderLayout.WEST);
        navbar.add(right, BorderLayout.EAST);   

        return navbar;
    }

    //  LISTA DE USUARIOS 
    private JScrollPane listaUsuarios() {
        JPanel lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setOpaque(false);
        lista.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //  DATOS DE PRUEBA
        lista.add(userCard("Juan Pérez", "juan@mail.com", "Administrador", true));
        lista.add(Box.createVerticalStrut(10));

        lista.add(userCard("Ana Gómez", "ana@mail.com", "Recepcionista", true));
        lista.add(Box.createVerticalStrut(10));

        lista.add(userCard("Carlos Ruiz", "carlos@mail.com", "Recepcionista", false));

        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        return scroll;
    }

    // CARD USUARIO
    private JPanel userCard(String nombre, String correo, String rol, boolean activo) {

        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(getPanelCol());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(getBorde(), 1, true),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        //  INFO
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel lblNombre = new JLabel(nombre);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNombre.setForeground(getTextCol());

        JLabel lblCorreo = new JLabel(correo);
        lblCorreo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCorreo.setForeground(getLabelCol());

        info.add(lblNombre);
        info.add(Box.createVerticalStrut(3));
        info.add(lblCorreo);

        //  ROL + ESTADO
        JPanel estadoPanel = new JPanel();
        estadoPanel.setLayout(new BoxLayout(estadoPanel, BoxLayout.Y_AXIS));
        estadoPanel.setOpaque(false);

        JLabel lblRol = new JLabel(rol);
        lblRol.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRol.setForeground(
            rol.equals("Administrador") ? new Color(0xE74C3C) : new Color(0x3498DB)
        );

        JLabel lblEstado = new JLabel(activo ? "Activo" : "Inactivo");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblEstado.setForeground(
            activo ? new Color(0x27AE60) : new Color(0x95A5A6)
        );

        estadoPanel.add(lblRol);
        estadoPanel.add(Box.createVerticalStrut(5));
        estadoPanel.add(lblEstado);

        //  ACCIONES
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");

        estilizarBoton(btnEditar, getPrimario());
        estilizarBoton(btnEliminar, new Color(0xE74C3C));

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        acciones.add(btnEditar);
        acciones.add(btnEliminar);

        card.add(info, BorderLayout.WEST);
        card.add(estadoPanel, BorderLayout.CENTER);
        card.add(acciones, BorderLayout.EAST);

        return card;
    }

    //  BOTONES BONITOS
    private void estilizarBoton(JButton b, Color bg) {
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(90, 28));
    }

    private void estilizarBotonSecundario(JButton b) {
        b.setBackground(ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT 
                ? new Color(0xE8F1FD) 
                : new Color(0x334155));
        b.setForeground(getPrimario());
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(110, 32));
    }
}