package com.santaana.view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import com.toedter.calendar.JDateChooser;
import com.santaana.util.ThemeManager;

public class NuevaReservaDialog extends JDialog {

    public NuevaReservaDialog(Window owner) {
        super(owner, "Nueva Reserva", ModalityType.APPLICATION_MODAL);
        setSize(960, 680);
        setMinimumSize(new Dimension(860, 600));
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(ThemeManager.getBackground());

        // Header
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

    private JPanel crearPanelHuesped() {
        JPanel p = tarjeta();
        p.add(titulo("Datos de huésped"));
        p.add(campo("Identificación", "Cédula o Pasaporte"));
        p.add(Box.createVerticalStrut(12));
        p.add(campo("Nombre completo del huésped", "Ej: Juan Pérez"));
        p.add(Box.createVerticalStrut(12));
        p.add(campo("Correo (Opcional)", "correo@ejemplo.com"));
        p.add(Box.createVerticalStrut(12));
        p.add(campo("Teléfono", "Ej: +57 ..."));
        return p;
    }

    private JPanel crearPanelReserva() {
        JPanel p = tarjeta();
        p.add(titulo("Datos de reserva"));
        JPanel grid = new JPanel(new GridLayout(0, 2, 15, 12));
        grid.setOpaque(false);
        grid.add(crearCajaFecha("Fecha de Entrada"));
        grid.add(crearCajaCombo("Hora de Entrada", new String[]{"06:00","07:00","08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00"}));
        grid.add(crearCajaFecha("Fecha de Salida"));
        grid.add(crearCajaCombo("Hora de Salida", new String[]{"06:00","07:00","08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00"}));
        grid.add(crearCajaCombo("Tipo de estadía", new String[]{"Por horas","Media noche","Noche completa","Día completo"}));
        p.add(grid);
        return p;
    }

    private JPanel crearCajaFecha(String label) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 5));
        wrapper.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(ThemeManager.getTextSecondary());
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
        wrapper.add(lbl, BorderLayout.NORTH);
        wrapper.add(dc, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel crearCajaCombo(String label, String[] items) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 5));
        wrapper.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(ThemeManager.getTextSecondary());
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setPreferredSize(new Dimension(0, 36));
        combo.setBackground(ThemeManager.getPanelBackground());
        combo.setForeground(ThemeManager.getTextPrimary());
        combo.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder(), 1, true));
        wrapper.add(lbl, BorderLayout.NORTH);
        wrapper.add(combo, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel crearPanelPago() {
        JPanel panel = tarjeta();
        panel.add(titulo("Pago"));
        panel.add(crearCajaCombo("Límite de hospedaje", new String[]{"1 hora","2 horas","6 horas","12 horas","1 noche"}));
        panel.add(Box.createVerticalStrut(15));
        panel.add(campo("Anticipo", "$0"));
        panel.add(Box.createVerticalStrut(15));
        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        JLabel lblTotal = new JLabel("Total a pagar");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotal.setForeground(ThemeManager.getTextPrimary());
        JLabel valTotal = new JLabel("$0");
        valTotal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valTotal.setForeground(ThemeManager.getPrimary());
        totalRow.add(lblTotal, BorderLayout.WEST);
        totalRow.add(valTotal, BorderLayout.EAST);
        panel.add(totalRow);
        panel.add(Box.createVerticalStrut(15));
        JPanel mtdPago = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        mtdPago.setOpaque(false);
        String[] opts = {"Efectivo","Transferencia","Tarjeta"};
        ButtonGroup bg = new ButtonGroup();
        for (String opt : opts) {
            JRadioButton rb = new JRadioButton(opt);
            rb.setOpaque(false);
            rb.setForeground(ThemeManager.getTextPrimary());
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
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(ThemeManager.getBorder());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setOpaque(false);
        c.setBackground(ThemeManager.getPanelBackground());
        c.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel n = new JLabel("Habitación " + num);
        n.setFont(new Font("Segoe UI", Font.BOLD, 15));
        n.setForeground(ThemeManager.getPrimary());
        n.setAlignmentX(0.5f);

        JLabel disp = new JLabel("Disponible");
        disp.setForeground(new Color(0x27AE60));
        disp.setFont(new Font("Segoe UI", Font.BOLD, 11));
        disp.setAlignmentX(0.5f);

        JLabel info = new JLabel("<html><center>Individual<br>$70.000 / Noche</center></html>");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        info.setForeground(ThemeManager.getTextSecondary());
        info.setAlignmentX(0.5f);

        c.add(n);
        c.add(Box.createVerticalStrut(5));
        c.add(disp);
        c.add(Box.createVerticalStrut(5));
        c.add(info);

        c.addMouseListener(new MouseAdapter() {
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
        btnReserva.setBackground(ThemeManager.getPrimary());
        btnReserva.setForeground(Color.WHITE);
        btnReserva.setContentAreaFilled(false);
        btnReserva.setBorderPainted(false);
        btnReserva.setPreferredSize(new Dimension(180, 38));
        btnReserva.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnReserva.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Reserva confirmada correctamente.", "Reserva creada", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        });

        p.add(btnCancela);
        p.add(btnReserva);
        return p;
    }

    private JPanel campo(String label, String placeholder) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 5));
        wrapper.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(ThemeManager.getTextSecondary());
        JTextField tf = new JTextField();
        tf.setPreferredSize(new Dimension(0, 36));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBackground(ThemeManager.getPanelBackground());
        tf.setForeground(ThemeManager.getTextPrimary());
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorder(), 1, true),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        wrapper.add(lbl, BorderLayout.NORTH);
        wrapper.add(tf, BorderLayout.CENTER);
        return wrapper;
    }
}
