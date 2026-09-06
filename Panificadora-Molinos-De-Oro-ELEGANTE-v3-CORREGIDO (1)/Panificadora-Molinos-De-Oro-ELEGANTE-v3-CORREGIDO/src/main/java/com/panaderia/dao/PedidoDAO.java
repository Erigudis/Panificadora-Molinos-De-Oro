package com.panaderia.dao;

import com.panaderia.model.*;
import com.panaderia.util.ConexionDB;
import java.sql.*;
import java.time.*;
import java.util.*;

public class PedidoDAO {

 public int insertar(Pedido p, List<DetallePedido> d) throws SQLException {
  try (Connection c = ConexionDB.getConnection()) {

   c.setAutoCommit(false);

   try (PreparedStatement q = c.prepareStatement(
           "INSERT INTO pedido(nombre_cliente,telefono,fecha_entrega,estado,observaciones) VALUES(?,?,?,?,?)",
           Statement.RETURN_GENERATED_KEYS)) {

    q.setString(1, p.getCliente());
    q.setString(2, p.getTelefono());
    q.setDate(3, java.sql.Date.valueOf(p.getFechaEntrega()));
    q.setString(4, "Pendiente");
    q.setString(5, p.getObservaciones());

    q.executeUpdate();

    int id;

    try (ResultSet r = q.getGeneratedKeys()) {
     if (!r.next()) {
      throw new SQLException("No se pudo crear el pedido.");
     }

     id = r.getInt(1);
    }

    try (PreparedStatement x = c.prepareStatement(
            "INSERT INTO detalle_pedido(id_pedido,id_producto,cantidad,precio_unitario) VALUES(?,?,?,?)")) {

     for (DetallePedido z : d) {
      x.setInt(1, id);
      x.setInt(2, z.getIdProducto());
      x.setInt(3, z.getCantidad());
      x.setDouble(4, z.getPrecioUnitario());
      x.addBatch();
     }

     x.executeBatch();
    }

    c.commit();
    return id;

   } catch (Exception e) {

    try {
     c.rollback();
    } catch (SQLException ignored) {
    }

    if (e instanceof SQLException) {
     throw (SQLException) e;
    }

    if (e instanceof RuntimeException) {
     throw (RuntimeException) e;
    }

    throw new SQLException(e);
   }
  }
 }

 public List<Pedido> listar() throws SQLException {

  List<Pedido> l = new ArrayList<>();

  String q = "SELECT id,nombre_cliente,telefono,fecha_entrega,estado,observaciones " +
          "FROM pedido ORDER BY fecha_entrega,id DESC";

  try (Connection c = ConexionDB.getConnection();
       PreparedStatement p = c.prepareStatement(q);
       ResultSet r = p.executeQuery()) {

   while (r.next()) {
    l.add(new Pedido(
            r.getInt(1),
            r.getString(2),
            r.getString(3),
            r.getDate(4).toLocalDate(),
            r.getString(5),
            r.getString(6)
    ));
   }
  }

  return l;
 }

 public List<DetallePedido> detalles(int id) throws SQLException {

  List<DetallePedido> l = new ArrayList<>();

  String q = "SELECT d.id_producto,p.nombre,d.cantidad,d.precio_unitario " +
          "FROM detalle_pedido d " +
          "JOIN producto p ON p.id=d.id_producto " +
          "WHERE d.id_pedido=?";

  try (Connection c = ConexionDB.getConnection();
       PreparedStatement p = c.prepareStatement(q)) {

   p.setInt(1, id);

   try (ResultSet r = p.executeQuery()) {

    while (r.next()) {
     l.add(new DetallePedido(
             r.getInt(1),
             r.getString(2),
             r.getInt(3),
             r.getDouble(4)
     ));
    }
   }
  }

  return l;
 }

 public boolean estado(int id, String estado) throws SQLException {

  try (Connection c = ConexionDB.getConnection();
       PreparedStatement p = c.prepareStatement(
               "UPDATE pedido SET estado=? WHERE id=?")) {

   p.setString(1, estado);
   p.setInt(2, id);

   return p.executeUpdate() > 0;
  }
 }

 public Pedido buscar(int id) throws SQLException {

  try (Connection c = ConexionDB.getConnection();
       PreparedStatement p = c.prepareStatement(
               "SELECT id,nombre_cliente,telefono,fecha_entrega,estado,observaciones " +
                       "FROM pedido WHERE id=?")) {

   p.setInt(1, id);

   try (ResultSet r = p.executeQuery()) {

    return r.next()
            ? new Pedido(
            r.getInt(1),
            r.getString(2),
            r.getString(3),
            r.getDate(4).toLocalDate(),
            r.getString(5),
            r.getString(6)
    )
            : null;
   }
  }
 }
}

