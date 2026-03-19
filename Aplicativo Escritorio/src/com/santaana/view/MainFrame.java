package com.santaana.view;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.santaana.util.ThemeManager;

public class MainFrame extends JFrame implements ThemeManager.ThemeListener {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JPanel sidebarPanel;
    private String userRole;
    private String currentView = "Tablero";

    public MainFrame(String role, String welcomeMessage) {
        this.userRole = role;
        setTitle("Hotel Santa Ana — Sistema de Gestión");
        setSize(1280, 800);
        setMinimumSize(new Dimension(1100, 720));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ThemeManager.addListener(this);
        initUI();
        
        // Mostrar mensaje de bienvenida si es necesario
        if (welcomeMessage != null && !welcomeMessage.isEmpty()) {
            JOptionPane.showMessageDialog(this, welcomeMessage, "Bienvenido", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void initUI() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        // 0. Navbar Global
        add(crearNavbarGlobal(), BorderLayout.NORTH);

        // 1. Sidebar
        sidebarPanel = crearSidebar();
        add(sidebarPanel, BorderLayout.WEST);

        // 2. Contenedor de Vistas (CardLayout)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);

        // Agregar vistas
        contentPanel.add(new TableroPanel(userRole), "Tablero");
        contentPanel.add(new ReservaPanel(userRole), "Reserva");
        contentPanel.add(new GestHabitacionPanel(userRole), "Gestión de Habitaciones");

        add(contentPanel, BorderLayout.CENTER);
        
        getContentPane().setBackground(ThemeManager.getBackground());
        
        revalidate();
        repaint();
    }

    private JPanel crearNavbarGlobal() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(ThemeManager.getPanelBackground());
        bar.setPreferredSize(new Dimension(0, 62));
        bar.setBorder(new MatteBorder(0, 0, 1, 0, ThemeManager.getBorder()));

        // Izquierda: Logo y Nombre
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 12));
        left.setOpaque(false);
        
        JLabel logo = new JLabel("🏨"); 
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        logo.setForeground(ThemeManager.getPrimary());
        
        JLabel brand = new JLabel("<html><b style='font-size:13px; color:" + (ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT ? "#1F2937" : "#F3F4F6") + "'>HOTEL SANTA ANA</b><br>"
                + "<span style='color:" + (ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT ? "#6B84A0" : "#94A3B8") + ";font-size:9px'>Sistema de gestión hotelera</span></html>");
        
        left.add(logo);
        left.add(brand);

        // Derecha: Notificaciones, Tema y Usuario
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        right.setOpaque(false);

        JLabel notifIcon = new JLabel("🔔");
        notifIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        notifIcon.setForeground(ThemeManager.getPrimary());
        notifIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton themeToggle = new JButton(ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT ? "🌙" : "☀️");
        themeToggle.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        themeToggle.setContentAreaFilled(false);
        themeToggle.setBorderPainted(false);
        themeToggle.setFocusPainted(false);
        themeToggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        themeToggle.addActionListener(e -> ThemeManager.toggleTheme());

        // Panel de Usuario
        JPanel userPnl = new JPanel();
        userPnl.setLayout(new BoxLayout(userPnl, BoxLayout.Y_AXIS));
        userPnl.setOpaque(false);
        JLabel uName = new JLabel("USUARIO ACTIVO");
        uName.setFont(new Font("Segoe UI", Font.BOLD, 11));
        uName.setForeground(ThemeManager.getTextPrimary());
        JLabel uRol = new JLabel(userRole);
        uRol.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        uRol.setForeground(ThemeManager.getTextSecondary());
        userPnl.add(uName);
        userPnl.add(uRol);

        right.add(notifIcon);
        right.add(themeToggle);
        right.add(userPnl);

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        
        return bar;
    }

    private JPanel crearSidebar() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(ThemeManager.getPanelBackground());
        side.setPreferredSize(new Dimension(200, 0));
        side.setBorder(new MatteBorder(0, 0, 0, 1, ThemeManager.getBorder()));

        side.add(Box.createVerticalStrut(20));
        
        // Logo en sidebar
        JLabel logoBrand = new JLabel(" HOTEL SANTA ANA ");
        logoBrand.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoBrand.setForeground(ThemeManager.getPrimary());
        logoBrand.setAlignmentX(Component.CENTER_ALIGNMENT);
        side.add(logoBrand);
        
        side.add(Box.createVerticalStrut(30));

        String[] items = { "Tablero", "Gestión de Habitaciones", "Reserva", "Punto de venta", "Historial", "Reporte" };
        for (String item : items) {
            side.add(crearBotonSidebar(item));
            side.add(Box.createVerticalStrut(8));
        }

        side.add(Box.createVerticalGlue());
        
        // Opciones de sistema (Temporales)
        side.add(crearBotonSidebar("Cambiar Rol (" + userRole + ")"));
        side.add(Box.createVerticalStrut(8));
        side.add(crearBotonSidebar("Cerrar Sesión"));
        side.add(Box.createVerticalStrut(20));

        return side;
    }

    private JPanel crearBotonSidebar(String text) {
        boolean active = text.equals(currentView);
        JPanel p = new JPanel(new BorderLayout());
        p.setMaximumSize(new Dimension(190, 40));
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        p.setOpaque(true);
        
        if (active) {
            p.setBackground(ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT ? new Color(0xE8F1FD) : new Color(0x334155));
        } else {
            p.setBackground(ThemeManager.getPanelBackground());
        }
        
        p.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 13));
        lbl.setForeground(active ? ThemeManager.getPrimary() : ThemeManager.getTextSecondary());
        p.add(lbl, BorderLayout.CENTER);

        // Indicador lateral activo
        if (active) {
            JPanel indicator = new JPanel();
            indicator.setBackground(ThemeManager.getPrimary());
            indicator.setPreferredSize(new Dimension(4, 0));
            p.add(indicator, BorderLayout.WEST);
        }

        p.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (text.startsWith("Cambiar Rol")) {
                    userRole = userRole.equalsIgnoreCase("Administrador") ? "Recepcionista" : "Administrador";
                    initUI();
                    cardLayout.show(contentPanel, currentView);
                    return;
                }
                
                if (text.equals("Cerrar Sesión")) {
                    dispose();
                    new LoginFrame().setVisible(true);
                    return;
                }
                
                // Solo cambiar si la vista existe
                if (text.equals("Tablero") || text.equals("Reserva") || text.equals("Gestión de Habitaciones")) {
                    currentView = text;
                    cardLayout.show(contentPanel, text);
                    refreshSidebar();
                } else {
                    JOptionPane.showMessageDialog(MainFrame.this, "El módulo '" + text + "' está en desarrollo.", "Próximamente", JOptionPane.INFORMATION_MESSAGE);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!text.equals(currentView)) {
                    p.setBackground(ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT ? new Color(0xF1F5F9) : new Color(0x2D3748));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!text.equals(currentView)) {
                    p.setBackground(ThemeManager.getPanelBackground());
                } else {
                    p.setBackground(ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT ? new Color(0xE8F1FD) : new Color(0x334155));
                }
            }
        });

        return p;
    }

    private void refreshSidebar() {
        sidebarPanel.removeAll();
        // Re-generar sidebar con el currentView actualizado
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        
        sidebarPanel.add(Box.createVerticalStrut(20));
        JLabel logoBrand = new JLabel(" HOTEL SANTA ANA ");
        logoBrand.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoBrand.setForeground(ThemeManager.getPrimary());
        logoBrand.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(logoBrand);
        sidebarPanel.add(Box.createVerticalStrut(30));

        String[] items = { "Tablero", "Gestión de Habitaciones", "Reserva", "Punto de venta", "Historial", "Reporte" };
        for (String item : items) {
            sidebarPanel.add(crearBotonSidebar(item));
            sidebarPanel.add(Box.createVerticalStrut(8));
        }
        sidebarPanel.add(Box.createVerticalGlue());
        sidebarPanel.add(crearBotonSidebar("Cambiar Rol (" + userRole + ")"));
        sidebarPanel.add(Box.createVerticalStrut(8));
        sidebarPanel.add(crearBotonSidebar("Cerrar Sesión"));
        sidebarPanel.add(Box.createVerticalStrut(20));
        
        sidebarPanel.revalidate();
        sidebarPanel.repaint();
    }

    @Override
    public void onThemeChanged() {
        SwingUtilities.invokeLater(() -> {
            initUI();
            cardLayout.show(contentPanel, currentView);
        });
    }
}
