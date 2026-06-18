package com.santaana.view;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.*;

import com.toedter.calendar.JDateChooser;
import com.santaana.dao.HistorialDAO;
import com.santaana.db.DatabaseException;
import com.santaana.model.Actividad;
import com.santaana.util.DateUtil;
import com.santaana.util.ErrorUtil;
import com.santaana.util.ThemeManager;

public class HistorialPanel extends JPanel implements ThemeManager.ThemeListener {

    private final HistorialDAO historialDAO = new HistorialDAO();

    private JTextField txtBuscar;
    private JDateChooser dateDesde;
    private JDateChooser dateHasta;
    private JPanel listaContainer;
    private javax.swing.Timer debounce;

    // Secciones de fecha abiertas por defecto
    private final Set<String> fechasAbiertas = new HashSet<>(Arrays.asList("Hoy", "Ayer"));
    // Subsecciones de tipo abiertas: clave = "fecha::tipo"
    private final Set<String> tiposAbiertos  = new HashSet<>();

    private static final String[] ORDEN_FECHA = {"Hoy", "Ayer", "Esta semana", "Este mes", "Anterior"};
    private static final String[] ORDEN_TIPO  = {"Reserva", "Checkout", "Cancelacion", "Habitacion", "Login", "Sistema"};

    private Color getPrimario() { return ThemeManager.getPrimary(); }
    private Color getFondo()    { return ThemeManager.getBackground(); }
    private Color getPanelCol() { return ThemeManager.getPanelBackground(); }
    private Color getBorde()    { return ThemeManager.getBorder(); }
    private Color getTextCol()  { return ThemeManager.getTextPrimary(); }
    private Color getLabelCol() { return ThemeManager.getTextSecondary(); }

    public HistorialPanel(String role, String welcomeMessage) {
        ThemeManager.addListener(this);
        setLayout(new BorderLayout());
        refreshUI();
        addComponentListener(new ComponentAdapter() {
            @Override public void componentShown(ComponentEvent e) { aplicarFiltros(); }
        });
    }

    private void refreshUI() {
        removeAll();
        setBackground(getFondo());
        add(crearNavbar(),    BorderLayout.NORTH);
        add(crearContenido(), BorderLayout.CENTER);
        revalidate();
        repaint();
        aplicarFiltros();
    }

    @Override public void onThemeChanged() { refreshUI(); }

    // ── Navbar ────────────────────────────────────────────────────────────────
    private JPanel crearNavbar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(getPanelCol());
        bar.setPreferredSize(new Dimension(0, 60));
        bar.setBorder(new MatteBorder(0, 0, 1, 0, getBorde()));
        JLabel title = new JLabel("  HISTORIAL DE ACTIVIDADES");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(getTextCol());
        bar.add(title, BorderLayout.WEST);
        return bar;
    }

    // ── Layout ────────────────────────────────────────────────────────────────
    private JPanel crearContenido() {
        JPanel cont = new JPanel(new BorderLayout(0, 14));
        cont.setOpaque(false);
        cont.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        cont.add(crearBarraFiltros(), BorderLayout.NORTH);
        cont.add(crearScroll(),       BorderLayout.CENTER);
        return cont;
    }

    // ── Filtros ───────────────────────────────────────────────────────────────
    private JPanel crearBarraFiltros() {
        JPanel barra = new JPanel(new BorderLayout(12, 0));
        barra.setOpaque(false);

        txtBuscar = campoBusqueda("Buscar por título o descripción...", 300);
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { dispararDebounce(); }
            public void removeUpdate(DocumentEvent e)  { dispararDebounce(); }
            public void changedUpdate(DocumentEvent e) { dispararDebounce(); }
        });

        JPanel fechas = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        fechas.setOpaque(false);

        dateDesde = new JDateChooser();
        dateHasta = new JDateChooser();
        estilizar(dateDesde);
        estilizar(dateHasta);

        PropertyChangeListener cl = e -> { if ("date".equals(e.getPropertyName())) dispararDebounce(); };
        dateDesde.addPropertyChangeListener(cl);
        dateHasta.addPropertyChangeListener(cl);

        JButton btnLimpiar = new JButton("Limpiar filtros");
        btnLimpiar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnLimpiar.setForeground(getLabelCol());
        btnLimpiar.setBackground(getPanelCol());
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setBorder(BorderFactory.createLineBorder(getBorde(), 1, true));
        btnLimpiar.setPreferredSize(new Dimension(110, 30));
        btnLimpiar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLimpiar.addActionListener(e -> {
            txtBuscar.setText("");
            dateDesde.setDate(null);
            dateHasta.setDate(null);
            aplicarFiltros();
        });

        fechas.add(etiqueta("Desde:")); fechas.add(dateDesde);
        fechas.add(etiqueta("Hasta:")); fechas.add(dateHasta);
        fechas.add(Box.createHorizontalStrut(4));
        fechas.add(btnLimpiar);

        barra.add(txtBuscar, BorderLayout.CENTER);
        barra.add(fechas,    BorderLayout.EAST);
        return barra;
    }

    private JTextField campoBusqueda(String ph, int w) {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setColor(getLabelCol());
                    g2.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                    Insets ins = getInsets();
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(ph, ins.left + 4, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                    g2.dispose();
                }
            }
        };
        f.setPreferredSize(new Dimension(w, 32));
        f.setBackground(getPanelCol());
        f.setForeground(getTextCol());
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getBorde(), 1, true),
            BorderFactory.createEmptyBorder(0, 8, 0, 8)));
        return f;
    }

    private void estilizar(JDateChooser d) {
        d.setPreferredSize(new Dimension(118, 30));
        d.setBorder(BorderFactory.createLineBorder(getBorde(), 1, true));
        d.setBackground(getPanelCol());
        JTextField ed = (JTextField) d.getDateEditor().getUiComponent();
        ed.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 4));
        ed.setBackground(getPanelCol());
        ed.setForeground(getTextCol());
        ed.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    private JLabel etiqueta(String txt) {
        JLabel l = new JLabel(txt);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setForeground(getLabelCol());
        return l;
    }

    // ── Scroll ────────────────────────────────────────────────────────────────
    private JScrollPane crearScroll() {
        listaContainer = new JPanel();
        listaContainer.setLayout(new BoxLayout(listaContainer, BoxLayout.Y_AXIS));
        listaContainer.setOpaque(false);
        JScrollPane sc = new JScrollPane(listaContainer);
        sc.setBorder(null);
        sc.setOpaque(false);
        sc.getViewport().setOpaque(false);
        sc.getVerticalScrollBar().setUnitIncrement(16);
        return sc;
    }

    // ── Debounce / filtros ────────────────────────────────────────────────────
    private void dispararDebounce() {
        if (debounce != null && debounce.isRunning()) debounce.stop();
        debounce = new javax.swing.Timer(300, e -> aplicarFiltros());
        debounce.setRepeats(false);
        debounce.start();
    }

    private void aplicarFiltros() {
        String texto = txtBuscar != null ? txtBuscar.getText().trim() : null;
        if (texto != null && texto.isEmpty()) texto = null;
        String desde = (dateDesde != null && dateDesde.getDate() != null)
                ? DateUtil.formatearFecha(dateDesde.getDate()) : null;
        String hasta = (dateHasta != null && dateHasta.getDate() != null)
                ? DateUtil.formatearFecha(dateHasta.getDate()) : null;
        cargarYMostrar(texto, desde, hasta);
    }


    // ── Renderizado principal ─────────────────────────────────────────────────
    public void cargarYMostrar(String texto, String desde, String hasta) {
        if (listaContainer == null) return;
        listaContainer.removeAll();

        List<Actividad> actividades;
        try {
            actividades = historialDAO.buscarConClientes(texto, desde, hasta);
        } catch (DatabaseException e) {
            ErrorUtil.mostrarError(this, "buscar en historial", e);
            return;
        }

        if (actividades.isEmpty()) {
            String msg = (texto != null || desde != null || hasta != null)
                ? "No se encontraron resultados para los filtros aplicados."
                : "No hay actividades registradas aún.";
            JLabel vacio = new JLabel(msg);
            vacio.setForeground(getLabelCol());
            vacio.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            vacio.setAlignmentX(Component.LEFT_ALIGNMENT);
            listaContainer.add(Box.createVerticalStrut(24));
            listaContainer.add(vacio);
        } else {
            // Agrupar: fecha → tipo → lista
            Map<String, Map<String, List<Actividad>>> porFecha = agrupar(actividades);

            boolean primero = true;
            for (String fecha : ORDEN_FECHA) {
                Map<String, List<Actividad>> porTipo = porFecha.get(fecha);
                if (porTipo == null) continue;

                if (!primero) listaContainer.add(Box.createVerticalStrut(6));
                primero = false;

                int totalFecha = porTipo.values().stream().mapToInt(List::size).sum();
                listaContainer.add(seccionFecha(fecha, totalFecha, porTipo));
            }
        }

        listaContainer.revalidate();
        listaContainer.repaint();
    }

    // ── Agrupación fecha → tipo ───────────────────────────────────────────────
    private Map<String, Map<String, List<Actividad>>> agrupar(List<Actividad> lista) {
        Map<String, Map<String, List<Actividad>>> resultado = new LinkedHashMap<>();
        LocalDate hoy = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Actividad a : lista) {
            String fecha = "Anterior";
            try {
                LocalDate d = LocalDate.parse(a.getFechaHora().substring(0, 10), fmt);
                long dias = hoy.toEpochDay() - d.toEpochDay();
                if      (dias == 0)  fecha = "Hoy";
                else if (dias == 1)  fecha = "Ayer";
                else if (dias <= 7)  fecha = "Esta semana";
                else if (dias <= 30) fecha = "Este mes";
            } catch (Exception ignored) {}

            String tipo = a.getTipo() != null ? a.getTipo() : "Sistema";

            resultado
                .computeIfAbsent(fecha, k -> new LinkedHashMap<>())
                .computeIfAbsent(tipo,  k -> new ArrayList<>())
                .add(a);
        }
        return resultado;
    }

    // ── Sección de fecha (nivel 1) ────────────────────────────────────────────
    private JPanel seccionFecha(String fecha, int total,
                                 Map<String, List<Actividad>> porTipo) {
        boolean isDark  = ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK;
        Color bgNormal  = isDark ? new Color(0x1E293B) : new Color(0xF1F5F9);
        Color bgHover   = isDark ? new Color(0x263042) : new Color(0xE2E8F0);
        boolean abierto = fechasAbiertas.contains(fecha);

        // Cuerpo con subsecciones de tipo
        JPanel cuerpo = new JPanel();
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setOpaque(false);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 0));
        for (String tipo : ORDEN_TIPO) {
            List<Actividad> items = porTipo.get(tipo);
            if (items == null) continue;
            cuerpo.add(seccionTipo(fecha, tipo, items));
            cuerpo.add(Box.createVerticalStrut(5));
        }
        cuerpo.setVisible(abierto);

        // Flecha pintada
        boolean[] ref = {abierto};
        JComponent icono = iconoFlecha(ref, getTextCol());

        JLabel lblFecha = new JLabel(fecha);
        lblFecha.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblFecha.setForeground(getTextCol());

        JLabel badge = badge(total + " registro" + (total != 1 ? "s" : ""));

        // Header
        JPanel header = new JPanel(new BorderLayout(8, 0));
        header.setOpaque(false);
        header.setBackground(bgNormal);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        header.setPreferredSize(new Dimension(0, 36));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(bgNormal, 8),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        izq.setOpaque(false);
        izq.add(icono);
        izq.add(lblFecha);

        header.add(izq,   BorderLayout.WEST);
        header.add(badge, BorderLayout.EAST);

        attachToggle(header, cuerpo, ref, icono, badge, bgNormal, bgHover,
                     fecha, fechasAbiertas);

        JPanel bloque = new JPanel();
        bloque.setLayout(new BoxLayout(bloque, BoxLayout.Y_AXIS));
        bloque.setOpaque(false);
        bloque.setAlignmentX(Component.LEFT_ALIGNMENT);
        bloque.add(header);
        bloque.add(cuerpo);
        return bloque;
    }

    // ── Sección de tipo (nivel 2) ─────────────────────────────────────────────
    private JPanel seccionTipo(String fecha, String tipo, List<Actividad> items) {
        boolean isDark    = ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK;
        Color acento      = colorPorTipo(tipo);
        Color bgNormal    = new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), isDark ? 18 : 12);
        Color bgHover     = new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), isDark ? 35 : 25);
        String clave      = fecha + "::" + tipo;
        boolean abierto   = tiposAbiertos.contains(clave);

        // Cards
        JPanel cuerpo = new JPanel();
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setOpaque(false);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(5, 0, 2, 0));
        for (Actividad a : items) {
            cuerpo.add(card(a));
            cuerpo.add(Box.createVerticalStrut(4));
        }
        cuerpo.setVisible(abierto);

        // Flecha y título
        boolean[] ref = {abierto};
        JComponent icono = iconoFlecha(ref, acento.darker());

        JLabel lblTipo = new JLabel(tipo);
        lblTipo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTipo.setForeground(acento.darker());

        JLabel badge = badgeColoreado(String.valueOf(items.size()), acento);

        // Header
        JPanel header = new JPanel(new BorderLayout(8, 0));
        header.setOpaque(false);
        header.setBackground(bgNormal);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        header.setPreferredSize(new Dimension(0, 30));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(bgNormal, 6),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        izq.setOpaque(false);
        izq.add(icono);
        izq.add(lblTipo);

        header.add(izq,   BorderLayout.WEST);
        header.add(badge, BorderLayout.EAST);

        attachToggle(header, cuerpo, ref, icono, badge, bgNormal, bgHover,
                     clave, tiposAbiertos);

        JPanel bloque = new JPanel();
        bloque.setLayout(new BoxLayout(bloque, BoxLayout.Y_AXIS));
        bloque.setOpaque(false);
        bloque.setAlignmentX(Component.LEFT_ALIGNMENT);
        bloque.add(header);
        bloque.add(cuerpo);
        return bloque;
    }

    // ── Flecha pintada con Graphics2D ─────────────────────────────────────────
    private JComponent iconoFlecha(boolean[] ref, Color color) {
        JComponent ic = new JComponent() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                int cx = getWidth() / 2, cy = getHeight() / 2;
                if (ref[0]) {                           // abierto: triángulo abajo
                    int[] xs = {cx - 5, cx + 5, cx};
                    int[] ys = {cy - 3, cy - 3, cy + 4};
                    g2.fillPolygon(xs, ys, 3);
                } else {                                // cerrado: triángulo derecha
                    int[] xs = {cx - 3, cx - 3, cx + 4};
                    int[] ys = {cy - 5, cy + 5, cy};
                    g2.fillPolygon(xs, ys, 3);
                }
                g2.dispose();
            }
        };
        ic.setPreferredSize(new Dimension(14, 14));
        ic.setOpaque(false);
        return ic;
    }

    // ── Toggle reutilizable ───────────────────────────────────────────────────
    private void attachToggle(JPanel header, JPanel cuerpo, boolean[] ref,
                               JComponent icono, JLabel badge,
                               Color bgNormal, Color bgHover,
                               String clave, Set<String> estado) {
        MouseAdapter ma = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                ref[0] = !cuerpo.isVisible();
                cuerpo.setVisible(ref[0]);
                if (ref[0]) estado.add(clave);
                else        estado.remove(clave);
                icono.repaint();
                listaContainer.revalidate();
                listaContainer.repaint();
            }
            @Override public void mouseEntered(MouseEvent e) {
                header.putClientProperty("bg", bgHover);
                header.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                header.putClientProperty("bg", bgNormal);
                header.repaint();
            }
        };
        // Propagar cursor y listener a todos los hijos
        addMouseRecursive(header, ma);
    }

    private void addMouseRecursive(Component comp, MouseAdapter ma) {
        comp.addMouseListener(ma);
        comp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (comp instanceof Container) {
            for (Component c : ((Container) comp).getComponents()) {
                addMouseRecursive(c, ma);
            }
        }
    }

    // ── Card de actividad ─────────────────────────────────────────────────────
    private JPanel card(Actividad a) {
        Color acento = colorPorTipo(a.getTipo());

        JPanel c = new JPanel(new BorderLayout(10, 0));
        c.setBackground(getPanelCol());
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getBorde(), 1, true),
            BorderFactory.createEmptyBorder(7, 0, 7, 12)));

        JPanel barra = new JPanel();
        barra.setBackground(acento);
        barra.setPreferredSize(new Dimension(4, 0));

        JPanel texto = new JPanel();
        texto.setLayout(new BoxLayout(texto, BoxLayout.Y_AXIS));
        texto.setOpaque(false);
        texto.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));

        JLabel tituloLbl = new JLabel(a.getTitulo());
        tituloLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tituloLbl.setForeground(getTextCol());

        JLabel descLbl = new JLabel(a.getDescripcion());
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descLbl.setForeground(getLabelCol());

        texto.add(tituloLbl);
        texto.add(Box.createVerticalStrut(2));
        texto.add(descLbl);

        JLabel horaLbl = new JLabel(horaCorta(a.getFechaHora()));
        horaLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        horaLbl.setForeground(getLabelCol());

        c.add(barra,   BorderLayout.WEST);
        c.add(texto,   BorderLayout.CENTER);
        c.add(horaLbl, BorderLayout.EAST);
        return c;
    }

    // ── RoundedBorder: pinta fondo redondeado sin afectar layout ─────────────
    private static class RoundedBorder implements javax.swing.border.Border {
        private final Color color;
        private final int radio;
        RoundedBorder(Color c, int r) { this.color = c; this.radio = r; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(x, y, w, h, radio, radio);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(0, 0, 0, 0); }
        @Override public boolean isBorderOpaque() { return false; }
    }

    private JLabel badge(String txt) {
        JLabel l = new JLabel(txt) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBorde());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        l.setForeground(getLabelCol());
        l.setOpaque(false);
        l.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        return l;
    }

    private JLabel badgeColoreado(String txt, Color acento) {
        JLabel l = new JLabel(txt) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(acento.darker());
        l.setOpaque(false);
        l.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        return l;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private Color colorPorTipo(String tipo) {
        if (tipo == null) return new Color(0x95A5A6);
        switch (tipo) {
            case "Reserva":     return new Color(0x3498DB);
            case "Checkout":    return new Color(0x27AE60);
            case "Cancelacion": return new Color(0xE74C3C);
            case "Habitacion":  return new Color(0xF39C12);
            case "Login":       return new Color(0x8E44AD);
            default:            return new Color(0x95A5A6);
        }
    }

    private String horaCorta(String fh) {
        if (fh == null || fh.length() < 16) return "";
        return fh.substring(11, 16);
    }
}
