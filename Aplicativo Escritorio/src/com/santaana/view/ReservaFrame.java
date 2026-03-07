package com.santaana.view;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;

public class ReservaFrame extends JFrame {
    private String role;
    private static final Color COLOR_PRIMARIO = new Color(0x3A7BD5);
    private static final Color COLOR_FONDO    = new Color(0xF0F6FF);
    private static final Color COLOR_PANEL    = Color.WHITE;
    private static final Color COLOR_BORDE    = new Color(0xDDE8F5);
    private static final Color COLOR_TEXTO    = new Color(40,50,70);
    private static final Color COLOR_VERDE = new Color(0,170,90);
    private static final Color COLOR_LABEL = new Color(110,120,140);
    

    public ReservaFrame(String role, String welcomeMessage) {
        this.role = role;

        setTitle("Hotel Santa Ana — Reservas");
        setSize(1280,800);
        setMinimumSize(new Dimension(1100,720));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(crearNavbar(), BorderLayout.NORTH);
        add(sidebar(), BorderLayout.WEST);
        add(crearContenido(), BorderLayout.CENTER);
        setVisible(true);
    }


    private JPanel crearNavbar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(Color.WHITE);
        navbar.setPreferredSize(new Dimension(0, 60));
        navbar.setBorder(new MatteBorder(0,0,1,0,COLOR_BORDE));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT,14,10));
        left.setOpaque(false);

        JLabel logo = new JLabel();

        try{
            ImageIcon icon = new ImageIcon("resources/logo.png");
            Image scaled = icon.getImage().getScaledInstance(40,40,Image.SCALE_SMOOTH);
            logo.setIcon(new ImageIcon(scaled));
        }catch(Exception e){}

        JLabel nombre = new JLabel("<html><b>HOTEL SANTA ANA</b><br><span style='font-size:9px;color:#6B84A0'>Sistema de gestión hotelera</span></html>");

        left.add(logo);
        left.add(nombre);

        JLabel notifLbl = new JLabel("🔔");
        notifLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 17  )); 
        notifLbl.setForeground(new Color(0x3A7BD5));

        left.add(notifLbl);

        JPanel mid = new JPanel(new FlowLayout(FlowLayout.CENTER,10,12));
        mid.setOpaque(false);

        mid.add(crearBotonNavbar("+ Nueva reserva",COLOR_PRIMARIO));
        mid.add(crearBotonNavbar("Venta rápida",new Color(0xE8F1FD)));

        // Lado derecho: botones + usuario
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16,14));
        right.setOpaque(false);
        right.add(userPanel());

        navbar.add(left,BorderLayout.WEST);
        navbar.add(mid,BorderLayout.CENTER);
        navbar.add(right,BorderLayout.EAST);
       

        return navbar;
    }

    private JButton crearBotonNavbar(String texto, Color bg) {
        JButton btn = new JButton(texto);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150,34));
        return btn;
    }
    private JPanel userPanel(){

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel name = new JLabel("Usuario");
        name.setFont(new Font("Segoe UI",Font.BOLD,12));

        JLabel rol = new JLabel(role);
        rol.setFont(new Font("Segoe UI",Font.PLAIN,10));
        rol.setForeground(COLOR_LABEL);

        p.add(name);
        p.add(rol);

        return p;
    }

    // SIDEBAR

    private JPanel sidebar(){

        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side,BoxLayout.Y_AXIS));
        side.setBackground(Color.WHITE);
        side.setPreferredSize(new Dimension(190,0));
        side.setBorder(new MatteBorder(0,0,0,1,COLOR_BORDE));

        side.add(Box.createVerticalStrut(20));

        String[] items={
                "Tablero",
                "Gestión de Habitaciones",
                "Reserva",
                "Punto de venta",
                "Historial",
                "Reporte"
        };

        for(int i=0;i<items.length;i++){

            side.add(sideBtn(items[i], i==2));
            side.add(Box.createVerticalStrut(8));

        }

        side.add(Box.createVerticalGlue());

        return side;
    }

    private JPanel sideBtn(String text, boolean active){

        JPanel p = new JPanel(new BorderLayout());
        p.setMaximumSize(new Dimension(180,36));
        p.setBorder(BorderFactory.createEmptyBorder(8,14,8,8));
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lbl = new JLabel(text);

        lbl.setFont(new Font("Segoe UI",active?Font.BOLD:Font.PLAIN,12));
        lbl.setForeground(active?COLOR_PRIMARIO:COLOR_LABEL);

        if(active)
            p.setBackground(new Color(0xE8F1FD));
        else
            p.setBackground(Color.WHITE);

        p.add(lbl,BorderLayout.CENTER);

        return p;
    }

    // CONTENIDO CENTRAL

    private JScrollPane crearContenido(){

        JPanel cont = new JPanel();
        cont.setLayout(new BoxLayout(cont,BoxLayout.Y_AXIS));
        cont.setBackground(COLOR_FONDO);
        cont.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JPanel fila1 = new JPanel(new GridLayout(1,2,20,20));
        fila1.setOpaque(false);

        fila1.add(crearPanelHuesped());
        fila1.add(crearPanelReserva());

        JPanel fila2 = new JPanel(new GridLayout(1,2,20,20));
        fila2.setOpaque(false);

        fila2.add(crearPanelPago());
        fila2.add(crearPanelHabitaciones());

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

    // TARJETA BASE

    private JPanel tarjeta(){

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        p.setBackground(COLOR_PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(16,16,16,16)
        ));

        return p;
    }

    private JLabel titulo(String txt){

        JLabel l = new JLabel(txt);
        l.setForeground(COLOR_PRIMARIO);
        l.setFont(new Font("Segoe UI",Font.BOLD,14));

        l.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

        return l;
    }

    // HUESPED

    private JPanel crearPanelHuesped(){

        JPanel p = tarjeta();

        p.add(titulo("Datos de huésped"));

        p.add(campo("Nombre"));
        p.add(Box.createVerticalStrut(8));
        p.add(campo("Apellido"));
        p.add(Box.createVerticalStrut(8));
        p.add(campo("Identificación"));
        p.add(Box.createVerticalStrut(8));
        p.add(campo("Correo"));
        p.add(Box.createVerticalStrut(8));
        p.add(campo("Teléfono"));

        return p;
    }

    // RESERVA

    private JPanel crearPanelReserva(){

        JPanel p = tarjeta();

        p.add(titulo("Datos de reserva"));

        p.add(campo("Fecha entrada"));
        p.add(Box.createVerticalStrut(8));
        p.add(campo("Fecha salida"));

        return p;
    }

    // PAGO

    private JPanel crearPanelPago(){

        JPanel p = tarjeta();

        p.add(titulo("Pago"));

        p.add(campo("Anticipo"));
        p.add(Box.createVerticalStrut(10));

        JLabel total = new JLabel("Total: $0");
        total.setForeground(COLOR_PRIMARIO);

        p.add(total);

        return p;
    }

    // HABITACIONES

    private JPanel crearPanelHabitaciones(){

        JPanel p = tarjeta();

        p.add(titulo("Seleccione habitación"));

        JPanel cards = new JPanel(new GridLayout(1,3,10,0));
        cards.setOpaque(false);

        cards.add(cardHabitacion("07"));
        cards.add(cardHabitacion("01"));
        cards.add(cardHabitacion("15"));

        p.add(cards);

        return p;
    }

    private JPanel cardHabitacion(String num){

        JPanel c = new JPanel();
        c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
        c.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));
        c.setBackground(Color.WHITE);

        JLabel n = new JLabel("Habitación "+num);
        n.setForeground(COLOR_PRIMARIO);

        JLabel disp = new JLabel("Disponible");
        disp.setForeground(COLOR_VERDE);

        JButton sel = new JButton("Seleccionar");

        sel.setBackground(COLOR_PRIMARIO);
        sel.setForeground(Color.WHITE);
        sel.setFocusPainted(false);

        c.add(n);
        c.add(disp);
        c.add(sel);

        return c;
    }

    // BOTONES

    private JPanel crearPanelBotones(){

        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.setOpaque(false);

        JButton cancelar = new JButton("Cancelar");
        JButton reservar = new JButton("Reservar");

        reservar.setBackground(COLOR_PRIMARIO);
        reservar.setForeground(Color.WHITE);

        p.add(cancelar);
        p.add(reservar);

        return p;
    }

    // CAMPO

    private JPanel campo(String txt){

        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JTextField t = new JTextField();
        t.setBorder(BorderFactory.createTitledBorder(txt));

        p.add(t);

        return p;
    }
}

