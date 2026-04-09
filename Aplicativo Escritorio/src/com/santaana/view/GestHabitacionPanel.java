package com.santaana.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;
import com.santaana.dao.HabitacionDAO;
import com.santaana.model.Habitacion;
import com.santaana.util.ThemeManager;

public class GestHabitacionPanel extends JPanel {
    private String userRole;
    private boolean isPlaceholderActive = true;
    private final String PLACEHOLDER = " Buscar habitación...";
    private final HabitacionDAO habitacionDAO = new HabitacionDAO();

    private Color getBorde()       { return ThemeManager.getBorder(); }
    private Color getPrimario()    { return ThemeManager.getPrimary(); }
    private Color getLabel()       { return ThemeManager.getTextSecondary(); }
    private Color getBackgroundCol() { return ThemeManager.getBackground(); }
    private Color getPanelCol()    { return ThemeManager.getPanelBackground(); }
    private Color getTextCol()     { return ThemeManager.getTextPrimary(); }

    public GestHabitacionPanel(String role) {
        this.userRole = role;
        setLayout(new BorderLayout());
        setBackground(getBackgroundCol());
        refreshUI();
    }

    public void refreshUI() {
        removeAll();
        add(crearNavbar(), BorderLayout.NORTH);
        add(centerPanel(), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel crearNavbar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(getPanelCol());
        navbar.setPreferredSize(new Dimension(0, 50));
        navbar.setBorder(new MatteBorder(0, 0, 1, 0, getBorde()));

        JLabel title = new JLabel("  GESTION DE HABITACIONES");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(getTextCol());
        navbar.add(title, BorderLayout.WEST);

        if (userRole.equalsIgnoreCase("Administrador")) {
            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
            actions.setOpaque(false);
            JButton btnNueva = crearBotonAccion("+ Nueva Habitación", getPrimario(), Color.WHITE);
            btnNueva.addActionListener(e -> mostrarDialogoNueva());
            actions.add(btnNueva);
            navbar.add(actions, BorderLayout.EAST);
        }

        return navbar;
    }

    private void mostrarDialogoNueva() {
        JTextField numero = new JTextField();
        JComboBox<String> tipo = new JComboBox<>(new String[]{"Simple", "Doble", "Suite"});
        JTextField precio = new JTextField();

        Object[] campos = {"Número:", numero, "Tipo:", tipo, "Precio/noche:", precio};
        int res = JOptionPane.showConfirmDialog(this, campos, "Nueva Habitación", JOptionPane.OK_CANCEL_OPTION);

        if (res == JOptionPane.OK_OPTION) {
            try {
                double p = Double.parseDouble(precio.getText().trim());
                boolean ok = habitacionDAO.agregar(numero.getText().trim(), (String) tipo.getSelectedItem(), p);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Habitación agregada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    refreshUI();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo agregar.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Precio inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JButton crearBotonAccion(String text, Color bg, Color fg) {
        JButton b = new JButton(text) {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(fg);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(160, 30));
        return b;
    }

    private JPanel centerPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        p.add(roomsArea(), BorderLayout.CENTER);
        return p;
    }

    private JPanel roomsArea() {
        JPanel area = new JPanel(new BorderLayout(0, 12));
        area.setOpaque(false);

        JPanel header = new JPanel(new BorderLayout(15, 0));
        header.setOpaque(false);

        JLabel title = new JLabel("Filtros y búsqueda");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(getTextCol());
        header.add(title, BorderLayout.WEST);
        header.add(searchBar(), BorderLayout.CENTER);
        area.add(header, BorderLayout.NORTH);

        List<Habitacion> habitaciones = habitacionDAO.listarTodas();

        JPanel grid = new JPanel(new GridLayout(0, 3, 14, 14));
        grid.setOpaque(false);

        if (habitaciones.isEmpty()) {
            JLabel vacio = new JLabel("No hay habitaciones registradas.");
            vacio.setForeground(getLabel());
            grid.add(vacio);
        } else {
            for (Habitacion h : habitaciones) {
                grid.add(roomCard(h));
            }
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        area.add(scroll, BorderLayout.CENTER);

        return area;
    }

    private JPanel searchBar() {
        final JTextField field = new JTextField(PLACEHOLDER);
        field.setPreferredSize(new Dimension(300, 32));
        field.setBackground(getPanelCol());
        field.setForeground(getLabel());
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getBorde(), 1, true),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (isPlaceholderActive) {
                    field.setText("");
                    field.setForeground(getTextCol());
                    isPlaceholderActive = false;
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(PLACEHOLDER);
                    field.setForeground(getLabel());
                    isPlaceholderActive = true;
                }
            }
        });

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapper.setOpaque(false);
        wrapper.add(field);
        return wrapper;
    }

    private JPanel roomCard(Habitacion h) {
        JPanel c = new JPanel() {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getPanelCol());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(getBorde());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        c.setOpaque(false);
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel numLbl = new JLabel("Habitación " + h.getNumero());
        numLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        numLbl.setForeground(getTextCol());
        numLbl.setAlignmentX(0.0f);

        Color colorEstado;
        switch (h.getEstado()) {
            case "Disponible":    colorEstado = new Color(0x27AE60); break;
            case "Ocupada":       colorEstado = new Color(0xE74C3C); break;
            case "Limpieza":      colorEstado = new Color(0x3A7BD5); break;
            case "Mantenimiento": colorEstado = new Color(0xE67E22); break;
            default:              colorEstado = new Color(0x95A5A6);
        }

        JLabel status = new JLabel(h.getEstado());
        status.setForeground(colorEstado);
        status.setFont(new Font("Segoe UI", Font.BOLD, 11));
        status.setAlignmentX(0.0f);

        JLabel info = new JLabel(String.format(
            "<html>%s<br>$%,.0f / Noche</html>", h.getTipo(), h.getPrecio()));
        info.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        info.setForeground(getLabel());
        info.setAlignmentX(0.0f);

        c.add(numLbl);
        c.add(Box.createVerticalStrut(6));
        c.add(status);
        c.add(Box.createVerticalStrut(12));
        c.add(info);

        if (h.getEstado().equals("Limpieza")) {
            JButton btnHabilitar = crearBotonAccion("✓ Habilitar", new Color(0x27AE60), Color.WHITE);
            btnHabilitar.setPreferredSize(new Dimension(120, 30));
            btnHabilitar.setMaximumSize(new Dimension(120, 30));
            btnHabilitar.setAlignmentX(0.0f);
            btnHabilitar.addActionListener(e -> {
                habitacionDAO.actualizarEstado(h.getId(), "Disponible");
                refreshUI();
            });
            c.add(Box.createVerticalStrut(18));
            c.add(btnHabilitar);
        }

        if (userRole.equalsIgnoreCase("Administrador")) {
            JButton btn = crearBotonAccion("Gestionar ›", getPrimario(), Color.WHITE);
            btn.setPreferredSize(new Dimension(120, 30));
            btn.setMaximumSize(new Dimension(120, 30));
            btn.setAlignmentX(0.0f);
            btn.addActionListener(e -> mostrarGestion(h));
            c.add(Box.createVerticalStrut(h.getEstado().equals("Limpieza") ? 8 : 18));
            c.add(btn);
        }

        return c;
    }

    private void mostrarGestion(Habitacion h) {
        JComboBox<String> cmbTipo   = new JComboBox<>(new String[]{"Simple","Doble","Suite"});
        cmbTipo.setSelectedItem(h.getTipo());

        JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"Disponible","Ocupada","Mantenimiento"});
        cmbEstado.setSelectedItem(h.getEstado());

        JTextField tfPrecio = new JTextField(String.valueOf((int) h.getPrecio()));

        Object[] campos = {
            "Habitación N°: " + h.getNumero(),
            " ",
            "Tipo:",    cmbTipo,
            "Estado:",  cmbEstado,
            "Precio / noche ($):", tfPrecio
        };

        int res = JOptionPane.showConfirmDialog(this, campos,
            "Gestionar habitación " + h.getNumero(),
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (res != JOptionPane.OK_OPTION) return;

        try {
            double nuevoPrecio = Double.parseDouble(tfPrecio.getText().trim());
            String nuevoTipo   = (String) cmbTipo.getSelectedItem();
            String nuevoEstado = (String) cmbEstado.getSelectedItem();

            habitacionDAO.actualizarDatos(h.getId(), nuevoTipo, nuevoPrecio);
            if (!nuevoEstado.equals(h.getEstado())) {
                habitacionDAO.actualizarEstado(h.getId(), nuevoEstado);
            }
            refreshUI();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Precio inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
