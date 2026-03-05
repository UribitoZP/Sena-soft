package com.santaana.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class HomeFrame extends JFrame {
    private String role;
    private String welcomeMessage;

    public HomeFrame(String role, String welcomeMessage) {
        this.role = role;
        this.welcomeMessage = welcomeMessage;
        initComponents();
    }

    private void initComponents() {
        setTitle("Panel de Control - " + role);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 350);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);

        // Fondo degradado
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(20, 33, 61), 0, getHeight(),
                        new Color(44, 62, 80));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        add(mainPanel);

        // Icono de perfil dinámico
        JLabel iconLabel = new JLabel("👤");
        iconLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 72));
        iconLabel.setForeground(
                role.equalsIgnoreCase("Administrador") ? new Color(52, 152, 219) : new Color(155, 89, 182));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(iconLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        centerPanel.add(Box.createVerticalStrut(20));

        JLabel roleLabel = new JLabel(role.toUpperCase());
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        roleLabel.setForeground(new Color(252, 163, 17));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(roleLabel);

        centerPanel.add(Box.createVerticalStrut(10));

        JLabel welcomeLabel = new JLabel(
                "<html><body style='width: 400px; text-align: center; color: white; font-family: Segoe UI, Arial;'>"
                        + welcomeMessage + "</body></html>");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(welcomeLabel);

        // Botones de acción
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        footer.setOpaque(false);
        mainPanel.add(footer, BorderLayout.SOUTH);

        JButton logoutButton = createStyledButton("CERRAR SESIÓN", new Color(231, 76, 60));
        logoutButton.addActionListener(e -> {
            this.dispose();
            new LoginFrame().setVisible(true);
        });
        footer.add(logoutButton);

        JButton continueButton = createStyledButton("CONTINUAR", new Color(24, 188, 156));
        continueButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Redirigiendo al dashboard completo...");
        });
        footer.add(continueButton);
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(160, 40));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
