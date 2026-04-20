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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.SwingUtilities;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;

import com.santaana.dao.HabitacionDAO;
import com.santaana.dao.ReservaDAO;
import com.santaana.model.Habitacion;
import com.santaana.util.ThemeManager;

public class TableroPanel extends JPanel {
    private String userRole;
    private Runnable onNuevaReserva;
    private Runnable onEstadoCambiado;
    private boolean isPlaceholderActive = true;
    private boolean suppressFilter = false;
    private final String PLACEHOLDER = " Buscar habitación...";
    private final HabitacionDAO habitacionDAO = new HabitacionDAO();
    private final ReservaDAO    reservaDAO    = new ReservaDAO();
    private JPanel roomsGrid;

    private Color getBorde() { return ThemeManager.getBorder(); }
    private Color getPrimario() { return ThemeManager.getPrimary(); }
    private Color getLabel() { return ThemeManager.getTextSecondary(); }
    private Color getBackgroundCol() { return ThemeManager.getBackground(); }
    private Color getPanelCol() { return ThemeManager.getPanelBackground(); }
    private Color getTextCol() { return ThemeManager.getTextPrimary(); }

    public TableroPanel(String role, Runnable onNuevaReserva, Runnable onEstadoCambiado) {
        this.userRole         = role;
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
        cont.add(statsRow(), BorderLayout.NORTH);

        JPanel alertas = crearAlertasVencidas();
        if (alertas != null) cont.add(alertas, BorderLayout.SOUTH);

        cont.add(roomsArea(), BorderLayout.CENTER);
        return cont;
    }

    private JPanel crearAlertasVencidas() {
        String ahora = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date());
        java.util.List<com.santaana.model.Reserva> vencidas = new java.util.ArrayList<>();
        for (com.santaana.model.Reserva r : reservaDAO.listarActivas()) {
            if ("Indefinido".equals(r.getTipoEstadia())) continue;
            String salida = r.getFechaSalida() + " " + r.getHoraSalida();
            if (salida.compareTo(ahora) < 0) {
                vencidas.add(r);
            }
        }
        if (vencidas.isEmpty()) return null;

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(0xFFF3CD));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xFFC107), 1),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        JLabel titulo = new JLabel("⚠  Checkouts vencidos (" + vencidas.size() + ")");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titulo.setForeground(new Color(0x856404));
        titulo.setAlignmentX(0f);
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(6));

        for (com.santaana.model.Reserva r : vencidas) {
            JLabel fila = new JLabel("  · Hab. " + obtenerNumeroHabitacion(r.getIdHabitacion())
                + "  —  " + r.getClienteNombre()
                + "  —  Salida: " + r.getFechaSalida() + " " + r.getHoraSalida());
            fila.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            fila.setForeground(new Color(0x856404));
            fila.setAlignmentX(0f);
            panel.add(fila);
        }
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

        // Cargar próximas reservas (check-in futuro) para mostrar en tarjetas
        java.util.Map<Integer, com.santaana.model.Reserva> proximas = new java.util.HashMap<>();
        String hoyStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
        for (com.santaana.model.Reserva r : reservaDAO.listarActivas()) {
            if (r.getFechaEntrada().compareTo(hoyStr) > 0) {
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
        dialog.setSize(320, 260);
        dialog.setLocationRelativeTo(this);
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
        acciones.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        acciones.setBackground(getPanelCol());

        if (h.getEstado().equals("Ocupada")) {
            JButton btn = crearBoton("Hacer Checkout", new Color(0xE74C3C));
            btn.addActionListener(e -> { dialog.dispose(); hacerCheckout(h, null); });
            acciones.add(btn);
            acciones.add(Box.createVerticalStrut(10));
        }

        if (h.getEstado().equals("Limpieza")) {
            JButton btn = crearBoton("✓ Marcar Disponible", new Color(0x27AE60));
            btn.addActionListener(e -> {
                habitacionDAO.actualizarEstado(h.getId(), "Disponible");
                dialog.dispose();
                if (onEstadoCambiado != null) SwingUtilities.invokeLater(onEstadoCambiado);
            });
            acciones.add(btn);
            acciones.add(Box.createVerticalStrut(10));
        }

        if (h.getEstado().equals("Disponible")) {
            if (proximaReserva != null) {
                JButton btnCI = crearBoton("Check-in: " + proximaReserva.getClienteNombre(),
                    new Color(0xE74C3C));
                btnCI.addActionListener(e -> {
                    habitacionDAO.actualizarEstado(h.getId(), "Ocupada");
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
        dialog.setVisible(true);
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

    private void hacerCheckout(Habitacion h, JPanel card) {
        com.santaana.model.Reserva reservaActiva = reservaDAO.buscarActivaPorHabitacion(h.getId());

        String cliente = reservaActiva != null ? reservaActiva.getClienteNombre() : "desconocido";

        String infoBilling = "";
        if (reservaActiva != null && "Indefinido".equals(reservaActiva.getTipoEstadia())) {
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
                java.util.Date entrada = sdf.parse(
                    reservaActiva.getFechaEntrada() + " " + reservaActiva.getHoraEntrada());
                long minutos = (System.currentTimeMillis() - entrada.getTime()) / (1000 * 60);
                long horas   = minutos / 60;
                long mins    = minutos % 60;
                String bloque = horas < 3 ? "3 horas (mínimo)"
                              : horas < 6 ? "6 horas"
                              : horas < 12 ? "12 horas"
                              : "1 noche";
                infoBilling = "<br><b>Estadía indefinida:</b> " + horas + "h " + mins + "min  →  cobro: " + bloque;
            } catch (Exception ignored) {}
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "<html>¿Confirmar checkout de <b>" + cliente + "</b>?<br>"
            + "Habitación " + h.getNumero() + " pasará a <b>Limpieza</b>."
            + infoBilling + "</html>",
            "Confirmar Checkout", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        if (reservaActiva != null) {
            reservaDAO.actualizarEstado(reservaActiva.getId(), "Completada");
        }
        habitacionDAO.actualizarEstado(h.getId(), "Limpieza");

        JOptionPane.showMessageDialog(this,
            "<html>Checkout realizado.<br>Habitación " + h.getNumero() + " en <b>Limpieza</b>.</html>",
            "Checkout exitoso", JOptionPane.INFORMATION_MESSAGE);

        if (onEstadoCambiado != null) SwingUtilities.invokeLater(onEstadoCambiado);
    }
}
