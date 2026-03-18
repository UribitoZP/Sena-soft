package com.santaana.view;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

public class InfoHabitacionFrame extends JFrame {

    private String role;

    private static final Color COLOR_PRIMARIO = new Color(0x3A7BD5);
    private static final Color COLOR_FONDO    = new Color(0xF0F6FF);
    private static final Color COLOR_PANEL    = Color.WHITE;
    private static final Color COLOR_BORDE    = new Color(0xEAF2FB);
    private static final Color COLOR_TEXTO    = new Color(40, 50, 70);
    private static final Color COLOR_VERDE    = new Color(0, 170, 90);
    private static final Color COLOR_LABEL    = new Color(110, 120, 140);
    private static final Color COLOR_AMARILLO = new Color(0xFFD600);

    //Datos de la reserva ( vendrían por constructor)
    private String numeroHabitacion = "07";
    private String huesped          = "Carlos Elles";
    private String serviciosExtra   = "Lavandería, servicio a la habitación";
    private String tipoPago         = "Tarjeta";
    private String pagoAnticipado   = "$0";
    private String cargosExtra      = "$50.000";
    private String telefono         = "3024567324";
    private String correo           = "KrlosEllz@gmail.com";
    private String tipoEstadia       = "Por hora";
    private String fechaEntrada     = "02/02/2026 1:15 pm";
    private String fechaSalida      = "02/02/2026 12:45 pm";
    private String totalPago        = "$15.000";

    //Constructor
    public InfoHabitacionFrame(String role, String welcomeMessage) {
        this.role = role;

        setTitle("Hotel Santa Ana — Información de la Habitación");
        setSize(1280, 800);
        setMinimumSize(new Dimension(1100, 720));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(crearNavbar(),    BorderLayout.NORTH);
        add(sidebar(),        BorderLayout.WEST);
        add(crearContenido(), BorderLayout.CENTER);

        setVisible(true);
    }


    //  NAVBAR
    private JPanel crearNavbar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(Color.WHITE);
        navbar.setPreferredSize(new Dimension(0, 60));
        navbar.setBorder(new MatteBorder(0, 0, 1, 0, COLOR_BORDE));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        left.setOpaque(false);

        JLabel logo = new JLabel();
        try {
            ImageIcon icon = new ImageIcon("resources/logo.png");
            Image scaled = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            logo.setIcon(new ImageIcon(scaled));
        } catch (Exception ignored) {}

        JLabel nombre = new JLabel("<html><b>HOTEL SANTA ANA</b><br>"
                + "<span style='font-size:9px;color:#6B84A0'>Sistema de gestión hotelera</span></html>");

        JLabel notifLbl = new JLabel("🔔");
        notifLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 17));
        notifLbl.setForeground(COLOR_PRIMARIO);

        left.add(logo);
        left.add(nombre);
        left.add(notifLbl);

        JPanel mid = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 12));
        mid.setOpaque(false);
        mid.add(crearBotonNavbar("+ Nueva reserva", COLOR_PRIMARIO, Color.WHITE));
        mid.add(crearBotonNavbar("Venta rápida", new Color(0xE8F1FD), COLOR_PRIMARIO));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 14));
        right.setOpaque(false);
        right.add(userPanel());

        navbar.add(left,  BorderLayout.WEST);
        navbar.add(mid,   BorderLayout.CENTER);
        navbar.add(right, BorderLayout.EAST);
        return navbar;
    }

    private JButton crearBotonNavbar(String texto, Color bg, Color fg) {
        JButton btn = new JButton(texto);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 34));
        return btn;
    }

    private JPanel userPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel name = new JLabel("Usuario");
        name.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JLabel rol = new JLabel(role);
        rol.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        rol.setForeground(COLOR_LABEL);

        p.add(name);
        p.add(rol);
        return p;
    }

    
    //  SIDEBAR
    private JPanel sidebar() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(Color.WHITE);
        side.setPreferredSize(new Dimension(190, 0));
        side.setBorder(new MatteBorder(0, 0, 0, 1, COLOR_BORDE));

        side.add(Box.createVerticalStrut(20));

        String[] items = {
            "Tablero",
            "Gestión de Habitaciones",
            "Reserva",
            "Punto de venta",
            "Historial",
            "Reporte"
        };

        for (int i = 0; i < items.length; i++) {
            side.add(sideBtn(items[i], i == 1));
            side.add(Box.createVerticalStrut(8));
        }

        side.add(Box.createVerticalGlue());
        return side;
    }

    private JPanel sideBtn(String text, boolean active) {
        JPanel p = new JPanel(new BorderLayout());
        p.setMaximumSize(new Dimension(180, 36));
        p.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 8));
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 12));
        lbl.setForeground(active ? COLOR_PRIMARIO : COLOR_LABEL);

        p.setBackground(active ? new Color(0xE8F1FD) : Color.WHITE);
        p.add(lbl, BorderLayout.CENTER);
        return p;
    }

    private JScrollPane crearContenido() {
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(COLOR_FONDO);
        main.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        main.add(buildHabitacionHeader());
        main.add(Box.createVerticalStrut(20));
        main.add(buildHuespedHeader());
        main.add(Box.createVerticalStrut(18));
        main.add(buildDatosReservaCard());
        main.add(Box.createVerticalStrut(24));
        main.add(buildBotonFinalizar());
        main.add(Box.createVerticalStrut(10));

        JScrollPane scroll = new JScrollPane(main);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(COLOR_FONDO);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scroll;
    }


    private JPanel buildHabitacionHeader() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        p.setOpaque(false);

        JPanel imgBox = new JPanel(new BorderLayout());
        imgBox.setPreferredSize(new Dimension(60, 60));
        imgBox.setBackground(new Color(0xDDE8F7));
        imgBox.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));

        try {
            ImageIcon ico = new ImageIcon(getClass().getResource("/resources/habitacion1.png"));
            Image scaled = ico.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            imgBox.add(new JLabel(new ImageIcon(scaled)), BorderLayout.CENTER);
        } catch (Exception e) {
            JLabel x = new JLabel(" ", SwingConstants.CENTER);
            x.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
            imgBox.add(x, BorderLayout.CENTER);
        }

        JLabel titulo = new JLabel("Habitación " + numeroHabitacion);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(COLOR_PRIMARIO);

        p.add(imgBox);
        p.add(titulo);
        
        return p;
    }

    private JPanel buildHuespedHeader() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel lbl = new JLabel("Huésped titular : " + huesped);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(COLOR_TEXTO);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(COLOR_BORDE);
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(lbl);
        p.add(Box.createVerticalStrut(6));
        p.add(sep);
        return p;
    }

    private JPanel buildDatosReservaCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(COLOR_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titulo = new JLabel("Datos de reserva");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(COLOR_PRIMARIO);
        card.setMaximumSize(new Dimension(800, Integer.MAX_VALUE));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createVerticalStrut(14));

        card.add(buildFilaDoble(
                "Servicios extra adquiridos:", serviciosExtra,
                "Tipo de pago:", tipoPago));
        card.add(Box.createVerticalStrut(10));

        card.add(buildFilaSimple("Pago anticipado:", pagoAnticipado));
        card.add(Box.createVerticalStrut(8));

        card.add(buildFilaCargos());
        card.add(Box.createVerticalStrut(12));

        card.add(separador());
        card.add(Box.createVerticalStrut(12));

        card.add(buildFilaSimple("Número de teléfono:", telefono));
        card.add(Box.createVerticalStrut(8));

        card.add(buildFilaSimple("Correo electrónico:", correo));
        card.add(Box.createVerticalStrut(8));

        card.add(buildFilaSimple("Tipo de estadía:", tipoEstadia));
        card.add(Box.createVerticalStrut(14));

        card.add(separador());
        card.add(Box.createVerticalStrut(12));

        card.add(buildFilaFecha("Fecha y hora de entrada:", fechaEntrada));
        card.add(Box.createVerticalStrut(10));
        card.add(buildFilaFecha("Fecha y hora de salida:", fechaSalida));
        card.add(Box.createVerticalStrut(14));

        card.add(separador());
        card.add(Box.createVerticalStrut(12));

        card.add(buildFilaTotal());

        return card;
    }

    //Botón
    private JPanel buildBotonFinalizar() {
        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrap.setOpaque(false);
        wrap.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btn = new JButton("Finalizar hospedaje y realizar cobro");
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(COLOR_TEXTO);
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(COLOR_TEXTO, 1));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(300, 36));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(COLOR_PRIMARIO);
                btn.setForeground(Color.WHITE);
                btn.setBorder(BorderFactory.createLineBorder(COLOR_PRIMARIO, 1));
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(Color.WHITE);
                btn.setForeground(COLOR_TEXTO);
                btn.setBorder(BorderFactory.createLineBorder(COLOR_TEXTO, 1));
            }
        });

        wrap.add(btn);
        return wrap;
    }

    //filas

    private JPanel buildFilaDoble(String l1, String v1, String l2, String v2) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(labelGris(l1));
        row.add(Box.createHorizontalStrut(6));
        row.add(valorNormal(v1));
        row.add(Box.createHorizontalStrut(40));
        row.add(labelGris(l2));
        row.add(Box.createHorizontalStrut(6));
        row.add(valorNormal(v2));
        return row;
    }

    private JPanel buildFilaSimple(String labelTxt, String valorTxt) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(labelGris(labelTxt));
        row.add(Box.createHorizontalStrut(6));
        row.add(valorNormal(valorTxt));
        return row;
    }

    private JPanel buildFilaCargos() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(labelGris("Cargos extra:"));
        row.add(Box.createHorizontalStrut(6));
        row.add(valorNormal(cargosExtra));
        row.add(Box.createHorizontalStrut(8));

        return row;
    }

    private JPanel buildFilaFecha(String labelTxt, String valor) {
    JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    row.setOpaque(false);
    row.setAlignmentX(Component.LEFT_ALIGNMENT);

    row.add(labelGris(labelTxt));
    row.add(Box.createHorizontalStrut(6));
    row.add(valorNormal(valor));
    row.add(Box.createHorizontalStrut(6));

    return row;
}

    private JPanel buildFilaTotal() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(labelGris("Total de pago:"));
        row.add(Box.createHorizontalStrut(6));

        JLabel val = new JLabel(totalPago);
        val.setFont(new Font("Segoe UI", Font.BOLD, 14));
        val.setForeground(COLOR_VERDE);
        row.add(val);
        return row;
    }

    private JSeparator separador() {
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(COLOR_BORDE);
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        return sep;
    }


    private JLabel labelGris(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        l.setForeground(COLOR_LABEL);
        return l;
    }

    private JLabel valorNormal(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        l.setForeground(COLOR_TEXTO);
        return l;
    }
}