package com.panaderia.dao;

import com.panaderia.model.Categoria;
import com.panaderia.util.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

 /**
  * Consulta y devuelve todas las categorías ordenadas alfabéticamente.
  */
 public List<Categoria> listar() throws SQLException {
  List<Categoria> l = new ArrayList<>();
  String sql = "SELECT id, nombre, descripcion FROM categoria ORDER BY nombre";

  try (Connection c = ConexionDB.getConnection();
       PreparedStatement p = c.prepareStatement(sql);
       ResultSet r = p.executeQuery()) {

   while (r.next()) {
    // Si el constructor da error aquí, verifica el Paso 2 de abajo
    l.add(new Categoria(r.getInt(1), r.getString(2), r.getString(3)));
   }
  }
  return l;
 }

 /**
  * Inserta una nueva categoría y retorna el ID autogenerado por la base de datos.
  */
 public int insertar(Categoria x) throws SQLException {
  String sql = "INSERT INTO categoria(nombre, descripcion) VALUES(?, ?)";

  try (Connection c = ConexionDB.getConnection();
       PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

   p.setString(1, x.getNombre());
   p.setString(2, x.getDescripcion());
   p.executeUpdate();

   try (ResultSet r = p.getGeneratedKeys()) {
    return r.next() ? r.getInt(1) : -1;
   }
  }
 }

 /**
  * Actualiza los datos de una categoría existente según su ID.
  */
 public boolean actualizar(Categoria x) throws SQLException {
  String sql = "UPDATE categoria SET nombre = ?, descripcion = ? WHERE id = ?";

  try (Connection c = ConexionDB.getConnection();
       PreparedStatement p = c.prepareStatement(sql)) {

   p.setString(1, x.getNombre());
   p.setString(2, x.getDescripcion());
   p.setInt(3, x.getId());

   return p.executeUpdate() > 0;
  }
 }

 /**
  * Elimina una categoría de la base de datos según su ID.
  */
 public boolean eliminar(int id) throws SQLException {
  String sql = "DELETE FROM categoria WHERE id = ?";

  try (Connection c = ConexionDB.getConnection();
       PreparedStatement p = c.prepareStatement(sql)) {

   p.setInt(1, id);
   return p.executeUpdate() > 0;
  }
 }
}
