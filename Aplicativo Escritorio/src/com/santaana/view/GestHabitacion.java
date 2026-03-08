package com.santaana.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;

public class GestHabitacion extends JFrame {
    private static final Color COLOR_BORDE    = new Color(0xDDE8F5);
    private static final Color COLOR_PRIMARIO = new Color(0x3A7BD5);
    private static final Color COLOR_LABEL    = new Color(0x6B84A0);

    private final String role;

    public GestHabitacion(String role, String welcomeMessage) {
        this.role = role;
        setTitle("Hotel Santa Ana — Gestión de Habitaciones");
        setSize(1100, 720);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(0xF0F6FF));
        main.add(topBar(),   BorderLayout.NORTH);
        main.add(sidebar(),  BorderLayout.WEST);
        main.add(center(),   BorderLayout.CENTER);
        add(main);
        setVisible(true);
    }

    private JPanel topBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setPreferredSize(new Dimension(0, 62));
        bar.setBorder(new MatteBorder(0, 0, 1, 0, new Color(0xDDE8F5)));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 12));
        left.setOpaque(false);


        JLabel logo = new JLabel();
        try {
            ImageIcon icon = new ImageIcon("resources/logo.png");
            Image scaledImage = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            logo.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            System.err.println("Logo no encontrado");
        }

        JLabel nombre = new JLabel("<html><b style='font-size:13px'>HOTEL SANTA ANA</b><br>"+ "<span style='color:#6B84A0;font-size:9px'>Sistema de gestion hotelera</span></html>");

        left.add(logo);
        left.add(nombre);

        JLabel notifLbl = new JLabel("🔔");
        notifLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16)); 
        notifLbl.setForeground(new Color(0x3A7BD5));

        left.add(notifLbl);

        JPanel mid = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 14));
        mid.setOpaque(false);
        mid.add(actionBtn("+ Nueva reserva", new Color(0x3A7BD5), Color.WHITE));
        mid.add(actionBtn("$  Venta rapida",   new Color(0xE8F1FD), new Color(0x3A7BD5)));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 16));
        right.setOpaque(false);
        

        right.add(userPanel());

        bar.add(left,  BorderLayout.WEST);
        bar.add(mid,   BorderLayout.CENTER);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JButton actionBtn(String text, Color bg, Color fg) {
        JButton b = new JButton(text) {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.darker() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(fg);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(150, 34));
        return b;
    }


    private JPanel userPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        JLabel name = new JLabel("----");
        name.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel rol = new JLabel(role);
        rol.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        rol.setForeground(new Color(0x6B84A0));
        p.add(name);
        p.add(rol);
        return p;
    }

    private JPanel sidebar(){

        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side,BoxLayout.Y_AXIS));
        side.setBackground(Color.WHITE);
        side.setPreferredSize(new Dimension(190,0));
        side.setBorder(new MatteBorder(0,0,0,1,COLOR_BORDE));

        side.add(Box.createVerticalStrut(20));

        String[] items={
                "Tablero",
                "Gestión de Habitaciones",
                "Reserva",
                "Punto de venta",
                "Historial",
                "Reporte"
        };

        for(int i=0;i<items.length;i++){

            side.add(sideBtn(items[i], i==2));
            side.add(Box.createVerticalStrut(8));

        }

        side.add(Box.createVerticalGlue());

        return side;
    }

    private JPanel sideBtn(String text, boolean active){

        JPanel p = new JPanel(new BorderLayout());
        p.setMaximumSize(new Dimension(180,36));
        p.setBorder(BorderFactory.createEmptyBorder(8,14,8,8));
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lbl = new JLabel(text);

        lbl.setFont(new Font("Segoe UI",active?Font.BOLD:Font.PLAIN,12));
        lbl.setForeground(active?COLOR_PRIMARIO:COLOR_LABEL);

        if(active)
            p.setBackground(new Color(0xE8F1FD));
        else
            p.setBackground(Color.WHITE);

        p.add(lbl,BorderLayout.CENTER);

        return p;
    }
        private JPanel center() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        p.add(roomsArea(), BorderLayout.CENTER);
        return p;
    }

    private JPanel roomsArea() {
    JPanel area = new JPanel(new BorderLayout(0, 10));
    area.setOpaque(false);

    JPanel header = new JPanel(new BorderLayout(10, 0));
    header.setOpaque(false);

    JLabel title = new JLabel("Buscar habitación ");
    title.setFont(new Font("Segoe UI", Font.BOLD, 13));

    header.add(title,      BorderLayout.WEST);
    header.add(searchBar(), BorderLayout.CENTER);
    area.add(header, BorderLayout.NORTH);

    JPanel grid = new JPanel(new GridLayout(0, 3, 10, 10));
    grid.setOpaque(false);
    for (String n : new String[]{"101","102","103","104","105","106","107","208","209","210","211","212","213","214","215"})
        grid.add(roomCard(n));

    JScrollPane scroll = new JScrollPane(grid,
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scroll.setBorder(null);
    scroll.setOpaque(false);
    scroll.getViewport().setOpaque(false);
    area.add(scroll, BorderLayout.CENTER);
    return area;
}

private JPanel searchBar() {
    JPanel fieldWrapper = new JPanel(new BorderLayout()) {
        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            g2.setColor(new Color(0xCCCCCC));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
            g2.dispose();
        }
    };
    fieldWrapper.setOpaque(false);
    fieldWrapper.setPreferredSize(new Dimension(0, 30));

    JLabel searchIcon = new JLabel("🔍");
    searchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
    searchIcon.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 4));

    JTextField field = new JTextField("Buscar habitación...") {
        @Override
        public void paintComponent(Graphics g) {
            setOpaque(false);
            super.paintComponent(g);
        }
    };
    field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    field.setForeground(Color.GRAY);
    field.setOpaque(false);
    field.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

    field.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent e) {
            if (field.getText().equals("Buscar habitación...")) {
                field.setText("");
                field.setForeground(Color.BLACK);
            }
        }
        public void focusLost(java.awt.event.FocusEvent e) {
            if (field.getText().isEmpty()) {
                field.setText("Buscar habitación...");
                field.setForeground(Color.GRAY);
            }
        }
    });

    fieldWrapper.add(searchIcon, BorderLayout.WEST);
    fieldWrapper.add(field,      BorderLayout.CENTER);

    return fieldWrapper;
}

private JPanel roomCard(String num) {
    JPanel c = new JPanel(new BorderLayout(0, 0)) {
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.setColor(new Color(0xDDE8F5));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
            g2.dispose();
        }
    };
    c.setOpaque(false);
    c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
    c.setBorder(BorderFactory.createEmptyBorder(5, 10, 8, 10));
    c.setMaximumSize(c.getPreferredSize());

    JLabel numLbl = new JLabel("Habitación " + num);
    numLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
    numLbl.setAlignmentX(JLabel.LEFT_ALIGNMENT);

    JLabel badge = new JLabel(" Disponible");
    badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
    badge.setForeground(new Color(0x27AE60));


    JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
    top.setOpaque(false);
    top.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
    top.setAlignmentX(JPanel.LEFT_ALIGNMENT);
    top.add(numLbl);
    top.add(badge);

    JLabel info = new JLabel("<html><span style='color:#6B84A0'>Individual<br>"
            + "Noche: $70.000 &nbsp;|&nbsp; Hora: $15.000</span></html>");
    info.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    info.setVerticalAlignment(JLabel.TOP);
    info.setAlignmentX(JLabel.LEFT_ALIGNMENT);

    JButton btn = new JButton("Gestionar  \u203a") {
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isRollover() ? new Color(0x2563C0) : new Color(0x3A7BD5));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            g2.dispose();
            super.paintComponent(g);
        }
    };
    btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
    btn.setForeground(Color.WHITE);
    btn.setContentAreaFilled(false);
    btn.setBorderPainted(false);
    btn.setFocusPainted(false);
    btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
    btn.setAlignmentX(JButton.LEFT_ALIGNMENT);

    c.add(top);
    c.add(Box.createVerticalStrut(10));
    c.add(info);
    c.add(Box.createVerticalStrut(10));
    c.add(btn);
    JPanel wrapper = new JPanel(new BorderLayout());
    wrapper.setOpaque(false);
    wrapper.add(c, BorderLayout.NORTH);
    return wrapper;
  
    }


   
    
}
