package com.santaana.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.santaana.db.DatabaseConnection;
import com.santaana.model.ReservaProducto;

public class ReservaProductoDAO {

    public boolean agregarProductoAReserva(ReservaProducto rp) {

        String insertSQL = """
            INSERT INTO reserva_productos
            (id_reserva, id_producto, cantidad, precio)
            VALUES (?, ?, ?, ?)
        """;

        String updateStockSQL = """
            UPDATE productos
            SET stock = stock - ?
            WHERE id = ? AND stock >= ?
        """;

        try (Connection conn = DatabaseConnection.getConnection()) {

            conn.setAutoCommit(false);

            // ===== DESCONTAR STOCK =====
            try (PreparedStatement stockPS = conn.prepareStatement(updateStockSQL)) {

                stockPS.setInt(1, rp.getCantidad());
                stockPS.setInt(2, rp.getIdProducto());
                stockPS.setInt(3, rp.getCantidad());

                int filas = stockPS.executeUpdate();

                // Si no actualizó, no había stock suficiente
                if (filas == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // ===== INSERTAR PRODUCTO EN LA RESERVA =====
            try (PreparedStatement insertPS = conn.prepareStatement(insertSQL)) {

                insertPS.setInt(1, rp.getIdReserva());
                insertPS.setInt(2, rp.getIdProducto());
                insertPS.setInt(3, rp.getCantidad());
                insertPS.setDouble(4, rp.getPrecio());

                insertPS.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<ReservaProducto> listarPorReserva(int idReserva) {

        List<ReservaProducto> lista = new ArrayList<>();

        String sql = """
            SELECT *
            FROM reserva_productos
            WHERE id_reserva = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idReserva);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                ReservaProducto rp = new ReservaProducto(
                    rs.getInt("id_reserva"),
                    rs.getInt("id_producto"),
                    rs.getInt("cantidad"),
                    rs.getDouble("precio")
                );

                lista.add(rp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
    public int obtenerUltimaReserva() {
        String sql = """
            SELECT id
            FROM reservas
            ORDER BY id DESC
            LIMIT 1
        """;
        try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {
                return rs.getInt("id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    public double obtenerTotalProductos(int idReserva) {
        String sql = """
            SELECT COALESCE(SUM(cantidad * precio),0)
            FROM reserva_productos
            WHERE id_reserva = ?
        """;

        try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, idReserva);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}