package com.santaana.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;

import com.santaana.util.ThemeManager;

public class ProductoPanel extends JPanel implements ThemeManager.ThemeListener {

    private Color getPrimario() { return ThemeManager.getPrimary(); }
    private Color getFondo() { return ThemeManager.getBackground(); }
    private Color getPanelCol() { return ThemeManager.getPanelBackground(); }
    private Color getBorde() { return ThemeManager.getBorder(); }
    private Color getTextCol() { return ThemeManager.getTextPrimary(); }
    private Color getLabelCol() { return ThemeManager.getTextSecondary(); }

    private JTextField txtBuscar;
    private JTable tabla;

    public ProductoPanel() {
        ThemeManager.addListener(this);
        setLayout(new BorderLayout());
        refreshUI();
    }

    private void refreshUI() {
        removeAll();
        setBackground(getFondo());

        add(crearNavbar(), BorderLayout.NORTH);
        add(crearContenido(), BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    @Override
    public void onThemeChanged() {
        refreshUI();
    }

    // 🔹 NAVBAR
    private JPanel crearNavbar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(getPanelCol());
        navbar.setPreferredSize(new Dimension(0, 60));
        navbar.setBorder(new MatteBorder(0, 0, 1, 0, getBorde()));

        JLabel title = new JLabel("  GESTIÓN DE PRODUCTOS");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(getTextCol());

        navbar.add(title, BorderLayout.WEST);

        return navbar;
    }

    // 🔹 CONTENIDO
    private JPanel crearContenido() {
        JPanel cont = new JPanel(new BorderLayout());
        cont.setOpaque(false);
        cont.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));

        cont.add(headerBusqueda(), BorderLayout.NORTH);
        cont.add(tablaProductos(), BorderLayout.CENTER);

        return cont;
    }

    // 🔍 BUSCADOR + BOTÓN JUNTOS
    private JPanel headerBusqueda() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        txtBuscar = new JTextField(" Buscar producto...");
        txtBuscar.setPreferredSize(new Dimension(260, 36));
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(getBorde(), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtBuscar.setBackground(getPanelCol());
        txtBuscar.setForeground(getTextCol());

        JButton btnNuevo = new JButton("+ Nuevo producto");
        estilizarBoton(btnNuevo, getPrimario());

        btnNuevo.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Abrir formulario de producto")
        );

        panel.add(txtBuscar);
        panel.add(btnNuevo);

        return panel;
    }

    // 📊 TABLA MINIMALISTA + DARK/LIGHT
    private JScrollPane tablaProductos() {

        String[] columnas = {"ID", "Producto", "Stock", "Compra", "Venta", "Acciones"};

        Object[][] data = {
            {"#001", "Coca Cola 400ml", "50 uds", "$2.500", "$4.000", "Editar | Eliminar"},
            {"#002", "Agua Cristal", "30 uds", "$1.000", "$2.000", "Editar | Eliminar"},
            {"#003", "Papas Margarita", "5 uds ⚠", "$2.000", "$3.500", "Editar | Eliminar"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columnas) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(model);

        // 🔥 ESTILO
        tabla.setRowHeight(38);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setIntercellSpacing(new Dimension(20, 10));

        // 🔥 COLORES DINÁMICOS
        tabla.setBackground(getPanelCol());
        tabla.setForeground(getTextCol());
        tabla.setSelectionBackground(getPrimario().darker());
        tabla.setSelectionForeground(Color.WHITE);

        tabla.setGridColor(getBorde());
        tabla.setShowVerticalLines(false);
        tabla.setShowHorizontalLines(true);

        // HEADER
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabla.getTableHeader().setBackground(getPanelCol());
        tabla.getTableHeader().setForeground(getLabelCol());
        tabla.getTableHeader().setBorder(null);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(getBorde(), 1, true));
        scroll.getViewport().setBackground(getPanelCol());
        scroll.setBackground(getPanelCol());

        return scroll;
    }

    // 🎨 BOTONES
    private void estilizarBoton(JButton b, Color bg) {
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(150, 34));
    }
}