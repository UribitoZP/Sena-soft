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
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;

import com.santaana.dao.HabitacionDAO;
import com.santaana.model.Habitacion;
import com.santaana.util.ThemeManager;

public class TableroPanel extends JPanel {
    private String userRole;
    private Runnable onNuevaReserva;
    private boolean isPlaceholderActive = true;
    private final String PLACEHOLDER = " Buscar habitación...";
    private final HabitacionDAO habitacionDAO = new HabitacionDAO();
    
    private Color getBorde() { return ThemeManager.getBorder(); }
    private Color getPrimario() { return ThemeManager.getPrimary(); }
    private Color getLabel() { return ThemeManager.getTextSecondary(); }
    private Color getBackgroundCol() { return ThemeManager.getBackground(); }
    private Color getPanelCol() { return ThemeManager.getPanelBackground(); }
    private Color getTextCol() { return ThemeManager.getTextPrimary(); }

    public TableroPanel(String role, Runnable onNuevaReserva) {
        this.userRole = role;
        this.onNuevaReserva = onNuevaReserva;
        setLayout(new BorderLayout());
        setBackground(getBackgroundCol());
        refreshUI();
    }

    public void refreshUI() {
        removeAll();
        add(crearNavbar(), BorderLayout.NORTH);
        add(crearContenido(), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel crearNavbar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(getPanelCol());
        navbar.setPreferredSize(new Dimension(0, 50));
        navbar.setBorder(new MatteBorder(0, 0, 1, 0, getBorde()));

        JLabel title = new JLabel("  DASHBOARD PRINCIPAL");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(getTextCol());
        navbar.add(title, BorderLayout.WEST);

        JPanel mid = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        mid.setOpaque(false);
        mid.add(crearBotonAccion("$ Venta Rápida",
            ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT ? new Color(0xE8F1FD) : new Color(0x334155),
            getPrimario()));
        navbar.add(mid, BorderLayout.CENTER);

        return navbar;
    }

    private JButton crearBotonAccion(String text, Color bg, Color fg) {
        JButton b = new JButton(text) {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setForeground(fg);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(150, 32));
        return b;
    }

    private JPanel crearContenido() {
        JPanel cont = new JPanel(new BorderLayout(0, 16));
        cont.setOpaque(false);
        cont.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cont.add(statsRow(), BorderLayout.NORTH);
        cont.add(roomsArea(), BorderLayout.CENTER);
        return cont;
    }

    private JPanel statsRow() {
        java.util.List<Habitacion> todas = habitacionDAO.listarTodas();
        long disponibles   = todas.stream().filter(h -> h.getEstado().equals("Disponible")).count();
        long ocupadas      = todas.stream().filter(h -> h.getEstado().equals("Ocupada")).count();
        long limpieza      = todas.stream().filter(h -> h.getEstado().equals("Limpieza")).count();
        long mantenimiento = todas.stream().filter(h -> h.getEstado().equals("Mantenimiento")).count();

        JPanel row = new JPanel(new GridLayout(1, 5, 14, 0));
        row.setOpaque(false);
        row.add(statCard("Disponibles",   String.valueOf(disponibles),   new Color(0x27AE60)));
        row.add(statCard("Ocupadas",      String.valueOf(ocupadas),      new Color(0xE74C3C)));
        row.add(statCard("En limpieza",   String.valueOf(limpieza),      new Color(0x3A7BD5)));
        row.add(statCard("Mantenimiento", String.valueOf(mantenimiento), new Color(0xE67E22)));
        row.add(statCard("Total",         String.valueOf(todas.size()),  new Color(0x95A5A6)));
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

        JLabel titleLabel = new JLabel("Estado actual de habitaciones");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(getTextCol());

        header.add(titleLabel, BorderLayout.WEST);
        
        final JTextField searchField = new JTextField(PLACEHOLDER);
        searchField.setPreferredSize(new Dimension(250, 30));
        searchField.setBackground(getPanelCol());
        searchField.setForeground(getLabel());
        searchField.setBorder(BorderFactory.createLineBorder(getBorde(), 1, true));
        
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (isPlaceholderActive) {
                    searchField.setText("");
                    searchField.setForeground(getTextCol());
                    isPlaceholderActive = false;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText(PLACEHOLDER);
                    searchField.setForeground(getLabel());
                    isPlaceholderActive = true;
                }
            }
        });

        header.add(searchField, BorderLayout.EAST);

        area.add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 3, 12, 12));
        grid.setOpaque(false);
        java.util.List<Habitacion> habitaciones = habitacionDAO.listarTodas();
        if (habitaciones.isEmpty()) {
            grid.add(new JLabel("No hay habitaciones registradas."));
        } else {
            for (Habitacion h : habitaciones) {
                grid.add(roomCard(h));
            }
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        area.add(scroll, BorderLayout.CENTER);
        return area;
    }

    private JPanel roomCard(Habitacion h) {
        JPanel c = new JPanel() {
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
        c.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel n = new JLabel("Habitación " + h.getNumero());
        n.setFont(new Font("Segoe UI", Font.BOLD, 15));
        n.setForeground(getTextCol());
        n.setAlignmentX(0.0f);

        Color colorEstado;
        switch (h.getEstado()) {
            case "Disponible":    colorEstado = new Color(0x27AE60); break;
            case "Ocupada":       colorEstado = new Color(0xE74C3C); break;
            case "Limpieza":      colorEstado = new Color(0x3A7BD5); break;
            case "Mantenimiento": colorEstado = new Color(0xE67E22); break;
            default:              colorEstado = new Color(0x95A5A6);
        }
        JLabel estadoLbl = new JLabel(h.getEstado());
        estadoLbl.setForeground(colorEstado);
        estadoLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        estadoLbl.setAlignmentX(0.0f);

        JLabel info = new JLabel(String.format("<html>%s<br>$%,.0f / Noche</html>", h.getTipo(), h.getPrecio()));
        info.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        info.setForeground(getLabel());
        info.setAlignmentX(0.0f);
        info.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        c.add(n);
        c.add(Box.createVerticalStrut(5));
        c.add(estadoLbl);
        c.add(Box.createVerticalStrut(10));
        c.add(info);

        return c;
    }
}
