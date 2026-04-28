package com.santaana.view;

import com.santaana.dao.ReporteDAO;
import com.santaana.util.ThemeManager;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.util.*;

public class ReportePanel extends JPanel implements ThemeManager.ThemeListener {

    private static final NumberFormat FMT_MONEDA;
    static {
        FMT_MONEDA = NumberFormat.getInstance(new java.util.Locale("es", "CO"));
        FMT_MONEDA.setMaximumFractionDigits(0);
    }

    private final ReporteDAO dao = new ReporteDAO();

    public ReportePanel() {
        ThemeManager.addListener(this);
        setLayout(new BorderLayout());
        refreshUI();
    }

    public void refreshUI() {
        removeAll();
        setBackground(ThemeManager.getBackground());
        add(crearNavbar(), BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(crearContenido());
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    @Override
    public void onThemeChanged() { refreshUI(); }

    // ── Navbar ────────────────────────────────────────────────────────────────

    private JPanel crearNavbar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(ThemeManager.getPanelBackground());
        bar.setPreferredSize(new Dimension(0, 60));
        bar.setBorder(new MatteBorder(0, 0, 1, 0, ThemeManager.getBorder()));

        JLabel title = new JLabel("  REPORTES Y CONTABILIDAD");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(ThemeManager.getTextPrimary());
        bar.add(title, BorderLayout.WEST);

        JButton btnRefresh = new JButton("↺ Actualizar") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getPrimary());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefresh.setContentAreaFilled(false);
        btnRefresh.setBorderPainted(false);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setPreferredSize(new Dimension(130, 34));
        btnRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> refreshUI());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 13));
        right.setOpaque(false);
        right.add(btnRefresh);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ── Contenido principal ────────────────────────────────────────────────────

    private JPanel crearContenido() {
        JPanel cont = new JPanel();
        cont.setLayout(new BoxLayout(cont, BoxLayout.Y_AXIS));
        cont.setOpaque(false);
        cont.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        double totalIngresos  = dao.getTotalIngresos();
        double totalAnticipos = dao.getTotalAnticipos();
        int    totalReservas  = dao.getTotalReservas();
        Map<String, Integer> porEstado   = dao.getReservasPorEstado();
        Map<String, Double>  ingresosMes = dao.getIngresosPorMes();
        Map<String, Integer> reservasMes = dao.getReservasPorMes();
        Map<String, Integer> topHabs     = dao.getTopHabitaciones();

        int completadas = porEstado.getOrDefault("Completada", 0);
        int canceladas  = porEstado.getOrDefault("Cancelada",  0);
        int activas     = porEstado.getOrDefault("Activa",     0);

        // ── KPIs ──────────────────────────────────────────────────────────────
        JPanel kpiRow = new JPanel(new GridLayout(1, 4, 14, 0));
        kpiRow.setOpaque(false);
        kpiRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        kpiRow.setAlignmentX(LEFT_ALIGNMENT);

        kpiRow.add(kpiCard("Ingresos Confirmados", "$ " + FMT_MONEDA.format(totalIngresos),
                new Color(0x22C55E), "De reservas completadas"));
        kpiRow.add(kpiCard("Anticipos Totales", "$ " + FMT_MONEDA.format(totalAnticipos),
                new Color(0x3A7BD5), "Suma de todos los anticipos"));
        kpiRow.add(kpiCard("Total Reservas", String.valueOf(totalReservas),
                new Color(0xF59E0B), activas + " activas · " + completadas + " completadas"));
        kpiRow.add(kpiCard("Cancelaciones", String.valueOf(canceladas),
                new Color(0xEF4444), totalReservas > 0
                        ? String.format("%.1f%% tasa de cancelación", 100.0 * canceladas / totalReservas)
                        : "Sin datos suficientes"));

        cont.add(kpiRow);
        cont.add(Box.createVerticalStrut(20));

        // ── Grid 2x2 de gráficas ───────────────────────────────────────────────
        JPanel grid = new JPanel(new GridLayout(2, 2, 14, 14));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 700));
        grid.setAlignmentX(LEFT_ALIGNMENT);

        grid.add(barChartVertical("Ingresos por mes (últimos 6 meses)", ingresosMes, true));
        grid.add(pieChart("Reservas por estado", porEstado));
        grid.add(barChartVertical("Reservas por mes (últimos 6 meses)", toDoubleMap(reservasMes), false));
        grid.add(barChartHorizontal("Top habitaciones más reservadas", topHabs));

        cont.add(grid);
        cont.add(Box.createVerticalStrut(24));

        return cont;
    }

    // ── KPI Card ──────────────────────────────────────────────────────────────

    private JPanel kpiCard(String titulo, String valor, Color accent, String subtitulo) {
        JPanel card = new JPanel(new BorderLayout(0, 4)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getPanelBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(accent);
                g2.fillRect(0, 0, 4, getHeight());
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getBorder(), 1, true),
                BorderFactory.createEmptyBorder(14, 18, 14, 14)));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTitulo.setForeground(ThemeManager.getTextSecondary());

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblValor.setForeground(accent);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSub.setForeground(ThemeManager.getTextSecondary());

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(lblTitulo);
        text.add(Box.createVerticalStrut(4));
        text.add(lblValor);
        text.add(Box.createVerticalStrut(2));
        text.add(lblSub);

        card.add(text, BorderLayout.CENTER);
        return card;
    }

    // ── Bar Chart vertical ────────────────────────────────────────────────────

    private JPanel barChartVertical(String titulo, Map<String, Double> data, boolean esDinero) {
        JPanel wrap = panelCarta(titulo);
        wrap.add(new JPanel() {
            { setOpaque(false); }
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (data.isEmpty()) { drawNoData(g, getWidth(), getHeight()); return; }
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int padL = 60, padR = 14, padT = 14, padB = 40;
                int w = getWidth()  - padL - padR;
                int h = getHeight() - padT - padB;

                double maxVal = data.values().stream().mapToDouble(Double::doubleValue).max().orElse(1);
                if (maxVal == 0) maxVal = 1;

                String[] keys = data.keySet().toArray(new String[0]);
                int n    = keys.length;
                int gap  = 10;
                int barW = Math.max(10, (w - gap * (n + 1)) / n);

                // Líneas guía Y
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                for (int i = 0; i <= 4; i++) {
                    int y = padT + h - (int)(h * i / 4.0);
                    g2.setColor(ThemeManager.getBorder());
                    g2.drawLine(padL, y, padL + w, y);
                    double val = maxVal * i / 4.0;
                    String lbl = esDinero ? "$" + abreviar(val) : String.valueOf((int) val);
                    g2.setColor(ThemeManager.getTextSecondary());
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(lbl, padL - fm.stringWidth(lbl) - 4, y + 4);
                }

                // Barras
                for (int i = 0; i < n; i++) {
                    double val  = data.get(keys[i]);
                    int    barH = (int)(h * val / maxVal);
                    int    x    = padL + gap + i * (barW + gap);
                    int    y    = padT + h - barH;

                    // Barra con gradiente sutil
                    GradientPaint gp = new GradientPaint(
                            x, y, ThemeManager.getPrimary(),
                            x, padT + h, alphaColor(ThemeManager.getPrimary(), 160));
                    g2.setPaint(gp);
                    g2.fillRoundRect(x, y, barW, barH, 6, 6);

                    // Valor encima de la barra
                    g2.setColor(ThemeManager.getTextSecondary());
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                    String vLbl = esDinero ? "$" + abreviar(val) : String.valueOf((int) val);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(vLbl, x + (barW - fm.stringWidth(vLbl)) / 2, y - 3);

                    // Etiqueta mes en X
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                    fm = g2.getFontMetrics();
                    String mesLabel = keys[i].length() >= 7 ? keys[i].substring(5) : keys[i];
                    g2.drawString(mesLabel, x + (barW - fm.stringWidth(mesLabel)) / 2, padT + h + 16);
                }
                g2.dispose();
            }
        }, BorderLayout.CENTER);
        return wrap;
    }

    // ── Bar Chart horizontal ──────────────────────────────────────────────────

    private JPanel barChartHorizontal(String titulo, Map<String, Integer> data) {
        JPanel wrap = panelCarta(titulo);
        wrap.add(new JPanel() {
            { setOpaque(false); }
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (data.isEmpty()) { drawNoData(g, getWidth(), getHeight()); return; }
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int padL = 80, padR = 40, padT = 14, padB = 14;
                int w = getWidth()  - padL - padR;
                int h = getHeight() - padT - padB;

                String[] keys = data.keySet().toArray(new String[0]);
                int n      = keys.length;
                int maxVal = data.values().stream().mapToInt(Integer::intValue).max().orElse(1);
                if (maxVal == 0) maxVal = 1;

                int gap  = 12;
                int barH = Math.max(14, (h - gap * (n + 1)) / n);

                Color[] palette = {
                    ThemeManager.getPrimary(),
                    new Color(0x22C55E),
                    new Color(0xF59E0B),
                    new Color(0xEF4444),
                    new Color(0xA855F7)
                };

                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                FontMetrics fm = g2.getFontMetrics();

                for (int i = 0; i < n; i++) {
                    int val  = data.get(keys[i]);
                    int barW = Math.max(4, (int)(w * val / (double) maxVal));
                    int y    = padT + gap + i * (barH + gap);

                    GradientPaint gp = new GradientPaint(
                            padL, y, palette[i % palette.length],
                            padL + w, y, alphaColor(palette[i % palette.length], 160));
                    g2.setPaint(gp);
                    g2.fillRoundRect(padL, y, barW, barH, 6, 6);

                    // Etiqueta izquierda
                    g2.setColor(ThemeManager.getTextSecondary());
                    g2.drawString(keys[i], padL - fm.stringWidth(keys[i]) - 6, y + barH / 2 + fm.getAscent() / 2 - 1);

                    // Valor derecha
                    g2.setColor(ThemeManager.getTextPrimary());
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    g2.drawString(String.valueOf(val), padL + barW + 6, y + barH / 2 + g2.getFontMetrics().getAscent() / 2 - 1);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                }
                g2.dispose();
            }
        }, BorderLayout.CENTER);
        return wrap;
    }

    // ── Pie / Donut Chart ─────────────────────────────────────────────────────

    private JPanel pieChart(String titulo, Map<String, Integer> data) {
        JPanel wrap = panelCarta(titulo);
        wrap.add(new JPanel() {
            { setOpaque(false); }
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int total = data.values().stream().mapToInt(Integer::intValue).sum();
                if (total == 0) { drawNoData(g, getWidth(), getHeight()); return; }

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Donut centrado a la izquierda, leyenda a la derecha
                int available = getHeight() - 20;
                int size      = Math.min(available, (int)(getWidth() * 0.45));
                size = Math.max(size, 80);
                int cx = size / 2 + 20;
                int cy = getHeight() / 2;

                Color[] colors = {new Color(0x3A7BD5), new Color(0x22C55E), new Color(0xEF4444)};
                String[] keys  = data.keySet().toArray(new String[0]);

                int startAngle = 90; // empieza desde arriba
                for (int i = 0; i < keys.length; i++) {
                    int val = data.get(keys[i]);
                    int arc = (int) Math.round(360.0 * val / total);
                    if (i == keys.length - 1) arc = (90 + 360) - startAngle;
                    g2.setColor(colors[i % colors.length]);
                    g2.fillArc(cx - size / 2, cy - size / 2, size, size, startAngle, arc);
                    startAngle += arc;
                }

                // Hueco central
                int hole = (int)(size * 0.42);
                g2.setColor(ThemeManager.getPanelBackground());
                g2.fillOval(cx - hole / 2, cy - hole / 2, hole, hole);

                // Total en el centro
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                g2.setColor(ThemeManager.getTextPrimary());
                String totalStr = String.valueOf(total);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(totalStr, cx - fm.stringWidth(totalStr) / 2, cy + fm.getAscent() / 2 - 2);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                g2.setColor(ThemeManager.getTextSecondary());
                String sub = "total";
                fm = g2.getFontMetrics();
                g2.drawString(sub, cx - fm.stringWidth(sub) / 2, cy + 14);

                // Leyenda
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                fm = g2.getFontMetrics();
                int lx = cx + size / 2 + 20;
                int ly = cy - (keys.length * 28) / 2 + 4;
                for (int i = 0; i < keys.length; i++) {
                    int val = data.get(keys[i]);
                    double pct = 100.0 * val / total;
                    g2.setColor(colors[i % colors.length]);
                    g2.fillRoundRect(lx, ly + i * 28, 12, 12, 4, 4);
                    g2.setColor(ThemeManager.getTextPrimary());
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    g2.drawString(keys[i], lx + 18, ly + i * 28 + 10);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                    g2.setColor(ThemeManager.getTextSecondary());
                    g2.drawString(String.format("%d  (%.0f%%)", val, pct), lx + 18, ly + i * 28 + 22);
                }
                g2.dispose();
            }
        }, BorderLayout.CENTER);
        return wrap;
    }

    // ── Utilidades ────────────────────────────────────────────────────────────

    private JPanel panelCarta(String titulo) {
        JPanel card = new JPanel(new BorderLayout(0, 10)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getPanelBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getBorder(), 1, true),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(ThemeManager.getTextPrimary());
        card.add(lbl, BorderLayout.NORTH);
        return card;
    }

    private void drawNoData(Graphics g, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(ThemeManager.getTextSecondary());
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        String msg = "Sin datos disponibles";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(msg, (w - fm.stringWidth(msg)) / 2, h / 2 + 4);
        g2.dispose();
    }

    private Color alphaColor(Color base, int alpha) {
        return new Color(base.getRed(), base.getGreen(), base.getBlue(), alpha);
    }

    private String abreviar(double val) {
        if (val >= 1_000_000) return String.format("%.1fM", val / 1_000_000);
        if (val >= 1_000)     return String.format("%.0fk", val / 1_000);
        return String.valueOf((int) val);
    }

    private Map<String, Double> toDoubleMap(Map<String, Integer> src) {
        Map<String, Double> out = new LinkedHashMap<>();
        src.forEach((k, v) -> out.put(k, v.doubleValue()));
        return out;
    }
}
