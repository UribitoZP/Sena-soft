package com.santaana.view;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.MatteBorder;

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
            @Override public void componentShown(ComponentEvent e) {
                cargarYMostrar(null, null, null);
            }
        });
    }

    private void refreshUI() {
        removeAll();
        setBackground(getFondo());
        add(crearNavbar(), BorderLayout.NORTH);
        add(crearContenido(), BorderLayout.CENTER);
        revalidate();
        repaint();
        cargarYMostrar(null, null, null);
    }

    @Override
    public void onThemeChanged() { refreshUI(); }

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

    // ── Contenido ─────────────────────────────────────────────────────────────
    private JPanel crearContenido() {
        JPanel cont = new JPanel(new BorderLayout(0, 12));
        cont.setOpaque(false);
        cont.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        cont.add(crearFiltros(), BorderLayout.NORTH);
        cont.add(crearScroll(), BorderLayout.CENTER);
        return cont;
    }

    // ── Barra de filtros ──────────────────────────────────────────────────────
    private JPanel crearFiltros() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        row.setOpaque(false);

        txtBuscar = campo(180);

        dateDesde = new JDateChooser();
        dateHasta = new JDateChooser();
        estilizar(dateDesde);
        estilizar(dateHasta);

        JButton btnBuscar  = btn("Buscar",  getPrimario(), Color.WHITE, false);
        JButton btnLimpiar = btn("Limpiar", getPanelCol(), getLabelCol(), true);

        btnBuscar.addActionListener(e -> aplicarFiltros());
        btnLimpiar.addActionListener(e -> {
            txtBuscar.setText("");
            dateDesde.setDate(null);
            dateHasta.setDate(null);
            cargarYMostrar(null, null, null);
        });

        row.add(lbl("Buscar:")); row.add(txtBuscar);
        row.add(lbl("Desde:")); row.add(dateDesde);
        row.add(lbl("Hasta:")); row.add(dateHasta);
        row.add(btnBuscar);
        row.add(btnLimpiar);
        return row;
    }

    private JTextField campo(int w) {
        JTextField f = new JTextField();
        f.setPreferredSize(new Dimension(w, 30));
        f.setBackground(getPanelCol());
        f.setForeground(getTextCol());
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createLineBorder(getBorde(), 1, true));
        return f;
    }

    private void estilizar(JDateChooser d) {
        d.setPreferredSize(new Dimension(120, 30));
        d.setBorder(BorderFactory.createLineBorder(getBorde(), 1, true));
        d.setBackground(getPanelCol());
        JTextField ed = (JTextField) d.getDateEditor().getUiComponent();
        ed.setBorder(null);
        ed.setBackground(getPanelCol());
        ed.setForeground(getTextCol());
        ed.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    private JButton btn(String txt, Color bg, Color fg, boolean borde) {
        JButton b = new JButton(txt);
        b.setFont(new Font("Segoe UI", Font.BOLD, 11));
        b.setPreferredSize(new Dimension(82, 30));
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
        b.setBorderPainted(borde);
        if (borde) b.setBorder(BorderFactory.createLineBorder(getBorde(), 1, true));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JLabel lbl(String txt) {
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

    // ── Filtrado ──────────────────────────────────────────────────────────────
    private void aplicarFiltros() {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) texto = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String desde = dateDesde.getDate() != null ? sdf.format(dateDesde.getDate()) : null;
        String hasta = dateHasta.getDate() != null ? sdf.format(dateHasta.getDate()) : null;
        cargarYMostrar(texto, desde, hasta);
    }

    // ── Carga y renderizado con subsecciones ──────────────────────────────────
    public void cargarYMostrar(String texto, String desde, String hasta) {
        if (listaContainer == null) return;
        listaContainer.removeAll();

        List<Actividad> actividades = historialDAO.buscar(texto, desde, hasta);

        if (actividades.isEmpty()) {
            JLabel vacio = new JLabel("No hay actividades registradas.");
            vacio.setForeground(getLabelCol());
            vacio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            vacio.setAlignmentX(Component.LEFT_ALIGNMENT);
            listaContainer.add(Box.createVerticalStrut(20));
            listaContainer.add(vacio);
        } else {
            // Agrupar por sección de fecha
            LinkedHashMap<String, List<Actividad>> grupos = agruparPorFecha(actividades);

            for (Map.Entry<String, List<Actividad>> entry : grupos.entrySet()) {
                listaContainer.add(encabezadoSeccion(entry.getKey()));
                listaContainer.add(Box.createVerticalStrut(6));

                for (Actividad a : entry.getValue()) {
                    listaContainer.add(card(
                        colorPorTipo(a.getTipo()),
                        a.getTitulo(),
                        a.getDescripcion(),
                        horaCorta(a.getFechaHora())
                    ));
                    listaContainer.add(Box.createVerticalStrut(5));
                }
                listaContainer.add(Box.createVerticalStrut(10));
            }
        }

        listaContainer.revalidate();
        listaContainer.repaint();
    }

    // ── Agrupación por fecha relativa ─────────────────────────────────────────
    private LinkedHashMap<String, List<Actividad>> agruparPorFecha(List<Actividad> lista) {
        LinkedHashMap<String, List<Actividad>> grupos = new LinkedHashMap<>();
        LocalDate hoy  = LocalDate.now();
        LocalDate ayer = hoy.minusDays(1);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Actividad a : lista) {
            String seccion = "Anterior";
            try {
                String fechaParte = a.getFechaHora().substring(0, 10);
                LocalDate fecha = LocalDate.parse(fechaParte, fmt);
                long dias = hoy.toEpochDay() - fecha.toEpochDay();
                if (dias == 0)       seccion = "Hoy";
                else if (dias == 1)  seccion = "Ayer";
                else if (dias <= 7)  seccion = "Esta semana";
                else if (dias <= 30) seccion = "Este mes";
            } catch (Exception ignored) {}

            grupos.computeIfAbsent(seccion, k -> new ArrayList<>()).add(a);
        }
        return grupos;
    }

    // ── Encabezado de sección ─────────────────────────────────────────────────
    private JPanel encabezadoSeccion(String titulo) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(titulo.toUpperCase());
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(getLabelCol());

        JPanel linea = new JPanel();
        linea.setOpaque(true);
        linea.setBackground(getBorde());
        linea.setPreferredSize(new Dimension(0, 1));

        row.add(lbl,   BorderLayout.WEST);
        row.add(linea, BorderLayout.CENTER);
        return row;
    }

    // ── Card compacta ─────────────────────────────────────────────────────────
    private JPanel card(Color acento, String titulo, String desc, String hora) {
        JPanel c = new JPanel(new BorderLayout(10, 0));
        c.setBackground(getPanelCol());
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getBorde(), 1, true),
            BorderFactory.createEmptyBorder(7, 8, 7, 10)
        ));

        // Barra de color izquierda
        JPanel barra = new JPanel();
        barra.setBackground(acento);
        barra.setPreferredSize(new Dimension(4, 0));

        // Contenido central
        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setOpaque(false);

        JLabel tituloLbl = new JLabel(titulo);
        tituloLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tituloLbl.setForeground(getTextCol());

        JLabel descLbl = new JLabel(desc);
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descLbl.setForeground(getLabelCol());

        centro.add(tituloLbl);
        centro.add(Box.createVerticalStrut(2));
        centro.add(descLbl);

        // Hora alineada a la derecha
        JLabel horaLbl = new JLabel(hora);
        horaLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        horaLbl.setForeground(getLabelCol());
        horaLbl.setVerticalAlignment(SwingConstants.CENTER);

        c.add(barra,  BorderLayout.WEST);
        c.add(centro, BorderLayout.CENTER);
        c.add(horaLbl, BorderLayout.EAST);
        return c;
    }

    // ── Utilidades ────────────────────────────────────────────────────────────
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
        if (fechaHora == null) return "";
        try {
            return fechaHora.substring(11, 16); // "HH:mm"
        } catch (Exception e) {
            return "";
        }
    }
}
