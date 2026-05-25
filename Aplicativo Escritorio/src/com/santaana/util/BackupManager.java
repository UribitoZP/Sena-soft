package com.santaana.util;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.santaana.db.DatabaseConnection;

public class BackupManager {

    public static void exportarReservasCSV(String ruta) {

        String sql = "SELECT r.*, c.nombre AS cliente_nombre, c.documento AS cliente_doc " +
                     "FROM reservas r " +
                     "LEFT JOIN clientes c ON r.id_cliente = c.id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql);
             FileWriter writer = new FileWriter(ruta)) {

            // CABECERA (IMPORTANTE)
            writer.append("ID_HABITACION,ID_USUARIO,CLIENTE,DOCUMENTO,FECHA_ENTRADA,HORA_ENTRADA,FECHA_SALIDA,HORA_SALIDA,TIPO_ESTADIA\n");

            while (rs.next()) {
                writer.append(rs.getInt("id_habitacion") + ",");
                writer.append(rs.getInt("id_usuario") + ",");
                writer.append(rs.getString("cliente_nombre") + ",");
                writer.append(rs.getString("cliente_doc") + ",");
                writer.append(rs.getString("fecha_entrada") + ",");
                writer.append(rs.getString("hora_entrada") + ",");
                writer.append(rs.getString("fecha_salida") + ",");
                writer.append(rs.getString("hora_salida") + ",");
                writer.append(rs.getString("tipo_estadia") + "\n");
            }

            System.out.println("✅ Backup generado correctamente");

        } catch (Exception e) {
            System.err.println("❌ Error en backup: " + e.getMessage());
        }
    }
}