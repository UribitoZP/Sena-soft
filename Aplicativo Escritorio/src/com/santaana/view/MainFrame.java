package com.santaana.view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;

import com.santaana.util.ThemeManager;

public class MainFrame extends JFrame implements ThemeManager.ThemeListener {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JPanel sidebarPanel;
    private String userRole;
    private String currentView = "Tablero";
    private int idUsuario;
    private ReservaPanel  reservaPanel;
    private TableroPanel  tableroPanel;

    public MainFrame(String role, String welcomeMessage, int idUsuario) {
        this.userRole  = role;
        this.idUsuario = idUsuario;
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
        tableroPanel = new TableroPanel(userRole, () -> abrirNuevaReserva(), () -> refrescarTodo());
        contentPanel.add(tableroPanel, "Tablero");
        reservaPanel = new ReservaPanel(userRole);
        reservaPanel.setOnEstadoCambiado(() -> refrescarTodo());
        contentPanel.add(reservaPanel, "Reserva");
        contentPanel.add(new GestHabitacionPanel(userRole), "Gestión de Habitaciones");
        contentPanel.add(new NotificacionPanel(), "Notificaciones");

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

        // Centro: Botón Nueva Reserva
        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 12));
        center.setOpaque(false);
        JButton btnNuevaReserva = new JButton("+ Nueva Reserva") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getPrimary());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnNuevaReserva.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNuevaReserva.setForeground(Color.WHITE);
        btnNuevaReserva.setContentAreaFilled(false);
        btnNuevaReserva.setBorderPainted(false);
        btnNuevaReserva.setFocusPainted(false);
        btnNuevaReserva.setPreferredSize(new Dimension(150, 36));
        btnNuevaReserva.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnNuevaReserva.addActionListener(e -> abrirNuevaReserva());
        center.add(btnNuevaReserva);
        bar.add(center, BorderLayout.CENTER);

        // Derecha: Notificaciones, Tema y Usuario
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        right.setOpaque(false);

        JButton notifBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                boolean isActive = currentView.equals("Notificaciones");
                boolean isDark = ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK;

                if (isActive) {
                    g2.setColor(isDark ? new Color(133, 183, 235, 38) : new Color(24, 95, 165, 25));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }

                int cx = getWidth() / 2;
                int cy = getHeight() / 2;

                Color iconColor = isActive
                    ? (isDark ? new Color(0x85B7EB) : new Color(0x185FA5))
                    : ThemeManager.getTextSecondary();

                g2.setColor(iconColor);
                g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                g2.drawLine(cx, cy - 9, cx, cy - 7);
                g2.drawArc(cx - 7, cy - 8, 14, 14, 0, 180);
                g2.drawLine(cx - 7, cy - 1, cx - 7, cy + 5);
                g2.drawLine(cx + 7, cy - 1, cx + 7, cy + 5);
                g2.drawLine(cx - 7, cy + 5, cx + 7, cy + 5);
                g2.drawArc(cx - 2, cy + 5, 4, 4, 180, 180);

                g2.dispose();
            }
        };

        notifBtn.setContentAreaFilled(false);
        notifBtn.setBorderPainted(false);
        notifBtn.setFocusPainted(false);
        notifBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        notifBtn.setPreferredSize(new Dimension(36, 36));
        notifBtn.addActionListener(e -> {
            currentView = "Notificaciones";
            cardLayout.show(contentPanel, "Notificaciones");
            refreshSidebar();
            notifBtn.repaint();
        });



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

        right.add(notifBtn);
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


        String[] items = { "Tablero", "Gestión de Habitaciones", "Reserva", "Historial", "Reporte" };
        for (String item : items) {
            if(userRole.equalsIgnoreCase("Recepcionista")&& item.equals("Gestión de Habitaciones") ) {
                continue;
            }
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
            p.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 13));
        lbl.setForeground(active ? ThemeManager.getPrimary() : ThemeManager.getTextSecondary());

        JPanel textWrapper = new JPanel(new BorderLayout());
        textWrapper.setOpaque(false);
        textWrapper.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0)); 
        textWrapper.add(lbl, BorderLayout.CENTER);

        p.add(textWrapper, BorderLayout.CENTER);
        // Indicador lateral activo
        if (active) {
            JPanel indicator = new JPanel();
            indicator.setBackground(ThemeManager.getPrimary());
            indicator.setPreferredSize(new Dimension(2, 0));
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
                if (text.equals("Tablero") || text.equals("Reserva") || text.equals("Gestión de Habitaciones") || text.equals("Notificaciones")) {
                    currentView = text;
                    cardLayout.show(contentPanel, text);
                    refreshSidebar();
                    repaint();
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

        String[] items = { "Tablero", "Gestión de Habitaciones", "Reserva", "Historial", "Reporte" };
        for (String item : items) {
            if(userRole.equalsIgnoreCase("Recepcionista")&& item.equals("Gestión de Habitaciones") ) {
                continue;
            }
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

    private void abrirNuevaReserva() {
        NuevaReservaDialog dialog = new NuevaReservaDialog(this, idUsuario);
        dialog.setVisible(true);
        refrescarTodo();
    }

    public void refrescarTodo() {
        if (tableroPanel  != null) tableroPanel.refreshUI();
        if (reservaPanel  != null) reservaPanel.refreshUI();
    }

    @Override
    public void onThemeChanged() {
        SwingUtilities.invokeLater(() -> {
            initUI();
            cardLayout.show(contentPanel, currentView);
        });
    }
}
