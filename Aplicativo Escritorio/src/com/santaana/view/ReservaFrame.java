package com.santaana.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import com.toedter.calendar.JDateChooser;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ReservaFrame extends JFrame {
    private String role;
    private static final Color COLOR_PRIMARIO = new Color(0x3A7BD5);
    private static final Color COLOR_FONDO = new Color(0xF0F6FF);
    private static final Color COLOR_PANEL = Color.WHITE;
    private static final Color COLOR_BORDE = new Color(0xEAF2FB);
    private static final Color COLOR_TEXTO = new Color(40, 50, 70);
    private static final Color COLOR_VERDE = new Color(0, 170, 90);
    private static final Color COLOR_LABEL = new Color(110, 120, 140);

    public ReservaFrame(String role, String welcomeMessage) {
        this.role = role;

        setTitle("Hotel Santa Ana — Reservas");
        setSize(1280, 800);
        setMinimumSize(new Dimension(1100, 720));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(crearNavbar(), BorderLayout.NORTH);
        add(sidebar(), BorderLayout.WEST);
        add(crearContenido(), BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel crearNavbar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(Color.WHITE);
        navbar.setPreferredSize(new Dimension(0, 60));
        navbar.setBorder(new MatteBorder(0, 0, 1, 0, COLOR_BORDE));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        left.setOpaque(false);

        JLabel logo = new JLabel();

        try {
            java.net.URL logoUrl = getClass().getResource("/resources/logo.png");
            if (logoUrl != null) {
                ImageIcon icon = new ImageIcon(logoUrl);
                Image scaled = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                logo.setIcon(new ImageIcon(scaled));
            } else {
                System.err.println("Logo no encontrado en /resources/logo.png");
            }
        } catch (Exception e) {
            System.err.println("Error cargando logo en navbar: " + e.getMessage());
        }

        JLabel nombre = new JLabel(
                "<html><b>HOTEL SANTA ANA</b><br><span style='font-size:9px;color:#6B84A0'>Sistema de gestión hotelera</span></html>");

        left.add(logo);
        left.add(nombre);

        JLabel notifLbl = new JLabel("🔔");
        notifLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 17));
        notifLbl.setForeground(new Color(0x3A7BD5));

        left.add(notifLbl);

        JPanel mid = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 12));
        mid.setOpaque(false);

        mid.add(crearBotonNavbar("+ Nueva reserva", COLOR_PRIMARIO));
        mid.add(crearBotonNavbar("Venta rápida", new Color(0xE8F1FD)));

        // Lado derecho: botones + usuario
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 14));
        right.setOpaque(false);
        right.add(userPanel());

        navbar.add(left, BorderLayout.WEST);
        navbar.add(mid, BorderLayout.CENTER);
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

    // SIDEBAR

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

            side.add(sideBtn(items[i], i == 2));
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

        if (active)
            p.setBackground(new Color(0xE8F1FD));
        else
            p.setBackground(Color.WHITE);

        p.add(lbl, BorderLayout.CENTER);

        return p;
    }

    // CONTENIDO CENTRAL

    private JScrollPane crearContenido() {

        JPanel cont = new JPanel();
        cont.setLayout(new BoxLayout(cont, BoxLayout.Y_AXIS));
        cont.setBackground(COLOR_FONDO);
        cont.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Fila1: Huesped + Reserva
        JPanel fila1 = new JPanel(new GridBagLayout());
        fila1.setOpaque(false);
        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.fill = GridBagConstraints.HORIZONTAL;
        gbc1.insets = new Insets(0, 0, 0, 20);

        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 0.35;
        gbc1.weighty = 1.0;
        fila1.add(crearPanelHuesped(), gbc1);

        gbc1.gridx = 1;
        gbc1.weightx = 0.65;
        gbc1.insets = new Insets(0, 0, 0, 0);
        fila1.add(crearPanelReserva(), gbc1);

        // Fila2: Pago + Habitaciones
        JPanel fila2 = new JPanel(new GridBagLayout());
        fila2.setOpaque(false);
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.insets = new Insets(0, 0, 0, 20);

        gbc2.gridx = 0;
        gbc2.gridy = 0;
        gbc2.weightx = 0.35;
        gbc2.weighty = 1.0;
        fila2.add(crearPanelPago(), gbc2);

        gbc2.gridx = 1;
        gbc2.weightx = 0.65;
        gbc2.insets = new Insets(0, 0, 0, 0);
        fila2.add(crearPanelHabitaciones(), gbc2);

        cont.add(fila1);
        cont.add(Box.createVerticalStrut(20));
        cont.add(fila2);
        cont.add(Box.createVerticalStrut(20));
        cont.add(crearPanelBotones());

        JScrollPane scroll = new JScrollPane(cont);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        return scroll;
    }

    // TARJETA BASE

    private JPanel tarjeta() {

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(COLOR_PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        return p;
    }

    private JLabel titulo(String txt) {

        JLabel l = new JLabel(txt);
        l.setForeground(COLOR_PRIMARIO);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));

        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        return l;
    }

    // HUESPED

    private JPanel crearPanelHuesped() {

        JPanel p = tarjeta();

        p.add(titulo("Datos de huésped"));

        p.add(campo("Nombre"));
        p.add(Box.createVerticalStrut(7));
        p.add(campo("Apellido"));
        p.add(Box.createVerticalStrut(7));
        p.add(campo("Identificación"));
        p.add(Box.createVerticalStrut(7));
        p.add(campo("Correo"));
        p.add(Box.createVerticalStrut(7));
        p.add(campo("Teléfono"));

        return p;
    }

    // RESERVA

    private JPanel crearPanelReserva() {

        JPanel p = tarjeta();
        p.add(titulo("Datos de reserva"));

        JPanel cont = new JPanel(new GridLayout(0, 2, 10, 8));
        cont.setOpaque(false);

        // Fecha entrada
        JLabel lblEntrada = new JLabel("Fecha de Entrada");
        lblEntrada.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblEntrada.setForeground(COLOR_LABEL);

        JDateChooser fechaEntrada = new JDateChooser();
        fechaEntrada.setDateFormatString("dd/MM/yyyy");

        JTextField txtEntrada = (JTextField) fechaEntrada.getDateEditor().getUiComponent();
        txtEntrada.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtEntrada.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));

        // Hora entrada
        JLabel lblHoraEntrada = new JLabel("Hora de Entrada");
        lblHoraEntrada.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblHoraEntrada.setForeground(COLOR_LABEL);

        JComboBox<String> horaEntrada = new JComboBox<>(new String[] {
                "06:00", "07:00", "08:00", "09:00", "10:00", "11:00",
                "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"
        });
        horaEntrada.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        horaEntrada.setBackground(Color.WHITE);

        // Fecha salida
        JLabel lblSalida = new JLabel("Fecha de Salida");
        lblSalida.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSalida.setForeground(COLOR_LABEL);

        JDateChooser fechaSalida = new JDateChooser();
        fechaSalida.setDateFormatString("dd/MM/yyyy");

        JTextField txtSalida = (JTextField) fechaSalida.getDateEditor().getUiComponent();
        txtSalida.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSalida.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));

        // Hora Salida
        JLabel lblHoraSalida = new JLabel("Hora de Salida");
        lblHoraSalida.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblHoraSalida.setForeground(COLOR_LABEL);

        JComboBox<String> horaSalida = new JComboBox<>(new String[] {
                "06:00", "07:00", "08:00", "09:00", "10:00", "11:00",
                "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"
        });
        horaSalida.setBackground(Color.WHITE);

        // Tipo de estadía
        JLabel lblTipo = new JLabel("Tipo de estadía");
        lblTipo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTipo.setForeground(COLOR_LABEL);

        JComboBox<String> tipoEstadia = new JComboBox<>(new String[] {
                "Por horas",
                "Media noche",
                "Noche completa",
                "Día completo"
        });
        tipoEstadia.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        tipoEstadia.setBackground(Color.WHITE);

        cont.add(lblEntrada);
        cont.add(fechaEntrada);

        cont.add(lblHoraEntrada);
        cont.add(horaEntrada);

        cont.add(lblSalida);
        cont.add(fechaSalida);

        cont.add(lblHoraSalida);
        cont.add(horaSalida);

        cont.add(lblTipo);
        cont.add(tipoEstadia);

        p.add(cont);

        return p;
    }

    // PAGO

    private JPanel crearPanelPago() {

        JPanel panel = tarjeta();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(titulo("Pago"));
        panel.add(Box.createVerticalStrut(12));

        /* LIMITE DE HOSPEDAJE */

        JLabel lblLimite = new JLabel("Límite de hospedaje");
        lblLimite.setForeground(COLOR_TEXTO);
        lblLimite.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<String> cmbLimite = new JComboBox<>(new String[] {
                "1 hora", "2 horas", "6 horas", "12 horas", "1 noche"
        });
        cmbLimite.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        cmbLimite.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbLimite.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));
        cmbLimite.setBackground(Color.WHITE);

        panel.add(lblLimite);
        panel.add(Box.createVerticalStrut(4));
        panel.add(cmbLimite);

        panel.add(Box.createVerticalStrut(14));

        /* ANTICIPO */

        JPanel filaAnticipo = new JPanel(new BorderLayout(8, 0));
        filaAnticipo.setOpaque(false);
        filaAnticipo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        filaAnticipo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblAnticipo = new JLabel("Anticipo");
        lblAnticipo.setForeground(COLOR_TEXTO);

        JTextField txtAnticipo = new JTextField("$0");
        txtAnticipo.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));

        filaAnticipo.add(lblAnticipo, BorderLayout.WEST);
        filaAnticipo.add(txtAnticipo, BorderLayout.CENTER);

        panel.add(filaAnticipo);

        panel.add(Box.createVerticalStrut(12));

        /* TOTAL */

        JPanel filaTotal = new JPanel(new BorderLayout());
        filaTotal.setOpaque(false);
        filaTotal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        filaTotal.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTotal = new JLabel("Total");

        JLabel lblTotalVal = new JLabel("$0");
        lblTotalVal.setForeground(COLOR_PRIMARIO);
        lblTotalVal.setFont(lblTotalVal.getFont().deriveFont(Font.BOLD, 16f));

        filaTotal.add(lblTotal, BorderLayout.WEST);
        filaTotal.add(lblTotalVal, BorderLayout.EAST);

        panel.add(filaTotal);

        panel.add(Box.createVerticalStrut(14));

        /* MÉTODO DE PAGO */

        JPanel filaPago = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filaPago.setOpaque(false);
        filaPago.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        filaPago.setAlignmentX(Component.LEFT_ALIGNMENT);

        JRadioButton rbEfectivo = new JRadioButton("Efectivo");
        JRadioButton rbTransferencia = new JRadioButton("Transferencia");
        JRadioButton rbTarjeta = new JRadioButton("Tarjeta");

        rbEfectivo.setOpaque(false);
        rbTransferencia.setOpaque(false);
        rbTarjeta.setOpaque(false);

        rbEfectivo.setSelected(true);

        ButtonGroup grupoPago = new ButtonGroup();
        grupoPago.add(rbEfectivo);
        grupoPago.add(rbTransferencia);
        grupoPago.add(rbTarjeta);

        filaPago.add(rbEfectivo);
        filaPago.add(rbTransferencia);
        filaPago.add(rbTarjeta);

        panel.add(filaPago);

        return panel;
    }

    // HABITACIONES

    private JPanel crearPanelHabitaciones() {

        JPanel p = tarjeta();

        p.add(titulo("Seleccionar    habitación"));

        JPanel cards = new JPanel(new GridLayout(1, 3, 10, 0));
        cards.setOpaque(false);
        cards.setBorder(BorderFactory.createEmptyBorder(1, 3, 12, 0));

        cards.add(cardHabitacion("07"));
        cards.add(cardHabitacion("01"));
        cards.add(cardHabitacion("15"));

        p.add(cards);

        return p;
    }

    private JPanel cardHabitacion(String num) {

        JPanel c = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);

                g2.dispose();
            }
        };

        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setOpaque(false);
        c.setBackground(Color.WHITE);
        c.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel n = new JLabel("Habitación " + num);
        n.setAlignmentX(Component.CENTER_ALIGNMENT);
        n.setFont(new Font("Segoe UI", Font.BOLD, 15));
        n.setForeground(COLOR_PRIMARIO);

        JLabel disp = new JLabel("Disponible");
        disp.setAlignmentX(Component.CENTER_ALIGNMENT);
        disp.setForeground(COLOR_VERDE);

        JLabel info = new JLabel("<html><span style='color:#6B84A0'>Individual<br>"
                + "Noche: $70.000 &nbsp;|&nbsp; Hora: $15.000</span></html>");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        info.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton sel = new JButton("Seleccionar");
        sel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sel.setBackground(COLOR_PRIMARIO);
        sel.setForeground(Color.WHITE);
        sel.setFocusPainted(false);
        sel.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));

        c.add(n);
        c.add(Box.createVerticalStrut(5));
        c.add(disp);
        c.add(Box.createVerticalStrut(5));
        c.add(info);
        c.add(Box.createVerticalStrut(10));
        c.add(sel);

        // Efecto hover

        c.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                c.setBackground(new Color(0xF4F8FF));
                c.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                c.setBackground(Color.WHITE);
                c.repaint();
            }
        });

        return c;
    }

    // BOTONES

    private JPanel crearPanelBotones() {

        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.setOpaque(false);

        JButton cancelar = new JButton("Cancelar");
        JButton reservar = new JButton("Reservar");

        reservar.setBackground(COLOR_PRIMARIO);
        reservar.setForeground(Color.WHITE);

        p.add(cancelar);
        p.add(reservar);

        return p;
    }

    // CAMPO

    private JPanel campo(String txt) {

        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JTextField t = new JTextField();
        t.setBorder(BorderFactory.createTitledBorder(txt));

        p.add(t);

        return p;
    }
}
