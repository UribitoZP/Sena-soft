package com.santaana.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.santaana.db.DatabaseConnection;

public class ReservaClienteDAO {

    public boolean guardar(
            int idReserva,
            int idCliente,
            String tipoPersona) {

        String sql = """
            INSERT INTO reserva_clientes
            (id_reserva, id_cliente, tipo_persona)
            VALUES (?, ?, ?)
        """;

        try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idReserva);
            ps.setInt(2, idCliente);
            ps.setString(3, tipoPersona);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}