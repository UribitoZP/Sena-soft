package com.santaana.view;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.*;
import com.santaana.dao.HistorialDAO;
import com.santaana.dao.ProductoDAO;
import com.santaana.db.DatabaseException;
import com.santaana.model.Producto;
import com.santaana.util.ErrorUtil;
import com.santaana.util.ThemeManager;

public class ProductoPanel extends JPanel {

    private JTable tabla;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final List<Producto> productos = new ArrayList<>();
    private boolean cargaInicial = true;
    private boolean stockBajoNotificacionRegistrada = false;
    private JLabel lblTotalProductos;
    private JLabel lblStockBajo;
    private JLabel lblValorInventario;
    private Color getPanelCol() { return ThemeManager.getPanelBackground(); }
    private Color getBorde()    { return ThemeManager.getBorder(); }
    private Color getTextCol()  { return ThemeManager.getTextPrimary(); }

    public ProductoPanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.getBackground());
        add(crearContenido(), BorderLayout.CENTER);
    }

    private JPanel crearContenido() {
        JPanel root = new JPanel(new BorderLayout(0, 24));
        root.setOpaque(false);
        
        JPanel inner = new JPanel(new BorderLayout(0, 24));
        inner.setOpaque(false);
        inner.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        inner.add(crearHeader(), BorderLayout.NORTH);
        inner.add(crearCentro(), BorderLayout.CENTER);

        root.add(crearNavbar(), BorderLayout.NORTH);
        root.add(inner, BorderLayout.CENTER);
        return root;
    }
    private JPanel crearNavbar() {
    JPanel navbar = new JPanel(new BorderLayout());
    navbar.setBackground(getPanelCol());
    navbar.setPreferredSize(new Dimension(0, 50));
    navbar.setBorder(new MatteBorder(0, 0, 1, 0, getBorde()));

    JLabel title = new JLabel("  GESTIÓN DE PRODUCTOS");
    title.setFont(new Font("Segoe UI", Font.BOLD, 14));
    title.setForeground(getTextCol());
    navbar.add(title, BorderLayout.WEST);

    return navbar;
}

    private JPanel crearHeader() {
    JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(crearTarjetas());
        return header;
    }

    private JPanel crearTarjetas() {
        JPanel cards = new JPanel(new GridLayout(1, 3, 14, 0));
        cards.setOpaque(false);
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        cards.setAlignmentX(LEFT_ALIGNMENT);

        lblTotalProductos = new JLabel("0");
        lblStockBajo = new JLabel("0");
        lblValorInventario = new JLabel("$0");

        cards.add(crearCard("📦", "Productos", lblTotalProductos, new Color(99, 102, 241)));
        cards.add(crearCard("⚠️", "Stock Bajo", lblStockBajo, new Color(245, 158, 11)));
        cards.add(crearCard("💰", "Valor Inventario", lblValorInventario, new Color(16, 185, 129)));

        return cards;
    }

    private JPanel crearCard(String icono, String titulo, JLabel lblValor, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(ThemeManager.getPanelBackground());
                g2.fill(new RoundRectangle2D.Double(
                        0, 0, getWidth(), getHeight(), 16, 16));

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

        JPanel searchWrapper = new JPanel(new BorderLayout(8, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(ThemeManager.getPanelBackground());

                g2.fill(new RoundRectangle2D.Double(
                        0, 0, getWidth(), getHeight(), 10, 10));

                g2.setColor(ThemeManager.getBorder());

                g2.draw(new RoundRectangle2D.Double(
                        0.5, 0.5,
                        getWidth() - 1,
                        getHeight() - 1,
                        10, 10));

                g2.dispose();
                super.paintComponent(g);
            }
        };

        searchWrapper.setOpaque(false);
        searchWrapper.setPreferredSize(new Dimension(300, 40));
        searchWrapper.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

        JLabel lupa = new JLabel("🔍");
        searchWrapper.add(lupa, BorderLayout.WEST);
        searchWrapper.add(txtBuscar, BorderLayout.CENTER);

        txtBuscar.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {

                    public void changedUpdate(javax.swing.event.DocumentEvent e) {
                        filtrar();
                    }

                    public void removeUpdate(javax.swing.event.DocumentEvent e) {
                        filtrar();
                    }

                    public void insertUpdate(javax.swing.event.DocumentEvent e) {
                        filtrar();
                    }

                    private void filtrar() {
                        String texto = txtBuscar.getText().trim();

                        if (sorter != null) {
                            sorter.setRowFilter(
                                    texto.isEmpty()
                                            ? null
                                            : RowFilter.regexFilter("(?i)" + texto));
                        }
                    }
                });

        JButton btnNuevo = crearBoton(" + Nuevo Producto", ThemeManager.getPrimary());
        btnNuevo.setPreferredSize(new Dimension(200, 40));

        btnNuevo.addActionListener(e -> {
            Window padre = SwingUtilities.getWindowAncestor(this);

            ProductoAgregarDialog dlg =
                    new ProductoAgregarDialog(
                            padre,
                            productoDAO,
                            model,
                            this::cargarProductos);

            dlg.setVisible(true);
        });

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
                g2.fill(new RoundRectangle2D.Double(
                        0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(ThemeManager.getBorder());
                g2.draw(new RoundRectangle2D.Double(
                        0.5, 0.5, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        card.add(crearTabla(), BorderLayout.CENTER);
        card.add(crearPieBotones(), BorderLayout.SOUTH);
        return card;
    }

    private JScrollPane crearTabla() {
        String[] columnas = {"ID", "Nombre", "Stock", "Precio Compra (UD)", "Precio Venta (UD)"};
        model = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabla = new JTable(model);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setForeground(ThemeManager.getTextPrimary());
        tabla.setBackground(ThemeManager.getPanelBackground());
        tabla.setSelectionBackground(new Color(99, 102, 241, 40));
        tabla.setSelectionForeground(ThemeManager.getTextPrimary());
        tabla.setRowHeight(36);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setShowGrid(false);
        tabla.setDefaultRenderer(Object.class, new PaddedCellRenderer());
        tabla.getTableHeader().setDefaultRenderer(new MinimalHeaderRenderer());
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 40));
        tabla.getTableHeader().setBackground(ThemeManager.getBackground());
        tabla.getTableHeader().setForeground(ThemeManager.getTextSecondary());
        tabla.getTableHeader().setBorder(new BottomLineBorder());
        
        sorter = new TableRowSorter<>(model);
        tabla.setRowSorter(sorter);
        cargarProductos();
        
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(ThemeManager.getPanelBackground());
        scroll.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI());
        return scroll;
    }

    private void cargarProductos() {
        productos.clear();
        model.setRowCount(0);
        try {
            List<Producto> lista = productoDAO.listarTodos();
            productos.addAll(lista);
            for (Producto producto : lista) {
                model.addRow(new Object[]{
                        producto.getId(),
                        producto.getNombre(),
                        producto.getStock(),
                        formatearMoneda(producto.getPrecioCompra()),
                        formatearMoneda(producto.getPrecioVenta())
                });
            }
        } catch (DatabaseException e) {
            ErrorUtil.mostrarError(this, "cargar productos", e);
        }
        actualizarTarjetas();
        if (!cargaInicial) {
            registrarNotificacionStockBajo();
        }
        cargaInicial = false;
    }

    private void actualizarTarjetas() {
        int total = productos.size();
        long bajo = productos.stream().filter(p -> p.getStock() < 10).count();
        double valorInventario = productos.stream()
                .mapToDouble(p -> p.getStock() * p.getPrecioCompra())
                .sum();

        lblTotalProductos.setText(String.valueOf(total));
        lblStockBajo.setText(String.valueOf(bajo));
        lblValorInventario.setText(formatearMoneda(valorInventario));
    }

    private void registrarNotificacionStockBajo() {
        List<Producto> productosBajoStock = productos.stream()
                .filter(p -> p.getStock() < 10)
                .collect(Collectors.toList());

        if (productosBajoStock.isEmpty()) {
            stockBajoNotificacionRegistrada = false;
            return;
        }

        if (stockBajoNotificacionRegistrada) {
            return;
        }

        String descripcion = productosBajoStock.stream()
                .map(p -> p.getNombre() + " (" + p.getStock() + ")")
                .collect(Collectors.joining(", "));

        String mensaje = "Productos con stock bajo: " + descripcion;
        try {
            HistorialDAO.registrar("Sistema", "Stock bajo", mensaje);
            stockBajoNotificacionRegistrada = true;
        } catch (DatabaseException e) {
            ErrorUtil.mostrarError(this, "registrar alerta de stock", e);
        }
    }

    private String formatearMoneda(double valor) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat formatter = new DecimalFormat("#,##0", symbols);
        return "$" + formatter.format(Math.round(valor));
    }

    private Producto productoDesdeFila(int modelRow) {
        int id = Integer.parseInt(model.getValueAt(modelRow, 0).toString());
        String nombre = model.getValueAt(modelRow, 1).toString();
        int stock = Integer.parseInt(model.getValueAt(modelRow, 2).toString());
        double compra = parsePrecio(model.getValueAt(modelRow, 3).toString());
        double venta = parsePrecio(model.getValueAt(modelRow, 4).toString());
        return new Producto(id, nombre, stock, compra, venta);
    }

    private double parsePrecio(String texto) {
        if (texto == null || texto.isEmpty()) {
            return 0;
        }
        String limpio = texto.replaceAll("[^0-9.,-]", "").replace(',', '.');
        try {
            return Double.parseDouble(limpio);
        } catch (NumberFormatException e) { 
            return 0;
        }
    }

    private JPanel crearPieBotones() {

        JPanel pie = new JPanel(new FlowLayout(
                FlowLayout.RIGHT, 10, 12));

        pie.setOpaque(false);

        JButton btnEditar =
                crearBoton("Editar", new Color(37, 99, 235));

        JButton btnEliminar =
                crearBoton("Eliminar", new Color(220, 38, 38));

        btnEditar.addActionListener(e -> {

            int fila = tabla.getSelectedRow();

            if (fila == -1) {
                mostrarAlerta(
                        "Seleccione un producto para editar.");
                return;
            }

            int modelRow =
                    tabla.convertRowIndexToModel(fila);

            Producto producto = productoDesdeFila(modelRow);

            Window padre =
                    SwingUtilities.getWindowAncestor(this);

            ProductoFormDialog dlg =
                    new ProductoFormDialog(
                        padre,
                        productoDAO,
                        producto,
                        modelRow,
                        model,
                        this::cargarProductos);

            dlg.setVisible(true);
        });

        btnEliminar.addActionListener(e -> {

            int fila = tabla.getSelectedRow();

            if (fila == -1) {
                mostrarAlerta(
                        "Seleccione un producto para eliminar.");
                return;
            }

            int modelRow =
                    tabla.convertRowIndexToModel(fila);

            int id = Integer.parseInt(model.getValueAt(modelRow, 0).toString());
            String nombreProducto =
                    model.getValueAt(modelRow, 1).toString();

            Window padre =
                    SwingUtilities.getWindowAncestor(this);

            ProductoEliminarDialog dlg =
                    new ProductoEliminarDialog(
                            padre,
                            nombreProducto,
                            () -> {
                                try {
                                    if (productoDAO.eliminar(id)) {
                                        cargarProductos();
                                    } else {
                                        JOptionPane.showMessageDialog(
                                                this,
                                                "No se pudo eliminar el producto.",
                                                "Atención",
                                                JOptionPane.INFORMATION_MESSAGE);
                                    }
                                } catch (DatabaseException ex) {
                                    ErrorUtil.mostrarError(this, "eliminar producto", ex);
                                }
                            });

            dlg.setVisible(true);
        });

        pie.add(btnEditar);
        pie.add(btnEliminar);

        return pie;
    }

    private void mostrarAlerta(String msg) {
        JOptionPane.showMessageDialog(
                this,
                msg,
                "Atención",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private JButton crearBoton(String texto, Color color) {
        JButton b = new JButton(texto) {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(120, 36));
        return b;
    }

    private static class PaddedCellRenderer extends DefaultTableCellRenderer {
        public PaddedCellRenderer() {
            setOpaque(true);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            setBackground(isSelected ? new Color(99, 102, 241, 40) : ThemeManager.getPanelBackground());
            setForeground(ThemeManager.getTextPrimary());
            return c;
        }
    }

    private static class MinimalHeaderRenderer extends DefaultTableCellRenderer {
        public MinimalHeaderRenderer() {
            setOpaque(true);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBackground(ThemeManager.getBackground());
            setForeground(ThemeManager.getTextSecondary());
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            return c;
        }
    }

    private static class BottomLineBorder extends AbstractBorder {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(ThemeManager.getBorder());
            g2.drawLine(x, y + height - 1, x + width, y + height - 1);
            g2.dispose();
        }
    }
}