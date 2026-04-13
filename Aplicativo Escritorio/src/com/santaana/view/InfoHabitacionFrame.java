package com.santaana.view;

import com.santaana.dao.ReservaDAO;
import com.santaana.model.Habitacion;
import com.santaana.model.Reserva;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InfoHabitacionFrame extends JDialog {

    private Habitacion habitacion;
    private Reserva reservaActiva;

    // Paleta de colores consistente
    private static final Color COLOR_PRIMARIO = new Color(0x3A7BD5);
    private static final Color COLOR_FONDO    = Color.WHITE;
    private static final Color COLOR_BORDE    = new Color(0xEAF2FB);
    private static final Color COLOR_TEXTO    = new Color(40, 50, 70);
    private static final Color COLOR_LABEL    = new Color(110, 120, 140);
    private static final Color COLOR_VERDE    = new Color(0, 170, 90);

    public InfoHabitacionFrame(JFrame parent, Habitacion habitacion) {
        super(parent, "Detalles de Habitación " + habitacion.getNumero(), true); 
        this.habitacion = habitacion;
        
        buscarDatosReales();
        configurarVentana();
        inicializarComponentes();
        
        setLocationRelativeTo(parent); // Se centra la ventana
    }

    private void buscarDatosReales() {
        ReservaDAO dao = new ReservaDAO();
        for (Reserva r : dao.listarTodas()) {
            if (r.getIdHabitacion() == habitacion.getId() && "Activa".equalsIgnoreCase(r.getEstado())) {
                this.reservaActiva = r;
                break;
            }
        }
    }

    private void configurarVentana() {
        setSize(500, 600);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_FONDO);
    }

    private void inicializarComponentes() {
        // --- HEADER ---
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(COLOR_PRIMARIO);
        header.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel lblHab = new JLabel("HABITACIÓN " + habitacion.getNumero());
        lblHab.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHab.setForeground(Color.WHITE);

        JLabel lblTipo = new JLabel(habitacion.getTipo() + " — " + habitacion.getEstado());
        lblTipo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTipo.setForeground(new Color(0xD0E1F9));

        header.add(lblHab);
        header.add(lblTipo);
        add(header, BorderLayout.NORTH);

        
        JPanel cuerpo = new JPanel();
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setBackground(COLOR_FONDO);
        cuerpo.setBorder(new EmptyBorder(30, 35, 30, 35));

        if (reservaActiva != null) {
            cuerpo.add(crearSeccionInfo("Huésped Titular", reservaActiva.getClienteNombre()));
            cuerpo.add(Box.createVerticalStrut(20));
            cuerpo.add(crearSeccionInfo("Fecha de Entrada", reservaActiva.getFechaEntrada()));
            cuerpo.add(Box.createVerticalStrut(20));
            cuerpo.add(crearSeccionInfo("Fecha de Salida", reservaActiva.getFechaSalida()));
            cuerpo.add(Box.createVerticalStrut(20));
            cuerpo.add(crearSeccionInfo("Precio por Noche", "$" + habitacion.getPrecio()));
        } else {
            JLabel msg = new JLabel("<html><center>No hay una reserva activa para esta habitación.</center></html>");
            msg.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            msg.setForeground(COLOR_LABEL);
            msg.setAlignmentX(Component.CENTER_ALIGNMENT);
            cuerpo.add(Box.createVerticalGlue());
            cuerpo.add(msg);
            cuerpo.add(Box.createVerticalGlue());
        }

        add(cuerpo, BorderLayout.CENTER);

        
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(COLOR_FONDO);
        footer.setBorder(new EmptyBorder(10, 10, 20, 25));

        JButton btnCerrar = new JButton("Entendido");
        estilizarBoton(btnCerrar);
        btnCerrar.addActionListener(e -> dispose());
        
        footer.add(btnCerrar);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel crearSeccionInfo(String titulo, String contenido) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(500, 45));

        JLabel lblTit = new JLabel(titulo.toUpperCase());
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTit.setForeground(COLOR_LABEL);

        JLabel lblCont = new JLabel(contenido != null ? contenido : "No registrado");
        lblCont.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblCont.setForeground(COLOR_TEXTO);

        p.add(lblTit, BorderLayout.NORTH);
        p.add(lblCont, BorderLayout.CENTER);

        // Línea divisoria sutil
        p.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE));
        
        return p;
    }

    private void estilizarBoton(JButton btn) {
        btn.setPreferredSize(new Dimension(120, 35));
        btn.setBackground(COLOR_PRIMARIO);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}