package com.santaana.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import com.santaana.util.ThemeManager;

public class TableroPanel extends JPanel {
    private String userRole;
    private boolean isPlaceholderActive = true;
    private final String PLACEHOLDER = " Buscar habitación...";
    
    private Color getBorde() { return ThemeManager.getBorder(); }
    private Color getPrimario() { return ThemeManager.getPrimary(); }
    private Color getLabel() { return ThemeManager.getTextSecondary(); }
    private Color getBackgroundCol() { return ThemeManager.getBackground(); }
    private Color getPanelCol() { return ThemeManager.getPanelBackground(); }
    private Color getTextCol() { return ThemeManager.getTextPrimary(); }

    public TableroPanel(String role) {
        this.userRole = role;
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
        mid.add(crearBotonAccion("+ Nueva Reserva", getPrimario(), Color.WHITE));
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
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
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
        for (int i = 101; i <= 115; i++) {
            grid.add(roomCard(String.valueOf(i)));
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        area.add(scroll, BorderLayout.CENTER);
        return area;
    }

    private JPanel roomCard(String num) {
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

        JLabel n = new JLabel("Habitación " + num);
        n.setFont(new Font("Segoe UI", Font.BOLD, 15));
        n.setForeground(getTextCol());
        n.setAlignmentX(0.0f);

        JLabel dispel = new JLabel("Disponible");
        dispel.setForeground(new Color(0x27AE60));
        dispel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        dispel.setAlignmentX(0.0f);

        JLabel info = new JLabel("<html>Individual<br>$70.000 / Noche</html>");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        info.setForeground(getLabel());
        info.setAlignmentX(0.0f);
        
        JButton btn = new JButton("Gestionar ›") {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getPrimario());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(100, 26));
        btn.setAlignmentX(0.0f);

        c.add(n);
        c.add(Box.createVerticalStrut(5));
        c.add(dispel);
        c.add(Box.createVerticalStrut(10));
        c.add(info);
        c.add(Box.createVerticalStrut(15));
        c.add(btn);

        return c;
    }
}
