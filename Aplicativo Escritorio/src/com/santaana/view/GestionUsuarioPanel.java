package com.santaana.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;

import com.santaana.dao.ReservaDAO;
import com.santaana.dao.UsuarioDAO;
import com.santaana.db.DatabaseException;
import com.santaana.model.Usuario;
import com.santaana.util.BackupManager;
import com.santaana.util.ErrorUtil;
import com.santaana.util.ThemeManager;

public class GestionUsuarioPanel extends JPanel implements ThemeManager.ThemeListener {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final ReservaDAO reservaDAO = new ReservaDAO();
    private JPanel contenedorLista;
    private JPanel contenedorHistorial;
    private String role;

    private Color getPrimario() { return ThemeManager.getPrimary(); }
    private Color getFondo() { return ThemeManager.getBackground(); }
    private Color getPanelCol() { return ThemeManager.getPanelBackground(); }
    private Color getBorde() { return ThemeManager.getBorder(); }
    private Color getTextCol() { return ThemeManager.getTextPrimary(); }
    private Color getLabelCol() { return ThemeManager.getTextSecondary(); }

    public GestionUsuarioPanel(String role) {
        this.role = role;
        ThemeManager.addListener(this);
        setLayout(new BorderLayout());
        refreshUI();
    }

    public void refreshUI() {
        removeAll();
        setBackground(getFondo());
        add(crearNavbar(), BorderLayout.NORTH);
        add(crearScroll(), BorderLayout.CENTER);
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
        navbar.setPreferredSize(new Dimension(0, 60));
        navbar.setBorder(new MatteBorder(0, 0, 1, 0, getBorde()));

        JLabel title = new JLabel("  GESTIÓN DE USUARIOS Y CLIENTES");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(getTextCol());

        JButton btnNuevo = new JButton("+ Nuevo usuario");
        btnNuevo.setBackground(getPrimario());
        btnNuevo.setForeground(Color.WHITE);
        btnNuevo.setFocusPainted(false);
        btnNuevo.setBorderPainted(false);
        btnNuevo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnNuevo.addActionListener(e -> abrirNuevoUsuario());

        JButton btnBackup = new JButton(" Backup");
        estilizarBotonSecundario(btnBackup);

        btnBackup.addActionListener(e -> {
            try {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Guardar Backup");
                int opcion = fileChooser.showSaveDialog(this);
                if (opcion == JFileChooser.APPROVE_OPTION) {
                    String ruta = fileChooser.getSelectedFile().getAbsolutePath() + ".csv";
                    BackupManager.exportarReservasCSV(ruta);
                    JOptionPane.showMessageDialog(this,
                            "Backup generado correctamente",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al generar backup", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        right.setOpaque(false);
        right.add(btnNuevo);
        right.add(btnBackup);

        navbar.add(title, BorderLayout.WEST);
        navbar.add(right, BorderLayout.EAST);
        return navbar;
    }

    private JScrollPane crearScroll() {
        contenedorLista = new JPanel();
        contenedorLista.setLayout(new BoxLayout(contenedorLista, BoxLayout.Y_AXIS));
        contenedorLista.setOpaque(false);
        contenedorLista.setBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cargarUsuarios();
        JScrollPane scroll = new JScrollPane(contenedorLista);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        return scroll;
    }

    private void cargarUsuarios() {
        contenedorLista.removeAll();
        List<Usuario> usuarios;
        try {
            usuarios = usuarioDAO.listarTodos();
        } catch (DatabaseException e) {
            ErrorUtil.mostrarError(this, "cargar usuarios", e);
            contenedorLista.revalidate();
            contenedorLista.repaint();
            return;
        }
        // ===== USUARIOS =====
        if (usuarios.isEmpty()) {
            JLabel vacio = new JLabel("No hay usuarios registrados.", SwingConstants.CENTER);
            vacio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            vacio.setForeground(getLabelCol());
            contenedorLista.add(vacio);
        } else {
            for (Usuario u : usuarios) {
                contenedorLista.add(userCard(u));
                contenedorLista.add(Box.createVerticalStrut(10));
            }
        }
        // === HISTORIAL CLIENTES ===
        contenedorLista.add(Box.createVerticalStrut(25));

        JLabel tituloClientes = new JLabel("Historial de clientes");
        tituloClientes.setFont(new Font("Segoe UI", Font.BOLD, 20));
        tituloClientes.setForeground(getPrimario());
        tituloClientes.setHorizontalAlignment(SwingConstants.CENTER);
        tituloClientes.setAlignmentX(Component.CENTER_ALIGNMENT);

        contenedorLista.add(tituloClientes);
        contenedorLista.add(Box.createVerticalStrut(15));

        List<Object[]> clientes = reservaDAO.obtenerHistorialClientes();

        if (clientes.isEmpty()) {

            JLabel vacioClientes = new JLabel("No hay historial de clientes.");
            vacioClientes.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            vacioClientes.setForeground(getLabelCol());

            contenedorLista.add(vacioClientes);

        } else {

            JPanel gridClientes = new JPanel();

            gridClientes.setOpaque(false);

            gridClientes.setLayout(
                new java.awt.GridLayout(
                    0,
                    3,
                    15,
                    15
                )
            );

            for (Object[] datos : clientes) {

                gridClientes.add(clienteCard(datos));
            }

            contenedorLista.add(gridClientes);
        }
    }
    public void refrescarHistorialClientes() {
        cargarUsuarios();
        revalidate();
        repaint();
    }

    private JPanel userCard(Usuario u) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(getPanelCol());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(getBorde(), 1, true),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel lblNombre = new JLabel(u.getNombre());
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNombre.setForeground(getTextCol());

        JLabel lblUsuario = new JLabel("@" + u.getUsuario());
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblUsuario.setForeground(getLabelCol());

        String tel = u.getTelefono();
        String correo = u.getCorreo();
        StringBuilder sub = new StringBuilder();
        if (tel != null && !tel.isEmpty()) sub.append(tel);
        if (correo != null && !correo.isEmpty()) {
            if (sub.length() > 0) sub.append("  |  ");
            sub.append(correo);
        }
        JLabel lblContacto = sub.length() > 0
                ? new JLabel(sub.toString())
                : new JLabel("Sin datos de contacto");
        lblContacto.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblContacto.setForeground(getLabelCol());

        info.add(lblNombre);
        info.add(Box.createVerticalStrut(3));
        info.add(lblUsuario);
        info.add(Box.createVerticalStrut(2));
        info.add(lblContacto);

        JPanel estadoPanel = new JPanel();
        estadoPanel.setLayout(new BoxLayout(estadoPanel, BoxLayout.Y_AXIS));
        estadoPanel.setOpaque(false);

        JLabel lblRol = new JLabel(u.getRol());
        lblRol.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRol.setForeground(
            u.getRol().equals("Administrador") ? new Color(0xE74C3C) : new Color(0x3498DB)
        );
        estadoPanel.add(lblRol);

        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");

        estilizarBoton(btnEditar, getPrimario());
        estilizarBoton(btnEliminar, new Color(0xE74C3C));

        btnEditar.addActionListener(e -> abrirEditarUsuario(u));
        btnEliminar.addActionListener(e -> eliminarUsuario(u));

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        acciones.add(btnEditar);
        acciones.add(btnEliminar);

        card.add(info, BorderLayout.WEST);
        card.add(estadoPanel, BorderLayout.CENTER);
        card.add(acciones, BorderLayout.EAST);
        return card;
    }

    private void abrirNuevoUsuario() {
        try {
            abrirNuevoUsuarioInterno();
        } catch (DatabaseException e) {
            ErrorUtil.mostrarError(this, "crear usuario", e);
        }
    }

    private void abrirNuevoUsuarioInterno() {
        String rol = null;
        UsuarioDialog d = new UsuarioDialog(SwingUtilities.getWindowAncestor(this));
        d.setVisible(true);
        if (!d.isAcepto()) return;

        rol = d.getRolValue();
        int actuales = usuarioDAO.contarPorRol(rol);
        if (rol.equals("Administrador") && actuales >= 2) {
            JOptionPane.showMessageDialog(this,
                    "Solo se permiten hasta 2 Administradores.",
                    "Límite alcanzado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (rol.equals("Recepcionista") && actuales >= 3) {
            JOptionPane.showMessageDialog(this,
                    "Solo se permiten hasta 3 Recepcionistas.",
                    "Límite alcanzado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario u = new Usuario(0,
                d.getNombreValue(), d.getUsuarioValue(),
                d.getClaveValue(), d.getRolValue(),
                d.getTelefonoValue(), d.getCorreoValue());
        if (usuarioDAO.crear(u)) {
            cargarUsuarios();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo crear el usuario. El nombre de usuario ya existe.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirEditarUsuario(Usuario u) {
        try {
            abrirEditarUsuarioInterno(u);
        } catch (DatabaseException e) {
            ErrorUtil.mostrarError(this, "editar usuario", e);
        }
    }

    private void abrirEditarUsuarioInterno(Usuario u) {
        String rolOriginal = u.getRol();
        UsuarioDialog d = new UsuarioDialog(SwingUtilities.getWindowAncestor(this), u);
        d.setVisible(true);
        if (!d.isAcepto()) return;

        String nuevoRol = d.getRolValue();
        if (!nuevoRol.equals(rolOriginal)) {
            int actuales = usuarioDAO.contarPorRol(nuevoRol);
            if (nuevoRol.equals("Administrador") && actuales >= 2) {
                JOptionPane.showMessageDialog(this,
                        "Solo se permiten hasta 2 Administradores.",
                        "Límite alcanzado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (nuevoRol.equals("Recepcionista") && actuales >= 3) {
                JOptionPane.showMessageDialog(this,
                        "Solo se permiten hasta 3 Recepcionistas.",
                        "Límite alcanzado", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        String clave = d.getClaveValue();
        u.setNombre(d.getNombreValue());
        u.setUsuario(d.getUsuarioValue());
        if (!clave.isEmpty()) u.setClave(clave);
        u.setRol(d.getRolValue());
        u.setTelefono(d.getTelefonoValue());
        u.setCorreo(d.getCorreoValue());
        if (usuarioDAO.actualizar(u)) {
            cargarUsuarios();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo actualizar el usuario. El nombre de usuario ya existe.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarUsuario(Usuario u) {
        int r = JOptionPane.showConfirmDialog(this,
                "¿Eliminar al usuario \"" + u.getNombre() + "\" (" + u.getUsuario() + ")?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (r == JOptionPane.YES_OPTION) {
            try {
                if (usuarioDAO.eliminar(u.getId())) {
                    cargarUsuarios();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo eliminar el usuario.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DatabaseException e) {
                ErrorUtil.mostrarError(this, "eliminar usuario", e);
            }
        }
    }

    private void estilizarBoton(JButton b, Color bg) {
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(90, 28));
    }

    private void estilizarBotonSecundario(JButton b) {
        b.setBackground(ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT
                ? new Color(0xE8F1FD) : new Color(0x334155));
        b.setForeground(getPrimario());
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(110, 32));
    }
    private JPanel clienteCard(Object[] datos) {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(getPanelCol());

        card.setPreferredSize(new Dimension(320, 220));

        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(
                new Color(0, 0, 0, 25),
                1,
                true
            ),
            BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        // =========================
        // HEADER
        // =========================

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel lblNombre = new JLabel(datos[0].toString());

        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblNombre.setForeground(getTextCol());

        JLabel tipo = new JLabel(datos[7].toString());

        tipo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        tipo.setOpaque(true);

        tipo.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        if (datos[7].toString().equalsIgnoreCase("Acompañante")) {

            tipo.setBackground(new Color(255, 193, 7));
            tipo.setForeground(Color.BLACK);

        } else {

            tipo.setBackground(getPrimario());
            tipo.setForeground(Color.WHITE);
        }

        header.add(lblNombre, BorderLayout.WEST);
        header.add(tipo, BorderLayout.EAST);

        // =========================
        // SEPARADOR
        // =========================

        JPanel linea = new JPanel();
        linea.setBackground(getBorde());
        linea.setPreferredSize(new Dimension(0, 1));

        // =========================
        // INFO
        // =========================

        JPanel info = new JPanel();
        info.setOpaque(false);

        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel lblDoc = crearInfo("Documento", datos[1].toString());
        JLabel lblTelefono = crearInfo("Teléfono", datos[2].toString());
        JLabel lblCorreo = crearInfo("Correo", datos[3].toString());

        JLabel hab = new JLabel("Habitación " + datos[4]);

        hab.setFont(new Font("Segoe UI", Font.BOLD, 14));
        hab.setForeground(getPrimario());

        JLabel fechas = new JLabel(
            "Estadía: " + datos[5] + " → " + datos[6]
        );
        JLabel relacion = null;
        if (datos[7].toString().equalsIgnoreCase("Acompañante")) {

            relacion = new JLabel(
                "Acompañante de " + datos[8]
            );

            relacion.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            relacion.setForeground(new Color(255, 193, 7));
        }

        fechas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fechas.setForeground(getLabelCol());

        JLabel estado = new JLabel("● Historial registrado");

        estado.setForeground(new Color(0x27AE60));
        estado.setFont(new Font("Segoe UI", Font.BOLD, 11));

        // =========================
        // AGREGAR INFO
        // =========================

        info.add(Box.createVerticalStrut(12));

        info.add(lblDoc);
        info.add(Box.createVerticalStrut(6));

        info.add(lblTelefono);
        info.add(Box.createVerticalStrut(6));

        info.add(lblCorreo);
        info.add(Box.createVerticalStrut(14));

        info.add(hab);
        info.add(Box.createVerticalStrut(8));

        info.add(fechas);
        if (relacion != null) {
            info.add(Box.createVerticalStrut(6));
            info.add(relacion);
        }
        info.add(Box.createVerticalGlue());

        info.add(Box.createVerticalStrut(14));
        info.add(estado);

        // =========================
        // ENSAMBLAR
        // =========================

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);

        center.add(linea, BorderLayout.NORTH);
        center.add(info, BorderLayout.CENTER);

        card.add(header, BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);

        return card;
    }
    private JLabel crearInfo(String titulo, String valor) {
        JLabel lbl = new JLabel(
            "<html><b>" + titulo + ":</b> " + valor + "</html>"
        );
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(getLabelCol());

        return lbl;
    }
}