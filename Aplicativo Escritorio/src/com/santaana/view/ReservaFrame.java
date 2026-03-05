package com.santaana.view;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ReservaFrame extends JFrame {

    private static final Color COLOR_PRIMARIO = new Color(30, 90, 160);
    private static final Color COLOR_FONDO    = new Color(245, 247, 250);
    private static final Color COLOR_PANEL    = Color.WHITE;
    private static final Color COLOR_BORDE    = new Color(200, 210, 225);
    private static final Color COLOR_TEXTO    = new Color(40, 50, 70);

    public ReservaFrame() {
        configurarVentana();
        add(crearNavbar(), BorderLayout.NORTH);
        setVisible(true);
    }

    private void configurarVentana() {
        setTitle("Hotel Santa Ana — Reserva");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 780);
        setMinimumSize(new Dimension(860, 700));
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout(0, 0));
    }

    private JPanel crearNavbar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(COLOR_PRIMARIO);
        navbar.setPreferredSize(new Dimension(0, 52));
        navbar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // Logo / nombre del hotel
        JLabel logo = new JLabel("HOTEL SANTA ANA");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        navbar.add(logo, BorderLayout.WEST);

        // Lado derecho: botones + usuario
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        right.setOpaque(false);

        JButton btnNueva  = crearBotonNavbar("+ Nueva reserva", new Color(255, 140, 0));
        JButton btnVentas = crearBotonNavbar("$ Venta rápida",  new Color(80, 130, 200));

        JLabel lblUser = new JLabel("  Julio Mata  ");
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        right.add(btnNueva);
        right.add(btnVentas);
        right.add(lblUser);
        navbar.add(right, BorderLayout.EAST);

        return navbar;
    }

    private JButton crearBotonNavbar(String texto, Color bg) {
        JButton btn = new JButton(texto);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 30));
        return btn;
    }
}

