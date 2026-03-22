package com.santaana.view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.border.MatteBorder;

import com.santaana.util.ThemeManager;

public class NotificacionFrame extends JFrame implements ThemeManager.ThemeListener {

    private Color getBorde() { return ThemeManager.getBorder(); }
    private Color getPrimario() { return ThemeManager.getPrimary(); }
    private Color getLabel() { return ThemeManager.getTextSecondary(); }
    private Color getBackgroundCol() { return ThemeManager.getBackground(); }
    private Color getPanelCol() { return ThemeManager.getPanelBackground(); }
    private Color getTextCol() { return ThemeManager.getTextPrimary(); }

    private static final Color ROJO_WARNING = new Color(0xFF2020);
    private static final Color VERDE_RECORDATORIO = new Color(0x33C24D);
    private static final Color AZUL_TODO = new Color(0x2A7BF5);
    private static final Color GRIS_MANTENIMIENTO = new Color(0x6B6D78);
    private static final Color NARANJA_STOCK = new Color(0xFF8C00);
    private static final Color ROJO_WARNING_BG        = new Color(0xFFEAEA);
    private static final Color VERDE_RECORDATORIO_BG  = new Color(0xE6F9EA);
    private static final Color AZUL_TODO_BG           = new Color(0xEAF1FF);
    private static final Color GRIS_MANTENIMIENTO_BG  = new Color(0xF0F0F3);
    private static final Color NARANJA_STOCK_BG       = new Color(0xFFF3E0);

    private final String role;
    private JPanel list; 

    public NotificacionFrame(String role, String welcomeMessage) {
        this.role = role;
        setTitle("Hotel Santa Ana — Notificaciones");
        setSize(1100, 720);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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

        JLabel logo = new JLabel();
        try {
            ImageIcon icon = new ImageIcon("resources/logo.png");
            Image scaledImage = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            logo.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            System.err.println("Logo no encontrado");
        }

        JLabel nombre = new JLabel("<html><b style='font-size:13px; color:" + (ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT ? "#1F2937" : "#F3F4F6") + "'>HOTEL SANTA ANA</b><br>"+ "<span style='color:" + (ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT ? "#6B84A0" : "#94A3B8") + ";font-size:9px'>Sistema de gestion hotelera</span></html>");

        left.add(logo);
        left.add(nombre);
        JButton notifBtn = new JButton() {
            private boolean selected = true;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

                if (selected) {

                    g2.setColor(new Color(0xE8F1FD));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(new Color(0x3A7BD5));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 8, 8);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(230, 240, 255));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                } else {
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };



        JPanel mid = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 14));
        mid.setOpaque(false);
        mid.add(actionBtn("+ Nueva reserva", getPrimario(), Color.WHITE));
        mid.add(actionBtn("$  Venta rapida",   
            ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT ? new Color(0xE8F1FD) : new Color(0x334155), 
            getPrimario()));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 16));
        right.setOpaque(false);
        

        right.add(userPanel());

        JButton themeToggle = new JButton(ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT ? "🌙" : "☀️");
        themeToggle.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        themeToggle.setContentAreaFilled(false);
        themeToggle.setBorderPainted(false);
        themeToggle.setFocusPainted(false);
        themeToggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        themeToggle.addActionListener(e -> ThemeManager.toggleTheme());
        right.add(themeToggle);
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
        rol.setForeground(getLabel());
        name.setForeground(getTextCol());
        p.add(name);
        p.add(rol);
        return p;
    }

    private JPanel sidebar(){
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side,BoxLayout.Y_AXIS));
        side.setBackground(getPanelCol());
        side.setPreferredSize(new Dimension(190,0));
        side.setBorder(new MatteBorder(0,0,0,1,getBorde()));

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
            side.add(sideBtn(items[i], false));
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
        lbl.setForeground(active?getPrimario():getLabel());

        if(active)
            p.setBackground(ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT ? new Color(0xE8F1FD) : new Color(0x2D3748));
        else
            p.setBackground(getPanelCol());

        p.add(lbl,BorderLayout.CENTER);

        return p;
    }
    private JPanel center() {
        
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(new Color(0xE8F1FD));
        container.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        container.add(navbar(), BorderLayout.NORTH);

        list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(getBackgroundCol());

        loadNotifications("Todo");

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(getBackgroundCol());
        wrapper.setBorder(BorderFactory.createEmptyBorder(16,0,0,0));
        wrapper.add(list, BorderLayout.NORTH);

        container.add(wrapper, BorderLayout.CENTER);
        return container;
    }
        private void loadNotifications(String filter) {
        list.removeAll();

        if (filter.equals("Todo") || filter.equals("Importantes")) {
            list.add(notificationCard(ROJO_WARNING,   "alerta.png",  ROJO_WARNING_BG));
            list.add(Box.createVerticalStrut(12));
            list.add(notificationCard(NARANJA_STOCK,  "stock.png",   NARANJA_STOCK_BG));
            list.add(Box.createVerticalStrut(12));
        }

        if (filter.equals("Todo") || filter.equals("Recordatorios")) {
            list.add(notificationCard(VERDE_RECORDATORIO, "controlar.png", VERDE_RECORDATORIO_BG));
            list.add(Box.createVerticalStrut(12));
            list.add(notificationCard(AZUL_TODO, "tiempo.png", AZUL_TODO_BG));
            list.add(Box.createVerticalStrut(12));
        }

        if (filter.equals("Todo")) {
            list.add(notificationCard(GRIS_MANTENIMIENTO, "mecanico.png", GRIS_MANTENIMIENTO_BG));
            list.add(Box.createVerticalStrut(12));
        }

        list.revalidate();
        list.repaint();
    }

   
 

    private JPanel navbar() {

        Color bg = getBackgroundCol();

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(bg);

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 0));
        nav.setBackground(bg);

        JToggleButton todo          = createTab("Todo");
        JToggleButton importantes   = createTab("Importantes");
        JToggleButton recordatorios = createTab("Recordatorios");

        ButtonGroup group = new ButtonGroup();
        group.add(todo);
        group.add(importantes);
        group.add(recordatorios);

        todo.setSelected(true);

        todo.addActionListener(e          -> loadNotifications("Todo"));
        importantes.addActionListener(e   -> loadNotifications("Importantes"));
        recordatorios.addActionListener(e -> loadNotifications("Recordatorios"));

        nav.add(todo);
        nav.add(importantes);
        nav.add(recordatorios);

        JSeparator divider = new JSeparator();
        divider.setForeground(getBorde());

        container.add(nav,     BorderLayout.NORTH);
        container.add(divider, BorderLayout.SOUTH);

        return container;
    }
    
    private JToggleButton createTab(String text) {

        JToggleButton tab = new JToggleButton(text) {

            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (isSelected()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(getPrimario());
                    g2.fillRect(0, getHeight() - 2, getWidth(), 2);
                }
            }
        };

        tab.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tab.setForeground(getLabel());
        tab.setBorderPainted(false);
        tab.setFocusPainted(false);
        tab.setContentAreaFilled(false);
        tab.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        tab.addChangeListener(e -> {
            if (tab.isSelected())
                tab.setForeground(getTextCol());
            else
                tab.setForeground(getLabel());
            }
        );

        return tab;
    }

    private JPanel notificationCard(Color lineColor, String iconPath, Color bgColor) {

        JPanel card = new JPanel(new BorderLayout(12,0));
        card.setBackground(getPanelCol());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getBorde(), 2, true),
            BorderFactory.createEmptyBorder(10,2,10,10)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE,80));

        
        JPanel line = new JPanel();
        line.setBackground(lineColor);
        line.setPreferredSize(new Dimension(4,10));

        
        JPanel iconWrapper = new JPanel(new GridBagLayout());
        iconWrapper.setBackground(ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT ? bgColor : bgColor.darker().darker());
        iconWrapper.setPreferredSize(new Dimension(50,50));
        iconWrapper.setMaximumSize(new Dimension(50,50));

        ImageIcon icon = new ImageIcon(getClass().getResource("/resources/" + iconPath));
        Image img = icon.getImage().getScaledInstance(25,25,Image.SCALE_SMOOTH);
        JLabel iconLabel = new JLabel(new ImageIcon(img));

        iconWrapper.add(iconLabel);

        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel,BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel title = new JLabel("Título de notificación");
        title.setFont(new Font("Segoe UI",Font.BOLD,13));
        title.setForeground(getTextCol());

        JLabel desc = new JLabel("Descripción corta de la notificación.");
        desc.setFont(new Font("Segoe UI",Font.PLAIN,12));
        desc.setForeground(getLabel());

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(desc);

        JPanel center = new JPanel(new BorderLayout(10,0));
        center.setOpaque(false);
        center.add(iconWrapper,BorderLayout.WEST);
        center.add(textPanel,BorderLayout.CENTER);

        card.add(line,BorderLayout.WEST);
        card.add(center,BorderLayout.CENTER);

        return card;
    }


}