package com.santaana.view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import com.santaana.util.ThemeManager;

public class ReservaPanel extends JPanel {

    private String userRole;
    private YearMonth mesActual = YearMonth.now();
    private JPanel calendarGrid;
    private JLabel lblMesAnio;

    // ── Modelo simple de reserva ──────────────────────────────────────────────
    private static class Reserva {
        final String id, huesped, habitacion, estado;
        final LocalDate inicio, fin;
        final Color color;

        Reserva(String id, String huesped, String habitacion,
                LocalDate inicio, LocalDate fin, String estado, Color color) {
            this.id = id; this.huesped = huesped; this.habitacion = habitacion;
            this.inicio = inicio; this.fin = fin;
            this.estado = estado; this.color = color;
        }

        boolean activa(LocalDate dia) {
            return !dia.isBefore(inicio) && !dia.isAfter(fin);
        }
    }

    // ── Datos de ejemplo ─────────────────────────────────────────────────────
    private final List<Reserva> reservas = new ArrayList<>();

    private void cargarDatosEjemplo() {
        int anio = mesActual.getYear();
        int mes  = mesActual.getMonthValue();
        reservas.clear();
        reservas.add(new Reserva("001","Juan Pérez",      "Hab 101", LocalDate.of(anio,mes, 1), LocalDate.of(anio,mes, 3), "Confirmada", new Color(0x27AE60)));
        reservas.add(new Reserva("002","María López",     "Hab 102", LocalDate.of(anio,mes, 2), LocalDate.of(anio,mes, 5), "Pendiente",  new Color(0xE67E22)));
        reservas.add(new Reserva("003","Carlos Rodríguez","Hab 103", LocalDate.of(anio,mes, 3), LocalDate.of(anio,mes, 6), "Check-in",   new Color(0x3A7BD5)));
        reservas.add(new Reserva("004","Ana Gómez",       "Hab 104", LocalDate.of(anio,mes, 7), LocalDate.of(anio,mes, 9), "Check-out",  new Color(0x8E44AD)));
        reservas.add(new Reserva("005","Luis Martínez",   "Hab 105", LocalDate.of(anio,mes, 5), LocalDate.of(anio,mes, 8), "Confirmada", new Color(0x27AE60)));
        reservas.add(new Reserva("006","Sandra Torres",   "Hab 101", LocalDate.of(anio,mes,10), LocalDate.of(anio,mes,13), "Pendiente",  new Color(0xE67E22)));
        reservas.add(new Reserva("007","Andrés Vargas",   "Hab 102", LocalDate.of(anio,mes, 9), LocalDate.of(anio,mes,11), "Cancelada",  new Color(0xE74C3C)));
        reservas.add(new Reserva("008","Patricia Silva",  "Hab 103", LocalDate.of(anio,mes,14), LocalDate.of(anio,mes,17), "Confirmada", new Color(0x27AE60)));
        reservas.add(new Reserva("009","Jorge Mendoza",   "Hab 104", LocalDate.of(anio,mes,18), LocalDate.of(anio,mes,22), "Pendiente",  new Color(0xE67E22)));
        reservas.add(new Reserva("010","Claudia Herrera", "Hab 102", LocalDate.of(anio,mes,20), LocalDate.of(anio,mes,24), "Confirmada", new Color(0x27AE60)));
        reservas.add(new Reserva("011","Roberto Castro",  "Hab 105", LocalDate.of(anio,mes,25), LocalDate.of(anio,mes,28), "Confirmada", new Color(0x27AE60)));
        reservas.add(new Reserva("012","Diana Morales",   "Hab 101", LocalDate.of(anio,mes,22), LocalDate.of(anio,mes,26), "Check-in",   new Color(0x3A7BD5)));
    }

    // ─────────────────────────────────────────────────────────────────────────
    public ReservaPanel(String role) {
        this.userRole = role;
        setLayout(new BorderLayout());
        setBackground(ThemeManager.getBackground());
        refreshUI();
    }

    public void refreshUI() {
        removeAll();
        cargarDatosEjemplo();
        add(crearNavbar(),    BorderLayout.NORTH);
        add(crearCuerpo(),    BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // ── Navbar ────────────────────────────────────────────────────────────────
    private JPanel crearNavbar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(ThemeManager.getPanelBackground());
        bar.setPreferredSize(new Dimension(0, 52));
        bar.setBorder(new MatteBorder(0, 0, 1, 0, ThemeManager.getBorder()));

        JLabel title = new JLabel("  GESTIONAR RESERVAS");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(ThemeManager.getTextPrimary());
        bar.add(title, BorderLayout.WEST);

        // Navegación mes
        JPanel navMes = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
        navMes.setOpaque(false);

        JButton btnPrev = btnNavMes("‹");
        lblMesAnio = new JLabel();
        lblMesAnio.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMesAnio.setForeground(ThemeManager.getTextPrimary());
        lblMesAnio.setPreferredSize(new Dimension(180, 28));
        lblMesAnio.setHorizontalAlignment(SwingConstants.CENTER);
        actualizarLblMes();
        JButton btnNext = btnNavMes("›");
        JButton btnHoy  = btnHoy();

        btnPrev.addActionListener(e -> { mesActual = mesActual.minusMonths(1); refreshUI(); });
        btnNext.addActionListener(e -> { mesActual = mesActual.plusMonths(1);  refreshUI(); });
        btnHoy.addActionListener(e  -> { mesActual = YearMonth.now();          refreshUI(); });

        navMes.add(btnPrev);
        navMes.add(lblMesAnio);
        navMes.add(btnNext);
        navMes.add(Box.createHorizontalStrut(10));
        navMes.add(btnHoy);
        bar.add(navMes, BorderLayout.CENTER);

        // Leyenda de estados
        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        leyenda.setOpaque(false);
        leyenda.add(puntito(new Color(0x27AE60), "Confirmada"));
        leyenda.add(puntito(new Color(0xE67E22), "Pendiente"));
        leyenda.add(puntito(new Color(0x3A7BD5), "Check-in"));
        leyenda.add(puntito(new Color(0x8E44AD), "Check-out"));
        leyenda.add(puntito(new Color(0xE74C3C), "Cancelada"));
        bar.add(leyenda, BorderLayout.EAST);

        return bar;
    }

    private JButton btnNavMes(String txt) {
        JButton b = new JButton(txt);
        b.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b.setForeground(ThemeManager.getTextPrimary());
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(32, 28));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setForeground(ThemeManager.getPrimary()); }
            public void mouseExited(MouseEvent e)  { b.setForeground(ThemeManager.getTextPrimary()); }
        });
        return b;
    }

    private JButton btnHoy() {
        JButton b = new JButton("Hoy") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean isDark = ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK;
                g2.setColor(isDark ? new Color(0x334155) : new Color(0xE8F1FD));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 11));
        b.setForeground(ThemeManager.getPrimary());
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(52, 26));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JPanel puntito(Color color, String label) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setOpaque(false);
        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(0, 2, 10, 10);
                g2.dispose();
            }
        };
        dot.setOpaque(false);
        dot.setPreferredSize(new Dimension(10, 14));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lbl.setForeground(ThemeManager.getTextSecondary());
        p.add(dot); p.add(lbl);
        return p;
    }

    private void actualizarLblMes() {
        if (lblMesAnio == null) return;
        String mes = mesActual.getMonth()
            .getDisplayName(TextStyle.FULL, new Locale("es","CO"));
        mes = mes.substring(0,1).toUpperCase() + mes.substring(1);
        lblMesAnio.setText(mes + "  " + mesActual.getYear());
    }

    // ── Cuerpo con encabezado de días + grid ─────────────────────────────────
    private JPanel crearCuerpo() {
        JPanel cuerpo = new JPanel(new BorderLayout(0, 0));
        cuerpo.setBackground(ThemeManager.getBackground());
        cuerpo.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        cuerpo.add(crearEncabezadoDias(), BorderLayout.NORTH);

        calendarGrid = crearGridCalendario();
        JScrollPane scroll = new JScrollPane(calendarGrid);
        scroll.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder(), 1));
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        cuerpo.add(scroll, BorderLayout.CENTER);

        return cuerpo;
    }

    private JPanel crearEncabezadoDias() {
        String[] dias = {"Domingo","Lunes","Martes","Miércoles","Jueves","Viernes","Sábado"};
        JPanel header = new JPanel(new GridLayout(1, 7, 1, 0));
        header.setBackground(ThemeManager.getBorder());
        header.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, ThemeManager.getBorder()));

        boolean isDark = ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK;
        Color headerBg = isDark ? new Color(0x1E293B) : new Color(0x1F2937);

        for (String dia : dias) {
            JLabel lbl = new JLabel(dia, SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setForeground(Color.WHITE);
            lbl.setOpaque(true);
            lbl.setBackground(headerBg);
            lbl.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
            header.add(lbl);
        }
        return header;
    }

    private JPanel crearGridCalendario() {
        JPanel grid = new JPanel(new GridLayout(0, 7, 1, 1));
        grid.setBackground(ThemeManager.getBorder()); // color de separador
        grid.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        LocalDate primerDia = mesActual.atDay(1);
        int diasEnMes = mesActual.lengthOfMonth();
        // 0=domingo, 1=lunes, …, 6=sábado
        int offsetInicio = primerDia.getDayOfWeek().getValue() % 7; // Sunday=0

        LocalDate hoy = LocalDate.now();

        // Celdas vacías al inicio
        for (int i = 0; i < offsetInicio; i++) {
            grid.add(celdaVacia());
        }

        // Celdas de días
        for (int d = 1; d <= diasEnMes; d++) {
            LocalDate fecha = mesActual.atDay(d);
            List<Reserva> reservasDelDia = new ArrayList<>();
            for (Reserva r : reservas) {
                if (r.activa(fecha)) reservasDelDia.add(r);
            }
            grid.add(crearCeldaDia(fecha, reservasDelDia, fecha.equals(hoy)));
        }

        // Celdas vacías al final para completar la cuadrícula
        int total = offsetInicio + diasEnMes;
        int resto = total % 7;
        if (resto != 0) {
            for (int i = 0; i < 7 - resto; i++) {
                grid.add(celdaVacia());
            }
        }

        return grid;
    }

    private JPanel celdaVacia() {
        JPanel p = new JPanel();
        boolean isDark = ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK;
        p.setBackground(isDark ? new Color(0x111827) : new Color(0xF1F5F9));
        p.setPreferredSize(new Dimension(0, 110));
        return p;
    }

    private JPanel crearCeldaDia(LocalDate fecha, List<Reserva> reservasDia, boolean esHoy) {
        boolean isDark = ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK;

        JPanel celda = new JPanel();
        celda.setLayout(new BoxLayout(celda, BoxLayout.Y_AXIS));
        celda.setPreferredSize(new Dimension(0, 110));

        Color bgNormal  = ThemeManager.getPanelBackground();
        Color bgHoy     = isDark ? new Color(0x1E3A5F) : new Color(0xEBF4FF);
        Color bgWeekend = isDark ? new Color(0x1A2535) : new Color(0xF8F9FB);

        boolean esFinDeSemana = fecha.getDayOfWeek() == DayOfWeek.SATURDAY
                             || fecha.getDayOfWeek() == DayOfWeek.SUNDAY;

        celda.setBackground(esHoy ? bgHoy : (esFinDeSemana ? bgWeekend : bgNormal));
        celda.setBorder(BorderFactory.createEmptyBorder(6, 6, 4, 6));

        // Número del día
        JPanel filaNro = new JPanel(new BorderLayout());
        filaNro.setOpaque(false);
        filaNro.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        JLabel nroDia = new JLabel(String.valueOf(fecha.getDayOfMonth()));
        nroDia.setFont(new Font("Segoe UI", Font.BOLD, esHoy ? 13 : 12));

        if (esHoy) {
            // Círculo azul para hoy
            nroDia = new JLabel(String.valueOf(fecha.getDayOfMonth())) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(ThemeManager.getPrimary());
                    g2.fillOval(0, 0, getWidth(), getHeight());
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            nroDia.setFont(new Font("Segoe UI", Font.BOLD, 12));
            nroDia.setForeground(Color.WHITE);
            nroDia.setHorizontalAlignment(SwingConstants.CENTER);
            nroDia.setPreferredSize(new Dimension(24, 24));
            nroDia.setOpaque(false);
        } else {
            nroDia.setForeground(esFinDeSemana
                ? (isDark ? new Color(0x94A3B8) : new Color(0xB0B8C4))
                : ThemeManager.getTextPrimary());
        }

        filaNro.add(nroDia, BorderLayout.WEST);

        // Indicador de cantidad si hay más de 2 reservas
        if (reservasDia.size() > 2) {
            JLabel masLbl = new JLabel("+" + (reservasDia.size() - 2) + " más");
            masLbl.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            masLbl.setForeground(ThemeManager.getTextSecondary());
            filaNro.add(masLbl, BorderLayout.EAST);
        }

        celda.add(filaNro);
        celda.add(Box.createVerticalStrut(4));

        // Bloques de reservas (máximo 2 visibles)
        int mostrar = Math.min(reservasDia.size(), 2);
        for (int i = 0; i < mostrar; i++) {
            Reserva r = reservasDia.get(i);
            celda.add(crearBloqueReserva(r));
            celda.add(Box.createVerticalStrut(2));
        }

        celda.add(Box.createVerticalGlue());

        // Tooltip con todas las reservas del día
        if (!reservasDia.isEmpty()) {
            StringBuilder sb = new StringBuilder("<html><b>Reservas del día:</b><br>");
            for (Reserva r : reservasDia) {
                sb.append("• ").append(r.huesped).append(" — ").append(r.habitacion)
                  .append(" (").append(r.estado).append(")<br>");
            }
            sb.append("</html>");
            celda.setToolTipText(sb.toString());
        }

        return celda;
    }

    private JPanel crearBloqueReserva(Reserva r) {
        Color bgBloque = new Color(r.color.getRed(), r.color.getGreen(), r.color.getBlue(), 35);

        JPanel bloque = new JPanel(new BorderLayout(4, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgBloque);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                g2.dispose();
            }
        };
        bloque.setOpaque(false);
        bloque.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        bloque.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

        // Barra de color izquierda
        JPanel barra = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(r.color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 2, 2);
                g2.dispose();
            }
        };
        barra.setOpaque(false);
        barra.setPreferredSize(new Dimension(3, 0));

        JLabel nombre = new JLabel(r.huesped.split(" ")[0] + " — " + r.habitacion);
        nombre.setFont(new Font("Segoe UI", Font.BOLD, 10));
        nombre.setForeground(r.color.darker());

        bloque.add(barra, BorderLayout.WEST);
        bloque.add(nombre, BorderLayout.CENTER);

        // Click para ver detalle
        bloque.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bloque.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                mostrarDetalleReserva(r);
            }
            @Override public void mouseEntered(MouseEvent e) {
                bloque.setOpaque(true);
                bloque.setBackground(new Color(r.color.getRed(), r.color.getGreen(), r.color.getBlue(), 60));
                bloque.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                bloque.setOpaque(false);
                bloque.repaint();
            }
        });

        return bloque;
    }

    private void mostrarDetalleReserva(Reserva r) {
        String msg = "<html><b>Reserva #" + r.id + "</b><br><br>"
            + "<b>Huésped:</b> " + r.huesped + "<br>"
            + "<b>Habitación:</b> " + r.habitacion + "<br>"
            + "<b>Entrada:</b> " + r.inicio + "<br>"
            + "<b>Salida:</b> " + r.fin + "<br>"
            + "<b>Estado:</b> <font color='" + String.format("#%06X", r.color.getRGB() & 0xFFFFFF) + "'>" + r.estado + "</font>"
            + "</html>";
        JOptionPane.showMessageDialog(this, msg, "Detalle de reserva", JOptionPane.INFORMATION_MESSAGE);
    }
}
