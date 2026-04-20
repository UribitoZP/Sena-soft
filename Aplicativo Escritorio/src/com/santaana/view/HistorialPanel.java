package com.santaana.view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

import com.toedter.calendar.JDateChooser;
import com.santaana.util.ThemeManager;

public class ReservaFrame extends JFrame implements ThemeManager.ThemeListener {
    private String role;
    private Color getPrimario() { return ThemeManager.getPrimary(); }
    private Color getFondo() { return ThemeManager.getBackground(); }
    private Color getPanelCol() { return ThemeManager.getPanelBackground(); }
    private Color getBorde() { return ThemeManager.getBorder(); }
    private Color getTextCol() { return ThemeManager.getTextPrimary(); }
    private Color getLabelCol() { return ThemeManager.getTextSecondary(); }

    public ReservaFrame(String role, String welcomeMessage) {
        this.role = role;

        setTitle("Hotel Santa Ana — Reservas");
        setSize(1280, 800);
        setMinimumSize(new Dimension(1100, 720));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        ThemeManager.addListener(this);
        refreshUI();
        setVisible(true);
    }

    private void refreshUI() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());
        add(crearNavbar(), BorderLayout.NORTH);
        add(sidebar(), BorderLayout.WEST);
        add(crearContenido(), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    @Override
    public void onThemeChanged() {
        refreshUI();
    }

    private JPanel crearNavbar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(getPanelCol());
        navbar.setPreferredSize(new Dimension(0, 62));
        navbar.setBorder(new MatteBorder(0, 0, 1, 0, getBorde()));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 12));
        left.setOpaque(false);

        JLabel logo = new JLabel();
        try {
            java.net.URL logoUrl = getClass().getResource("/resources/logo.png");
            if (logoUrl != null) {
                ImageIcon icon = new ImageIcon(logoUrl);
                Image scaled = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                logo.setIcon(new ImageIcon(scaled));
            }
        } catch (Exception e) {}

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
        mid.add(crearBotonNavbar("+ Nueva reserva", getPrimario(), Color.WHITE));
        mid.add(crearBotonNavbar("$  Venta rapida", 
            ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT ? new Color(0xE8F1FD) : new Color(0x334155), 
            getPrimario()));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 16));
        right.setOpaque(false);
        right.add(userPanel());

        navbar.add(left, BorderLayout.WEST);
        navbar.add(mid, BorderLayout.CENTER);
        navbar.add(right, BorderLayout.EAST);
        return navbar;
    }

    private JButton crearBotonNavbar(String text, Color bg, Color fg) {
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
}