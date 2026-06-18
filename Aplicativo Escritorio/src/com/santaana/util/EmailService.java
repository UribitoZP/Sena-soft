package com.santaana.util;

import java.util.Properties;

import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailService {

    private static final String REMITENTE = "santaanahotel897@gmail.com";
    private static final String PASSWORD = "yozpnzutrfgmfuqq";

    public static void enviarReserva(
            String destinatario,
            String nombre,
            String documento,
            String telefono,
            String fechaEntrada,
            String horaEntrada) {

        try {

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props,
                    new jakarta.mail.Authenticator() {
                        @Override
                        protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                            return new jakarta.mail.PasswordAuthentication(
                                    REMITENTE,
                                    PASSWORD
                            );
                        }
                    });

            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(REMITENTE));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(destinatario)
            );

            message.setSubject("Reserva confirmada - Hotel Santa Ana");

            String cuerpo =
                    "Hola " + nombre + ",\n\n" +
                    "Su reserva ha sido registrada exitosamente.\n\n" +

                    "Datos de la reserva:\n\n" +
                    "Nombre: " + nombre + "\n" +
                    "Identificación: " + documento + "\n" +
                    "Teléfono: " + telefono + "\n" +
                    "Fecha de entrada: " + fechaEntrada + "\n" +
                    "Hora de entrada: " + horaEntrada + "\n\n" +

                    "Estamos felices de recibirlo en Hotel Santa Ana.\n" +
                    "Gracias por confiar en nosotros.\n\n" +

                    "Atentamente,\n" +
                    "Hotel Santa Ana";

            message.setText(cuerpo);

            Transport.send(message);

            System.out.println("Correo enviado a: " + destinatario);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}   