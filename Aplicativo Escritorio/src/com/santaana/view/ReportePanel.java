package com.santaana.view;

import com.santaana.dao.CierreMesDAO;
import com.santaana.dao.ReporteDAO;
import com.santaana.db.DatabaseException;
import com.santaana.model.CierreMes;
import com.santaana.util.ErrorUtil;
import com.santaana.util.ExportadorPDF;
import com.santaana.util.ThemeManager;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class ReportePanel extends JPanel implements ThemeManager.ThemeListener {

    private static final NumberFormat FMT_MONEDA;
    static {
        FMT_MONEDA = NumberFormat.getInstance(new java.util.Locale("es", "CO"));
        FMT_MONEDA.setMaximumFractionDigits(0);
    }

    private static final DateTimeFormatter FMT_MES = DateTimeFormatter.ofPattern("MMMM yyyy",
            new java.util.Locale("es", "CO"));

    private final ReporteDAO    dao         = new ReporteDAO();
    private final CierreMesDAO  cierreDAO   = new CierreMesDAO();
    private String userRole;
    private int    idUsuario;

    public ReportePanel(String userRole, int idUsuario) {
        this.userRole  = userRole;
        this.idUsuario = idUsuario;
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

        JButton btnExportar = new JButton("⬇ Exportar PDF") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xC9, 0xA8, 0x4C));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnExportar.setForeground(Color.WHITE);
        btnExportar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnExportar.setContentAreaFilled(false);
        btnExportar.setBorderPainted(false);
        btnExportar.setFocusPainted(false);
        btnExportar.setPreferredSize(new Dimension(155, 34));
        btnExportar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnExportar.addActionListener(e -> {
            String mes = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            java.io.File pdf = ExportadorPDF.exportar(mes, idUsuario, userRole, null);
            if (pdf != null) {
                JOptionPane.showMessageDialog(
                    ReportePanel.this,
                    "PDF generado correctamente:\n" + pdf.getAbsolutePath(),
                    "Exportación exitosa",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

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
        right.add(btnExportar);
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

        double totalIngresos; double totalAnticipos; int totalReservas;
        Map<String, Integer> porEstado; Map<String, Double> ingresosMes;
        Map<String, Integer> reservasMes; Map<String, Integer> topHabs;
        try {
            totalIngresos  = dao.getTotalIngresos();
            totalAnticipos = dao.getTotalAnticipos();
            totalReservas  = dao.getTotalReservas();
            porEstado   = dao.getReservasPorEstado();
            ingresosMes = dao.getIngresosPorMes();
            reservasMes = dao.getReservasPorMes();
            topHabs     = dao.getTopHabitaciones();
        } catch (DatabaseException e) {
            ErrorUtil.mostrarError(this, "cargar reportes", e);
            totalIngresos = 0; totalAnticipos = 0; totalReservas = 0;
            porEstado = new java.util.LinkedHashMap<>(); ingresosMes = new java.util.LinkedHashMap<>();
            reservasMes = new java.util.LinkedHashMap<>(); topHabs = new java.util.LinkedHashMap<>();
        }

        int completadas = porEstado.getOrDefault("Completada", 0);
        int canceladas  = porEstado.getOrDefault("Cancelada",  0);
        int activas     = porEstado.getOrDefault("Activa",     0);

        // ── Banner de cierre del mes ──────────────────────────────────────────
        JPanel banner = crearBannerCierre();
        if (banner != null) {
            cont.add(banner);
            cont.add(Box.createVerticalStrut(16));
        }

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

    // ── Banner compacto de cierre del mes actual ───────────────────────────────

    private JPanel crearBannerCierre() {
        // El cierre aplica al mes anterior si el actual aún no ha terminado
        LocalDate hoy        = LocalDate.now();
        LocalDate primeroDiaMes = hoy.withDayOfMonth(1);
        // El mes a reportar es el anterior si estamos en curso, o el actual si ya terminó
        // Como el mes actual siempre está "en curso", siempre mostramos el mes anterior
        LocalDate mesCierre  = primeroDiaMes.minusMonths(1);

        String mesActual  = mesCierre.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String mesLabel   = mesCierre.format(FMT_MES);
        mesLabel = mesLabel.substring(0, 1).toUpperCase() + mesLabel.substring(1);
        CierreMes cierre;
        try { cierre = cierreDAO.getCierre(mesActual); } catch (DatabaseException e) { cierre = null; }
        boolean   cerrado = cierre != null;

        Color bg      = cerrado ? new Color(0xF0FDF4) : new Color(0xFFFBEB);
        Color bgDark  = cerrado ? new Color(0x14532D)  : new Color(0x451A03);
        Color accent  = cerrado ? new Color(0x22C55E)  : new Color(0xF59E0B);
        boolean dark  = ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK;

        JPanel banner = new JPanel(new BorderLayout(12, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(dark ? bgDark : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
                g2.dispose();
            }
        };
        banner.setOpaque(false);
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        banner.setAlignmentX(LEFT_ALIGNMENT);
        banner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(alphaColor(accent, 80), 1, true),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));

        // Texto del banner
        String texto;
        if (cerrado) {
            String fecha = cierre.getFechaCierre() != null && cierre.getFechaCierre().length() >= 10
                    ? cierre.getFechaCierre().substring(0, 10) : cierre.getFechaCierre();
            texto = "Mes de " + mesLabel + " cerrado contablemente"
                    + " — por " + (cierre.getNombreUsuario() != null ? cierre.getNombreUsuario() : "Admin")
                    + " el " + fecha;
        } else {
            texto = "El mes de " + mesLabel + " aún no ha sido cerrado contablemente.";
        }

        JLabel lblTexto = new JLabel(texto);
        lblTexto.setFont(new Font("Segoe UI", cerrado ? Font.PLAIN : Font.BOLD, 12));
        lblTexto.setForeground(accent);
        banner.add(lblTexto, BorderLayout.CENTER);

        // Botón solo para Admin cuando no está cerrado
        if (!cerrado && "Administrador".equalsIgnoreCase(userRole)) {
            JButton btnCerrar = new JButton("Cerrar mes") {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(accent);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            btnCerrar.setForeground(Color.WHITE);
            btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnCerrar.setContentAreaFilled(false);
            btnCerrar.setBorderPainted(false);
            btnCerrar.setFocusPainted(false);
            btnCerrar.setPreferredSize(new Dimension(110, 28));
            btnCerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnCerrar.addActionListener(e -> confirmarCierre(mesActual));
            banner.add(btnCerrar, BorderLayout.EAST);
        }

        return banner;
    }

    private void confirmarCierre(String mes) {
        String mesLabel = LocalDate.parse(mes + "-01").format(FMT_MES);
        mesLabel = mesLabel.substring(0, 1).toUpperCase() + mesLabel.substring(1);

        JTextArea txtNotas = new JTextArea(3, 30);
        txtNotas.setLineWrap(true);
        txtNotas.setWrapStyleWord(true);
        txtNotas.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getBorder()),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        JScrollPane spNotas = new JScrollPane(txtNotas);

        JPanel dialogPanel = new JPanel(new BorderLayout(0, 8));
        dialogPanel.add(new JLabel("<html>¿Confirmar cierre contable de <b>" + mesLabel + "</b>?<br>" +
                "<small>Esta acción es solo contable y no afecta las operaciones.</small></html>"),
                BorderLayout.NORTH);
        dialogPanel.add(new JLabel("Notas (opcional):"), BorderLayout.CENTER);
        dialogPanel.add(spNotas, BorderLayout.SOUTH);

        int res = JOptionPane.showConfirmDialog(this, dialogPanel,
                "Cerrar mes: " + mesLabel,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

        if (res == JOptionPane.OK_OPTION) {
            boolean ok;
            try {
                ok = cierreDAO.cerrarMes(mes, idUsuario, txtNotas.getText().trim());
            } catch (DatabaseException e) {
                ErrorUtil.mostrarError(ReportePanel.this, "cerrar mes", e);
                return;
            }
            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Mes " + mesLabel + " cerrado correctamente.",
                        "Cierre registrado", JOptionPane.INFORMATION_MESSAGE);
                refreshUI();
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo registrar el cierre.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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