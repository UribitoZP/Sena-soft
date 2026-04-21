package com.santaana.view;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.*;

import com.toedter.calendar.JDateChooser;
import com.santaana.dao.HistorialDAO;
import com.santaana.model.Actividad;
import com.santaana.util.ThemeManager;

public class HistorialPanel extends JPanel implements ThemeManager.ThemeListener {

    private final HistorialDAO historialDAO = new HistorialDAO();

    private JTextField txtBuscar;
    private JDateChooser dateDesde;
    private JDateChooser dateHasta;
    private JPanel listaContainer;
    private javax.swing.Timer debounce;

    // Recuerda qué secciones están abiertas entre recargas
    private final Set<String> seccionesAbiertas = new HashSet<>(
        Arrays.asList("Hoy", "Ayer")
    );

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

        PropertyChangeListener cambioFecha = e -> {
            if ("date".equals(e.getPropertyName())) dispararDebounce();
        };
        dateDesde.addPropertyChangeListener(cambioFecha);
        dateHasta.addPropertyChangeListener(cambioFecha);

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

    private JTextField campoBusqueda(String placeholder, int ancho) {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setColor(getLabelCol());
                    g2.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                    Insets ins = getInsets();
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(placeholder, ins.left + 4,
                        (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                    g2.dispose();
                }
            }
        };
        f.setPreferredSize(new Dimension(ancho, 32));
        f.setBackground(getPanelCol());
        f.setForeground(getTextCol());
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getBorde(), 1, true),
            BorderFactory.createEmptyBorder(0, 8, 0, 8)
        ));
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

    // ── Debounce ──────────────────────────────────────────────────────────────
    private void dispararDebounce() {
        if (debounce != null && debounce.isRunning()) debounce.stop();
        debounce = new javax.swing.Timer(300, e -> aplicarFiltros());
        debounce.setRepeats(false);
        debounce.start();
    }

    private void aplicarFiltros() {
        String texto = txtBuscar != null ? txtBuscar.getText().trim() : null;
        if (texto != null && texto.isEmpty()) texto = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String desde = (dateDesde != null && dateDesde.getDate() != null) ? sdf.format(dateDesde.getDate()) : null;
        String hasta = (dateHasta != null && dateHasta.getDate() != null) ? sdf.format(dateHasta.getDate()) : null;
        cargarYMostrar(texto, desde, hasta);
    }

    // ── Renderizado ───────────────────────────────────────────────────────────
    public void cargarYMostrar(String texto, String desde, String hasta) {
        if (listaContainer == null) return;
        listaContainer.removeAll();

        List<Actividad> actividades = historialDAO.buscar(texto, desde, hasta);

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
            LinkedHashMap<String, List<Actividad>> grupos = agruparPorFecha(actividades);
            boolean primero = true;
            for (Map.Entry<String, List<Actividad>> entry : grupos.entrySet()) {
                if (!primero) listaContainer.add(Box.createVerticalStrut(6));
                primero = false;
                listaContainer.add(seccionDesplegable(entry.getKey(), entry.getValue()));
            }
        }

        listaContainer.revalidate();
        listaContainer.repaint();
    }

    // ── Sección desplegable ───────────────────────────────────────────────────
    private JPanel seccionDesplegable(String titulo, List<Actividad> items) {
        boolean abierto = seccionesAbiertas.contains(titulo);

        // Panel de cards (el que se muestra/oculta)
        JPanel cuerpo = new JPanel();
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setOpaque(false);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        for (Actividad a : items) {
            cuerpo.add(card(a));
            cuerpo.add(Box.createVerticalStrut(4));
        }
        cuerpo.setVisible(abierto);

        // Encabezado clicable
        JPanel header = crearEncabezadoClicable(titulo, items.size(), abierto, cuerpo);

        // Contenedor del bloque completo
        JPanel bloque = new JPanel();
        bloque.setLayout(new BoxLayout(bloque, BoxLayout.Y_AXIS));
        bloque.setOpaque(false);
        bloque.setAlignmentX(Component.LEFT_ALIGNMENT);
        bloque.add(header);
        bloque.add(cuerpo);
        return bloque;
    }

    private JPanel crearEncabezadoClicable(String titulo, int cantidad,
                                            boolean inicialAbierto, JPanel cuerpo) {
        boolean isDark = ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK;
        Color bgHeader = isDark ? new Color(0x1E293B) : new Color(0xF1F5F9);

        JPanel header = new JPanel(new BorderLayout(10, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
            }
        };
        header.setBackground(bgHeader);
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        header.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        // Flecha + título
        JLabel flecha = new JLabel(inicialAbierto ? "▾" : "▸");
        flecha.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        flecha.setForeground(getLabelCol());

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitulo.setForeground(getTextCol());

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        izq.setOpaque(false);
        izq.add(flecha);
        izq.add(lblTitulo);

        // Badge de cantidad
        JLabel badge = new JLabel(cantidad + " registro" + (cantidad != 1 ? "s" : "")) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBorde());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        badge.setForeground(getLabelCol());
        badge.setOpaque(false);
        badge.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

        header.add(izq,   BorderLayout.WEST);
        header.add(badge, BorderLayout.EAST);

        // Toggle al hacer click
        MouseAdapter toggle = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                boolean ahora = !cuerpo.isVisible();
                cuerpo.setVisible(ahora);
                flecha.setText(ahora ? "▾" : "▸");
                if (ahora) seccionesAbiertas.add(titulo);
                else       seccionesAbiertas.remove(titulo);
                listaContainer.revalidate();
                listaContainer.repaint();
            }
            @Override public void mouseEntered(MouseEvent e) {
                header.setBackground(isDark ? new Color(0x263042) : new Color(0xE2E8F0));
                header.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                header.setBackground(bgHeader);
                header.repaint();
            }
        };
        header.addMouseListener(toggle);
        izq.addMouseListener(toggle);
        flecha.addMouseListener(toggle);
        lblTitulo.addMouseListener(toggle);

        return header;
    }

    // ── Agrupación ────────────────────────────────────────────────────────────
    private LinkedHashMap<String, List<Actividad>> agruparPorFecha(List<Actividad> lista) {
        String[] orden = {"Hoy", "Ayer", "Esta semana", "Este mes", "Anterior"};
        LinkedHashMap<String, List<Actividad>> grupos = new LinkedHashMap<>();
        for (String s : orden) grupos.put(s, new ArrayList<>());

        LocalDate hoy = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Actividad a : lista) {
            String sec = "Anterior";
            try {
                LocalDate fecha = LocalDate.parse(a.getFechaHora().substring(0, 10), fmt);
                long dias = hoy.toEpochDay() - fecha.toEpochDay();
                if      (dias == 0)  sec = "Hoy";
                else if (dias == 1)  sec = "Ayer";
                else if (dias <= 7)  sec = "Esta semana";
                else if (dias <= 30) sec = "Este mes";
            } catch (Exception ignored) {}
            grupos.get(sec).add(a);
        }

        LinkedHashMap<String, List<Actividad>> resultado = new LinkedHashMap<>();
        for (String s : orden) {
            if (!grupos.get(s).isEmpty()) resultado.put(s, grupos.get(s));
        }
        return resultado;
    }

    // ── Card ──────────────────────────────────────────────────────────────────
    private JPanel card(Actividad a) {
        Color acento = colorPorTipo(a.getTipo());

        JPanel c = new JPanel(new BorderLayout(10, 0));
        c.setBackground(getPanelCol());
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getBorde(), 1, true),
            BorderFactory.createEmptyBorder(7, 0, 7, 12)
        ));

        JPanel barra = new JPanel();
        barra.setBackground(acento);
        barra.setPreferredSize(new Dimension(4, 0));

        JLabel tipoBadge = new JLabel(a.getTipo()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 25));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tipoBadge.setFont(new Font("Segoe UI", Font.BOLD, 9));
        tipoBadge.setForeground(acento.darker());
        tipoBadge.setOpaque(false);
        tipoBadge.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

        JPanel texto = new JPanel();
        texto.setLayout(new BoxLayout(texto, BoxLayout.Y_AXIS));
        texto.setOpaque(false);

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        fila1.setOpaque(false);
        JLabel tituloLbl = new JLabel(a.getTitulo());
        tituloLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tituloLbl.setForeground(getTextCol());
        fila1.add(tituloLbl);
        fila1.add(tipoBadge);

        JLabel descLbl = new JLabel(a.getDescripcion());
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descLbl.setForeground(getLabelCol());

        texto.add(fila1);
        texto.add(descLbl);

        JLabel horaLbl = new JLabel(horaCorta(a.getFechaHora()));
        horaLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        horaLbl.setForeground(getLabelCol());

        c.add(barra,   BorderLayout.WEST);
        c.add(texto,   BorderLayout.CENTER);
        c.add(horaLbl, BorderLayout.EAST);
        return c;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private Color colorPorTipo(String tipo) {
        if (tipo == null) return new Color(0x95A5A6);
        switch (tipo) {
            case "Reserva":     return new Color(0x3498DB);
            case "Checkout":    return new Color(0x27AE60);
            case "Cancelacion": return new Color(0xE74C3C);
            case "Habitacion":  return new Color(0xF39C12);
            default:            return new Color(0x95A5A6);
        }
    }

    private String horaCorta(String fechaHora) {
        if (fechaHora == null || fechaHora.length() < 16) return "";
        return fechaHora.substring(11, 16);
    }
}
