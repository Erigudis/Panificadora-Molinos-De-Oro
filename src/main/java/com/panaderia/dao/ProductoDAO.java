package com.panaderia.dao;

import com.panaderia.model.Producto;
import com.panaderia.util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos del producto (JDBC + PreparedStatement) – v2.
 * CRUD completo + búsqueda por id + actualización de stock.
 */
public class ProductoDAO {

    private static final String SQL_LISTAR =
            "SELECT id, nombre, categoria, precio, stock, descripcion FROM producto ORDER BY id DESC";
    private static final String SQL_BUSCAR_ID =
            "SELECT id, nombre, categoria, precio, stock, descripcion FROM producto WHERE id=?";
    private static final String SQL_INSERTAR =
            "INSERT INTO producto (nombre, categoria, precio, stock, descripcion) VALUES (?,?,?,?,?)";
    private static final String SQL_ACTUALIZAR =
            "UPDATE producto SET nombre=?, categoria=?, precio=?, stock=?, descripcion=? WHERE id=?";
    private static final String SQL_ACTUALIZAR_STOCK =
            "UPDATE producto SET stock=? WHERE id=?";
    private static final String SQL_ELIMINAR =
            "DELETE FROM producto WHERE id=?";

    public List<Producto> listarTodos() throws SQLException {
        List<Producto> lista = new ArrayList<>();
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Producto buscarPorId(int id) throws SQLException {
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_BUSCAR_ID)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public int insertar(Producto p) throws SQLException {
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getCategoria());
            ps.setDouble(3, p.getPrecio());
            ps.setInt(4, p.getStock());
            ps.setString(5, p.getDescripcion());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return -1;
    }

    public boolean actualizar(Producto p) throws SQLException {
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_ACTUALIZAR)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getCategoria());
            ps.setDouble(3, p.getPrecio());
            ps.setInt(4, p.getStock());
            ps.setString(5, p.getDescripcion());
            ps.setInt(6, p.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizarStock(int id, int nuevoStock) throws SQLException {
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_ACTUALIZAR_STOCK)) {

            ps.setInt(1, nuevoStock);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_ELIMINAR)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Producto mapear(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("categoria"),
                rs.getDouble("precio"),
                rs.getInt("stock"),
                rs.getString("descripcion")
        );
    }
}
