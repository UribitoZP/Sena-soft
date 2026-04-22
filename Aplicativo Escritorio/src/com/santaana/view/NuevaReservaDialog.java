package com.santaana.view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import com.toedter.calendar.JDateChooser;
import com.santaana.dao.HabitacionDAO;
import com.santaana.dao.HistorialDAO;
import com.santaana.dao.ReservaDAO;
import com.santaana.model.Habitacion;
import com.santaana.util.ThemeManager;

public class NuevaReservaDialog extends JDialog {

    private final HabitacionDAO habitacionDAO = new HabitacionDAO();
    private final ReservaDAO    reservaDAO    = new ReservaDAO();

    // Campos de huésped
    private JTextField campoDoc;
    private JTextField campoNombre;
    private JTextField campoCorreo;
    private JTextField campoTelefono;

    // Campos de reserva
    private JDateChooser fechaEntrada;
    private JSpinner     horaEntrada;
    private JDateChooser fechaSalida;
    private JSpinner     horaSalida;
    private JComboBox<String> tipoEstadia;

    // Pago
    private JTextField campoAnticipo;
    private JLabel     lblTotal;

    // Habitación seleccionada
    private Habitacion habitacionSeleccionada;
    private JPanel     panelHabitaciones;

    private int idUsuario;

    public NuevaReservaDialog(Window owner, int idUsuario) {
        super(owner, "Nueva Reserva", ModalityType.APPLICATION_MODAL);
        this.idUsuario = idUsuario;
        setSize(980, 700);
        setMinimumSize(new Dimension(880, 620));
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(ThemeManager.getBackground());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ThemeManager.getPanelBackground());
        header.setPreferredSize(new Dimension(0, 52));
        header.setBorder(new MatteBorder(0, 0, 1, 0, ThemeManager.getBorder()));
        JLabel title = new JLabel("  NUEVA RESERVA");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(ThemeManager.getTextPrimary());
        header.add(title, BorderLayout.WEST);

        root.add(header, BorderLayout.NORTH);
        root.add(crearContenido(), BorderLayout.CENTER);
        setContentPane(root);
    }

    private JScrollPane crearContenido() {
        JPanel cont = new JPanel();
        cont.setLayout(new BoxLayout(cont, BoxLayout.Y_AXIS));
        cont.setBackground(ThemeManager.getBackground());
        cont.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel fila1 = new JPanel(new GridBagLayout());
        fila1.setOpaque(false);
        GridBagConstraints g1 = new GridBagConstraints();
        g1.fill = GridBagConstraints.HORIZONTAL; g1.gridy = 0;
        g1.gridx = 0; g1.weightx = 0.35; g1.insets = new Insets(0, 0, 0, 20);
        fila1.add(crearPanelHuesped(), g1);
        g1.gridx = 1; g1.weightx = 0.65; g1.insets = new Insets(0, 0, 0, 0);
        fila1.add(crearPanelReserva(), g1);

        JPanel fila2 = new JPanel(new GridBagLayout());
        fila2.setOpaque(false);
        GridBagConstraints g2 = new GridBagConstraints();
        g2.fill = GridBagConstraints.BOTH; g2.gridy = 0;
        g2.gridx = 0; g2.weightx = 0.35; g2.insets = new Insets(0, 0, 0, 20);
        fila2.add(crearPanelPago(), g2);
        g2.gridx = 1; g2.weightx = 0.65; g2.insets = new Insets(0, 0, 0, 0);
        panelHabitaciones = crearPanelHabitaciones(null, null);
        fila2.add(panelHabitaciones, g2);

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

    // ── Panel huésped ────────────────────────────────────────────────────────
    private JPanel crearPanelHuesped() {
        JPanel p = tarjeta();
        p.add(titulo("Datos del huésped"));
        campoDoc      = textField("Cédula o Pasaporte");
        campoNombre   = textField("Ej: Juan Pérez");
        campoCorreo   = textField("correo@ejemplo.com");
        campoTelefono = textField("Ej: +57 300 000 0000");
        p.add(caja("Identificación *", campoDoc));
        p.add(Box.createVerticalStrut(12));
        p.add(caja("Nombre completo *", campoNombre));
        p.add(Box.createVerticalStrut(12));
        p.add(caja("Correo (opcional)", campoCorreo));
        p.add(Box.createVerticalStrut(12));
        p.add(caja("Teléfono (opcional)", campoTelefono));
        return p;
    }

    // ── Panel reserva ────────────────────────────────────────────────────────
    private JPanel crearPanelReserva() {
        JPanel p = tarjeta();
        p.add(titulo("Datos de la reserva"));

        fechaEntrada = dateChooser();
        horaEntrada  = timeSpinner(14, 0);
        fechaSalida  = dateChooser();
        horaSalida   = timeSpinner(12, 0);
        tipoEstadia  = new JComboBox<>(new String[]{"Noche completa","Día completo","Media noche","Por horas"});
        estilizarCombo(tipoEstadia);

        fechaEntrada.addPropertyChangeListener("date", e -> actualizarHabitaciones());
        fechaSalida.addPropertyChangeListener("date",  e -> actualizarHabitaciones());

        JPanel grid = new JPanel(new GridLayout(0, 2, 15, 12));
        grid.setOpaque(false);
        grid.add(caja("Fecha de entrada *", fechaEntrada));
        grid.add(caja("Hora de entrada",    horaEntrada));
        grid.add(caja("Fecha de salida *",  fechaSalida));
        grid.add(caja("Hora de salida",     horaSalida));
        grid.add(caja("Tipo de estadía",    tipoEstadia));

        p.add(grid);
        return p;
    }

    // ── Panel pago ───────────────────────────────────────────────────────────
    private JPanel crearPanelPago() {
        JPanel p = tarjeta();
        p.add(titulo("Pago"));

        campoAnticipo = textField("$0");
        p.add(caja("Anticipo", campoAnticipo));
        p.add(Box.createVerticalStrut(15));

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        JLabel lTotal = new JLabel("Total estimado");
        lTotal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lTotal.setForeground(ThemeManager.getTextPrimary());
        lblTotal = new JLabel("—");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotal.setForeground(ThemeManager.getPrimary());
        totalRow.add(lTotal,   BorderLayout.WEST);
        totalRow.add(lblTotal, BorderLayout.EAST);
        totalRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        p.add(totalRow);
        p.add(Box.createVerticalStrut(15));

        JPanel mtd = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        mtd.setOpaque(false);
        ButtonGroup bg = new ButtonGroup();
        for (String opt : new String[]{"Efectivo","Transferencia","Tarjeta"}) {
            JRadioButton rb = new JRadioButton(opt);
            rb.setOpaque(false);
            rb.setForeground(ThemeManager.getTextPrimary());
            rb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            if (opt.equals("Efectivo")) rb.setSelected(true);
            bg.add(rb);
            mtd.add(rb);
        }
        mtd.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        p.add(mtd);
        return p;
    }

    // ── Panel habitaciones ───────────────────────────────────────────────────
    private JPanel crearPanelHabitaciones(String desde, String hasta) {
        JPanel p = tarjeta();
        p.add(titulo("Seleccionar habitación"));

        List<Habitacion> lista = (desde != null && hasta != null)
            ? habitacionDAO.listarDisponiblesEnFechas(desde, hasta)
            : habitacionDAO.listarDisponibles();

        if (lista.isEmpty()) {
            JLabel aviso = new JLabel(desde != null
                ? "No hay habitaciones disponibles para esas fechas."
                : "No hay habitaciones disponibles.");
            aviso.setForeground(ThemeManager.getTextSecondary());
            aviso.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            p.add(aviso);
            return p;
        }

        JPanel grid = new JPanel(new GridLayout(0, 3, 12, 12));
        grid.setOpaque(false);
        for (Habitacion h : lista) {
            grid.add(cardHabitacion(h, p));
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        p.add(scroll);
        return p;
    }

    private JPanel cardHabitacion(Habitacion h, JPanel contenedor) {
        JPanel c = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                boolean sel = habitacionSeleccionada != null
                    && habitacionSeleccionada.getId() == h.getId();
                g2.setColor(sel ? ThemeManager.getPrimary() : ThemeManager.getBorder());
                g2.setStroke(new BasicStroke(sel ? 2f : 1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
            }
        };
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setOpaque(false);
        c.setBackground(ThemeManager.getPanelBackground());
        c.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel nLbl = new JLabel("Hab. " + h.getNumero());
        nLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nLbl.setForeground(ThemeManager.getPrimary());
        nLbl.setAlignmentX(0.5f);

        JLabel tipoLbl = new JLabel(h.getTipo());
        tipoLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tipoLbl.setForeground(ThemeManager.getTextSecondary());
        tipoLbl.setAlignmentX(0.5f);

        JLabel precioLbl = new JLabel(String.format("$%,.0f / noche", h.getPrecio()));
        precioLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        precioLbl.setForeground(ThemeManager.getTextPrimary());
        precioLbl.setAlignmentX(0.5f);

        c.add(nLbl);
        c.add(Box.createVerticalStrut(4));
        c.add(tipoLbl);
        c.add(Box.createVerticalStrut(6));
        c.add(precioLbl);

        c.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                habitacionSeleccionada = h;
                actualizarTotal();
                contenedor.repaint();
            }
            public void mouseEntered(MouseEvent e) {
                c.setBackground(ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT
                    ? new Color(0xF1F5F9) : new Color(0x334155));
                c.repaint();
            }
            public void mouseExited(MouseEvent e) {
                c.setBackground(ThemeManager.getPanelBackground());
                c.repaint();
            }
        });
        return c;
    }

    // ── Botones ───────────────────────────────────────────────────────────────
    private JPanel crearPanelBotones() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        p.setOpaque(false);

        JButton btnCancela = new JButton("Cancelar");
        btnCancela.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCancela.setForeground(ThemeManager.getTextPrimary());
        btnCancela.setContentAreaFilled(false);
        btnCancela.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder(), 1, true));
        btnCancela.setPreferredSize(new Dimension(120, 38));
        btnCancela.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancela.addActionListener(e -> dispose());

        JButton btnConfirmar = new JButton("Confirmar Reserva") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getPrimary());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setContentAreaFilled(false);
        btnConfirmar.setBorderPainted(false);
        btnConfirmar.setPreferredSize(new Dimension(190, 38));
        btnConfirmar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnConfirmar.addActionListener(e -> confirmarReserva());

        p.add(btnCancela);
        p.add(btnConfirmar);
        return p;
    }

    // ── Lógica ────────────────────────────────────────────────────────────────
    private void actualizarHabitaciones() {
        String desde = formatearFecha(fechaEntrada.getDate());
        String hasta = formatearFecha(fechaSalida.getDate());
        if (desde == null || hasta == null) return;

        Container parent = panelHabitaciones.getParent();
        if (parent == null) return;
        GridBagConstraints gbc = ((GridBagLayout) parent.getLayout())
            .getConstraints(panelHabitaciones);
        parent.remove(panelHabitaciones);
        habitacionSeleccionada = null;
        panelHabitaciones = crearPanelHabitaciones(desde, hasta);
        parent.add(panelHabitaciones, gbc);
        parent.revalidate();
        parent.repaint();
        actualizarTotal();
    }

    private void actualizarTotal() {
        if (habitacionSeleccionada == null) { lblTotal.setText("—"); return; }
        Date dE = fechaEntrada.getDate();
        Date dS = fechaSalida.getDate();
        if (dE != null && dS != null && dS.after(dE)) {
            long dias = (dS.getTime() - dE.getTime()) / (1000 * 60 * 60 * 24);
            lblTotal.setText(String.format("$%,.0f", dias * habitacionSeleccionada.getPrecio()));
        } else {
            lblTotal.setText(String.format("$%,.0f", habitacionSeleccionada.getPrecio()));
        }
    }

    private void confirmarReserva() {
        String doc    = campoDoc.getText().trim();
        String nombre = campoNombre.getText().trim();
        String desde  = formatearFecha(fechaEntrada.getDate());
        String hasta  = formatearFecha(fechaSalida.getDate());

        if (doc.isEmpty() || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Identificación y nombre son obligatorios.",
                "Campos requeridos", JOptionPane.WARNING_MESSAGE); return;
        }
        if (desde == null || hasta == null) {
            JOptionPane.showMessageDialog(this, "Seleccione fecha de entrada y salida.",
                "Fechas requeridas", JOptionPane.WARNING_MESSAGE); return;
        }
        if (!fechaSalida.getDate().after(fechaEntrada.getDate())) {
            JOptionPane.showMessageDialog(this, "La fecha de salida debe ser posterior a la de entrada.",
                "Fecha inválida", JOptionPane.WARNING_MESSAGE); return;
        }
        if (habitacionSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una habitación.",
                "Habitación requerida", JOptionPane.WARNING_MESSAGE); return;
        }

        boolean ok = reservaDAO.crear(
            habitacionSeleccionada.getId(), idUsuario, nombre, doc, desde, hasta);

        if (ok) {
            habitacionDAO.actualizarEstado(habitacionSeleccionada.getId(), "Ocupada");
            HistorialDAO.registrar("Reserva", "Reserva creada",
                "Nueva reserva de " + nombre + " en hab. " + habitacionSeleccionada.getNumero()
                + " (" + desde + " al " + hasta + ")");
            JOptionPane.showMessageDialog(this, "Reserva creada correctamente.",
                "Reserva confirmada", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo guardar la reserva. Intente de nuevo.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Utilidades UI ─────────────────────────────────────────────────────────
    private JPanel tarjeta() {
        JPanel p = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(ThemeManager.getBorder());
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
            }
        };
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(ThemeManager.getPanelBackground());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return p;
    }

    private JLabel titulo(String txt) {
        JLabel l = new JLabel(txt);
        l.setForeground(ThemeManager.getPrimary());
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        return l;
    }

    private JTextField textField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setPreferredSize(new Dimension(0, 36));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBackground(ThemeManager.getPanelBackground());
        tf.setForeground(ThemeManager.getTextPrimary());
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorder(), 1, true),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        return tf;
    }

    private JDateChooser dateChooser() {
        JDateChooser dc = new JDateChooser();
        dc.setDateFormatString("dd/MM/yyyy");
        dc.setPreferredSize(new Dimension(0, 36));
        dc.setBackground(ThemeManager.getPanelBackground());
        dc.setForeground(ThemeManager.getTextPrimary());
        JTextField tf = (JTextField) dc.getDateEditor().getUiComponent();
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorder(), 1, true),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        tf.setBackground(ThemeManager.getPanelBackground());
        tf.setForeground(ThemeManager.getTextPrimary());
        return dc;
    }

    private void estilizarCombo(JComboBox<?> combo) {
        combo.setPreferredSize(new Dimension(0, 36));
        combo.setBackground(ThemeManager.getPanelBackground());
        combo.setForeground(ThemeManager.getTextPrimary());
        combo.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder(), 1, true));
    }

    private JPanel caja(String label, JComponent campo) {
        JPanel w = new JPanel(new BorderLayout(0, 5));
        w.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(ThemeManager.getTextSecondary());
        w.add(lbl,   BorderLayout.NORTH);
        w.add(campo, BorderLayout.CENTER);
        return w;
    }

    private JSpinner timeSpinner(int hora, int minuto) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hora);
        cal.set(Calendar.MINUTE, minuto);
        cal.set(Calendar.SECOND, 0);
        SpinnerDateModel model = new SpinnerDateModel(
            cal.getTime(), null, null, Calendar.MINUTE);
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "HH:mm");
        spinner.setEditor(editor);
        spinner.setPreferredSize(new Dimension(0, 36));
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        spinner.setBackground(ThemeManager.getPanelBackground());
        spinner.setForeground(ThemeManager.getTextPrimary());
        spinner.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder(), 1, true));
        editor.getTextField().setBackground(ThemeManager.getPanelBackground());
        editor.getTextField().setForeground(ThemeManager.getTextPrimary());
        editor.getTextField().setHorizontalAlignment(JTextField.CENTER);
        return spinner;
    }

    private String formatearFecha(Date d) {
        if (d == null) return null;
        return new SimpleDateFormat("yyyy-MM-dd").format(d);
    }
}
