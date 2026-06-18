package com.santaana.view;

import com.santaana.dao.ReservaDAO;
import com.santaana.model.Habitacion;
import com.santaana.model.Reserva;
import com.santaana.service.CobroService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class InfoHabitacionFrame extends JDialog {

    private Habitacion habitacion;
    private Reserva reservaActiva;

    private static final Color COLOR_FONDO      = new Color(0xF8FAFF);
    private static final Color COLOR_CARD       = Color.WHITE;
    private static final Color COLOR_BORDE      = new Color(0xE4EDF8);
    private static final Color COLOR_TEXTO      = new Color(30, 42, 62);
    private static final Color COLOR_LABEL      = new Color(120, 135, 160);
    private static final Color COLOR_AZUL       = new Color(0x3A7BD5);
    private static final Color COLOR_VERDE      = new Color(0x00AA5A);
    private static final Color COLOR_NARANJA    = new Color(0xF59E0B);

    public InfoHabitacionFrame(JFrame parent, Habitacion habitacion) {
        super(parent, "Detalle de Habitación", true);
        this.habitacion = habitacion;
        buscarReservaActiva();
        configurarVentana();
        construirUI();
        setLocationRelativeTo(parent);
    }

    private void buscarReservaActiva() {
        ReservaDAO dao = new ReservaDAO();
        for (Reserva r : dao.listarTodas()) {
            if (r.getIdHabitacion() == habitacion.getId()
                    && "Activa".equalsIgnoreCase(r.getEstado())) {
                this.reservaActiva = r;
                break;
            }
        }
    }

    private void configurarVentana() {
        setSize(460, reservaActiva != null ? 640 : 420);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_FONDO);
    }

    private String calcularSaldo() {
        if (reservaActiva == null) return "—";
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime entrada = LocalDateTime.parse(
                reservaActiva.getFechaEntrada() + " " + reservaActiva.getHoraEntrada(), fmt);
            LocalDateTime salida;
            if ("Indefinido".equals(reservaActiva.getTipoEstadia())) {
                salida = LocalDateTime.now();
            } else {
                salida = LocalDateTime.parse(
                    reservaActiva.getFechaSalida() + " " + (reservaActiva.getHoraSalida() != null ? reservaActiva.getHoraSalida() : "12:00"), fmt);
            }
            double total = CobroService.calcularTotal(entrada, salida, habitacion);
            double saldo = total - reservaActiva.getAnticipo();
            return String.format("$ %,.0f", saldo);
        } catch (Exception ignored) {}
        return "Indefinido";
    }

    private void construirUI() {
        add(crearHeader(), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel();
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setBackground(COLOR_FONDO);
        cuerpo.setBorder(new EmptyBorder(20, 22, 10, 22));

        cuerpo.add(crearCardHabitacion());
        cuerpo.add(Box.createVerticalStrut(14));

        if (reservaActiva != null) {
            cuerpo.add(crearCardReserva());
        } else {
            cuerpo.add(crearEstadoDisponible());
        }

        add(cuerpo, BorderLayout.CENTER);
        add(crearFooter(), BorderLayout.SOUTH);
    }

    // ── HEADER ──────────────────────────────────────────────────────────────

    private JPanel crearHeader() {
        Color colorHeader = colorSegunEstado();

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(colorHeader);
        header.setBorder(new EmptyBorder(22, 26, 18, 26));

        // Número y tipo
        JPanel izq = new JPanel();
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));
        izq.setOpaque(false);

        JLabel lblNumero = new JLabel("Habitación " + habitacion.getNumero());
        lblNumero.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblNumero.setForeground(Color.WHITE);

        JLabel lblTipo = new JLabel(habitacion.getTipo());
        lblTipo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTipo.setForeground(new Color(255, 255, 255, 200));

        izq.add(lblNumero);
        izq.add(Box.createVerticalStrut(3));
        izq.add(lblTipo);

        // Badge de estado
        JLabel badge = crearBadgeEstado();

        header.add(izq, BorderLayout.WEST);
        header.add(badge, BorderLayout.EAST);
        return header;
    }

    private JLabel crearBadgeEstado() {
        String estado = habitacion.getEstado();
        JLabel badge = new JLabel(estado) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 50));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(Color.WHITE);
        badge.setBorder(new EmptyBorder(5, 12, 5, 12));
        badge.setOpaque(false);
        return badge;
    }

    private Color colorSegunEstado() {
        switch (habitacion.getEstado().toLowerCase()) {
            case "ocupada":    return COLOR_AZUL;
            case "disponible": return COLOR_VERDE;
            default:           return COLOR_NARANJA; // mantenimiento u otro
        }
    }

    // ── CARD HABITACIÓN ─────────────────────────────────────────────────────

    private JPanel crearCardHabitacion() {
        JPanel card = crearCard();

        JLabel titulo = crearTituloSeccion("INFORMACIÓN DE LA HABITACIÓN");
        card.add(titulo);
        card.add(Box.createVerticalStrut(14));

        JPanel grid = new JPanel(new GridLayout(1, 2, 16, 0));
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        grid.add(crearCampo("Tipo de habitación", habitacion.getTipo()));
        grid.add(crearCampo("Precio por noche",
                String.format("$ %,.0f", habitacion.getPrecio())));

        card.add(grid);
        return card;
    }

    // ── CARD RESERVA ─────────────────────────────────────────────────────────

    private JPanel crearCardReserva() {
        JPanel card = crearCard();

        JLabel titulo = crearTituloSeccion("RESERVA ACTIVA");
        card.add(titulo);
        card.add(Box.createVerticalStrut(14));

        // Fila 1: Huésped y Documento
        JPanel fila1 = new JPanel(new GridLayout(1, 2, 16, 0));
        fila1.setOpaque(false);
        fila1.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        fila1.add(crearCampo("Huésped titular", reservaActiva.getClienteNombre()));
        fila1.add(crearCampo("Documento", reservaActiva.getClienteDoc()));
        card.add(fila1);
        card.add(Box.createVerticalStrut(16));

        // Fila 2: Fechas
        JPanel fila2 = new JPanel(new GridLayout(1, 2, 16, 0));
        fila2.setOpaque(false);
        fila2.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        fila2.add(crearCampo("Fecha de entrada", reservaActiva.getFechaEntrada()));
        fila2.add(crearCampo("Fecha de salida", reservaActiva.getFechaSalida()));
        card.add(fila2);
        card.add(Box.createVerticalStrut(16));

        // Fila 3: ID y Estado
        JPanel fila3 = new JPanel(new GridLayout(1, 2, 16, 0));
        fila3.setOpaque(false);
        fila3.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        fila3.add(crearCampo("ID Reserva", "#" + reservaActiva.getId()));
        fila3.add(crearCampoDestacado("Estado", reservaActiva.getEstado(), COLOR_AZUL));
        card.add(fila3);
        card.add(Box.createVerticalStrut(16));

        // Fila 4: Anticipo y Saldo pendiente
        JPanel fila4 = new JPanel(new GridLayout(1, 2, 16, 0));
        fila4.setOpaque(false);
        fila4.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila4.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        fila4.add(crearCampo("Anticipo recibido",
                String.format("$ %,.0f", reservaActiva.getAnticipo())));
        String saldoStr = calcularSaldo();
        fila4.add(crearCampoDestacado("Saldo pendiente", saldoStr,
                saldoStr.startsWith("$") ? COLOR_NARANJA : COLOR_AZUL));
        card.add(fila4);

        return card;
    }

    // ── ESTADO DISPONIBLE ────────────────────────────────────────────────────

    private JPanel crearEstadoDisponible() {
        JPanel card = crearCard();
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel circulo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x00AA5A, false));
                g2.fillOval(0, 0, 48, 48);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 22));
                FontMetrics fm = g2.getFontMetrics();
                String tick = "OK";
                int x = (48 - fm.stringWidth(tick)) / 2;
                int y = (48 + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(tick, x, y);
                g2.dispose();
            }
        };
        circulo.setPreferredSize(new Dimension(48, 48));
        circulo.setMaximumSize(new Dimension(48, 48));
        circulo.setMinimumSize(new Dimension(48, 48));
        circulo.setOpaque(false);
        circulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msg = new JLabel("Habitacion disponible");
        msg.setFont(new Font("Segoe UI", Font.BOLD, 15));
        msg.setForeground(COLOR_TEXTO);
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("No hay reservas activas asignadas.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(COLOR_LABEL);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalStrut(14));
        card.add(circulo);
        card.add(Box.createVerticalStrut(12));
        card.add(msg);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(14));
        return card;
    }

    // ── FOOTER ───────────────────────────────────────────────────────────────

    private JPanel crearFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.setBackground(COLOR_FONDO);
        footer.setBorder(new EmptyBorder(8, 22, 20, 22));

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setPreferredSize(new Dimension(110, 36));
        btnCerrar.setBackground(COLOR_AZUL);
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCerrar.setFocusPainted(false);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(e -> dispose());

        footer.add(btnCerrar);
        return footer;
    }

    // ── HELPERS ──────────────────────────────────────────────────────────────

    private JPanel crearCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                new EmptyBorder(16, 18, 16, 18)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        return card;
    }

    private JLabel crearTituloSeccion(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(COLOR_LABEL);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JPanel crearCampo(String etiqueta, String valor) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(COLOR_LABEL);

        JLabel val = new JLabel(valor != null && !valor.isEmpty() ? valor : "—");
        val.setFont(new Font("Segoe UI", Font.BOLD, 14));
        val.setForeground(COLOR_TEXTO);

        p.add(lbl);
        p.add(Box.createVerticalStrut(2));
        p.add(val);
        return p;
    }

    private JPanel crearCampoDestacado(String etiqueta, String valor, Color colorValor) {
        JPanel p = crearCampo(etiqueta, valor);
        // Reemplaza el color del último componente añadido (el valor)
        Component[] comps = p.getComponents();
        for (Component c : comps) {
            if (c instanceof JLabel) {
                JLabel lbl = (JLabel) c;
                if (lbl.getFont().isBold()) {
                    lbl.setForeground(colorValor);
                }
            }
        }
        return p;
    }
}
