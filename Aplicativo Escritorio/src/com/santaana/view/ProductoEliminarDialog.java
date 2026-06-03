package com.santaana.view;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

import javax.swing.*;

import com.santaana.util.ThemeManager;

public class ProductoEliminarDialog extends JDialog {

    public ProductoEliminarDialog(
            Window padre,
            String nombreProducto,
            Runnable onConfirmar) {

        super(
                padre,
                "Eliminar Producto",
                ModalityType.APPLICATION_MODAL);

        setUndecorated(true);
        setSize(400, 260);
        setLocationRelativeTo(padre);
        setBackground(new Color(0, 0, 0, 0));
        getRootPane().setOpaque(false);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0, 0, 0, 55));

                g2.fill(new RoundRectangle2D.Double(
                        4,
                        4,
                        getWidth() - 4,
                        getHeight() - 4,
                        20,
                        20));

                g2.setColor(ThemeManager.getPanelBackground());

                g2.fill(new RoundRectangle2D.Double(
                        0,
                        0,
                        getWidth() - 4,
                        getHeight() - 4,
                        20,
                        20));

                g2.dispose();
            }
        };

        root.setOpaque(false);
        root.setBorder(BorderFactory.createEmptyBorder(32,32,28, 32));


        JPanel texto = new JPanel();

        texto.setOpaque(false);

        texto.setLayout(new BoxLayout(texto, BoxLayout.Y_AXIS));

        texto.setBorder(BorderFactory.createEmptyBorder( 14,0,24,0));

        JLabel lblTitulo =new JLabel("Eliminar producto");

        lblTitulo.setFont(new Font("Segoe UI",Font.BOLD,18));

        lblTitulo.setForeground(
                ThemeManager.getTextPrimary());

        JLabel lblMsg = new JLabel(
            "<html>¿Estás seguro de que deseas eliminar<br><b>" + nombreProducto + "</b>? Esta acción no se puede deshacer.</html>");

        lblMsg.setFont(new Font("Segoe UI",Font.PLAIN,13));

        lblMsg.setForeground(ThemeManager.getTextSecondary());

        texto.add(lblTitulo);
        texto.add(Box.createVerticalStrut(8));
        texto.add(lblMsg);

        JPanel botones = new JPanel( new GridLayout(1,2,12,0));

        botones.setOpaque(false);

        Color rojoEliminar =
            new Color(220, 38, 38);

        JButton btnCancelar =botonEliminar(
            "Cancelar",
            ThemeManager.getBorder(),
            ThemeManager.getTextSecondary(),
            false
        );

        JButton btnEliminar =    botonEliminar(
            "Sí, eliminar",
            rojoEliminar,              
             Color.WHITE,
            true
        );

        btnCancelar.addActionListener( e -> dispose());

        btnEliminar.addActionListener(e -> {
            onConfirmar.run();
            dispose();
        });

        botones.add(btnCancelar);
        botones.add(btnEliminar);
        root.add(texto, BorderLayout.CENTER);
        root.add(botones, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private static JButton botonEliminar(
            String txt,
            Color color,
            Color fg,
            boolean filled) {

        JButton b = new JButton(txt) {

            @Override
            protected void paintComponent(Graphics g) {

                Graphics2D g2 =
                        (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                if (filled) {

                    g2.setColor(
                            getModel().isRollover()
                                    ? color.darker()
                                    : color);

                    g2.fill(
                            new RoundRectangle2D.Double(
                                    0,
                                    0,
                                    getWidth(),
                                    getHeight(),
                                    10,
                                    10));

                } else {

                    g2.setColor(
                            ThemeManager.getPanelBackground());

                    g2.fill(
                            new RoundRectangle2D.Double(
                                    0,
                                    0,
                                    getWidth(),
                                    getHeight(),
                                    10,
                                    10));

                    g2.setColor(color);

                    g2.draw(
                            new RoundRectangle2D.Double(
                                    0.5,
                                    0.5,
                                    getWidth() - 1,
                                    getHeight() - 1,
                                    10,
                                    10));
                }

                g2.dispose();

                super.paintComponent(g);
            }
        };

        b.setFont(new Font("Segoe UI",Font.BOLD,13));

        b.setForeground(fg);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);

        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        b.setPreferredSize(new Dimension(0, 42));

        return b;
    }
}