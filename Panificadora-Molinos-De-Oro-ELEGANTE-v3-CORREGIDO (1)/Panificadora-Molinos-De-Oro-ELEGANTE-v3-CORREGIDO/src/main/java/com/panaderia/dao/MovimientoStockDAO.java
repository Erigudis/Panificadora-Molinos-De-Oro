package com.panaderia.dao;

import com.panaderia.model.MovimientoStock;
import com.panaderia.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MovimientoStockDAO {

 public void registrar(int idProducto, String tipo, int cantidad, String obs) throws SQLException {
  try (Connection c = ConexionDB.getConnection()) {
   c.setAutoCommit(false);
   try (PreparedStatement p = c.prepareStatement(
           "SELECT stock FROM producto WHERE id = ? FOR UPDATE")) {
    p.setInt(1, idProducto);
    try (ResultSet r = p.executeQuery()) {
     if (!r.next()) {
      throw new SQLException("Producto no encontrado.");
     }

     int stock = r.getInt(1);
     int nuevo = switch (tipo) {
      case "ENTRADA" -> stock + cantidad;
      case "SALIDA"  -> stock - cantidad;
      default -> throw new IllegalArgumentException(
              "Tipo de movimiento inválido: " + tipo);
     };

     if (nuevo < 0) {
      throw new IllegalArgumentException(
              "Stock insuficiente. Disponible: " + stock + ".");
     }

     try (PreparedStatement u = c.prepareStatement(
             "UPDATE producto SET stock = ? WHERE id = ?")) {
      u.setInt(1, nuevo);
      u.setInt(2, idProducto);
      u.executeUpdate();
     }

     try (PreparedStatement i = c.prepareStatement(
             "INSERT INTO movimiento_stock(id_producto, tipo, cantidad, observacion) "
                     + "VALUES (?, ?, ?, ?)")) {
      i.setInt(1, idProducto);
      i.setString(2, tipo);
      i.setInt(3, cantidad);
      i.setString(4, obs);
      i.executeUpdate();
     }

     c.commit();
    }
   } catch (Exception e) {
    try {
     c.rollback();
    } catch (SQLException ignored) {
    }
    if (e instanceof SQLException) throw (SQLException) e;
    if (e instanceof RuntimeException) throw (RuntimeException) e;
    throw new SQLException(e);
   }
  }
 }

 public List<MovimientoStock> listar() throws SQLException {
  List<MovimientoStock> lista = new ArrayList<>();
  String q = """
                SELECT m.id, m.id_producto, p.nombre, m.tipo, m.cantidad, m.fecha, m.observacion
                FROM movimiento_stock m
                JOIN producto p ON p.id = m.id_producto
                ORDER BY m.fecha DESC
                """;

  try (Connection c = ConexionDB.getConnection();
       PreparedStatement p = c.prepareStatement(q);
       ResultSet r = p.executeQuery()) {

   while (r.next()) {
    lista.add(new MovimientoStock(
            r.getInt(1),
            r.getInt(2),
            r.getString(3),
            r.getString(4),
            r.getInt(5),
            r.getTimestamp(6).toLocalDateTime(),
            r.getString(7)));
   }
  }
  return lista;
 }
}