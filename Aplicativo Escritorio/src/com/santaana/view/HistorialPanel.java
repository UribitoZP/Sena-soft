package com.santaana.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.MatteBorder;

import com.toedter.calendar.JDateChooser;
import com.santaana.util.ThemeManager;

public class HistorialPanel extends JPanel implements ThemeManager.ThemeListener {

    private String role;

    private Color getPrimario() { return ThemeManager.getPrimary(); }
    private Color getFondo() { return ThemeManager.getBackground(); }
    private Color getPanelCol() { return ThemeManager.getPanelBackground(); }
    private Color getBorde() { return ThemeManager.getBorder(); }
    private Color getTextCol() { return ThemeManager.getTextPrimary(); }
    private Color getLabelCol() { return ThemeManager.getTextSecondary(); }

    private JTextField txtBuscar;
    private JDateChooser dateDesde;
    private JDateChooser dateHasta;

    public HistorialPanel(String role, String welcomeMessage) {
        this.role = role;
        ThemeManager.addListener(this);
        setLayout(new BorderLayout());
        refreshUI();
    }

    private void refreshUI() {
        removeAll();
        setBackground(getFondo());
        add(crearNavbar(), BorderLayout.NORTH);
        add(crearContenido(), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    @Override
    public void onThemeChanged() {
        refreshUI();
    }

    // 🔝 NAVBAR
    private JPanel crearNavbar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(getPanelCol());
        navbar.setPreferredSize(new Dimension(0, 60));
        navbar.setBorder(new MatteBorder(0, 0, 1, 0, getBorde()));

        JLabel title = new JLabel("  HISTORIAL DE ACTIVIDADES");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(getTextCol());

        navbar.add(title, BorderLayout.WEST);
        return navbar;
    }

    // 🧱 CONTENIDO
    private JPanel crearContenido() {
        JPanel cont = new JPanel(new BorderLayout(0, 15));
        cont.setOpaque(false);
        cont.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        cont.add(headerFiltros(), BorderLayout.NORTH);
        cont.add(listaHistorial(), BorderLayout.CENTER);

        return cont;
    }

    // 🔎 FILTROS
    private JPanel headerFiltros() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filtros.setOpaque(false);

        txtBuscar = new JTextField(" Buscar...");
        txtBuscar.setPreferredSize(new Dimension(180, 32));
        txtBuscar.setForeground(getLabelCol());
        txtBuscar.setBackground(getPanelCol());
        txtBuscar.setBorder(BorderFactory.createLineBorder(getBorde(), 1, true));

        dateDesde = new JDateChooser();
        dateHasta = new JDateChooser();

        estilizarDateChooser(dateDesde);
        estilizarDateChooser(dateHasta);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLimpiar.setBackground(getPrimario());
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setBorderPainted(false);
        btnLimpiar.setPreferredSize(new Dimension(90, 32));

        btnLimpiar.addActionListener(e -> limpiarFiltros());

        filtros.add(txtBuscar);
        filtros.add(dateDesde);
        filtros.add(dateHasta);
        filtros.add(btnLimpiar);

        header.add(filtros, BorderLayout.EAST);
        return header;
    }

    // 🎨 estilo calendario
    private void estilizarDateChooser(JDateChooser date) {
        date.setPreferredSize(new Dimension(130, 32));
        date.setBorder(BorderFactory.createLineBorder(getBorde(), 1, true));
        date.setBackground(getPanelCol());

        JTextField editor = (JTextField) date.getDateEditor().getUiComponent();
        editor.setBorder(null);
        editor.setBackground(getPanelCol());
        editor.setForeground(getTextCol());
    }

    // 📜 LISTA
    private JScrollPane listaHistorial() {
        JPanel lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setOpaque(false);

        // 🔥 CARDS DE EJEMPLO
        lista.add(notificationCard(new Color(0xE74C3C),
                "Error en habitación",
                "La habitación 203 presenta fallas eléctricas"));

        lista.add(Box.createVerticalStrut(10));

        lista.add(notificationCard(new Color(0xF39C12),
                "Mantenimiento programado",
                "Habitación 305 será revisada"));

        lista.add(Box.createVerticalStrut(10));

        lista.add(notificationCard(new Color(0x27AE60),
                "Check-out completado",
                "Cliente Juan Pérez salió de la habitación 101"));

        lista.add(Box.createVerticalStrut(10));

        lista.add(notificationCard(new Color(0x3498DB),
                "Reserva creada",
                "Nueva reserva para habitación 202"));

        lista.add(Box.createVerticalStrut(10));

        lista.add(notificationCard(new Color(0x95A5A6),
                "Sistema",
                "Actualización realizada correctamente"));

        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        return scroll;
    }

    // 🧾 CARD
    private JPanel notificationCard(Color lineColor, String tituloTxt, String descTxt) {

        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(getPanelCol());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(getBorde(), 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JPanel line = new JPanel();
        line.setBackground(lineColor);
        line.setPreferredSize(new Dimension(5, 10));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel titulo = new JLabel(tituloTxt);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(getTextCol());

        JLabel desc = new JLabel(descTxt);
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        desc.setForeground(getLabelCol());

        content.add(titulo);
        content.add(Box.createVerticalStrut(4));
        content.add(desc);

        JLabel fecha = new JLabel("Hoy");
        fecha.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        fecha.setForeground(getLabelCol());

        card.add(line, BorderLayout.WEST);
        card.add(content, BorderLayout.CENTER);
        card.add(fecha, BorderLayout.EAST);

        return card;
    }

    // 🔄 limpiar filtros
    private void limpiarFiltros() {
        txtBuscar.setText("");
        dateDesde.setDate(null);
        dateHasta.setDate(null);
    }
}