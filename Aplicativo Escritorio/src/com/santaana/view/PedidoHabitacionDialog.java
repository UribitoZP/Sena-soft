package com.santaana.view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.MatteBorder;

import com.santaana.dao.ProductoDAO;
import com.santaana.dao.ReservaProductoDAO;
import com.santaana.model.Producto;
import com.santaana.model.Reserva;
import com.santaana.model.ReservaProducto;
import com.santaana.util.ThemeManager;

public class PedidoHabitacionDialog extends JDialog {

    private Reserva reserva;
    private JComboBox<Producto> comboProductos;
    private Runnable onProductoAgregado;
    private JButton btnAgregar;
    private ProductoDAO productoDAO = new ProductoDAO();
    private ReservaProductoDAO reservaProductoDAO = new ReservaProductoDAO();

    private JPanel listaContainer;
    private JLabel lblTotal;

    public PedidoHabitacionDialog(java.awt.Window parent, Reserva reserva) {

        super(parent, "Pedidos a habitación", ModalityType.APPLICATION_MODAL);

        this.reserva = reserva;

        setSize(560, 620);
        setMinimumSize(new Dimension(480, 480));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeManager.getBackground());

        initUI();
    }

    private void initUI() {

        // =========================
        // HEADER
        // =========================
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ThemeManager.getPanelBackground());
        header.setPreferredSize(new Dimension(0, 64));
        header.setBorder(new MatteBorder(0, 0, 1, 0, ThemeManager.getBorder()));

        JPanel headerTexto = new JPanel();
        headerTexto.setOpaque(false);
        headerTexto.setLayout(new BoxLayout(headerTexto, BoxLayout.Y_AXIS));
        headerTexto.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        JLabel titulo = new JLabel("Pedido a habitación");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(ThemeManager.getTextPrimary());

        JLabel subtitulo = new JLabel("Habitación " + reserva.getIdHabitacion() + " · Reserva #" + reserva.getId());
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(ThemeManager.getTextSecondary());

        headerTexto.add(Box.createVerticalGlue());
        headerTexto.add(titulo);
        headerTexto.add(Box.createVerticalStrut(2));
        headerTexto.add(subtitulo);
        headerTexto.add(Box.createVerticalGlue());

        header.add(headerTexto, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // =========================
        // CONTENIDO
        // =========================
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setOpaque(false);
        contenido.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        contenido.add(crearTarjetaAgregar());
        contenido.add(Box.createVerticalStrut(16));
        contenido.add(crearTarjetaLista());

        add(contenido, BorderLayout.CENTER);

        // =========================
        // FOOTER
        // =========================
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 16));
        bottom.setOpaque(false);
        bottom.setBorder(new MatteBorder(1, 0, 0, 0, ThemeManager.getBorder()));

        JButton cerrar = botonSecundario("Cerrar");
        cerrar.setPreferredSize(new Dimension(140, 38));
        cerrar.addActionListener(e -> dispose());
        bottom.add(cerrar);

        add(bottom, BorderLayout.SOUTH);

        cargarLista();
    }

    // ── Tarjeta: agregar producto ────────────────────────────────────────────
    private JPanel crearTarjetaAgregar() {
        JPanel tarjeta = tarjeta();

        JLabel sub = new JLabel("Agregar producto");
        sub.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sub.setForeground(ThemeManager.getPrimary());
        sub.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        tarjeta.add(sub);

        JPanel fila = new JPanel(new BorderLayout(10, 0));
        fila.setOpaque(false);

        comboProductos = new JComboBox<>();
        comboProductos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboProductos.setBackground(ThemeManager.getPanelBackground());
        for (Producto p : productoDAO.listarTodos()) {
            comboProductos.addItem(p);
        }

        btnAgregar = botonPrimario("Agregar");
        btnAgregar.setPreferredSize(new Dimension(120, 38));
        btnAgregar.addActionListener(e -> agregarProducto());

        fila.add(comboProductos, BorderLayout.CENTER);
        fila.add(btnAgregar, BorderLayout.EAST);

        tarjeta.add(fila);
        return tarjeta;
    }

    // ── Tarjeta: lista de productos ya pedidos ───────────────────────────────
    private JPanel crearTarjetaLista() {
        JPanel tarjeta = tarjeta();
        tarjeta.setLayout(new BorderLayout(0, 10));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);

        JLabel sub = new JLabel("Productos pedidos");
        sub.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sub.setForeground(ThemeManager.getPrimary());

        lblTotal = new JLabel("Total: $0");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTotal.setForeground(ThemeManager.getTextPrimary());

        encabezado.add(sub, BorderLayout.WEST);
        encabezado.add(lblTotal, BorderLayout.EAST);
        tarjeta.add(encabezado, BorderLayout.NORTH);

        listaContainer = new JPanel();
        listaContainer.setLayout(new BoxLayout(listaContainer, BoxLayout.Y_AXIS));
        listaContainer.setOpaque(false);

        JScrollPane scroll = new JScrollPane(listaContainer);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        scroll.setPreferredSize(new Dimension(0, 260));

        tarjeta.add(scroll, BorderLayout.CENTER);
        return tarjeta;
    }

    private void cargarLista() {
        listaContainer.removeAll();

        Map<Integer, String> nombresPorId = new HashMap<>();
        for (Producto p : productoDAO.listarTodos()) {
            nombresPorId.put(p.getId(), p.getNombre());
        }

        List<ReservaProducto> pedidos = reservaProductoDAO.listarPorReserva(reserva.getId());

        if (pedidos.isEmpty()) {
            JLabel vacio = new JLabel("Aún no se han pedido productos para esta habitación.");
            vacio.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            vacio.setForeground(ThemeManager.getTextSecondary());
            vacio.setAlignmentX(Component.LEFT_ALIGNMENT);
            listaContainer.add(Box.createVerticalStrut(8));
            listaContainer.add(vacio);
            lblTotal.setText("Total: $0");
        } else {
            double total = 0;
            for (ReservaProducto rp : pedidos) {
                total += rp.getCantidad() * rp.getPrecio();
                String nombre = nombresPorId.getOrDefault(rp.getIdProducto(), "Producto eliminado");
                listaContainer.add(filaProducto(nombre, rp.getCantidad(), rp.getPrecio()));
                listaContainer.add(Box.createVerticalStrut(8));
            }
            lblTotal.setText(String.format("Total: $%,.0f", total));
        }

        listaContainer.revalidate();
        listaContainer.repaint();
    }

    private JPanel filaProducto(String nombre, int cantidad, double precioUnit) {
        JPanel fila = new JPanel(new BorderLayout(10, 0));
        fila.setOpaque(true);
        fila.setBackground(ThemeManager.getBackground());
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        fila.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getBorder(), 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        JPanel izquierda = new JPanel();
        izquierda.setOpaque(false);
        izquierda.setLayout(new BoxLayout(izquierda, BoxLayout.Y_AXIS));

        JLabel lblNombre = new JLabel(nombre);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNombre.setForeground(ThemeManager.getTextPrimary());

        JLabel lblCantidad = new JLabel("Cantidad: " + cantidad + "  ·  $" + String.format("%,.0f", precioUnit) + " c/u");
        lblCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCantidad.setForeground(ThemeManager.getTextSecondary());

        izquierda.add(lblNombre);
        izquierda.add(lblCantidad);

        JLabel subtotal = new JLabel(String.format("$%,.0f", cantidad * precioUnit));
        subtotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        subtotal.setForeground(ThemeManager.getPrimary());

        fila.add(izquierda, BorderLayout.WEST);
        fila.add(subtotal, BorderLayout.EAST);

        return fila;
    }

    private void agregarProducto() {
        Producto producto = (Producto) comboProductos.getSelectedItem();
        if (producto == null) {
            return;
        }
        if (producto.getStock() <= 0) {
            JOptionPane.showMessageDialog(
                this, "No hay stock disponible de este producto"
            );
            return;
        }
        ReservaProducto rp = new ReservaProducto(
            reserva.getId(),
            producto.getId(),
            1,
            producto.getPrecioVenta()
        );
        boolean ok = reservaProductoDAO.agregarProductoAReserva(rp);

        if (ok) {
            producto.setStock(
                producto.getStock() - 1
            );
            productoDAO.actualizar(producto);
            if (onProductoAgregado != null) {
                onProductoAgregado.run();
            }
            cargarLista();
            JOptionPane.showMessageDialog(this, "Producto agregado correctamente");
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo agregar el producto");
        }
    }

    // ── Utilidades UI ─────────────────────────────────────────────────────────
    private JPanel tarjeta() {
        JPanel p = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(ThemeManager.getBorder());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
            }
        };
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(ThemeManager.getPanelBackground());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        return p;
    }

    private JButton botonPrimario(String texto) {
        JButton b = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getPrimary());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton botonSecundario(String texto) {
        JButton b = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getPanelBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(ThemeManager.getBorder());
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(ThemeManager.getTextPrimary());
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}