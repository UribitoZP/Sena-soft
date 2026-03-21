package com.santaana.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import com.santaana.util.ThemeManager;

public class NotificacionPanel extends JPanel {

    private static final Color ROJO_WARNING         = new Color(0xFF2020);
    private static final Color VERDE_RECORDATORIO   = new Color(0x33C24D);
    private static final Color AZUL_TODO            = new Color(0x2A7BF5);
    private static final Color GRIS_MANTENIMIENTO   = new Color(0x6B6D78);
    private static final Color NARANJA_STOCK        = new Color(0xFF8C00);
    private static final Color ROJO_WARNING_BG      = new Color(0xFFEAEA);
    private static final Color VERDE_RECORDATORIO_BG= new Color(0xE6F9EA);
    private static final Color AZUL_TODO_BG         = new Color(0xEAF1FF);
    private static final Color GRIS_MANTENIMIENTO_BG= new Color(0xF0F0F3);
    private static final Color NARANJA_STOCK_BG     = new Color(0xFFF3E0);

    private JPanel list;

    private Color getBorde()     { return ThemeManager.getBorder(); }
    private Color getPrimario()  { return ThemeManager.getPrimary(); }
    private Color getLabel()     { return ThemeManager.getTextSecondary(); }
    private Color getBackgroundCol() { return ThemeManager.getBackground(); }
    private Color getPanelCol()  { return ThemeManager.getPanelBackground(); }
    private Color getTextCol()   { return ThemeManager.getTextPrimary(); }

    public NotificacionPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(crearNavbar(), BorderLayout.NORTH);

        list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);

        loadNotifications("Todo");

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        wrapper.add(list, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        add(scroll, BorderLayout.CENTER);
    }

    private JPanel crearNavbar() {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 0));
        nav.setOpaque(false);

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
        tab.addChangeListener(e -> tab.setForeground(
            tab.isSelected() ? getTextCol() : getLabel()));
        return tab;
    }

    private void loadNotifications(String filter) {
        list.removeAll();

        if (filter.equals("Todo") || filter.equals("Importantes")) {
            list.add(notificationCard(ROJO_WARNING,  "alerta.png",    ROJO_WARNING_BG));
            list.add(Box.createVerticalStrut(12));
            list.add(notificationCard(NARANJA_STOCK, "stock.png",     NARANJA_STOCK_BG));
            list.add(Box.createVerticalStrut(12));
        }
        if (filter.equals("Todo") || filter.equals("Recordatorios")) {
            list.add(notificationCard(VERDE_RECORDATORIO, "controlar.png", VERDE_RECORDATORIO_BG));
            list.add(Box.createVerticalStrut(12));
            list.add(notificationCard(AZUL_TODO,     "tiempo.png",    AZUL_TODO_BG));
            list.add(Box.createVerticalStrut(12));
        }
        if (filter.equals("Todo")) {
            list.add(notificationCard(GRIS_MANTENIMIENTO, "mecanico.png", GRIS_MANTENIMIENTO_BG));
            list.add(Box.createVerticalStrut(12));
        }

        list.revalidate();
        list.repaint();
    }

    private JPanel notificationCard(Color lineColor, String iconPath, Color bgColor) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(getPanelCol());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getBorde(), 2, true),
            BorderFactory.createEmptyBorder(10, 2, 10, 10)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JPanel line = new JPanel();
        line.setBackground(lineColor);
        line.setPreferredSize(new Dimension(4, 10));

        JPanel iconWrapper = new JPanel(new GridBagLayout());
        iconWrapper.setBackground(ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT ? bgColor : bgColor.darker().darker());
        iconWrapper.setPreferredSize(new Dimension(50, 50));

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/resources/" + iconPath));
            Image img = icon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
            iconWrapper.add(new JLabel(new ImageIcon(img)));
        } catch (Exception e) {
            iconWrapper.add(new JLabel("📋"));
        }

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel title = new JLabel("Título de notificación");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(getTextCol());

        JLabel desc = new JLabel("Descripción corta de la notificación.");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        desc.setForeground(getLabel());

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(desc);

        JPanel center = new JPanel(new BorderLayout(10, 0));
        center.setOpaque(false);
        center.add(iconWrapper, BorderLayout.WEST);
        center.add(textPanel,   BorderLayout.CENTER);

        card.add(line,   BorderLayout.WEST);
        card.add(center, BorderLayout.CENTER);

        return card;
    }
}