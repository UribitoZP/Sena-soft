package com.santaana.view;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.*;
import com.santaana.util.ThemeManager;

public class ProductoPanel extends JPanel {

    private JTable tabla;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    public ProductoPanel() {

        setLayout(new BorderLayout());
        setBackground(ThemeManager.getBackground());
        add(crearContenido(), BorderLayout.CENTER);
    }

    private JPanel crearContenido() {
        
        JPanel root = new JPanel(new BorderLayout(0, 24));
        root.setOpaque(false);
        root.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        root.add(crearHeader(), BorderLayout.NORTH);
        root.add(crearCentro(), BorderLayout.CENTER);

        return root;
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Gestión de Productos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(ThemeManager.getTextPrimary());
        titulo.setAlignmentX(LEFT_ALIGNMENT);

        header.add(titulo);
        header.add(Box.createVerticalStrut(4));
        header.add(Box.createVerticalStrut(22));
        header.add(crearTarjetas());

        return header;
    }

    private JPanel crearTarjetas() {
        JPanel cards = new JPanel(new GridLayout(1, 3, 14, 0));
        cards.setOpaque(false);
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        cards.setAlignmentX(LEFT_ALIGNMENT);

        cards.add(crearCard("📦", "Productos", "4", new Color(99, 102, 241)));
        cards.add(crearCard("⚠️", "Stock Bajo", "1", new Color(245, 158, 11)));
        cards.add(crearCard("💰", "Valor Inventario", "$12.000", new Color(16, 185, 129)));

        return cards;
    }

    private JPanel crearCard(String icono, String titulo, String valor, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getPanelBackground());
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));

                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, getWidth(), 3, 3, 3);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        card.setPreferredSize(new Dimension(200, 106));

        JLabel lblIcono = new JLabel(icono);
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValor.setForeground(ThemeManager.getTextPrimary());

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitulo.setForeground(ThemeManager.getTextSecondary());

        card.add(lblIcono);
        card.add(Box.createVerticalStrut(8));
        card.add(lblValor);
        card.add(lblTitulo);

        return card;
    }

    private JPanel crearCentro() {
        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setOpaque(false);

        center.add(crearBarraAcciones(), BorderLayout.NORTH);
        center.add(crearTablaCard(), BorderLayout.CENTER);

        return center;
    }

    private JPanel crearBarraAcciones() {
        JPanel barra = new JPanel(new BorderLayout(12, 0));
        barra.setOpaque(false);

        JTextField txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBuscar.setForeground(ThemeManager.getTextPrimary());
        txtBuscar.setBackground(ThemeManager.getPanelBackground());
        txtBuscar.setBorder(null);
        txtBuscar.setCaretColor(ThemeManager.getPrimary());
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar producto...");

        JPanel searchWrapper = new JPanel(new BorderLayout(8, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getPanelBackground());
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(ThemeManager.getBorder());
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };

        searchWrapper.setOpaque(false);
        searchWrapper.setPreferredSize(new Dimension(300, 40));
        searchWrapper.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

        JLabel lupa = new JLabel("🔍");
        lupa.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));

        searchWrapper.add(lupa, BorderLayout.WEST);
        searchWrapper.add(txtBuscar, BorderLayout.CENTER);

        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filtrar(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filtrar(); }
            private void filtrar() {
                String texto = txtBuscar.getText().trim();
                if (sorter != null) {
                    sorter.setRowFilter(texto.isEmpty() ? null : RowFilter.regexFilter("(?i)" + texto));
                }
            }
        });

        JButton btnNuevo = crearBoton("＋  Nuevo Producto", ThemeManager.getPrimary());
        btnNuevo.setPreferredSize(new Dimension(170, 40));

        barra.add(searchWrapper, BorderLayout.WEST);
        barra.add(btnNuevo, BorderLayout.EAST);

        return barra;
    }

    private JPanel crearTablaCard() {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getPanelBackground());
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(ThemeManager.getBorder());
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
            }
        };

        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        card.add(crearTabla(), BorderLayout.CENTER);
        card.add(crearPieBotones(), BorderLayout.SOUTH);

        return card;
    }

    private JScrollPane crearTabla() {
        
        String[] columnas = { "ID", "Producto", "Stock", "Compra", "Venta" };
        Object[][] datos = {
            { 1, "Coca Cola 400ml",   50, "$2.500", "$4.000" },
            { 2, "Agua Cristal",      30, "$1.000", "$2.000" },
            { 3, "Papas Margarita",    5, "$2.000", "$3.500" },
            { 4, "Galletas Festival", 15, "$1.500", "$2.500" }
        };

        model = new DefaultTableModel(datos, columnas) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(model);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setForeground(ThemeManager.getTextPrimary());
        tabla.setBackground(ThemeManager.getPanelBackground());
        tabla.setSelectionBackground(new Color(
            ThemeManager.getPrimary().getRed(),
            ThemeManager.getPrimary().getGreen(),
            ThemeManager.getPrimary().getBlue(), 40));
        tabla.setSelectionForeground(ThemeManager.getTextPrimary());
        tabla.setRowHeight(48);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setShowVerticalLines(false);
        tabla.setShowHorizontalLines(true);
        tabla.setGridColor(ThemeManager.getBorder());
        tabla.setFocusable(false);
        tabla.getTableHeader().setReorderingAllowed(false);

        JTableHeader th = tabla.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setForeground(ThemeManager.getTextSecondary());
        th.setBackground(ThemeManager.getPanelBackground());
        th.setPreferredSize(new Dimension(th.getWidth(), 44));
        th.setBorder(new BottomLineBorder(ThemeManager.getBorder()));
        th.setDefaultRenderer(new MinimalHeaderRenderer(
            ThemeManager.getPanelBackground(),
            ThemeManager.getTextSecondary()));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tabla.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tabla.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        DefaultTableCellRenderer paddingRenderer = new PaddedCellRenderer();
        for (int i : new int[]{1, 3, 4}) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(paddingRenderer);
        }

       
        tabla.getColumnModel().getColumn(0).setMaxWidth(50);
        tabla.getColumnModel().getColumn(2).setMaxWidth(70);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(90);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(90);

        sorter = new TableRowSorter<>(model);
        tabla.setRowSorter(sorter);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(null);
        scroll.setOpaque(false);

        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(ThemeManager.getPanelBackground());
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        scroll.setViewportBorder(null);

        return scroll;
    }

    private JPanel crearPieBotones() {
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getBorder());
                g2.drawLine(16, 0, getWidth() - 16, 0);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        pie.setOpaque(false);

        JButton btnEditar   = crearBoton("Editar",   new Color(37, 99, 235));
        JButton btnEliminar = crearBoton("Eliminar", new Color(220, 38, 38));

        btnEditar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) { mostrarAlerta("Seleccione un producto para editar."); return; }
            JOptionPane.showMessageDialog(this,
                    "Editar: " + model.getValueAt(tabla.convertRowIndexToModel(fila), 1));
        });

        btnEliminar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) { mostrarAlerta("Seleccione un producto para eliminar."); return; }
            int op = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar el producto seleccionado?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) model.removeRow(tabla.convertRowIndexToModel(fila));
        });

        pie.add(btnEditar);
        pie.add(btnEliminar);

        return pie;
    }

    private void mostrarAlerta(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Atención", JOptionPane.INFORMATION_MESSAGE);
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? color.brighter() : color);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };

        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setForeground(Color.WHITE);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(140, 38));

        return boton;
    }

    private static class PaddedCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            setForeground(isSelected ? table.getSelectionForeground() : ThemeManager.getTextPrimary());
            return this;
        }
    }

    private static class MinimalHeaderRenderer extends DefaultTableCellRenderer {
        private final Color bg, fg;

        MinimalHeaderRenderer(Color bg, Color fg) { this.bg = bg; this.fg = fg; }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            setText(value == null ? "" : value.toString().toUpperCase());
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setForeground(fg);
            setBackground(bg);
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
            return this;
        }
    }

    private static class BottomLineBorder extends AbstractBorder {
        private final Color color;

        BottomLineBorder(Color color) { this.color = color; }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            g.setColor(color);
            g.drawLine(x, y + h - 1, x + w, y + h - 1);
        }

        @Override
        public Insets getBorderInsets(Component c) { return new Insets(0, 0, 1, 0); }
    }

    private boolean isDarkMode() {
        Color bg = ThemeManager.getPanelBackground();
        double luminance = (0.299 * bg.getRed() + 0.587 * bg.getGreen() + 0.114 * bg.getBlue()) / 255.0;
        return luminance < 0.5;
    }
}