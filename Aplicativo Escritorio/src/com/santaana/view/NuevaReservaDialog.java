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
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import com.toedter.calendar.JDateChooser;
import com.santaana.dao.HabitacionDAO;
import com.santaana.dao.HistorialDAO;
import com.santaana.dao.ReservaDAO;
import com.santaana.model.Habitacion;
import com.santaana.util.DateUtil;
import com.santaana.util.ThemeManager;
import com.santaana.util.EmailService;

public class NuevaReservaDialog extends JDialog {

    private final HabitacionDAO habitacionDAO = new HabitacionDAO();
    private final ReservaDAO reservaDAO = new ReservaDAO();

    // Campos de huésped
    private JTextField campoDoc;
    private JTextField campoNombre;
    private JTextField campoCorreo;
    private JTextField campoTelefono;

    // Campos de reserva
    private JDateChooser fechaEntrada;
    private JSpinner horaEntrada;
    private JDateChooser fechaSalida;
    private JSpinner horaSalida;

    // Tipo de estadía (interno)
    private String tipoEstadiaSeleccionado = "Noche";
    private JButton[] botonesPreset;
    private JLabel infoEstadia;
    private JPanel filaSalida; // para habilitar/deshabilitar

    // Pago
    private JTextField campoAnticipo;
    private JLabel lblTotal;
    private String metodoSeleccionado = "Efectivo";

    // Habitación seleccionada
    private List<Habitacion> habitacionesSeleccionadas = new java.util.ArrayList<>();
    private JPanel panelAcompanantes;
    private JPanel panelHabitaciones;

    private int idUsuario;

    public NuevaReservaDialog(Window owner, int idUsuario) {
        super(owner, "Nueva Reserva", ModalityType.APPLICATION_MODAL);
        this.idUsuario = idUsuario;
        setSize(980, 720);
        setMinimumSize(new Dimension(880, 640));
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

        // Aplicar preset por defecto después de construir la UI
        SwingUtilities.invokeLater(() -> setPreset("Noche"));
    }

    private JScrollPane crearContenido() {
        JPanel cont = new JPanel();
        cont.setLayout(new BoxLayout(cont, BoxLayout.Y_AXIS));
        cont.setBackground(ThemeManager.getBackground());
        cont.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel fila1 = new JPanel(new GridBagLayout());
        fila1.setOpaque(false);

        GridBagConstraints g1 = new GridBagConstraints();
        g1.fill = GridBagConstraints.HORIZONTAL;
        g1.gridy = 0;
        g1.gridx = 0;
        g1.weightx = 0.35;
        g1.insets = new Insets(0, 0, 0, 20);

        fila1.add(crearPanelHuesped(), g1);

        g1.gridx = 1;
        g1.weightx = 0.65;
        g1.insets = new Insets(0, 0, 0, 0);

        fila1.add(crearPanelReserva(), g1);

        JPanel fila2 = new JPanel(new GridBagLayout());
        fila2.setOpaque(false);

        GridBagConstraints g2 = new GridBagConstraints();
        g2.fill = GridBagConstraints.BOTH;
        g2.gridy = 0;
        g2.gridx = 0;
        g2.weightx = 0.35;
        g2.insets = new Insets(0, 0, 0, 20);

        fila2.add(crearPanelPago(), g2);

        g2.gridx = 1;
        g2.weightx = 0.65;
        g2.insets = new Insets(0, 0, 0, 0);

        panelHabitaciones = crearPanelHabitaciones(null, null);
        fila2.add(panelHabitaciones, g2);

        cont.add(fila1);
        cont.add(Box.createVerticalStrut(20));

        cont.add(fila2);

        // PANEL ACOMPAÑANTES
        cont.add(Box.createVerticalStrut(20));

        panelAcompanantes = crearPanelAcompanantes();
        panelAcompanantes.setVisible(false);

        cont.add(panelAcompanantes);

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
        campoDoc = textField("Cédula o Pasaporte");
        campoNombre = textField("Ej: Juan Pérez");
        campoCorreo = textField("correo@ejemplo.com");
        campoTelefono = textField("Ej: +57 300 000 0000");

        // Autocompletado al digitar o perder el foco
        campoDoc.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                autocompletarCliente();
            }
        });
        campoDoc.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { verificar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { verificar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { verificar(); }
            private void verificar() {
               if (campoDoc.hasFocus()) {
                    SwingUtilities.invokeLater(() -> {
                        String doc = campoDoc.getText().trim();
                        if (doc.length() >= 5) {
                            autocompletarCliente();
                        }
                    });
                }
            }
        });

        setInputFilter(campoDoc, "[0-9]*");
        setInputFilter(campoNombre, "[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]*");
        setInputFilter(campoTelefono, "[0-9]*");

        p.add(caja("Identificación *", campoDoc));
        p.add(Box.createVerticalStrut(12));
        p.add(caja("Nombre completo *", campoNombre));
        p.add(Box.createVerticalStrut(12));
        p.add(caja("Correo (opcional)", campoCorreo));
        p.add(Box.createVerticalStrut(12));
        p.add(caja("Teléfono *", campoTelefono));
        return p;
    }

    private void autocompletarCliente() {
        String doc = campoDoc.getText().trim();
        if (doc.isEmpty()) return;
        com.santaana.dao.ClienteDAO clienteDAO = new com.santaana.dao.ClienteDAO();
        com.santaana.model.Cliente cliente = clienteDAO.buscarPorDocumento(doc);
        if (cliente != null) {
            SwingUtilities.invokeLater(() -> {
                campoNombre.setText(cliente.getNombre());
                campoTelefono.setText(cliente.getTelefono());
                campoCorreo.setText(cliente.getCorreo());
            });
        }
    }
    private JPanel crearPanelAcompanantes() {
        JPanel p = tarjeta();

        p.add(titulo("Acompañante"));

        JTextField campoNombreAcomp = textField("Nombre del acompañante");
        JTextField campoDocAcomp = textField("Documento");

        p.add(caja("Nombre completo", campoNombreAcomp));
        p.add(Box.createVerticalStrut(12));
        p.add(caja("Documento", campoDocAcomp));

        return p;
    }

    // ── Panel reserva ────────────────────────────────────────────────────────
    private JPanel crearPanelReserva() {
        JPanel p = tarjeta();
        p.add(titulo("Datos de la reserva"));

        // Botones preset de tipo de estadía
        p.add(crearBotonesPreset());
        p.add(Box.createVerticalStrut(14));

        // Fila entrada
        fechaEntrada = dateChooser();
        fechaEntrada.setMinSelectableDate(new Date());
        horaEntrada = timeSpinner(12, 0);
        fechaEntrada.addPropertyChangeListener("date", e -> {
            actualizarHabitaciones();
            actualizarTotal();
            actualizarInfoEstadia();
        });
        ((JSpinner.DateEditor) horaEntrada.getEditor()).getTextField()
                .getDocument().addDocumentListener(new SimpleDocListener(() -> {
                    actualizarHabitaciones();
                    actualizarTotal();
                    actualizarInfoEstadia();
                }));

        JPanel filaEntrada = new JPanel(new GridLayout(1, 2, 15, 0));
        filaEntrada.setOpaque(false);
        filaEntrada.add(caja("Fecha de entrada *", fechaEntrada));
        filaEntrada.add(caja("Hora de entrada", horaEntrada));
        p.add(filaEntrada);
        p.add(Box.createVerticalStrut(12));

        // Fila salida (se deshabilita para Indefinido)
        fechaSalida = dateChooser();
        horaSalida = timeSpinner(12, 0);
        fechaSalida.addPropertyChangeListener("date", e -> {
            actualizarHabitaciones();
            actualizarTotal();
            actualizarInfoEstadia();
        });
        ((JSpinner.DateEditor) horaSalida.getEditor()).getTextField()
                .getDocument().addDocumentListener(new SimpleDocListener(() -> {
                    actualizarHabitaciones();
                    actualizarTotal();
                    actualizarInfoEstadia();
                }));

        filaSalida = new JPanel(new GridLayout(1, 2, 15, 0));
        filaSalida.setOpaque(false);
        filaSalida.add(caja("Fecha de salida *", fechaSalida));
        filaSalida.add(caja("Hora de salida", horaSalida));
        p.add(filaSalida);
        p.add(Box.createVerticalStrut(10));

        // Etiqueta informativa
        infoEstadia = new JLabel(" ");
        infoEstadia.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        infoEstadia.setForeground(ThemeManager.getTextSecondary());
        p.add(infoEstadia);

        return p;
    }

    private JPanel crearBotonesPreset() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 6));
        wrapper.setOpaque(false);

        JLabel lbl = new JLabel("Tipo de estadía");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(ThemeManager.getTextSecondary());
        wrapper.add(lbl, BorderLayout.NORTH);

        JPanel btns = new JPanel(new GridLayout(1, 2, 6, 0));
        btns.setOpaque(false);

        String[] tipos = { "Noche", "Indefinido" };
        String[] labels = { "Una Noche", "Indefinido" };
        String[] subtitles = { "Entrada 12h → Salida 12h", "Salida sin determinar" };

        botonesPreset = new JButton[2];
        for (int i = 0; i < 2; i++) {
            final String tipo = tipos[i];
            final String label = labels[i];
            final String sub = subtitles[i];
            JButton btn = new JButton() {
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    boolean sel = tipo.equals(tipoEstadiaSeleccionado);
                    if (sel) {
                        g2.setColor(ThemeManager.getPrimary());
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    } else {
                        g2.setColor(ThemeManager.getPanelBackground());
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                        g2.setColor(ThemeManager.getBorder());
                        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                    }
                    // label
                    boolean isDark = ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK;
                    g2.setColor(sel ? Color.WHITE : ThemeManager.getTextPrimary());
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    FontMetrics fm = g2.getFontMetrics();
                    int lx = (getWidth() - fm.stringWidth(label)) / 2;
                    g2.drawString(label, lx, getHeight() / 2 - 2);
                    // subtitle
                    g2.setColor(sel ? new Color(255, 255, 255, 190) : ThemeManager.getTextSecondary());
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                    FontMetrics fm2 = g2.getFontMetrics();
                    int sx = (getWidth() - fm2.stringWidth(sub)) / 2;
                    g2.drawString(sub, sx, getHeight() / 2 + 11);
                    g2.dispose();
                }
            };
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(0, 52));
            btn.addActionListener(e -> setPreset(tipo));
            botonesPreset[i] = btn;
            btns.add(btn);
        }
        wrapper.add(btns, BorderLayout.CENTER);
        return wrapper;
    }

    // ── Panel pago ───────────────────────────────────────────────────────────
    private JPanel crearPanelPago() {
        JPanel p = tarjeta();
        p.add(titulo("Pago"));

        // Método de pago — botones toggle con tamaño fijo, sin estirarse
        String[] metodos = { "Efectivo", "Transferencia", "Tarjeta" };
        JButton[] btnsMetodo = new JButton[3];
        JPanel metodosPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        metodosPanel.setOpaque(false);
        metodosPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        for (int i = 0; i < metodos.length; i++) {
            final String met = metodos[i];
            JButton btn = new JButton() {
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    boolean sel = met.equals(metodoSeleccionado);
                    g2.setColor(sel ? ThemeManager.getPrimary() : ThemeManager.getPanelBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    if (!sel) {
                        g2.setColor(ThemeManager.getBorder());
                        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                    }
                    g2.setColor(sel ? Color.WHITE : ThemeManager.getTextPrimary());
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(met, (getWidth() - fm.stringWidth(met)) / 2,
                            (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                    g2.dispose();
                }
            };
            btn.setPreferredSize(new Dimension(100, 32));
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> {
                metodoSeleccionado = met;
                for (JButton b : btnsMetodo) b.repaint();
            });
            btnsMetodo[i] = btn;
            metodosPanel.add(btn);
        }
        p.add(caja("Método de pago", metodosPanel));
        p.add(Box.createVerticalStrut(14));

        // Campo anticipo — ancho fijo, no se estira
        JPanel anticipoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        anticipoRow.setOpaque(false);
        anticipoRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        JLabel prefijo = new JLabel(" $ ");
        prefijo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        prefijo.setForeground(ThemeManager.getTextPrimary());
        prefijo.setOpaque(true);
        prefijo.setBackground(ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT
                ? new Color(0xF1F5F9) : new Color(0x2D3748));
        prefijo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 0, ThemeManager.getBorder()),
                BorderFactory.createEmptyBorder(0, 8, 0, 8)));
        prefijo.setPreferredSize(new Dimension(34, 36));
        campoAnticipo = new JTextField("0");
        campoAnticipo.setPreferredSize(new Dimension(150, 36));
        campoAnticipo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campoAnticipo.setBackground(ThemeManager.getPanelBackground());
        campoAnticipo.setForeground(ThemeManager.getTextPrimary());
        campoAnticipo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 1, ThemeManager.getBorder()),
                BorderFactory.createEmptyBorder(0, 6, 0, 10)));
        anticipoRow.add(prefijo);
        anticipoRow.add(campoAnticipo);
        p.add(caja("Anticipo recibido", anticipoRow));
        p.add(Box.createVerticalStrut(16));

        // Separador
        JSeparator sep = new JSeparator();
        sep.setForeground(ThemeManager.getBorder());
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        p.add(sep);
        p.add(Box.createVerticalStrut(14));

        // Total en caja destacada
        JPanel totalBox = new JPanel(new BorderLayout(0, 4)) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT
                        ? new Color(0xEBF3FF) : new Color(0x1E3A5F));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        totalBox.setOpaque(false);
        totalBox.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        totalBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        JLabel lTotal = new JLabel("Total estimado");
        lTotal.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lTotal.setForeground(ThemeManager.getTextSecondary());
        lblTotal = new JLabel("—");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTotal.setForeground(ThemeManager.getPrimary());
        totalBox.add(lTotal, BorderLayout.NORTH);
        totalBox.add(lblTotal, BorderLayout.CENTER);
        p.add(totalBox);

        return p;
    }

    // ── Panel habitaciones ───────────────────────────────────────────────────
    private JPanel crearPanelHabitaciones(String desdeDateTime, String hastaDateTime) {
        JPanel p = tarjeta();
        p.add(titulo("Seleccionar habitación"));

        List<Habitacion> lista = (desdeDateTime != null && hastaDateTime != null)
                ? habitacionDAO.listarDisponiblesEnFechas(desdeDateTime, hastaDateTime)
                : habitacionDAO.listarDisponibles();

        if (lista.isEmpty()) {
            JLabel aviso = new JLabel(desdeDateTime != null
                    ? "No hay habitaciones disponibles para ese horario."
                    : "No hay habitaciones disponibles.", SwingConstants.CENTER);
            aviso.setForeground(ThemeManager.getTextSecondary());
            aviso.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            p.add(aviso);
            return p;
        }

        // Tipos únicos para chips de filtro
        java.util.List<String> tipos = new java.util.ArrayList<>();
        tipos.add("Todas");
        for (Habitacion h : lista)
            if (!tipos.contains(h.getTipo())) tipos.add(h.getTipo());

        // Contenedor del grid (se reconstruye al filtrar)
        JPanel gridContainer = new JPanel(new BorderLayout());
        gridContainer.setOpaque(false);

        String[] filtroActivo = { "Todas" };

        // Chips de filtro
        JPanel filtroRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        filtroRow.setOpaque(false);
        filtroRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        JButton[] filtrosBtns = new JButton[tipos.size()];

        for (int i = 0; i < tipos.size(); i++) {
            final String tipo = tipos.get(i);
            JButton btn = new JButton() {
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    boolean sel = tipo.equals(filtroActivo[0]);
                    g2.setColor(sel ? ThemeManager.getPrimary() : ThemeManager.getPanelBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                    if (!sel) {
                        g2.setColor(ThemeManager.getBorder());
                        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
                    }
                    g2.setColor(sel ? Color.WHITE : ThemeManager.getTextSecondary());
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(tipo, (getWidth() - fm.stringWidth(tipo)) / 2,
                            (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                    g2.dispose();
                }
            };
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(btn.getFontMetrics(new Font("Segoe UI", Font.PLAIN, 11)).stringWidth(tipo) + 22, 26));
            btn.addActionListener(e -> {
                filtroActivo[0] = tipo;
                reconstruirGridHabitaciones(gridContainer, lista, filtroActivo[0], p);
                for (JButton b : filtrosBtns) b.repaint();
            });
            filtrosBtns[i] = btn;
            filtroRow.add(btn);
        }

        p.add(filtroRow);
        p.add(Box.createVerticalStrut(10));
        reconstruirGridHabitaciones(gridContainer, lista, filtroActivo[0], p);
        p.add(gridContainer);
        return p;
    }

    private void reconstruirGridHabitaciones(JPanel container, List<Habitacion> lista, String filtro, JPanel contenedorPadre) {
        JPanel grid = new JPanel(new GridLayout(0, 2, 10, 10));
        grid.setOpaque(false);
        for (Habitacion h : lista) {
            if (filtro.equals("Todas") || filtro.equals(h.getTipo()))
                grid.add(cardHabitacion(h, contenedorPadre));
        }
        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        scroll.setPreferredSize(new Dimension(0, 260));
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
        container.removeAll();
        container.add(scroll, BorderLayout.CENTER);
        container.revalidate();
        container.repaint();
    }

    private JPanel cardHabitacion(Habitacion h, JPanel contenedor) {
        JPanel c = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                boolean sel = habitacionesSeleccionadas.stream().anyMatch(hab -> hab.getId() == h.getId());
                g2.setColor(sel ? ThemeManager.getPrimary() : ThemeManager.getBorder());
                g2.setStroke(new BasicStroke(sel ? 2f : 1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
            }
        };
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setOpaque(false);
        c.setBackground(ThemeManager.getPanelBackground());
        c.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        c.setPreferredSize(new Dimension(0, 110));
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        String tipo = h.getTipo().toLowerCase();
        String emoji = tipo.contains("suite") ? "👑" : tipo.contains("doble") || tipo.contains("matrimonial") || tipo.contains("familiar") ? "🛏" : "🛏";

        JLabel emojiLbl = new JLabel(emoji, SwingConstants.CENTER);
        emojiLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        emojiLbl.setAlignmentX(0.5f);

        JLabel nLbl = new JLabel("Hab. " + h.getNumero(), SwingConstants.CENTER);
        nLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        nLbl.setForeground(ThemeManager.getPrimary());
        nLbl.setAlignmentX(0.5f);

        JLabel tipoLbl = new JLabel(h.getTipo(), SwingConstants.CENTER);
        tipoLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tipoLbl.setForeground(ThemeManager.getTextSecondary());
        tipoLbl.setAlignmentX(0.5f);

        JLabel precioLbl = new JLabel(String.format("$%,.0f / noche", h.getPrecio()), SwingConstants.CENTER);
        precioLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        precioLbl.setForeground(ThemeManager.getTextPrimary());
        precioLbl.setAlignmentX(0.5f);

        c.add(emojiLbl);
        c.add(Box.createVerticalStrut(6));
        c.add(nLbl);
        c.add(Box.createVerticalStrut(2));
        c.add(tipoLbl);
        c.add(Box.createVerticalStrut(8));
        c.add(precioLbl);

        c.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {

                boolean yaSeleccionada = habitacionesSeleccionadas.stream()
                        .anyMatch(hab -> hab.getId() == h.getId());

                if (yaSeleccionada) {

                    habitacionesSeleccionadas.removeIf(hab -> hab.getId() == h.getId());

                } else {

                    habitacionesSeleccionadas.add(h);

                }

                actualizarTotal();
                panelAcompanantes.setVisible(!habitacionesSeleccionadas.isEmpty());
                contenedor.repaint();
                panelAcompanantes.revalidate();
                panelAcompanantes.repaint();
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

    // ── Lógica de presets ─────────────────────────────────────────────────────
    private void setPreset(String tipo) {
        tipoEstadiaSeleccionado = tipo;
        Calendar cal = Calendar.getInstance();
        Date hoy = new Date();

        switch (tipo) {
            case "Noche": {
                fechaEntrada.setDate(hoy);
                setHoraSpinner(horaEntrada, 12, 0);
                Calendar manana = Calendar.getInstance();
                manana.add(Calendar.DAY_OF_MONTH, 1);
                fechaSalida.setDate(manana.getTime());
                setHoraSpinner(horaSalida, 12, 0);
                setSalidaEnabled(true);
                break;
            }
            
            case "Indefinido": {
                fechaEntrada.setDate(hoy);
                setHoraSpinner(horaEntrada, cal.get(Calendar.HOUR_OF_DAY), 0);
                fechaSalida.setDate(hoy);
                setHoraSpinner(horaSalida, 23, 59);
                setSalidaEnabled(false);
                break;
            }
        }

        if (botonesPreset != null) {
            for (JButton b : botonesPreset)
                b.repaint();
        }
        actualizarHabitaciones();
        actualizarTotal();
        actualizarInfoEstadia();
    }

    private void setSalidaEnabled(boolean enabled) {
        if (filaSalida == null)
            return;
        for (Component comp : filaSalida.getComponents()) {
            comp.setEnabled(enabled);
            if (comp instanceof JPanel) {
                for (Component inner : ((JPanel) comp).getComponents())
                    inner.setEnabled(enabled);
            }
        }
        fechaSalida.setEnabled(enabled);
        horaSalida.setEnabled(enabled);
    }

    private void actualizarInfoEstadia() {
        if (infoEstadia == null || fechaEntrada.getDate() == null)
            return;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String ent = sdf.format(fechaEntrada.getDate()) + " " + formatearHora(horaEntrada);
        switch (tipoEstadiaSeleccionado) {
            case "Noche":
                String sal = fechaSalida.getDate() != null
                        ? sdf.format(fechaSalida.getDate()) + " " + formatearHora(horaSalida)
                        : "?";
                long noches = calcularNoches();
                infoEstadia.setText("Entrada: " + ent + "  →  Salida: " + sal +
                        (noches > 0 ? "  (" + noches + " noche" + (noches > 1 ? "s" : "") + ")" : ""));
                break;
            case "Indefinido":
                infoEstadia.setText("Entrada: " + ent + "  |  Salida sin determinar cobro al salir");
                break;
        }
    }

    // ── Lógica general ────────────────────────────────────────────────────────
    private void actualizarHabitaciones() {
        String desde = formatearFecha(fechaEntrada.getDate());
        if (desde == null)
            return;
        String horaEnt = formatearHora(horaEntrada);
        String hasta, horaSal;
        if ("Indefinido".equals(tipoEstadiaSeleccionado)) {
            hasta = desde;
            horaSal = "23:59";
        } else {
            hasta = formatearFecha(fechaSalida.getDate());
            horaSal = formatearHora(horaSalida);
            if (hasta == null)
                return;
        }

        String desdeDateTime = desde + " " + horaEnt;
        String hastaDateTime = hasta + " " + horaSal;
        if (desdeDateTime.compareTo(hastaDateTime) >= 0 && !"Indefinido".equals(tipoEstadiaSeleccionado))
            return;

        Container parent = panelHabitaciones.getParent();
        if (parent == null)
            return;
        GridBagConstraints gbc = ((GridBagLayout) parent.getLayout()).getConstraints(panelHabitaciones);
        parent.remove(panelHabitaciones);
        habitacionesSeleccionadas.clear();
        panelHabitaciones = crearPanelHabitaciones(desdeDateTime, hastaDateTime);
        parent.add(panelHabitaciones, gbc);
        parent.revalidate();
        parent.repaint();
        actualizarTotal();
    }

    private void actualizarTotal() {
        if (habitacionesSeleccionadas.isEmpty()) {
            lblTotal.setText("—");
            return;
        }
        double total = 0;
        switch (tipoEstadiaSeleccionado) {
            case "Noche": {
                long noches = calcularNoches();
                for (Habitacion h : habitacionesSeleccionadas) {
                    total += (noches > 0 ? noches : 1) * h.getPrecio();
                }
                lblTotal.setText(String.format("$%,.0f", total));
                break;
            }
            case "Indefinido":
                lblTotal.setText("Cobro al salir");
                break;
        }
    }

    private long calcularNoches() {
        Date dE = fechaEntrada.getDate();
        Date dS = fechaSalida != null ? fechaSalida.getDate() : null;
        if (dE == null || dS == null || !dS.after(dE))
            return 0;
        return (dS.getTime() - dE.getTime()) / (1000L * 60 * 60 * 24);
    }

    private void confirmarReserva() {
        String doc = campoDoc.getText().trim();
        String nombre = campoNombre.getText().trim();
        String desde = formatearFecha(fechaEntrada.getDate());
        String horaEnt = formatearHora(horaEntrada);
        String telefono = campoTelefono.getText().trim();
        String correo = campoCorreo.getText().trim();

       if (doc.isEmpty() || nombre.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this,
            "Identificación, nombre y teléfono son obligatorios.",
            "Campos requeridos",
            JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validaciones estrictas
        if (!doc.matches("^[0-9]+$")) {
            JOptionPane.showMessageDialog(this, "La identificación debe ser únicamente numérica.",
                    "Dato inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$")) {
            JOptionPane.showMessageDialog(this, "El nombre completo debe contener únicamente letras y espacios.",
                    "Dato inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!telefono.matches("^[0-9]+$")) {
            JOptionPane.showMessageDialog(this, "El teléfono debe ser únicamente numérico.",
                    "Dato inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!correo.isEmpty() && !correo.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
            JOptionPane.showMessageDialog(this, "El correo electrónico no tiene un formato válido.",
                    "Dato inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (desde == null) {
            JOptionPane.showMessageDialog(this, "Seleccione fecha de entrada.",
                    "Fecha requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String hoyStr = formatearFecha(new Date());
        if (desde.compareTo(hoyStr) < 0) {
            JOptionPane.showMessageDialog(this, "La fecha de entrada no puede ser anterior a hoy.",
                    "Fecha inválida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String hasta, horaSal;
        if ("Indefinido".equals(tipoEstadiaSeleccionado)) {
            hasta = desde;
            horaSal = "23:59";
        } else {
            hasta = formatearFecha(fechaSalida.getDate());
            horaSal = formatearHora(horaSalida);
            if (hasta == null) {
                JOptionPane.showMessageDialog(this, "Seleccione fecha de salida.",
                        "Fecha requerida", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String dtEnt = desde + " " + horaEnt;
            String dtSal = hasta + " " + horaSal;
            if (dtSal.compareTo(dtEnt) <= 0) {
                JOptionPane.showMessageDialog(this,
                        "La salida debe ser posterior a la entrada.",
                        "Fecha inválida", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        if (habitacionesSeleccionadas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione al menos una habitación.",
                "Habitación requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double totalEstimado = 0;
        if ("Noche".equals(tipoEstadiaSeleccionado)) {
            long noches = calcularNoches();
            for (Habitacion h : habitacionesSeleccionadas) {
                totalEstimado += (noches > 0 ? noches : 1) * h.getPrecio();
            }
        }

        double anticipoVal = 0;
        try {
            String raw = campoAnticipo.getText().trim().replace(".", "").replace(",", ".");
            anticipoVal = Double.parseDouble(raw);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El anticipo debe ser un valor numérico.",
                    "Dato inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (anticipoVal < 0) {
            JOptionPane.showMessageDialog(this, "El anticipo no puede ser un valor negativo.",
                    "Dato inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if ("Noche".equals(tipoEstadiaSeleccionado) && anticipoVal > totalEstimado) {
            JOptionPane.showMessageDialog(this, "El anticipo no puede ser superior al total estimado ($" + String.format("%,.0f", totalEstimado) + ").",
                    "Dato inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok = true;
        for (Habitacion habitacion : habitacionesSeleccionadas) {
            boolean creada = reservaDAO.crear(
                    habitacion.getId(),
                    idUsuario,
                    nombre,
                    doc,
                    telefono,
                    correo,
                    desde,
                    horaEnt,
                    hasta,
                    horaSal,
                    tipoEstadiaSeleccionado,
                    anticipoVal
            );
            if (!creada) {
                ok = false;
                break;
            }
        }

        if (ok) {

            if (!correo.isEmpty()) {
                EmailService.enviarReserva(
                        correo,
                        nombre,
                        doc,
                        telefono,
                        desde,
                        horaEnt
                    );
                }
            // Solo marcar Ocupada si el check-in es hoy
            String hoy = formatearFecha(new Date());
            if (desde.equals(hoy)) {
                for (Habitacion habitacion : habitacionesSeleccionadas) {
                    habitacionDAO.actualizarEstado(habitacion.getId(), "Ocupada");
                    HistorialDAO.registrar("Checkin", "Check-in realizado",
                        nombre + " realizó check-in en Hab. " + habitacion.getNumero());
                }
            }
            String msg = "Reserva creada correctamente.";
            if (!desde.equals(hoy)) {
                msg += "\nLa habitación se marcará Ocupada al hacer Check-in el " + desde + ".";
            }
            JOptionPane.showMessageDialog(this, msg, "Reserva confirmada", JOptionPane.INFORMATION_MESSAGE);
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
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
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

    private static void setInputFilter(JTextField field, String regex) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
                    throws BadLocationException {
                if (text != null && text.matches(regex)) {
                    super.insertString(fb, offset, text, attr);
                }
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text != null && text.matches(regex)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
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

    private JPanel caja(String label, JComponent campo) {
        JPanel w = new JPanel(new BorderLayout(0, 5));
        w.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(ThemeManager.getTextSecondary());
        w.add(lbl, BorderLayout.NORTH);
        w.add(campo, BorderLayout.CENTER);
        return w;
    }

    private JSpinner timeSpinner(int hora, int minuto) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hora);
        cal.set(Calendar.MINUTE, minuto);
        cal.set(Calendar.SECOND, 0);
        SpinnerDateModel model = new SpinnerDateModel(cal.getTime(), null, null, Calendar.MINUTE);
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

    private void setHoraSpinner(JSpinner spinner, int hora, int minuto) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hora);
        cal.set(Calendar.MINUTE, minuto);
        cal.set(Calendar.SECOND, 0);
        spinner.setValue(cal.getTime());
    }

    private String formatearFecha(Date d) {
        return DateUtil.formatearFecha(d);
    }

    private String formatearHora(JSpinner spinner) {
        return DateUtil.formatearHora((Date) spinner.getValue());
    }

    // Listener auxiliar para JSpinner
    private static class SimpleDocListener implements javax.swing.event.DocumentListener {
        private final Runnable r;

        SimpleDocListener(Runnable r) {
            this.r = r;
        }

        public void insertUpdate(javax.swing.event.DocumentEvent e) {
            r.run();
        }

        public void removeUpdate(javax.swing.event.DocumentEvent e) {
            r.run();
        }

        public void changedUpdate(javax.swing.event.DocumentEvent e) {
            r.run();
        }
    }
}
