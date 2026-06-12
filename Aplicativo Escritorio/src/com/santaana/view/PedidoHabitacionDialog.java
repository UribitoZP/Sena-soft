package com.santaana.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JOptionPane;

import com.santaana.model.Producto;
import com.santaana.dao.ProductoDAO;
import com.santaana.dao.ReservaProductoDAO;
import com.santaana.model.Reserva;
import com.santaana.model.ReservaProducto;

public class PedidoHabitacionDialog extends JDialog {

    private Reserva reserva;
    private JComboBox<Producto> comboProductos;
    private JButton btnAgregar;
    private ProductoDAO productoDAO = new ProductoDAO();
    private ReservaProductoDAO reservaProductoDAO = new ReservaProductoDAO();

    public PedidoHabitacionDialog(java.awt.Window parent, Reserva reserva) {

        super(parent, "Pedidos a habitación", ModalityType.APPLICATION_MODAL);

        this.reserva = reserva;

        setSize(600, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        JLabel titulo = new JLabel("Pedido habitación");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        top.add(titulo);
        add(top, BorderLayout.NORTH);

        // =========================
        // PANEL CENTRAL
        // =========================

        JPanel center = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 15, 20)
        );
        comboProductos = new JComboBox<>();
        for (Producto p : productoDAO.listarTodos()) {    
            comboProductos.addItem(p);
        }
        center.add(new JLabel("Producto:"));
        center.add(comboProductos);
        btnAgregar = new JButton("Agregar Producto");
        btnAgregar.addActionListener(e -> agregarProducto());
        center.add(btnAgregar);
        add(center, BorderLayout.CENTER);

        // =========================
        // PANEL INFERIOR
        // =========================
        JButton cerrar = new JButton("Cerrar");
        cerrar.addActionListener(e -> dispose());
        JPanel bottom = new JPanel();
        bottom.add(cerrar);
        add(bottom, BorderLayout.SOUTH);

    }
    private void agregarProducto() {
            Producto producto =
                (Producto) comboProductos.getSelectedItem();
            if (producto == null) {
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
                JOptionPane.showMessageDialog(this,"Producto agregado correctamente");     
            }
            else {
                    JOptionPane.showMessageDialog(this,"No se pudo agregar el producto");
                }
    }
}