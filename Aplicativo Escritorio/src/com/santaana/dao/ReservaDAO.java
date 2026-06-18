package com.santaana.dao;

    import java.sql.Connection;
    import java.sql.PreparedStatement;
    import java.sql.ResultSet;
    import java.sql.SQLException;
    import java.sql.Statement;
    import java.util.ArrayList;
    import java.util.List;

    import com.santaana.db.DatabaseConnection;
    import com.santaana.db.DatabaseException;
    import com.santaana.model.Cliente;
    import com.santaana.model.Reserva;

public class ReservaDAO {

    public List<Reserva> listarTodas() {
        List<Reserva> lista = new ArrayList<>();    
        String sql = "SELECT r.*, c.nombre AS cliente_nombre, c.documento AS cliente_doc " +
                     "FROM reservas r " +
                     "LEFT JOIN clientes c ON r.id_cliente = c.id " +
                     "ORDER BY r.fecha_entrada DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new DatabaseException("listar reservas", e);
        }
        return lista;
    }
    public List<String[]> listarReservasActivas() {
        List<String[]> lista = new ArrayList<>();
        String sql = """
            SELECT
                r.id,
                h.numero,
                c.nombre
            FROM reservas r
            JOIN habitaciones h ON r.id_habitacion = h.id
            JOIN clientes c ON r.id_cliente = c.id
            WHERE r.estado = 'ACTIVA'
        """;
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new String[] {
                        rs.getString("id"),
                        rs.getString("numero"),
                        rs.getString("nombre")
                    });
                }
            }catch (Exception e) {
                e.printStackTrace();
                }
                return lista;
    }

    public List<Reserva> listarActivas() {
        List<Reserva> lista = new ArrayList<>();
        String sql = "SELECT r.*, c.nombre AS cliente_nombre, c.documento AS cliente_doc " +
                     "FROM reservas r " +
                     "LEFT JOIN clientes c ON r.id_cliente = c.id " +
                     "WHERE r.estado = 'Activa' ORDER BY r.fecha_entrada ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new DatabaseException("listar reservas activas", e);
        }
        return lista;
    }
    public boolean crear(int idHabitacion, int idUsuario, String clienteNombre,
                         String clienteDoc, String clienteTelefono, String clienteCorreo,
                         String fechaEntrada, String horaEntrada,
                         String fechaSalida, String horaSalida, String tipoEstadia,
                         double anticipo) {
        ClienteDAO clienteDAO = new ClienteDAO();
        Cliente cliente = clienteDAO.buscarPorDocumento(clienteDoc);
        int idCliente;
        if (cliente == null) {
            cliente = new Cliente(0, clienteNombre, clienteDoc, clienteTelefono, clienteCorreo);
            idCliente = clienteDAO.crear(cliente);
            if (idCliente == -1) {
                throw new DatabaseException("crear cliente durante reserva", new SQLException("No se pudo crear el cliente"));
            }
        } else {
            idCliente = cliente.getId();
        }

        String sql = "INSERT INTO reservas (id_habitacion, id_usuario, id_cliente, fecha_entrada, hora_entrada, fecha_salida, hora_salida, tipo_estadia, anticipo) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idHabitacion);
            ps.setInt(2, idUsuario);
            ps.setInt(3, idCliente);
            ps.setString(4, fechaEntrada);
            ps.setString(5, horaEntrada);
            ps.setString(6, fechaSalida);
            ps.setString(7, horaSalida);
            ps.setString(8, tipoEstadia);
            ps.setDouble(9, anticipo);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int idReserva = rs.getInt(1);
                    String sqlRelacion =
                        "INSERT INTO reserva_clientes " +
                        "(id_reserva, id_cliente, tipo_persona) " +
                        "VALUES (?, ?, ?)";
                    try (PreparedStatement rel = conn.prepareStatement(sqlRelacion)) {       
                            rel.setInt(1, idReserva);
                            rel.setInt(2, idCliente);
                            rel.setString(3, "Titular");
                            rel.executeUpdate();
                        }
                }
                return true;
            }

            return false;
        } catch (SQLException e) {
            throw new DatabaseException("crear reserva", e);
        }
    }

    public boolean actualizar(int id, String fechaEntrada, String horaEntrada,
                              String fechaSalida, String horaSalida,
                              String estado, double anticipo) {
        String sql = "UPDATE reservas SET fecha_entrada=?, hora_entrada=?, fecha_salida=?, " +
                     "hora_salida=?, estado=?, anticipo=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fechaEntrada);
            ps.setString(2, horaEntrada);
            ps.setString(3, fechaSalida);
            ps.setString(4, horaSalida);
            ps.setString(5, estado);
            ps.setDouble(6, anticipo);
            ps.setInt(7, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("actualizar reserva", e);
        }
    }

    public boolean actualizarAnticipo(int id, double nuevoAnticipo) {
        String sql = "UPDATE reservas SET anticipo = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, nuevoAnticipo);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("actualizar anticipo de reserva", e);
        }
    }

    public boolean actualizarEstado(int id, String nuevoEstado) {
        String sql = "UPDATE reservas SET estado = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("actualizar estado de reserva", e);
        }
    }

    public boolean finalizar(int id, double totalPagar) {
        String sql = "UPDATE reservas SET estado = 'Finalizada', total_pagar = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, totalPagar);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("finalizar reserva", e);
        }
    }

    public Reserva buscarPorId(int id) {
        String sql = "SELECT r.*, c.nombre AS cliente_nombre, c.documento AS cliente_doc " +
                     "FROM reservas r " +
                     "LEFT JOIN clientes c ON r.id_cliente = c.id " +
                     "WHERE r.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            throw new DatabaseException("buscar reserva por id", e);
        }
        return null;
    }

    public Reserva buscarActivaPorHabitacion(int idHabitacion) {
        String sql = "SELECT r.*, c.nombre AS cliente_nombre, c.documento AS cliente_doc " +
                     "FROM reservas r " +
                     "LEFT JOIN clientes c ON r.id_cliente = c.id " +
                     "WHERE r.id_habitacion = ? AND r.estado = 'Activa'" +
                     " AND r.fecha_entrada <= date('now') LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idHabitacion);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            throw new DatabaseException("buscar reserva activa por habitación", e);
        }
        return null;
    }

    public Reserva buscarProximaPorHabitacion(int idHabitacion) {
        String sql = "SELECT r.*, c.nombre AS cliente_nombre, c.documento AS cliente_doc " +
                     "FROM reservas r " +
                     "LEFT JOIN clientes c ON r.id_cliente = c.id " +
                     "WHERE r.id_habitacion = ? AND r.estado = 'Activa'" +
                     " AND r.fecha_entrada > date('now') ORDER BY r.fecha_entrada ASC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idHabitacion);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            throw new DatabaseException("buscar próxima reserva por habitación", e);
        }
        return null;
    }

    public List<Reserva> listarUltimasPorHabitacion(int idHabitacion, int limite) {
        List<Reserva> lista = new ArrayList<>();
        String sql = "SELECT r.*, c.nombre AS cliente_nombre, c.documento AS cliente_doc " +
                     "FROM reservas r " +
                     "LEFT JOIN clientes c ON r.id_cliente = c.id " +
                     "WHERE r.id_habitacion = ? ORDER BY r.id DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idHabitacion);
            ps.setInt(2, limite);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new DatabaseException("listar últimas reservas por habitación", e);
        }
        return lista;
    }

    private Reserva mapear(ResultSet rs) throws SQLException {
        String horaEnt = "12:00", horaSal = "12:00", tipo = "Noche";
        double anticipo = 0, totalPagar = 0;
        try { String v = rs.getString("hora_entrada"); if (v != null) horaEnt = v; } catch (SQLException ignored) {}
        try { String v = rs.getString("hora_salida");  if (v != null) horaSal = v; } catch (SQLException ignored) {}
        try { String v = rs.getString("tipo_estadia"); if (v != null) tipo    = v; } catch (SQLException ignored) {}
        try { anticipo = rs.getDouble("anticipo"); } catch (SQLException ignored) {}
        try { totalPagar = rs.getDouble("total_pagar"); } catch (SQLException ignored) {}
        return new Reserva(
            rs.getInt("id"),
            rs.getInt("id_habitacion"),
            rs.getInt("id_usuario"),
            rs.getString("cliente_nombre"),
            rs.getString("cliente_doc"),
            rs.getString("fecha_entrada"),
            horaEnt,
            rs.getString("fecha_salida"),
            horaSal,
            tipo,
            rs.getString("estado"),
            anticipo,
            totalPagar
        );
    }
    public List<Object[]> obtenerHistorialClientes() {
        List<Object[]> lista = new ArrayList<>();
        String sql = """
            SELECT
                c.nombre,
                c.documento,
                c.telefono,
                c.correo,
                MAX(r.id_habitacion),
                MAX(r.fecha_entrada),
                MAX(r.fecha_salida),
                rc.tipo_persona,
                (
                    SELECT ct.nombre
                    FROM reserva_clientes rct
                    INNER JOIN clientes ct
                        ON ct.id = rct.id_cliente
                    WHERE rct.id_reserva = r.id
                    AND rct.tipo_persona = 'Titular'
                    LIMIT 1
                ) AS nombre_titular
            FROM reserva_clientes rc
            INNER JOIN clientes c
                ON c.id = rc.id_cliente
            INNER JOIN reservas r
                ON r.id = rc.id_reserva
            GROUP BY c.id
            ORDER BY MAX(r.id) DESC
        """;

        try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Object[] fila = new Object[] {
                    rs.getString(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getInt(5),
                    rs.getString(6),
                    rs.getString(7),
                    rs.getString(8),
                    rs.getString(9)
                };

                lista.add(fila);
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
}