package com.santaana.view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;

import com.toedter.calendar.JDateChooser;
import com.santaana.dao.ReservaDAO;
import com.santaana.util.ThemeManager;

public class ReservaFrame extends JFrame implements ThemeManager.ThemeListener {
    private String role;
    private int idUsuario;
    // Form fields
    private JTextField txtNombre, txtApellido, txtIdentificacion, txtCorreo, txtTelefono;
    private JDateChooser dcFechaEntrada, dcFechaSalida;
    private JComboBox<String> cmbHoraEntrada, cmbHoraSalida, cmbTipoEstadia;
    private JTextField txtAnticipo;
    private String habitacionSeleccionada;
    private JLabel lblTotalValor;
    private Color getPrimario() { return ThemeManager.getPrimary(); }
    private Color getFondo() { return ThemeManager.getBackground(); }
    private Color getPanelCol() { return ThemeManager.getPanelBackground(); }
    private Color getBorde() { return ThemeManager.getBorder(); }
    private Color getTextCol() { return ThemeManager.getTextPrimary(); }
    private Color getLabelCol() { return ThemeManager.getTextSecondary(); }

    public ReservaFrame(int idUsuario, String role, String welcomeMessage) {
        this.role = role;
        this.idUsuario = idUsuario;

        setTitle("Hotel Santa Ana — Reservas");
        setSize(1280, 800);
        setMinimumSize(new Dimension(1100, 720));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        ThemeManager.addListener(this);
        refreshUI();
        setVisible(true);
    }

    private void refreshUI() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());
        add(crearNavbar(), BorderLayout.NORTH);
        add(sidebar(), BorderLayout.WEST);
        add(crearContenido(), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    @Override
    public void onThemeChanged() {
        refreshUI();
    }

    private JPanel crearNavbar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(getPanelCol());
        navbar.setPreferredSize(new Dimension(0, 62));
        navbar.setBorder(new MatteBorder(0, 0, 1, 0, getBorde()));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 12));
        left.setOpaque(false);

        JLabel logo = new JLabel();
        try {
            java.net.URL logoUrl = getClass().getResource("/resources/logo.png");
            if (logoUrl != null) {
                ImageIcon icon = new ImageIcon(logoUrl);
                Image scaled = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                logo.setIcon(new ImageIcon(scaled));
            }
        } catch (Exception e) {}

        JLabel nombre = new JLabel("<html><b style='font-size:13px; color:" + (ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT ? "#1F2937" : "#F3F4F6") + "'>HOTEL SANTA ANA</b><br>"
                + "<span style='color:" + (ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT ? "#6B84A0" : "#94A3B8") + ";font-size:9px'>Sistema de gestion hotelera</span></html>");

        left.add(logo);
        left.add(nombre);



        JButton themeToggle = new JButton(ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT ? "🌙" : "☀️");
        themeToggle.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        themeToggle.setContentAreaFilled(false);
        themeToggle.setBorderPainted(false);
        themeToggle.setFocusPainted(false);
        themeToggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        themeToggle.addActionListener(e -> ThemeManager.toggleTheme());

        left.add(themeToggle);

        JPanel mid = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 14));
        mid.setOpaque(false);
        mid.add(crearBotonNavbar("+ Nueva reserva", getPrimario(), Color.WHITE));
        mid.add(crearBotonNavbar("$  Venta rapida", 
            ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT ? new Color(0xE8F1FD) : new Color(0x334155), 
            getPrimario()));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 16));
        right.setOpaque(false);
        right.add(userPanel());

        navbar.add(left, BorderLayout.WEST);
        navbar.add(mid, BorderLayout.CENTER);
        navbar.add(right, BorderLayout.EAST);
        return navbar;
    }

    private JButton crearBotonNavbar(String text, Color bg, Color fg) {
        JButton b = new JButton(text) {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.darker() : bg);
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
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(150, 34));
        return b;
    }

    private JPanel userPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        JLabel name = new JLabel("Usuario");
        name.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel rol = new JLabel(role);
        rol.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        rol.setForeground(getLabelCol());
        name.setForeground(getTextCol());
        p.add(name);
        p.add(rol);
        return p;
    }

    private JPanel sidebar() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(getPanelCol());
        side.setPreferredSize(new Dimension(190, 0));
        side.setBorder(new MatteBorder(0, 0, 0, 1, getBorde()));
        side.add(Box.createVerticalStrut(20));
        String[] items = { "Tablero", "Gestión de Habitaciones", "Reserva", "Historial", "Reporte" };
        for (int i = 0; i < items.length; i++) {
            side.add(sideBtn(items[i], i == 2));
            side.add(Box.createVerticalStrut(8));
        }
        side.add(Box.createVerticalGlue());
        return side;
    }

    private JPanel sideBtn(String text, boolean active) {
        JPanel p = new JPanel(new BorderLayout());
        p.setMaximumSize(new Dimension(180, 36));
        p.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 8));
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 12));
        lbl.setForeground(active ? getPrimario() : getLabelCol());
        if (active)
            p.setBackground(ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT ? new Color(0xE8F1FD) : new Color(0x2D3748));
        else
            p.setBackground(getPanelCol());
        p.add(lbl, BorderLayout.CENTER);
        return p;
    }

    private JScrollPane crearContenido() {
        JPanel cont = new JPanel();
        cont.setLayout(new BoxLayout(cont, BoxLayout.Y_AXIS));
        cont.setBackground(getFondo());
        cont.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel fila1 = new JPanel(new GridBagLayout());
        fila1.setOpaque(false);
        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.fill = GridBagConstraints.HORIZONTAL;
        gbc1.insets = new Insets(0, 0, 0, 20);
        gbc1.gridx = 0; gbc1.gridy = 0; gbc1.weightx = 0.35;
        fila1.add(crearPanelHuesped(), gbc1);
        gbc1.gridx = 1; gbc1.weightx = 0.65; gbc1.insets = new Insets(0, 0, 0, 0);
        fila1.add(crearPanelReserva(), gbc1);

        JPanel fila2 = new JPanel(new GridBagLayout());
        fila2.setOpaque(false);
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.insets = new Insets(0, 0, 0, 20);
        gbc2.gridx = 0; gbc2.gridy = 0; gbc2.weightx = 0.35;
        fila2.add(crearPanelPago(), gbc2);
        gbc2.gridx = 1; gbc2.weightx = 0.65; gbc2.insets = new Insets(0, 0, 0, 0);
        fila2.add(crearPanelHabitaciones(), gbc2);

        cont.add(fila1);
        cont.add(Box.createVerticalStrut(20));
        cont.add(fila2);
        cont.add(Box.createVerticalStrut(20));
        cont.add(crearPanelBotones());

        JScrollPane scroll = new JScrollPane(cont);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel tarjeta() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(getBorde());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(getPanelCol());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return p;
    }

    private JLabel titulo(String txt) {
        JLabel l = new JLabel(txt);
        l.setForeground(getPrimario());
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        return l;
    }

    private JPanel crearPanelHuesped() {
        JPanel p = tarjeta();
        p.add(titulo("Datos de huésped"));
        txtNombre = new JTextField(); txtNombre.setPreferredSize(new Dimension(0, 36));
        p.add(campoConField("Nombre", txtNombre, "Ej: Juan"));
        p.add(Box.createVerticalStrut(12));
        txtApellido = new JTextField(); txtApellido.setPreferredSize(new Dimension(0, 36));
        p.add(campoConField("Apellido", txtApellido, "Ej: Pérez"));
        p.add(Box.createVerticalStrut(12));
        txtIdentificacion = new JTextField(); txtIdentificacion.setPreferredSize(new Dimension(0, 36));
        p.add(campoConField("Identificación", txtIdentificacion, "Cédula o Pasaporte"));
        p.add(Box.createVerticalStrut(12));
        txtCorreo = new JTextField(); txtCorreo.setPreferredSize(new Dimension(0, 36));
        p.add(campoConField("Correo", txtCorreo, "correo@ejemplo.com"));
        p.add(Box.createVerticalStrut(12));
        txtTelefono = new JTextField(); txtTelefono.setPreferredSize(new Dimension(0, 36));
        p.add(campoConField("Teléfono", txtTelefono, "Ej: +57 ..."));
        return p;
    }

    private JPanel crearPanelReserva() {
        JPanel p = tarjeta();
        p.add(titulo("Datos de reserva"));
        JPanel grid = new JPanel(new GridLayout(0, 2, 15, 12));
        grid.setOpaque(false);
        dcFechaEntrada = new JDateChooser(); dcFechaEntrada.setDateFormatString("dd/MM/yyyy");
        grid.add(crearCajaFecha("Fecha de Entrada", dcFechaEntrada));
        cmbHoraEntrada = new JComboBox<>(new String[] { "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00" });
        grid.add(crearCajaCombo("Hora de Entrada", cmbHoraEntrada));
        dcFechaSalida = new JDateChooser(); dcFechaSalida.setDateFormatString("dd/MM/yyyy");
        grid.add(crearCajaFecha("Fecha de Salida", dcFechaSalida));
        cmbHoraSalida = new JComboBox<>(new String[] { "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00" });
        grid.add(crearCajaCombo("Hora de Salida", cmbHoraSalida));
        cmbTipoEstadia = new JComboBox<>(new String[] { "Por horas", "Media noche", "Noche completa", "Día completo" });
        grid.add(crearCajaCombo("Tipo de estadía", cmbTipoEstadia));
        p.add(grid);
        return p;
    }

    private JPanel crearCajaFecha(String label, JDateChooser dc) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 5));
        wrapper.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(getLabelCol());
        dc.setPreferredSize(new Dimension(0, 36));
        dc.setBackground(getPanelCol());
        JTextField tf = (JTextField) dc.getDateEditor().getUiComponent();
        tf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(getBorde(), 1, true), BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        tf.setBackground(getPanelCol());
        tf.setForeground(getTextCol());
        wrapper.add(lbl, BorderLayout.NORTH);
        wrapper.add(dc, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel crearCajaCombo(String label, JComboBox<String> combo) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 5));
        wrapper.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(getLabelCol());
        combo.setPreferredSize(new Dimension(0, 36));
        combo.setBackground(getPanelCol());
        combo.setForeground(getTextCol());
        combo.setBorder(BorderFactory.createLineBorder(getBorde(), 1, true));
        wrapper.add(lbl, BorderLayout.NORTH);
        wrapper.add(combo, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel crearPanelPago() {
        JPanel panel = tarjeta();
        panel.add(titulo("Pago"));
        panel.add(crearCajaCombo("Límite de hospedaje", new JComboBox<>(new String[] { "1 hora", "2 horas", "6 horas", "12 horas", "1 noche" })));
        panel.add(Box.createVerticalStrut(15));
        txtAnticipo = new JTextField("0"); txtAnticipo.setPreferredSize(new Dimension(0, 36));
        panel.add(campoConField("Anticipo", txtAnticipo, "$0"));
        panel.add(Box.createVerticalStrut(15));
        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        JLabel lblTotal = new JLabel("Total a pagar");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotal.setForeground(getTextCol());
        lblTotalValor = new JLabel("$0");
        lblTotalValor.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotalValor.setForeground(getPrimario());
        totalRow.add(lblTotal, BorderLayout.WEST);
        totalRow.add(lblTotalValor, BorderLayout.EAST);
        panel.add(totalRow);
        panel.add(Box.createVerticalStrut(15));
        JPanel mtdPago = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        mtdPago.setOpaque(false);
        String[] opts = { "Efectivo", "Transferencia", "Tarjeta" };
        ButtonGroup bg = new ButtonGroup();
        for (String opt : opts) {
            JRadioButton rb = new JRadioButton(opt);
            rb.setOpaque(false);
            rb.setForeground(getTextCol());
            rb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            if (opt.equals("Efectivo")) rb.setSelected(true);
            bg.add(rb);
            mtdPago.add(rb);
        }
        panel.add(mtdPago);
        return panel;
    }

    private JPanel crearPanelHabitaciones() {
        JPanel p = tarjeta();
        p.add(titulo("Seleccionar habitación"));
        JPanel grid = new JPanel(new GridLayout(1, 3, 12, 0));
        grid.setOpaque(false);
        grid.add(cardHabitacion("101"));
        grid.add(cardHabitacion("102"));
        grid.add(cardHabitacion("103"));
        p.add(grid);
        return p;
    }

    private JPanel cardHabitacion(String num) {
        JPanel c = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean selected = num.equals(habitacionSeleccionada);
                g2.setColor(selected ? getPrimario() : getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                if (selected) {
                    g2.setColor(getPrimario());
                    g2.setStroke(new BasicStroke(3));
                } else {
                    g2.setColor(getBorde());
                    g2.setStroke(new BasicStroke(1));
                }
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setOpaque(false);
        c.setBackground(getPanelCol());
        c.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JLabel n = new JLabel("Habitación " + num);
        n.setFont(new Font("Segoe UI", Font.BOLD, 15));
        n.setForeground(getPrimario());
        n.setAlignmentX(0.5f);
        JLabel disp = new JLabel("Disponible");
        disp.setForeground(new Color(0x27AE60));
        disp.setFont(new Font("Segoe UI", Font.BOLD, 11));
        disp.setAlignmentX(0.5f);
        JLabel info = new JLabel("<html><center>Individual<br>$70.000 / Noche</center></html>");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        info.setForeground(getLabelCol());
        info.setAlignmentX(0.5f);
        c.add(n); c.add(Box.createVerticalStrut(5)); c.add(disp); c.add(Box.createVerticalStrut(5)); c.add(info);
        c.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                habitacionSeleccionada = num;
                refreshUI();
            }
            public void mouseEntered(MouseEvent e) {
                if (!num.equals(habitacionSeleccionada))
                    c.setBackground(ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT ? new Color(0xF1F5F9) : new Color(0x334155));
                c.repaint();
            }
            public void mouseExited(MouseEvent e) {
                c.setBackground(getPanelCol());
                c.repaint();
            }
        });
        return c;
    }

    private JPanel crearPanelBotones() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        p.setOpaque(false);
        JButton btnCancela = new JButton("Cancelar");
        btnCancela.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCancela.setForeground(getTextCol());
        btnCancela.setContentAreaFilled(false);
        btnCancela.setBorder(BorderFactory.createLineBorder(getBorde(), 1, true));
        btnCancela.setPreferredSize(new Dimension(120, 38));
        btnCancela.addActionListener(e -> dispose());
        JButton btnReserva = new JButton("Confirmar Reserva") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnReserva.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnReserva.setBackground(getPrimario());
        btnReserva.setForeground(Color.WHITE);
        btnReserva.setContentAreaFilled(false);
        btnReserva.setBorderPainted(false);
        btnReserva.setPreferredSize(new Dimension(180, 38));
        btnReserva.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReserva.addActionListener(e -> confirmarReserva());
        p.add(btnCancela);
        p.add(btnReserva);
        return p;
    }

    private void confirmarReserva() {
        String nom = txtNombre.getText().trim();
        String ape = txtApellido.getText().trim();
        String doc = txtIdentificacion.getText().trim();
        String correo = txtCorreo.getText().trim();
        String tel = txtTelefono.getText().trim();
        if (nom.isEmpty() || doc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre e Identificación son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (habitacionSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una habitación.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        java.util.Date fe = dcFechaEntrada.getDate();
        java.util.Date fs = dcFechaSalida.getDate();
        if (fe == null || fs == null) {
            JOptionPane.showMessageDialog(this, "Seleccione fechas de entrada y salida.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String fechaEnt = new java.text.SimpleDateFormat("yyyy-MM-dd").format(fe);
        String fechaSal = new java.text.SimpleDateFormat("yyyy-MM-dd").format(fs);
        String horaEnt = (String) cmbHoraEntrada.getSelectedItem();
        String horaSal = (String) cmbHoraSalida.getSelectedItem();
        String tipo = (String) cmbTipoEstadia.getSelectedItem();
        double anticipo;
        try {
            anticipo = Double.parseDouble(txtAnticipo.getText().trim().replace("$", "").replace(",", ""));
        } catch (NumberFormatException ex) {
            anticipo = 0;
        }

        // Mapear habitación seleccionada a ID real
        com.santaana.dao.HabitacionDAO hdao = new com.santaana.dao.HabitacionDAO();
        int idHab = -1;
        for (com.santaana.model.Habitacion h : hdao.listarTodas()) {
            if (String.valueOf(h.getNumero()).equals(habitacionSeleccionada)) {
                idHab = h.getId();
                break;
            }
        }
        if (idHab == -1) {
            JOptionPane.showMessageDialog(this, "Habitación no encontrada en BD.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ReservaDAO rdao = new ReservaDAO();
        boolean ok = rdao.crear(idHab, idUsuario,
            nom + " " + ape, doc, tel, correo,
            fechaEnt, horaEnt, fechaSal, horaSal, tipo, anticipo);

        if (ok) {
            JOptionPane.showMessageDialog(this, "Reserva creada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            com.santaana.dao.HistorialDAO.registrar("Reserva", "Nueva reserva creada",
                "Reserva para " + nom + " " + ape + " en Hab " + habitacionSeleccionada,
                idUsuario, null, idHab, null);
            new com.santaana.view.MainFrame(role, idUsuario, nom + " " + ape).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al crear la reserva.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel campoConField(String label, JTextField tf, String placeholder) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 5));
        wrapper.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(getLabelCol());
        tf.setPreferredSize(new Dimension(0, 36));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBackground(getPanelCol());
        tf.setForeground(getTextCol());
        tf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(getBorde(), 1, true), BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        wrapper.add(lbl, BorderLayout.NORTH);
        wrapper.add(tf, BorderLayout.CENTER);
        return wrapper;
    }
}
