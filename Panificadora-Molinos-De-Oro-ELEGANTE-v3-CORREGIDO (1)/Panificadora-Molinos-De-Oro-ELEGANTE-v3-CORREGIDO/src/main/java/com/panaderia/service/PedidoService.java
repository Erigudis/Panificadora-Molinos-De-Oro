package com.panaderia.service;

import com.panaderia.dao.PedidoDAO;
import com.panaderia.model.DetallePedido;
import com.panaderia.model.Pedido;
import com.panaderia.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PedidoService {

    private final PedidoDAO dao = new PedidoDAO();

    public String guardar(Pedido p, List<DetallePedido> d) throws SQLException {
        if (p.getCliente() == null || p.getCliente().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio.");
        }
        if (p.getTelefono() == null || !p.getTelefono().matches("[0-9+ -]{7,15}")) {
            throw new IllegalArgumentException("Ingrese un teléfono válido.");
        }
        if (p.getFechaEntrega() == null) {
            throw new IllegalArgumentException("La fecha de entrega es obligatoria.");
        }
        if (d == null || d.isEmpty()) {
            throw new IllegalArgumentException("Agregue al menos un producto al pedido.");
        }
        for (DetallePedido x : d) {
            if (x.getCantidad() <= 0) {
                throw new IllegalArgumentException("Las cantidades deben ser mayores a 0.");
            }
        }
        int id = dao.insertar(p, d);
        return "Pedido #" + id + " registrado correctamente.";
    }

    public List<Pedido> listar() throws SQLException {
        return dao.listar();
    }

    public List<DetallePedido> detalles(int id) throws SQLException {
        return dao.detalles(id);
    }

    /**
     * Cambia el estado del pedido respetando el flujo:
     * Pendiente → En preparación | Cancelado
     * En preparación → Listo | Cancelado
     * Listo → Entregado
     *
     * Al pasar a "Listo" se descuenta el stock y se registra un movimiento SALIDA.
     */
    public String cambiarEstado(Pedido p, String nuevo) throws SQLException {
        if (p == null) {
            throw new IllegalArgumentException("Seleccione un pedido.");
        }

        String actual = p.getEstado();
        Set<String> validos = new LinkedHashSet<>();

        if ("Pendiente".equals(actual)) {
            validos.addAll(Arrays.asList("En preparación", "Cancelado"));
        } else if ("En preparación".equals(actual)) {
            validos.addAll(Arrays.asList("Listo", "Cancelado"));
        } else if ("Listo".equals(actual)) {
            validos.add("Entregado");
        }

        if (!validos.contains(nuevo)) {
            throw new IllegalArgumentException("Transición no permitida desde " + actual + ".");
        }

        // Al marcar como Listo: verificar y descontar stock
        if ("Listo".equals(nuevo)) {
            descontarStockPedido(p.getId());
        }

        if (!dao.estado(p.getId(), nuevo)) {
            throw new SQLException("No se pudo actualizar el estado.");
        }

        p.setEstado(nuevo);
        return "Pedido #" + p.getId() + " actualizado a " + nuevo + ".";
    }

    private void descontarStockPedido(int idPedido) throws SQLException {
        List<DetallePedido> detalles = dao.detalles(idPedido);

        try (Connection c = ConexionDB.getConnection()) {
            c.setAutoCommit(false);
            try {
                for (DetallePedido d : detalles) {
                    try (PreparedStatement q = c.prepareStatement(
                            "SELECT stock FROM producto WHERE id = ? FOR UPDATE")) {
                        q.setInt(1, d.getIdProducto());
                        try (ResultSet r = q.executeQuery()) {
                            if (!r.next()) {
                                throw new IllegalArgumentException("Producto no encontrado: " + d.getProducto());
                            }
                            int stock = r.getInt(1);
                            if (stock < d.getCantidad()) {
                                throw new IllegalArgumentException(
                                        "Stock insuficiente para " + d.getProducto()
                                                + ". Disponible: " + stock + ", solicitado: " + d.getCantidad() + ".");
                            }
                            int nuevoStock = stock - d.getCantidad();

                            try (PreparedStatement u = c.prepareStatement(
                                    "UPDATE producto SET stock = ? WHERE id = ?")) {
                                u.setInt(1, nuevoStock);
                                u.setInt(2, d.getIdProducto());
                                u.executeUpdate();
                            }

                            try (PreparedStatement i = c.prepareStatement(
                                    "INSERT INTO movimiento_stock(id_producto, tipo, cantidad, observacion) VALUES (?,?,?,?)")) {
                                i.setInt(1, d.getIdProducto());
                                i.setString(2, "SALIDA");
                                i.setInt(3, d.getCantidad());
                                i.setString(4, "Descuento por pedido #" + idPedido + " (Listo)");
                                i.executeUpdate();
                            }
                        }
                    }
                }
                c.commit();
            } catch (Exception e) {
                try {
                    c.rollback();
                } catch (SQLException ignored) {}
                if (e instanceof SQLException) throw (SQLException) e;
                if (e instanceof RuntimeException) throw (RuntimeException) e;
                throw new SQLException(e);
            }
        }
    }
}
