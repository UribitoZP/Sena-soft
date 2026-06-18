package com.santaana.view;

import com.toedter.calendar.JDateChooser;
import java.util.Date;
import java.awt.*;
import javax.swing.*;
import com.santaana.model.Usuario;
import com.santaana.util.ThemeManager;

public class UsuarioDialog extends JDialog {

    private JTextField campoNombre;
    private JTextField campoUsuario;
    private JPasswordField campoClave;
    private JTextField campoTelefono;
    private JTextField campoCorreo;
    private JDateChooser campoFechaNacimiento;
    private JComboBox<String> comboTipo;
    private boolean acepto = false;

    public UsuarioDialog(Window owner) {
        super(owner, "Nuevo usuario", ModalityType.APPLICATION_MODAL);
        setSize(440, 520);
        setLocationRelativeTo(owner);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
    }

    public UsuarioDialog(Window owner, Usuario u) {
        super(owner, "Editar usuario", ModalityType.APPLICATION_MODAL);
        setSize(440, 520);
        setLocationRelativeTo(owner);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
        campoNombre.setText(u.getNombre());
        campoUsuario.setText(u.getUsuario());
        campoTelefono.setText(u.getTelefono());
        campoCorreo.setText(u.getCorreo());
        comboTipo.setSelectedItem(u.getRol());
    }

    private void initUI() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(ThemeManager.getBackground());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(ThemeManager.getPanelBackground());
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 6, 0);
        c.gridx = 0;
        c.weightx = 1;

        c.gridy = 0;
        form.add(label("Nombre completo"), c);
        c.gridy = 1;
        campoNombre = textField();
        form.add(campoNombre, c);

        c.gridy = 2;
        c.insets = new Insets(8, 0, 6, 0);
        form.add(label("Usuario (para iniciar sesión)"), c);
        c.insets = new Insets(0, 0, 6, 0);
        c.gridy = 3;
        campoUsuario = textField();
        form.add(campoUsuario, c);

        c.gridy = 4;
        c.insets = new Insets(8, 0, 6, 0);
        form.add(label("Contraseña"), c);
        c.insets = new Insets(0, 0, 6, 0);
        c.gridy = 5;
        campoClave = new JPasswordField();
        campoClave.setPreferredSize(new Dimension(0, 36));
        campoClave.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campoClave.setBackground(ThemeManager.getPanelBackground());
        campoClave.setForeground(ThemeManager.getTextPrimary());
        campoClave.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getBorder(), 1, true),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        form.add(campoClave, c);

        c.gridy = 6;
        c.insets = new Insets(8, 0, 6, 0);
        form.add(label("Teléfono"), c);
        c.insets = new Insets(0, 0, 6, 0);
        c.gridy = 7;
        campoTelefono = textField();
        form.add(campoTelefono, c);

        c.gridy = 8;
        c.insets = new Insets(8, 0, 6, 0);
        form.add(label("Correo electrónico"), c);
        c.insets = new Insets(0, 0, 6, 0);
        c.gridy = 9;
        campoCorreo = textField();
        form.add(campoCorreo, c);

        c.gridy = 10;
        c.insets = new Insets(8, 0, 6, 0);
        form.add(label("Fecha de nacimiento"), c);

        c.insets = new Insets(0, 0, 6, 0);
        c.gridy = 11;

        campoFechaNacimiento = new JDateChooser();
        campoFechaNacimiento.setDateFormatString("dd/MM/yyyy");
        campoFechaNacimiento.setPreferredSize(new Dimension(0, 36));

        form.add(campoFechaNacimiento, c);

        c.gridy = 12;
        c.insets = new Insets(8, 0, 6, 0);
        form.add(label("Rol"), c);
        c.insets = new Insets(0, 0, 6, 0);
        c.gridy = 13;
        comboTipo = new JComboBox<>(new String[]{"Administrador", "Recepcionista"});
        comboTipo.setPreferredSize(new Dimension(0, 36));
        comboTipo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboTipo.setBackground(ThemeManager.getPanelBackground());
        comboTipo.setForeground(ThemeManager.getTextPrimary());
        form.add(comboTipo, c);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        botones.setOpaque(false);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCancelar.setForeground(ThemeManager.getTextPrimary());
        btnCancelar.setContentAreaFilled(false);
        btnCancelar.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder(), 1, true));
        btnCancelar.setPreferredSize(new Dimension(100, 34));
        btnCancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> dispose());

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setBackground(ThemeManager.getPrimary());
        btnGuardar.setBorderPainted(false);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setPreferredSize(new Dimension(100, 34));
        btnGuardar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnGuardar.addActionListener(e -> guardar());

        botones.add(btnCancelar);
        botones.add(btnGuardar);

        p.add(form, BorderLayout.CENTER);
        p.add(botones, BorderLayout.SOUTH);
        setContentPane(p);
    }

    private void guardar() {
        String nombre = campoNombre.getText().trim();
        String usuario = campoUsuario.getText().trim();
        String clave = new String(campoClave.getPassword()).trim();
        String rol = (String) comboTipo.getSelectedItem();
        Date fechaNacimiento = campoFechaNacimiento.getDate();

        boolean editando = getTitle().equals("Editar usuario");
        if (editando && clave.isEmpty()) {
            // En edición la contraseña es opcional (se conserva la actual)
        } else if (nombre.isEmpty()
        || usuario.isEmpty()
        || clave.isEmpty()
        || campoCorreo.getText().trim().isEmpty()
        || fechaNacimiento == null) {

    JOptionPane.showMessageDialog(this,
        "Nombre, usuario, contraseña, correo y fecha de nacimiento son obligatorios.",
        "Campos requeridos",
        JOptionPane.WARNING_MESSAGE);

    return;
}
        acepto = true;
        dispose();
    }

    public boolean isAcepto()          { return acepto; }
    public String getNombreValue()     { return campoNombre.getText().trim(); }
    public String getUsuarioValue()    { return campoUsuario.getText().trim(); }
    public String getClaveValue()       { return new String(campoClave.getPassword()).trim(); }
    public String getTelefonoValue()   { return campoTelefono.getText().trim(); }
    public String getCorreoValue()     { return campoCorreo.getText().trim(); }
    public Date getFechaNacimientoValue() {    return campoFechaNacimiento.getDate();}
    public String getRolValue()         { return (String) comboTipo.getSelectedItem(); }

    private JLabel label(String txt) {
        JLabel l = new JLabel(txt);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(ThemeManager.getTextSecondary());
        return l;
    }

    private JTextField textField() {
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
}
