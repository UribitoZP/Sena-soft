package com.santaana.view;

import com.santaana.controller.LoginController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class LoginFrame extends JFrame {
    private JComboBox<String> roleComboBox;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private LoginController controller;
    private Point initialClick;

    public LoginFrame() {
        initComponents();
        controller = new LoginController(this);
    }

    private void initComponents() {
        setTitle("Hotel Santa Ana - Gestión Hotelera");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 550);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);
        // Permite que las esquinas del panel principal se vean redondeadas si el panel
        // las tiene
        setBackground(new Color(0, 0, 0, 0));

        // Panel principal con fondo y capacidad de arrastre
        BackgroundPanel mainContainer = new BackgroundPanel("resources/bg.png");
        mainContainer.setLayout(new GridBagLayout());
        add(mainContainer);

        // Habilitar arrastre de ventana
        mainContainer.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });
        mainContainer.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int thisX = getLocation().x;
                int thisY = getLocation().y;
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;
                setLocation(thisX + xMoved, thisY + yMoved);
            }
        });

        // Panel de login (Glassmorphism avanzado)
        JPanel glassPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fondo semi-transparente con degradado sutil
                g2.setColor(new Color(255, 255, 255, 220));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 40, 40));

                // Borde de cristal
                g2.setColor(new Color(255, 255, 255, 150));
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2, 40, 40));

                g2.dispose();
            }
        };
        glassPanel.setOpaque(false);
        glassPanel.setPreferredSize(new Dimension(420, 520));
        glassPanel.setLayout(new BoxLayout(glassPanel, BoxLayout.Y_AXIS));
        glassPanel.setBorder(new EmptyBorder(10, 40, 20, 40));

        // Barra superior con botones de Cerrar y Minimizar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        topBar.setOpaque(false);
        topBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JButton minimizeBtn = createControlBtn("—", new Color(20, 33, 61, 150));
        minimizeBtn.addActionListener(e -> setState(Frame.ICONIFIED));

        JButton closeBtn = createControlBtn("x", new Color(231, 76, 60, 200));
        closeBtn.addActionListener(e -> System.exit(0));

        topBar.add(minimizeBtn);
        topBar.add(closeBtn);
        glassPanel.add(topBar);

        // Branding
        JLabel logoLabel = new JLabel();
        try {
            ImageIcon icon = new ImageIcon("resources/logo.png");
            Image scaledImage = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
        }
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        glassPanel.add(logoLabel);
        glassPanel.add(Box.createVerticalStrut(5));

        JLabel titleLabel = new JLabel("SANTA ANA");
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 26));
        titleLabel.setForeground(new Color(20, 33, 61));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        glassPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Gestión de Hospitalidad Premium");
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        subtitleLabel.setForeground(new Color(120, 120, 120));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        glassPanel.add(subtitleLabel);
        glassPanel.add(Box.createVerticalStrut(25));

        // Campos de entrada (Centrados)
        createCenteredLabel(glassPanel, "ROL DE USUARIO");
        roleComboBox = new JComboBox<>(new String[] { "Seleccionar...", "Administrador", "Recepcionista" });
        styleComponent(roleComboBox);
        glassPanel.add(roleComboBox);
        glassPanel.add(Box.createVerticalStrut(10));

        createCenteredLabel(glassPanel, "IDENTIFICADOR");
        usernameField = new JTextField();
        usernameField.setHorizontalAlignment(JTextField.CENTER);
        styleComponent(usernameField);
        glassPanel.add(usernameField);
        glassPanel.add(Box.createVerticalStrut(10));

        createCenteredLabel(glassPanel, "CONTRASEÑA");
        passwordField = new JPasswordField();
        passwordField.setHorizontalAlignment(JTextField.CENTER);
        styleComponent(passwordField);
        glassPanel.add(passwordField);
        glassPanel.add(Box.createVerticalStrut(30));

        // Botón de Ingreso Premium con Efectos
        loginButton = new JButton("ACCEDER AL PORTAL") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color c1 = new Color(252, 163, 17);
                Color c2 = new Color(214, 137, 12);

                if (getModel().isPressed()) {
                    g2.setPaint(new GradientPaint(0, 0, c2, getWidth(), 0, c1));
                } else if (getModel().isRollover()) {
                    g2.setPaint(new GradientPaint(0, 0, c1.brighter(), getWidth(), 0, c2.brighter()));
                } else {
                    g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), 0, c2));
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2.setColor(new Color(20, 33, 61));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setContentAreaFilled(false);
        glassPanel.add(loginButton);

        glassPanel.add(Box.createVerticalStrut(15));

        JLabel footerLabel = new JLabel("¿Olvidó sus credenciales? Contacte a soporte");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        footerLabel.setForeground(new Color(150, 150, 150));
        footerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        glassPanel.add(footerLabel);

        mainContainer.add(glassPanel);
    }

    private JButton createControlBtn(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(color);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void createCenteredLabel(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 10));
        label.setForeground(new Color(100, 100, 100));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
    }

    private void styleComponent(JComponent c) {
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        c.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        c.setBackground(new Color(250, 250, 250));

        if (c instanceof JComboBox) {
            DefaultListCellRenderer renderer = new DefaultListCellRenderer();
            renderer.setHorizontalAlignment(SwingConstants.CENTER);
            ((JComboBox<?>) c).setRenderer(renderer);
        }

        // Borde inicial
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        // Efecto Focus
        c.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                c.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(252, 163, 17)),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                c.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)));
            }
        });
    }

    public String getSelectedRole() {
        return (String) roleComboBox.getSelectedItem();
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public void addLoginListener(ActionListener l) {
        loginButton.addActionListener(l);
    }

    public void showMessage(String m, String t, int type) {
        JOptionPane.showMessageDialog(this, m, t, type);
    }

    class BackgroundPanel extends JPanel {
        private Image img;

        public BackgroundPanel(String path) {
            try {
                img = new ImageIcon(path).getImage();
            } catch (Exception e) {
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) {
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                // Degradado de superposición para mejorar contraste
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, new Color(20, 33, 61, 100),
                        getWidth(), getHeight(), new Color(0, 0, 0, 180)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        }
    }
}
