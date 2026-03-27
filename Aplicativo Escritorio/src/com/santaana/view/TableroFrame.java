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

import com.santaana.util.ThemeManager;

public class TableroFrame extends JFrame implements ThemeManager.ThemeListener {
    private Color getBorde() { return ThemeManager.getBorder(); }
    private Color getPrimario() { return ThemeManager.getPrimary(); }
    private Color getLabel() { return ThemeManager.getTextSecondary(); }
    private Color getBackgroundCol() { return ThemeManager.getBackground(); }
    private Color getPanelCol() { return ThemeManager.getPanelBackground(); }
    private Color getTextCol() { return ThemeManager.getTextPrimary(); }

    private final String role;

    public TableroFrame(String role, String welcomeMessage) {
        this.role = role;
        setTitle("Hotel Santa Ana — Tablero");
        setSize(1100, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ThemeManager.addListener(this);
        refreshUI();
        setVisible(true);
    }

    private void refreshUI() {
        getContentPane().removeAll();
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(getBackgroundCol());
        main.add(topBar(), BorderLayout.NORTH);
        main.add(sidebar(), BorderLayout.WEST);
        main.add(center(), BorderLayout.CENTER);
        add(main);
        revalidate();
        repaint();
    }

    @Override
    public void onThemeChanged() {
        refreshUI();
    }

    private JPanel topBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(getPanelCol());
        bar.setPreferredSize(new Dimension(0, 62));
        bar.setBorder(new MatteBorder(0, 0, 1, 0, getBorde()));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 12));
        left.setOpaque(false);

        // Logo desde archivo
        JLabel logo = new JLabel();
        try {
            java.net.URL logoUrl = getClass().getResource("/resources/logo.png");
            if (logoUrl != null) {
                ImageIcon icon = new ImageIcon(logoUrl);
                Image scaledImage = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                logo.setIcon(new ImageIcon(scaledImage));
            } else {
                System.err.println("Logo no encontrado en /resources/logo.png");
            }
        } catch (Exception e) {
            System.err.println("Error cargando logo: " + e.getMessage());
        }

        JLabel nombre = new JLabel("<html><b style='font-size:13px; color:" + (ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT ? "#1F2937" : "#F3F4F6") + "'>HOTEL SANTA ANA</b><br>"
                + "<span style='color:" + (ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT ? "#6B84A0" : "#94A3B8") + ";font-size:9px'>Sistema de gestion hotelera</span></html>");

        left.add(logo);
        left.add(nombre);

        JButton themeToggle = new JButton(ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT ? "🌙" : "☀️");
        themeToggle.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        themeToggle.setContentAreaFilled(false);
        themeToggle.setBorderPainted(false);
        themeToggle.setFocusPainted(false);
        themeToggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        themeToggle.addActionListener(e -> ThemeManager.toggleTheme());

        left.add(themeToggle);

        JPanel mid = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 14));
        mid.setOpaque(false);
        mid.add(actionBtn("+ Nueva reserva", getPrimario(), Color.WHITE));
        mid.add(actionBtn("$  Venta rapida", 
            ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT ? new Color(0xE8F1FD) : new Color(0x334155), 
            getPrimario()));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 16));
        right.setOpaque(false);

        right.add(userPanel());

        bar.add(left, BorderLayout.WEST);
        bar.add(mid, BorderLayout.CENTER);
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
        rol.setForeground(getLabel());
        name.setForeground(getTextCol());
        p.add(name);
        p.add(rol);
        return p;
    }

    private JPanel sidebar() {

        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(getPanelCol());
        side.setPreferredSize(new Dimension(190, 0));
        side.setBorder(new MatteBorder(0, 0, 0, 1, getBorde()));

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

            side.add(sideBtn(items[i], i == 0));
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
        lbl.setForeground(active ? getPrimario() : getLabel());

        if (active)
            p.setBackground(ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT ? new Color(0xE8F1FD) : new Color(0x2D3748));
        else
            p.setBackground(getPanelCol());

        p.add(lbl, BorderLayout.CENTER);

        return p;
    }

    private JPanel center() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        p.add(statsRow(), BorderLayout.NORTH);
        p.add(roomsArea(), BorderLayout.CENTER);
        return p;
    }

    private JPanel statsRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 14, 0));
        row.setOpaque(false);
        row.add(statCard("Habitaciones disponibles", "15", new Color(0x27AE60)));
        row.add(statCard("Habitaciones ocupadas", "0", new Color(0xE67E22)));
        row.add(statCard("Total habitaciones", "15", new Color(0x3A7BD5)));
        return row;
    }

    private JPanel statCard(String label, String value, Color accent) {
        JPanel c = new JPanel(new BorderLayout()) {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getPanelCol());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(accent);
                g2.fillRect(0, getHeight() - 4, getWidth(), 4);
                g2.dispose();
            }
        };
        c.setOpaque(false);
        c.setPreferredSize(new Dimension(0, 85));
        c.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel num = new JLabel(value);
        num.setFont(new Font("Segoe UI", Font.BOLD, 26));
        num.setForeground(getTextCol());
        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lbl.setForeground(getLabel());
        c.add(num, BorderLayout.CENTER);
        c.add(lbl, BorderLayout.SOUTH);
        return c;
    }

    private JPanel roomsArea() {
        JPanel area = new JPanel(new BorderLayout(0, 10));
        area.setOpaque(false);

        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(false);

        JLabel title = new JLabel("Estado actual de habitaciones");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(getTextCol());

        header.add(title, BorderLayout.WEST);
        header.add(searchBar(), BorderLayout.CENTER);
        area.add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 3, 10, 10));
        grid.setOpaque(false);
        for (String n : new String[] { "101", "102", "103", "104", "105", "106", "107", "208", "209", "210", "211",
                "212", "213", "214", "215" })
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
                g2.setColor(getPanelCol());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(getBorde());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
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
        field.setForeground(getLabel());
        field.setOpaque(false);
        field.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals("Buscar habitación...")) {
                    field.setText("");
                    field.setForeground(getTextCol());
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText("Buscar habitación...");
                    field.setForeground(getLabel());
                }
            }
        });

        fieldWrapper.add(searchIcon, BorderLayout.WEST);
        searchIcon.setForeground(getLabel());
        fieldWrapper.add(field, BorderLayout.CENTER);

        return fieldWrapper;
    }

    private JPanel roomCard(String num) {
        JPanel c = new JPanel(new BorderLayout(0, 0)) {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getPanelCol());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(getBorde());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        c.setOpaque(false);
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBorder(BorderFactory.createEmptyBorder(5, 10, 8, 10));
        c.setMaximumSize(c.getPreferredSize());

        JLabel numLbl = new JLabel("Habitación " + num);
        numLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        numLbl.setForeground(getTextCol());
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

        JLabel info = new JLabel("<html><span style='color:" + (ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT ? "#6B84A0" : "#94A3B8") + "'>Individual<br>"
                + "Noche: $70.000 &nbsp;|&nbsp; Hora: $15.000</span></html>");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        info.setVerticalAlignment(JLabel.TOP);
        info.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        JButton btn = new JButton("Gestionar  \u203a") {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? getPrimario().darker() : getPrimario());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        JButton btn2 = new JButton("Ver detalles  \u203a") {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bg = ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT
                        ? new Color(0xE8F1FD)
                        : new Color(0x334155);

                g2.setColor(getModel().isRollover() ? bg.darker() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        JPanel espacio = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        espacio.setOpaque(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        btn.setAlignmentX(JButton.LEFT_ALIGNMENT);
        espacio.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        espacio.setAlignmentX(0.0f);

        info.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        //diseño de boton2
        btn2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn2.setForeground(getPrimario()); 
        btn2.setContentAreaFilled(false);
        btn2.setBorderPainted(false);
        btn2.setFocusPainted(false);
        btn2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        btn2.setAlignmentX(JButton.LEFT_ALIGNMENT);


        c.add(top);
        c.add(Box.createVerticalStrut(10));
        c.add(info);
        c.add(Box.createVerticalStrut(10));
        c.add(btn);
        c.add(btn2);
        c.add(espacio);
        return c;
    

    }
}
