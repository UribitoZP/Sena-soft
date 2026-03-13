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
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.border.MatteBorder;

public class NotificacionFrame extends JFrame {

    private static final Color COLOR_BORDE    = new Color(0xDDE8F5);
    private static final Color COLOR_PRIMARIO = new Color(0x3A7BD5);
    private static final Color COLOR_LABEL    = new Color(0x6B84A0);
    private static final Color AMARILLO_IMPORTANTE = new Color(0xFFE033);
    private static final Color ROJO_WARNING = new Color(0xFF2020);
    private static final Color VERDE_RECORDATORIO = new Color(0x33C24D);
    private static final Color AZUL_TODO = new Color(0x2A7BF5);
    private static final Color GRIS_MANTENIMIENTO = new Color(0x6B6D78);
    private static final Color NARANJA_STOCK = new Color(0xFF8C00);

    private final String role;

    public NotificacionFrame(String role, String welcomeMessage) {
        this.role = role;
        setTitle("Hotel Santa Ana — Gestión de Habitaciones");
        setSize(1100, 720);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(0xE8F1FD));
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

        ImageIcon rawIcon = new ImageIcon("resources/iconNoti.png");
        Image scaled = rawIcon.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
        notifBtn.setIcon(new ImageIcon(scaled));

        notifBtn.setContentAreaFilled(false);
        notifBtn.setBorderPainted(false);
        notifBtn.setFocusPainted(false);
        notifBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        notifBtn.setPreferredSize(new Dimension(40, 34));

        left.add(notifBtn);

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
        lbl.setForeground(active?COLOR_PRIMARIO:COLOR_LABEL);

        if(active)
            p.setBackground(new Color(0xE8F1FD));
        else
            p.setBackground(Color.WHITE);

        p.add(lbl,BorderLayout.CENTER);

        return p;
    }
    private JPanel center() {

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(new Color(0xE8F1FD));
        container.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        container.add(navbar(), BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(new Color(0xE8F1FD));

        list.add(notificationCard(ROJO_WARNING,"alerta.png"));
        list.add(Box.createVerticalStrut(12));

        list.add(notificationCard(NARANJA_STOCK,"stock.png"));
        list.add(Box.createVerticalStrut(12));

        list.add(notificationCard(VERDE_RECORDATORIO,"controlar.png"));
        list.add(Box.createVerticalStrut(12));


        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(0xE8F1FD));
        wrapper.setBorder(BorderFactory.createEmptyBorder(16,0,0,0));
        wrapper.add(list, BorderLayout.NORTH);

        container.add(wrapper, BorderLayout.CENTER);

        return container;
    }

   
 

    private JPanel navbar() {

        Color bg = new Color(0xE8F1FD);

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(bg);

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 0));
        nav.setBackground(bg);
        



        JToggleButton todo = createTab("Todo");
        JToggleButton importantes = createTab("Importantes");
        JToggleButton recordatorios = createTab("Recordatorios");

        ButtonGroup group = new ButtonGroup();
        group.add(todo);
        group.add(importantes);
        group.add(recordatorios);

        todo.setSelected(true);

        nav.add(todo);
        nav.add(importantes);
        nav.add(recordatorios);

        JSeparator divider = new JSeparator();
        divider.setForeground(new Color(220,220,220));

        container.add(nav, BorderLayout.NORTH);
        container.add(divider, BorderLayout.SOUTH);

        return container;
    }
    
    private JToggleButton createTab(String text) {

        JToggleButton tab = new JToggleButton(text) {

            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (isSelected()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(COLOR_PRIMARIO);
                    g2.fillRect(0, getHeight() - 2, getWidth(), 2);
                }
            }
        };

        tab.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tab.setForeground(COLOR_LABEL);
        tab.setBorderPainted(false);
        tab.setFocusPainted(false);
        tab.setContentAreaFilled(false);
        tab.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        tab.addChangeListener(e -> {
            if (tab.isSelected())
                tab.setForeground(Color.BLACK);
            else
                tab.setForeground(COLOR_LABEL);
            }
        );

        return tab;
    }
    private JPanel notificationCard(Color color, Icon icon) {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220,225,230),1,true),
            BorderFactory.createEmptyBorder(14,14,14,14)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        
        JPanel line = new JPanel();
        line.setBackground(color);
        line.setPreferredSize(new Dimension(4,0));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0,8,0,12));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel title = new JLabel("Título de notificación");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel desc = new JLabel("Descripción breve de la notificación para mostrar información.");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        desc.setForeground(new Color(120,140,160));

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(desc);

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.add(iconLabel, BorderLayout.WEST);
        content.add(textPanel, BorderLayout.CENTER);

        card.add(line, BorderLayout.WEST);
        card.add(content, BorderLayout.CENTER);

        return card;
    }
    private JPanel notificationCard(Color lineColor, String iconPath) {

        JPanel card = new JPanel(new BorderLayout(12,0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,225,230)),
                BorderFactory.createEmptyBorder(14,14,14,14)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE,80));

        
        JPanel line = new JPanel();
        line.setBackground(lineColor);
        line.setPreferredSize(new Dimension(4,0));

        
        JPanel iconWrapper = new JPanel(new GridBagLayout());
        iconWrapper.setBackground(new Color(240,243,247));
        iconWrapper.setPreferredSize(new Dimension(40,40));
        iconWrapper.setMaximumSize(new Dimension(40,40));

        ImageIcon icon = new ImageIcon(getClass().getResource("/resources/" + iconPath));
        Image img = icon.getImage().getScaledInstance(25,25,Image.SCALE_SMOOTH);
        JLabel iconLabel = new JLabel(new ImageIcon(img));

        iconWrapper.add(iconLabel);

        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel,BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel title = new JLabel("Título de notificación");
        title.setFont(new Font("Segoe UI",Font.BOLD,13));

        JLabel desc = new JLabel("Descripción corta de la notificación.");
        desc.setFont(new Font("Segoe UI",Font.PLAIN,12));
        desc.setForeground(new Color(120,140,160));

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