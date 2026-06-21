package com.santaana.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;

import com.santaana.dao.HabitacionDAO;
import com.santaana.dao.HistorialDAO;
import com.santaana.dao.ReservaDAO;
import com.santaana.dao.ReservaProductoDAO;
import com.santaana.model.Habitacion;
import com.santaana.model.ReservaProducto;
import com.santaana.service.CobroService;
import com.santaana.util.DateUtil;
import com.santaana.util.ThemeManager;


public class TableroPanel extends JPanel {
    private String nombreUsuario;
    private String userRole;
    private Runnable onNuevaReserva;
    private Runnable onEstadoCambiado;
    private boolean isPlaceholderActive = true;
    private boolean suppressFilter = false;
    private final String PLACEHOLDER = " Buscar habitación...";
    private final HabitacionDAO habitacionDAO = new HabitacionDAO();
    private final ReservaDAO    reservaDAO    = new ReservaDAO();
    private final ReservaProductoDAO reservaProductoDAO = new ReservaProductoDAO();
    private JPanel roomsGrid;
    

    private Color getBorde() { return ThemeManager.getBorder(); }
    private Color getPrimario() { return ThemeManager.getPrimary(); }
    private Color getLabel() { return ThemeManager.getTextSecondary(); }
    private Color getBackgroundCol() { return ThemeManager.getBackground(); }
    private Color getPanelCol() { return ThemeManager.getPanelBackground(); }
    private Color getTextCol() { return ThemeManager.getTextPrimary(); }

    

    public TableroPanel(String role,String nombreUsuario, Runnable onNuevaReserva, Runnable onEstadoCambiado) {
        this.userRole         = role;
        this.nombreUsuario    =  nombreUsuario;
        this.onNuevaReserva   = onNuevaReserva;
        this.onEstadoCambiado = onEstadoCambiado;
        setLayout(new BorderLayout());
        setBackground(getBackgroundCol());
        refreshUI();
    }

    public void refreshUI() {
        removeAll();
        add(crearNavbar(), BorderLayout.NORTH);
        add(crearContenido(), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel crearNavbar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(getPanelCol());
        navbar.setPreferredSize(new Dimension(0, 50));
        navbar.setBorder(new MatteBorder(0, 0, 1, 0, getBorde()));

        JLabel title = new JLabel("  DASHBOARD PRINCIPAL");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(getTextCol());
        navbar.add(title, BorderLayout.WEST);

        return navbar;
    }

    private JPanel crearContenido() {
        JPanel cont = new JPanel(new BorderLayout(0, 16));
        cont.setOpaque(false);
        cont.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel top = new JPanel(new BorderLayout(14, 0));
        top.setOpaque(false);
        top.add(statsRow(), BorderLayout.CENTER);

        JPanel alertas = crearAlertasVencidas();
        if (alertas != null) top.add(alertas, BorderLayout.EAST);

        cont.add(top, BorderLayout.NORTH);
        cont.add(roomsArea(), BorderLayout.CENTER);
        return cont;
    }

    private JPanel crearAlertasVencidas() {
        String ahora = DateUtil.formatearFechaHora(new java.util.Date());
        java.util.List<com.santaana.model.Reserva> vencidas = new java.util.ArrayList<>();
        for (com.santaana.model.Reserva r : reservaDAO.listarActivas()) {
            if ("Indefinido".equals(r.getTipoEstadia())) continue;
            // Saltar reservas que aun no han iniciado (futuras)
            if (r.getFechaEntrada().compareTo(java.time.LocalDate.now().toString()) > 0) continue;
            if ((r.getFechaSalida() + " " + r.getHoraSalida()).compareTo(ahora) < 0) vencidas.add(r);
        }
        if (vencidas.isEmpty()) return null;

        JPanel panel = new JPanel(new BorderLayout(0, 6)) {
            @Override public Dimension getPreferredSize() {
                return new Dimension(340, super.getPreferredSize().height);
            }
            @Override public Dimension getMinimumSize() { return getPreferredSize(); }
            @Override public Dimension getMaximumSize() { return new Dimension(340, Integer.MAX_VALUE); }
        };
        panel.setBackground(new Color(0xFFF3CD));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xFFC107), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)));

        JLabel titulo = new JLabel("⚠  Checkouts vencidos (" + vencidas.size() + ")");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titulo.setForeground(new Color(0x856404));
        panel.add(titulo, BorderLayout.NORTH);

        JPanel lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBackground(new Color(0xFFF3CD));

        java.util.Map<Integer, Habitacion> habMap = new java.util.HashMap<>();
        for (Habitacion h : habitacionDAO.listarTodas()) habMap.put(h.getId(), h);

        java.time.LocalDate hoy = java.time.LocalDate.now();

        for (com.santaana.model.Reserva r : vencidas) {
            long diasVencida = 0;
            try {
                diasVencida = hoy.toEpochDay() - java.time.LocalDate.parse(r.getFechaSalida()).toEpochDay();
            } catch (Exception ignored) {}
            String demora = diasVencida <= 0 ? "hoy" : "hace " + diasVencida + (diasVencida == 1 ? " día" : " días");

            JPanel fila = new JPanel(new BorderLayout(8, 0));
            fila.setOpaque(false);
            fila.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

            JPanel info = new JPanel();
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            info.setOpaque(false);

            JLabel linea1 = new JLabel("Hab. " + obtenerNumeroHabitacion(r.getIdHabitacion())
                + "  —  " + r.getClienteNombre());
            linea1.setFont(new Font("Segoe UI", Font.BOLD, 11));
            linea1.setForeground(new Color(0x856404));

            JLabel linea2 = new JLabel("Salida: " + r.getFechaSalida() + " " + r.getHoraSalida()
                + "  ·  " + demora);
            linea2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            linea2.setForeground(new Color(0xA07820));

            info.add(linea1);
            info.add(Box.createVerticalStrut(2));
            info.add(linea2);

            Habitacion hab = habMap.get(r.getIdHabitacion());

            JButton btnCO = miniBotonAlerta("Checkout", new Color(0xE74C3C));
            if (hab != null) btnCO.addActionListener(e -> hacerCheckout(hab, null));
            else btnCO.setEnabled(false);

            fila.add(info, BorderLayout.CENTER);
            fila.add(btnCO, BorderLayout.EAST);
            lista.add(fila);
        }

        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(null);
        scroll.setBackground(new Color(0xFFF3CD));
        scroll.getViewport().setBackground(new Color(0xFFF3CD));
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setPreferredSize(new Dimension(0, Math.min(vencidas.size(), 3) * 46));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private String obtenerNumeroHabitacion(int idHabitacion) {
        return habitacionDAO.listarTodas().stream()
            .filter(h -> h.getId() == idHabitacion)
            .map(Habitacion::getNumero)
            .findFirst().orElse("?");
    }

    private JPanel statsRow() {
        java.util.List<Habitacion> todas = habitacionDAO.listarTodas();
        long disponibles   = todas.stream().filter(h -> h.getEstado().equals("Disponible")).count();
        long ocupadas      = todas.stream().filter(h -> h.getEstado().equals("Ocupada")).count();
        long limpieza      = todas.stream().filter(h -> h.getEstado().equals("Limpieza")).count();
        long mantenimiento = todas.stream().filter(h -> h.getEstado().equals("Mantenimiento")).count();

        JPanel row = new JPanel(new GridLayout(1, 5, 14, 0));
        row.setOpaque(false);
        row.add(statCard("Disponibles",   String.valueOf(disponibles),   new Color(0x27AE60)));
        row.add(statCard("Ocupadas",      String.valueOf(ocupadas),      new Color(0xE74C3C)));
        row.add(statCard("En limpieza",   String.valueOf(limpieza),      new Color(0x3A7BD5)));
        row.add(statCard("Mantenimiento", String.valueOf(mantenimiento), new Color(0xE67E22)));
        row.add(statCard("Total",         String.valueOf(todas.size()),  new Color(0x95A5A6)));
        return row;
    }

    private JPanel statCard(String label, String value, Color accent) {
        JPanel c = new JPanel(new BorderLayout()) {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getPanelCol());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(accent);
                g2.fillRect(0, getHeight() - 4, getWidth(), 4);
                g2.dispose();
            }
        };
        c.setOpaque(false);
        c.setPreferredSize(new Dimension(0, 85));
        c.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel num = new JLabel(value);
        num.setFont(new Font("Segoe UI", Font.BOLD, 26));
        num.setForeground(getTextCol());
        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lbl.setForeground(getLabel());
        c.add(num, BorderLayout.CENTER);
        c.add(lbl, BorderLayout.SOUTH);
        return c;
    }

    private JPanel roomsArea() {
        JPanel area = new JPanel(new BorderLayout(0, 10));
        area.setOpaque(false);

        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(false);

        JLabel titleLabel = new JLabel("Estado actual de habitaciones");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(getTextCol());
        header.add(titleLabel, BorderLayout.WEST);

        final JTextField searchField = new JTextField(PLACEHOLDER);
        searchField.setPreferredSize(new Dimension(250, 30));
        searchField.setBackground(getPanelCol());
        searchField.setForeground(getLabel());
        searchField.setBorder(BorderFactory.createLineBorder(getBorde(), 1, true));

        searchField.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (isPlaceholderActive) {
                    searchField.setText("");
                    searchField.setForeground(getTextCol());
                    isPlaceholderActive = false;
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    suppressFilter = true;
                    isPlaceholderActive = true;
                    searchField.setText(PLACEHOLDER);
                    searchField.setForeground(getLabel());
                    suppressFilter = false;
                    // No reconstruimos el grid aquí para no destruir botones que se están clickeando
                }
            }
        });

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void filter() {
                if (suppressFilter) return;
                String txt = isPlaceholderActive ? "" : searchField.getText().trim().toLowerCase();
                actualizarGrid(txt);
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
        });

        header.add(searchField, BorderLayout.EAST);
        area.add(header, BorderLayout.NORTH);

        roomsGrid = new JPanel(new GridLayout(0, 3, 12, 12));
        roomsGrid.setOpaque(false);
        actualizarGrid("");

        JScrollPane scroll = new JScrollPane(roomsGrid);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        area.add(scroll, BorderLayout.CENTER);
        return area;
    }

    private void actualizarGrid(String filtro) {
        roomsGrid.removeAll();
        java.util.List<Habitacion> lista = habitacionDAO.listarTodas().stream()
            .filter(h -> filtro.isEmpty()
                || h.getNumero().toLowerCase().contains(filtro)
                || h.getTipo().toLowerCase().contains(filtro)
                || h.getEstado().toLowerCase().contains(filtro))
            .collect(java.util.stream.Collectors.toList());

        // Cargar próximas reservas (check-in de hoy o futuro) para mostrar en tarjetas
        java.util.Map<Integer, com.santaana.model.Reserva> proximas = new java.util.HashMap<>();
        String hoyStr = DateUtil.formatearFecha(new java.util.Date());
        for (com.santaana.model.Reserva r : reservaDAO.listarActivas()) {
            if (r.getFechaEntrada().compareTo(hoyStr) >= 0) { // >= incluye reservas de hoy
                proximas.putIfAbsent(r.getIdHabitacion(), r);
            }
        }

        if (lista.isEmpty()) {
            JLabel msg = new JLabel("No se encontraron habitaciones.");
            msg.setForeground(getLabel());
            roomsGrid.add(msg);
        } else {
            for (Habitacion h : lista) roomsGrid.add(roomCard(h, proximas.get(h.getId())));
        }
        roomsGrid.revalidate();
        roomsGrid.repaint();
    }

    private JPanel roomCard(Habitacion h, com.santaana.model.Reserva proximaReserva) {
        JPanel c = new JPanel() {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getPanelCol());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(getBorde());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        c.setOpaque(false);
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel n = new JLabel("Habitación " + h.getNumero());
        n.setFont(new Font("Segoe UI", Font.BOLD, 15));
        n.setForeground(getTextCol());
        n.setAlignmentX(0.0f);

        Color colorEstado;
        switch (h.getEstado()) {
            case "Disponible":    colorEstado = new Color(0x27AE60); break;
            case "Ocupada":       colorEstado = new Color(0xE74C3C); break;
            case "Limpieza":      colorEstado = new Color(0x3A7BD5); break;
            case "Mantenimiento": colorEstado = new Color(0xE67E22); break;
            default:              colorEstado = new Color(0x95A5A6);
        }
        JLabel estadoLbl = new JLabel(h.getEstado());
        estadoLbl.setForeground(colorEstado);
        estadoLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        estadoLbl.setAlignmentX(0.0f);

        JLabel info = new JLabel(String.format("<html>%s<br>$%,.0f / Noche</html>", h.getTipo(), h.getPrecio()));
        info.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        info.setForeground(getLabel());
        info.setAlignmentX(0.0f);
        info.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        c.add(n);
        c.add(Box.createVerticalStrut(5));
        c.add(estadoLbl);
        c.add(Box.createVerticalStrut(10));
        c.add(info);

        if (proximaReserva != null && h.getEstado().equals("Disponible")) {
            JLabel badge = new JLabel("Reservada: " + proximaReserva.getFechaEntrada()
                + " " + proximaReserva.getHoraEntrada());
            badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
            badge.setForeground(new Color(0xE67E22));
            badge.setAlignmentX(0.0f);
            c.add(Box.createVerticalStrut(4));
            c.add(badge);
        }

        JLabel hint = new JLabel("Doble clic para acciones");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        hint.setForeground(new Color(180, 190, 210));
        hint.setAlignmentX(0.0f);
        c.add(Box.createVerticalStrut(8));
        c.add(hint);

        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        c.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) abrirAcciones(h, proximaReserva);
            }
        });

        return c;
    }

    private void abrirAcciones(Habitacion h, com.santaana.model.Reserva proximaReserva) {
        javax.swing.JDialog dialog = new javax.swing.JDialog(
            SwingUtilities.getWindowAncestor(this) instanceof javax.swing.JFrame
                ? (javax.swing.JFrame) SwingUtilities.getWindowAncestor(this) : null,
            "Habitación " + h.getNumero(), true);
        dialog.setLayout(new BorderLayout());

        // Header
        Color headerColor;
        switch (h.getEstado()) {
            case "Ocupada":       headerColor = new Color(0xE74C3C); break;
            case "Limpieza":      headerColor = new Color(0x3A7BD5); break;
            case "Mantenimiento": headerColor = new Color(0xE67E22); break;
            default:              headerColor = new Color(0x27AE60);
        }
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(headerColor);
        header.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        JLabel titulo = new JLabel("Habitación " + h.getNumero() + "  —  " + h.getEstado());
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titulo.setForeground(Color.WHITE);
        header.add(titulo);
        dialog.add(header, BorderLayout.NORTH);

        // Botones de acción
        JPanel acciones = new JPanel();
        acciones.setLayout(new BoxLayout(acciones, BoxLayout.Y_AXIS));
        acciones.setBorder(BorderFactory.createEmptyBorder(12, 20, 16, 20));
        acciones.setBackground(getPanelCol());

        if (h.getEstado().equals("Ocupada")) {
            com.santaana.model.Reserva rActiva = reservaDAO.buscarActivaPorHabitacion(h.getId());
            if (rActiva != null) {
                // Monto de productos pedidos a esta reserva
                double montoProductos = 0;
                for (ReservaProducto rp : reservaProductoDAO.listarPorReserva(rActiva.getId())) {
                    montoProductos += rp.getCantidad() * rp.getPrecio();
                }

                // Calcular total y saldo para estadía por noches
                String totalStr = "—", saldoStr = "—";
                if ("Noche".equals(rActiva.getTipoEstadia())) {
                    try {
                        long dias = java.time.LocalDate.parse(rActiva.getFechaSalida()).toEpochDay()
                                  - java.time.LocalDate.parse(rActiva.getFechaEntrada()).toEpochDay();
                        if (dias < 1) dias = 1;
                        double total = dias * h.getPrecio() + montoProductos;
                        double saldo = total - rActiva.getAnticipo();
                        totalStr = String.format("$%,.0f", total);
                        saldoStr = String.format("$%,.0f", saldo);
                    } catch (Exception ignored) {}
                } else {
                    double total = montoProductos;
                    double saldo = total - rActiva.getAnticipo();
                    totalStr = montoProductos > 0
                        ? String.format("$%,.0f (al salir)", total)
                        : "Cobro por al salir";
                    saldoStr = montoProductos > 0
                        ? String.format("$%,.0f", saldo)
                        : (rActiva.getAnticipo() > 0
                            ? String.format("(Anticipo: $%,.0f)", rActiva.getAnticipo()) : "Pendiente checkout");
                }

                String salida = "Indefinido".equals(rActiva.getTipoEstadia()) ? "Sin determinar"
                    : rActiva.getFechaSalida() + " " + rActiva.getHoraSalida();

                JPanel info = new JPanel();
                info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
                info.setOpaque(false);
                info.setAlignmentX(0f);

                info.add(filaInfo("Huésped:", rActiva.getClienteNombre()));
                info.add(Box.createVerticalStrut(5));
                info.add(filaInfo("Documento:", rActiva.getClienteDoc()));
                info.add(Box.createVerticalStrut(5));
                info.add(filaInfo("Entrada:", rActiva.getFechaEntrada() + " " + rActiva.getHoraEntrada()));
                info.add(Box.createVerticalStrut(5));
                info.add(filaInfo("Salida:", salida));
                info.add(Box.createVerticalStrut(5));
                if (montoProductos > 0) {
                    info.add(filaInfo("Productos:", String.format("$%,.0f", montoProductos)));
                    info.add(Box.createVerticalStrut(5));
                }
                info.add(filaInfo("Total:", totalStr));
                info.add(Box.createVerticalStrut(5));
                info.add(filaInfo("Anticipo:", String.format("$%,.0f", rActiva.getAnticipo())));
                info.add(Box.createVerticalStrut(5));
                info.add(filaInfo("Saldo:", saldoStr));

                javax.swing.JSeparator sep = new javax.swing.JSeparator();
                sep.setForeground(getBorde());
                sep.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 1));

                acciones.add(info);
                acciones.add(Box.createVerticalStrut(10));
                acciones.add(sep);
                acciones.add(Box.createVerticalStrut(10));
            }
            if (rActiva != null) {
                JButton btnPago = crearBoton("Registrar Pago / Abono", new Color(0x27AE60));
                btnPago.addActionListener(e -> {
                    dialog.dispose();
                    mostrarDialogoPago(h, rActiva);
                });

                acciones.add(btnPago);
                acciones.add(Box.createVerticalStrut(8));

                JButton btnPedido = crearBoton(
                    "Pedir Productos",
                    new Color(0x3B82F6)
                );
                btnPedido.addActionListener(e -> {

                    PedidoHabitacionDialog pedidoDialog = new PedidoHabitacionDialog(dialog, rActiva);
                    pedidoDialog.setVisible(true);
                    dialog.dispose();
                    abrirAcciones(h, proximaReserva);
                });
                acciones.add(btnPedido);
                acciones.add(Box.createVerticalStrut(8));
            }

            if (rActiva != null) {
                JButton btn = crearBoton("Hacer Checkout", new Color(0xE74C3C));
                btn.addActionListener(e -> { dialog.dispose(); hacerCheckout(h, null); });
                acciones.add(btn);
                acciones.add(Box.createVerticalStrut(10));
            }
        }

        if (h.getEstado().equals("Limpieza")) {
            JButton btn = crearBoton("✓ Marcar Disponible", new Color(0x27AE60));
            btn.addActionListener(e -> {
                habitacionDAO.actualizarEstado(h.getId(), "Disponible");
                HistorialDAO.registrar("Habitacion", "Habitación disponible",
                    "Hab. " + h.getNumero() + " pasó de Limpieza a Disponible");
                dialog.dispose();
                if (onEstadoCambiado != null) SwingUtilities.invokeLater(onEstadoCambiado);
            });
            acciones.add(btn);
            acciones.add(Box.createVerticalStrut(10));
        }

        if (h.getEstado().equals("Disponible")) {
            if (proximaReserva != null) {
                JButton btnCI = crearBoton("Check-in: " + proximaReserva.getClienteNombre(),
                    new Color(0x27AE60));
                btnCI.addActionListener(e -> {
                    // Actualizar fecha de entrada al dia de hoy para que buscarActivaPorHabitacion() lo encuentre
                    String hoy = DateUtil.formatearFecha(new java.util.Date());
                    String ahora = DateUtil.formatearHora(new java.util.Date());
                    reservaDAO.actualizar(proximaReserva.getId(),
                        hoy, ahora,
                        proximaReserva.getFechaSalida(), proximaReserva.getHoraSalida(),
                        proximaReserva.getEstado(), proximaReserva.getAnticipo());
                    habitacionDAO.actualizarEstado(h.getId(), "Ocupada");
                    HistorialDAO.registrar("Checkin", "Check-in realizado",
                        proximaReserva.getClienteNombre() + " realizó check-in en Hab. " + h.getNumero());
                    dialog.dispose();
                    if (onEstadoCambiado != null) SwingUtilities.invokeLater(onEstadoCambiado);
                });
                acciones.add(btnCI);
                acciones.add(Box.createVerticalStrut(10));
            }
            JButton btn = crearBoton("Poner en Mantenimiento", new Color(0xE67E22));
            btn.addActionListener(e -> {
                int ok = JOptionPane.showConfirmDialog(dialog,
                    "¿Poner habitación " + h.getNumero() + " en Mantenimiento?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
                if (ok == JOptionPane.YES_OPTION) {
                    habitacionDAO.actualizarEstado(h.getId(), "Mantenimiento");
                    HistorialDAO.registrar("Habitacion", "Habitación en mantenimiento",
                        "Hab. " + h.getNumero() + " pasó a estado Mantenimiento");
                    dialog.dispose();
                    if (onEstadoCambiado != null) SwingUtilities.invokeLater(onEstadoCambiado);
                }
            });
            acciones.add(btn);
            acciones.add(Box.createVerticalStrut(10));
        }

        if (h.getEstado().equals("Mantenimiento")) {
            JButton btn = crearBoton("✓ Marcar Disponible", new Color(0x27AE60));
            btn.addActionListener(e -> {
                habitacionDAO.actualizarEstado(h.getId(), "Disponible");
                HistorialDAO.registrar("Habitacion", "Habitación disponible",
                    "Hab. " + h.getNumero() + " pasó de Mantenimiento a Disponible");
                dialog.dispose();
                if (onEstadoCambiado != null) SwingUtilities.invokeLater(onEstadoCambiado);
            });
            acciones.add(btn);
            acciones.add(Box.createVerticalStrut(10));
        }

        JButton btnHistorial = crearBoton("Ver Historial", new Color(0x6366F1));
        btnHistorial.addActionListener(e -> { dialog.dispose(); abrirHistorial(h); });
        acciones.add(btnHistorial);

        dialog.add(acciones, BorderLayout.CENTER);
        // Ajustar tamaño según contenido
        dialog.setSize(h.getEstado().equals("Ocupada") ? 360 : 320,
                       h.getEstado().equals("Ocupada") ? 470 : 260);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private JPanel filaInfo(String etiqueta, String valor) {
        JPanel fila = new JPanel(new BorderLayout(8, 0));
        fila.setOpaque(false);
        fila.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 18));
        fila.setAlignmentX(0f);
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(getLabel());
        lbl.setPreferredSize(new java.awt.Dimension(72, 16));
        JLabel val = new JLabel(valor);
        val.setFont(new Font("Segoe UI", Font.BOLD, 11));
        val.setForeground(getTextCol());
        fila.add(lbl, BorderLayout.WEST);
        fila.add(val, BorderLayout.CENTER);
        return fila;
    }

    private void abrirHistorial(Habitacion h) {
        java.util.List<com.santaana.model.Reserva> historial =
            reservaDAO.listarUltimasPorHabitacion(h.getId(), 10);

        javax.swing.JDialog dialog = new javax.swing.JDialog(
            SwingUtilities.getWindowAncestor(this) instanceof javax.swing.JFrame
                ? (javax.swing.JFrame) SwingUtilities.getWindowAncestor(this) : null,
            "Historial — Hab. " + h.getNumero(), true);
        dialog.setSize(780, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(0, 0));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0x6366F1));
        header.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        JLabel titulo = new JLabel("Últimas reservas — Habitación " + h.getNumero());
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(Color.WHITE);
        header.add(titulo, BorderLayout.WEST);
        dialog.add(header, BorderLayout.NORTH);

        // Tabla
        String[] cols = {"Cliente", "Documento", "Entrada", "Hora", "Salida", "Hora", "Tipo", "Estado"};
        Object[][] datos = new Object[historial.size()][8];
        for (int i = 0; i < historial.size(); i++) {
            com.santaana.model.Reserva r = historial.get(i);
            datos[i][0] = r.getClienteNombre();
            datos[i][1] = r.getClienteDoc();
            datos[i][2] = r.getFechaEntrada();
            datos[i][3] = r.getHoraEntrada();
            datos[i][4] = "Indefinido".equals(r.getTipoEstadia()) ? "—" : r.getFechaSalida();
            datos[i][5] = "Indefinido".equals(r.getTipoEstadia()) ? "—" : r.getHoraSalida();
            datos[i][6] = r.getTipoEstadia();
            datos[i][7] = r.getEstado();
        }

        javax.swing.JTable tabla = new javax.swing.JTable(datos, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setRowHeight(28);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tabla.setSelectionBackground(new Color(0xEEF2FF));
        tabla.setGridColor(new Color(0xE5E7EB));

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        dialog.add(scroll, BorderLayout.CENTER);

        if (historial.isEmpty()) {
            dialog.remove(scroll);
            JLabel vacio = new JLabel("No hay reservas registradas para esta habitación.", JLabel.CENTER);
            vacio.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            vacio.setForeground(new Color(0x94A3B8));
            dialog.add(vacio, BorderLayout.CENTER);
        }

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 10));
        JButton btnCerrar = crearBoton("Cerrar", new Color(0x6366F1));
        btnCerrar.setPreferredSize(new Dimension(90, 30));
        btnCerrar.addActionListener(e -> dialog.dispose());
        footer.add(btnCerrar);
        dialog.add(footer, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private JButton miniBotonAlerta(String texto, Color bg) {
        JButton b = new JButton(texto) {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 10));
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(85, 22));
        b.setMaximumSize(new Dimension(85, 22));
        b.setMinimumSize(new Dimension(85, 22));
        return b;
    }

    private JButton crearBoton(String texto, Color bg) {
        JButton b = new JButton(texto) {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 11));
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        b.setAlignmentX(0.0f);
        return b;
    }

    private void mostrarDialogoPago(Habitacion h, com.santaana.model.Reserva r) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        double total = 0;
        boolean totalValido = false;
        try {
            LocalDateTime entrada = LocalDateTime.parse(r.getFechaEntrada() + " " + r.getHoraEntrada(), fmt);
            boolean esIndefinido = "Indefinido".equals(r.getTipoEstadia());
            LocalDateTime salida;
            if (esIndefinido) {
                salida = LocalDateTime.now();
            } else {
                String fechaSalida = r.getFechaSalida();
                String horaSalida = r.getHoraSalida();
                salida = (fechaSalida != null && !fechaSalida.isEmpty())
                    ? LocalDateTime.parse(fechaSalida + " " + (horaSalida != null ? horaSalida : "12:00"), fmt)
                    : LocalDateTime.now();
            }
            total = CobroService.calcularTotal(entrada, salida, h);
            totalValido = true;
        } catch (Exception ignored) {}
        double saldo = totalValido ? Math.max(0, total - r.getAnticipo()) : 0;
        double totalFinal = total;

        javax.swing.JDialog dlg = new javax.swing.JDialog(
            SwingUtilities.getWindowAncestor(this) instanceof javax.swing.JFrame
                ? (javax.swing.JFrame) SwingUtilities.getWindowAncestor(this) : null,
            "Registrar Pago — Hab. " + h.getNumero(), true);
        dlg.setSize(360, 310);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setBackground(new Color(0x27AE60));
        hdr.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        JLabel tit = new JLabel("Registrar Pago — Hab. " + h.getNumero());
        tit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tit.setForeground(Color.WHITE);
        hdr.add(tit);
        dlg.add(hdr, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(14, 18, 10, 18));
        body.setBackground(getPanelCol());

        body.add(filaInfo("Huésped:", r.getClienteNombre()));
        body.add(Box.createVerticalStrut(5));
        body.add(filaInfo("Total:", String.format("$%,.0f", totalFinal)));
        body.add(Box.createVerticalStrut(4));
        body.add(filaInfo("Pagado:", String.format("$%,.0f", r.getAnticipo())));
        body.add(Box.createVerticalStrut(4));
        body.add(filaInfo("Saldo:", String.format("$%,.0f", saldo)));
        body.add(Box.createVerticalStrut(12));

        JPanel inputRow = new JPanel(new BorderLayout(8, 0));
        inputRow.setOpaque(false);
        inputRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JLabel lblM = new JLabel("Monto:");
        lblM.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblM.setForeground(getLabel());
        lblM.setPreferredSize(new Dimension(70, 16));
        JTextField txtMonto = new JTextField();
        txtMonto.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        inputRow.add(lblM, BorderLayout.WEST);
        inputRow.add(txtMonto, BorderLayout.CENTER);
        body.add(inputRow);
        dlg.add(body, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        footer.setBackground(getPanelCol());

        if (saldo > 0) {
            double saldoFinal = saldo;
            JButton btnFull = crearBoton("Pago completo", new Color(0x27AE60));
            btnFull.setPreferredSize(new Dimension(120, 28));
            btnFull.addActionListener(ev -> txtMonto.setText(String.format("%.0f", saldoFinal)));
            footer.add(btnFull);
        }

        JButton btnOk = crearBoton("Confirmar", new Color(0x3A7BD5));
        btnOk.setPreferredSize(new Dimension(90, 28));
        btnOk.addActionListener(ev -> {
            try {
                double monto = Double.parseDouble(txtMonto.getText().replace(",", ".").trim());
                if (monto <= 0) {
                    JOptionPane.showMessageDialog(dlg, "El monto debe ser mayor a cero.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                double nuevo = r.getAnticipo() + monto;
                if (nuevo > totalFinal) {
                    JOptionPane.showMessageDialog(dlg, "El monto supera el saldo pendiente.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                reservaDAO.actualizarAnticipo(r.getId(), nuevo);
                HistorialDAO.registrar("Pago", "Abono registrado",
                    r.getClienteNombre() + " abonó $" + String.format("%,.0f", monto) + " en Hab. " + h.getNumero());
                JOptionPane.showMessageDialog(dlg,
                    "<html>Abono registrado.<br>Monto abonado hoy: <b>$" + String.format("%,.0f", monto) + "</b><br>"
                    + "Total abonado acumulado: <b>$" + String.format("%,.0f", nuevo) + "</b></html>",
                    "Pago exitoso", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                if (onEstadoCambiado != null) SwingUtilities.invokeLater(onEstadoCambiado);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg, "Ingrese un monto válido.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });
        footer.add(btnOk);

        JButton btnNo = crearBoton("Cancelar", new Color(0x95A5A6));
        btnNo.setPreferredSize(new Dimension(80, 28));
        btnNo.addActionListener(ev -> dlg.dispose());
        footer.add(btnNo);

        dlg.add(footer, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void hacerCheckout(Habitacion h, JPanel card) {
        com.santaana.model.Reserva reservaActiva = reservaDAO.buscarActivaPorHabitacion(h.getId());
        if (reservaActiva == null) return;

        String cliente = reservaActiva.getClienteNombre();
        boolean esIndefinido = "Indefinido".equals(reservaActiva.getTipoEstadia());
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // Determinar fecha/hora de entrada y salida
        LocalDateTime entrada = LocalDateTime.parse(
            reservaActiva.getFechaEntrada() + " " + reservaActiva.getHoraEntrada(), fmt);

        String horaSalidaStr = reservaActiva.getHoraSalida();
        LocalDateTime salida;
        if (esIndefinido) {
            salida = LocalDateTime.now();
        } else {
            salida = LocalDateTime.parse(
                reservaActiva.getFechaSalida() + " " + (horaSalidaStr != null ? horaSalidaStr : "12:00"), fmt);
        }

        // Calcular total con la logica de 3 tramos
        double total = CobroService.calcularTotal(entrada, salida, h);
        double saldo = total - reservaActiva.getAnticipo();

        String infoBilling = String.format(
            "<br><b>Total a pagar:</b> $%,.0f  |  <b>Anticipo:</b> $%,.0f  |  <b>Saldo:</b> $%,.0f",
            total, reservaActiva.getAnticipo(), saldo);

        // Si hay saldo pendiente, preguntar si desea registrar pago
        if (saldo > 0) {
            int opt = JOptionPane.showConfirmDialog(this,
                "<html>El cliente <b>" + cliente + "</b> tiene un saldo pendiente de "
                + "<b>$" + String.format("%,.0f", saldo) + "</b>.<br>"
                + "Debe registrar el pago antes de hacer checkout.<br><br>"
                + "¿Desea registrar el pago ahora?</html>",
                "Saldo pendiente", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (opt == JOptionPane.YES_OPTION) {
                mostrarDialogoPago(h, reservaActiva);
                return;
            }
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "<html>¿Confirmar checkout de <b>" + cliente + "</b>?<br>"
            + "Habitación " + h.getNumero() + " pasará a <b>Limpieza</b>."
            + infoBilling + "</html>",
            "Confirmar Checkout", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        // Para estadias indefinidas: fijar la fecha de salida actual antes de finalizar
        if (esIndefinido) {
            String ahoraStr = salida.format(fmt);
            String[] partes = ahoraStr.split(" ");
            reservaDAO.actualizar(reservaActiva.getId(),
                reservaActiva.getFechaEntrada(), reservaActiva.getHoraEntrada(),
                partes[0], partes[1], reservaActiva.getEstado(), reservaActiva.getAnticipo());
        }

        // Finalizar: calcula total con CobroService y persiste en BD
        CobroService.finalizarReserva(reservaActiva.getId(), total);

        double anticipo = reservaActiva.getAnticipo();
        double cobradoHoy = Math.max(0, total - anticipo);

        habitacionDAO.actualizarEstado(h.getId(), "Limpieza");
        HistorialDAO.registrar("Checkout", "Check-out completado",
            cliente + " realizó check-out de Hab. " + h.getNumero()
            + " - Total estadía: $" + String.format("%,.0f", total)
            + " | Abonado: $" + String.format("%,.0f", anticipo)
            + " | Cobrado hoy: $" + String.format("%,.0f", cobradoHoy));

        JOptionPane.showMessageDialog(this,
            "<html>Checkout realizado.<br>"
            + "Total estadía: <b>$" + String.format("%,.0f", total) + "</b><br>"
            + "Total abonado: <b>$" + String.format("%,.0f", anticipo) + "</b><br>"
            + "Saldo cobrado hoy: <b>$" + String.format("%,.0f", cobradoHoy) + "</b><br><br>"
            + "Habitación " + h.getNumero() + " en <b>Limpieza</b>.</html>",
            "Checkout exitoso", JOptionPane.INFORMATION_MESSAGE);

        if (onEstadoCambiado != null) SwingUtilities.invokeLater(onEstadoCambiado);
    }

    private void abrirDetalleHabitacion(Habitacion h) {

        Window parentWindow = SwingUtilities.getWindowAncestor(this);

        if (parentWindow instanceof JFrame) {
            InfoHabitacionFrame dialog = new InfoHabitacionFrame((JFrame) parentWindow, h);
            dialog.setVisible(true);
        } else {
            InfoHabitacionFrame dialog = new InfoHabitacionFrame(null, h);
            dialog.setVisible(true);
        }
    }
}