package com.santaana.view;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.santaana.dao.ProductoDAO;
import com.santaana.model.Producto;
import com.santaana.util.ThemeManager;

public class ProductoAgregarDialog extends JDialog {

    public ProductoAgregarDialog(
            Window padre,
            ProductoDAO productoDAO,
            DefaultTableModel model,
            Runnable onSuccess) {

        super(
                padre,
                "Nuevo Producto",
                ModalityType.APPLICATION_MODAL);

        setUndecorated(true);
        setSize(460, 520);
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
        root.setBorder(
                BorderFactory.createEmptyBorder(
                        28,
                        32,
                        24,
                        32));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        0,
                        22,
                        0));

        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(
                new BoxLayout(
                        titulos,
                        BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel("Nuevo Producto");

        lblTitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        20));

        lblTitulo.setForeground(
                ThemeManager.getTextPrimary());

        JLabel lblSub = new JLabel(
                "Completa los datos del nuevo producto");

        lblSub.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12));

        lblSub.setForeground(
                ThemeManager.getTextSecondary());

        titulos.add(lblTitulo);
        titulos.add(Box.createVerticalStrut(3));
        titulos.add(lblSub);

        header.add(titulos, BorderLayout.CENTER);

        JTextField txtNombre = campo("Nombre del producto");
        JTextField txtStock = campo("Ej: 50");
        JTextField txtCompra = campo("Ej: $2.500");
        JTextField txtVenta = campo("Ej: $4.000");

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints g = new GridBagConstraints();

        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        g.gridx = 0;

        g.gridy = 0;
        g.insets = new Insets(0, 0, 6, 0);
        form.add(label("Nombre del Producto"), g);

        g.gridy = 1;
        g.insets = new Insets(0, 0, 16, 0);
        form.add(txtNombre, g);

        g.gridy = 2;
        g.insets = new Insets(0, 0, 6, 0);
        form.add(label("Stock"), g);

        g.gridy = 3;
        g.insets = new Insets(0, 0, 16, 0);
        form.add(txtStock, g);

        JPanel rowPrecios =
                new JPanel(new GridLayout(1, 2, 12, 0));

        rowPrecios.setOpaque(false);

        JPanel colCompra =
                new JPanel(new BorderLayout(0, 6));

        colCompra.setOpaque(false);

        colCompra.add(
                label("Precio de Compra por unidad"),
                BorderLayout.NORTH);

        colCompra.add(
                txtCompra,
                BorderLayout.CENTER);

        JPanel colVenta =
                new JPanel(new BorderLayout(0, 6));

        colVenta.setOpaque(false);

        colVenta.add(
                label("Precio de venta por unidad"),
                BorderLayout.NORTH);

        colVenta.add(
                txtVenta,
                BorderLayout.CENTER);

        rowPrecios.add(colCompra);
        rowPrecios.add(colVenta);

        g.gridy = 4;
        g.insets = new Insets(0, 0, 28, 0);
        form.add(rowPrecios, g);

        JPanel botones =
                new JPanel(new GridLayout(1, 2, 12, 0));

        botones.setOpaque(false);

        JButton btnCancelar =
                botonModal(
                        "Cancelar",
                        ThemeManager.getBorder(),
                        ThemeManager.getTextSecondary(),
                        false);

        JButton btnGuardar =
                botonModal(
                        "Agregar Producto",
                        ThemeManager.getPrimary(),
                        Color.WHITE,
                        true);

        btnCancelar.addActionListener(e -> dispose());

        btnGuardar.addActionListener(e -> {

            String nombre =
                    txtNombre.getText().trim();

            String stockStr =
                    txtStock.getText().trim();

            String compra =
                    txtCompra.getText().trim();

            String venta =
                    txtVenta.getText().trim();

            if (nombre.isEmpty()
                    || stockStr.isEmpty()
                    || compra.isEmpty()
                    || venta.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Por favor completa todos los campos.",
                        "Atención",
                        JOptionPane.INFORMATION_MESSAGE);

                return;
            }

            int stock;
            double precioCompra;
            double precioVenta;

            try {
                stock = Integer.parseInt(
                        stockStr.replaceAll("[^0-9]", ""));
                precioCompra = parsePrecio(compra);
                precioVenta = parsePrecio(venta);
            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                        this,
                        "El stock debe ser un número válido.",
                        "Atención",
                        JOptionPane.INFORMATION_MESSAGE);

                return;
            }

            if (precioCompra < 0 || precioVenta < 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "El precio debe ser un número válido.",
                        "Atención",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Producto producto = new Producto(0, nombre, stock, precioCompra, precioVenta);
            int nuevoId;
            try {
                nuevoId = productoDAO.crear(producto);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Error al guardar el producto. " + ex.getMessage(),
                        "Atención",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (nuevoId > 0) {
                model.addRow(
                        new Object[]{
                                nuevoId,
                                nombre,
                                stock,
                                formatearMoneda(precioCompra),
                                formatearMoneda(precioVenta)
                        });
                if (onSuccess != null) {
                    onSuccess.run();
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo guardar el producto.",
                        "Atención",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        botones.add(btnCancelar);
        botones.add(btnGuardar);

        root.add(header, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        root.add(botones, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private double parsePrecio(String texto) {
        if (texto == null || texto.isEmpty()) {
            return 0;
        }
        String limpio = texto.trim().replaceAll("[^0-9.,-]", "");
        if (limpio.isEmpty()) {
            return -1;
        }

        int coma = limpio.indexOf(',');
        if (coma >= 0) {
            limpio = limpio.substring(0, coma);
        }

        if (limpio.contains(".")) {
            String[] partes = limpio.split("\\.");
            if (partes.length > 1 && partes[partes.length - 1].length() == 3) {
                limpio = String.join("", partes);
            }
        }

        try {
            return Double.parseDouble(limpio);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String formatearMoneda(double valor) {
        if (Double.isNaN(valor) || Double.isInfinite(valor)) {
            return "$0";
        }
        long entero = Math.round(valor);
        return "$" + String.format("%,d", entero).replace(',', '.');
    }

    private JTextField campo(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBackground(ThemeManager.getPanelBackground());
        tf.setForeground(ThemeManager.getTextPrimary());
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        ThemeManager.getBorder(), 1, true),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        tf.setPreferredSize(new Dimension(0, 38));
        if (placeholder != null && !placeholder.isEmpty()) {
            tf.setText(placeholder);
            tf.setForeground(ThemeManager.getTextSecondary());
            tf.addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent e) {
                    if (tf.getText().equals(placeholder)) {
                        tf.setText("");
                        tf.setForeground(ThemeManager.getTextPrimary());
                    }
                }

                @Override
                public void focusLost(java.awt.event.FocusEvent e) {
                    if (tf.getText().isEmpty()) {
                        tf.setText(placeholder);
                        tf.setForeground(ThemeManager.getTextSecondary());
                    }
                }
            });
        }
        return tf;
    }

    private JLabel label(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(ThemeManager.getTextSecondary());
        return lbl;
    }

    private JButton botonModal(
            String texto,
            Color bg,
            Color fg,
            boolean isPrimary) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(
                        0, 0,
                        getWidth() - 1,
                        getHeight() - 1,
                        8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(0, 38));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
