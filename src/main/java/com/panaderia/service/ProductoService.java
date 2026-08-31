package com.panaderia.service;

import com.panaderia.dao.ProductoDAO;
import com.panaderia.model.Producto;

import java.sql.SQLException;
import java.util.List;

/**
 * Lógica de negocio y validaciones del módulo de productos (v2).
 * Incluye CRUD + ajuste de stock (entrada/salida).
 */
public class ProductoService {

    private final ProductoDAO dao = new ProductoDAO();

    public List<Producto> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    /**
     * Valida y guarda (inserta o actualiza) un producto.
     */
    public String guardar(Producto p) throws SQLException {
        validar(p);

        if (p.getId() <= 0) {
            int nuevoId = dao.insertar(p);
            p.setId(nuevoId);
            return "Producto registrado correctamente (ID " + nuevoId + ").";
        } else {
            boolean ok = dao.actualizar(p);
            if (!ok) {
                throw new SQLException("No se pudo actualizar el producto con ID " + p.getId());
            }
            return "Producto actualizado correctamente.";
        }
    }

    public String eliminar(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Seleccione un producto de la tabla para eliminar.");
        }
        boolean ok = dao.eliminar(id);
        if (!ok) {
            throw new SQLException("No se encontró el producto con ID " + id);
        }
        return "Producto eliminado correctamente.";
    }

    /**
     * Ajuste de stock v2: entrada (suma) o salida (resta).
     * No permite stock negativo.
     */
    public String ajustarStock(int id, int cantidad, boolean entrada) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Seleccione un producto de la tabla.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        }

        Producto p = dao.buscarPorId(id);
        if (p == null) {
            throw new SQLException("No se encontró el producto con ID " + id);
        }

        int nuevoStock;
        if (entrada) {
            nuevoStock = p.getStock() + cantidad;
        } else {
            nuevoStock = p.getStock() - cantidad;
            if (nuevoStock < 0) {
                throw new IllegalArgumentException(
                        "Stock insuficiente. Disponible: " + p.getStock() + ". No se puede restar " + cantidad + ".");
            }
        }

        boolean ok = dao.actualizarStock(id, nuevoStock);
        if (!ok) {
            throw new SQLException("No se pudo actualizar el stock del producto ID " + id);
        }

        String tipo = entrada ? "Entrada" : "Salida";
        return tipo + " de stock registrada. Nuevo stock de \"" + p.getNombre() + "\": " + nuevoStock;
    }

    /**
     * Validaciones de negocio (RN-1 / RF-6 / RF-7) – reforzadas en v2.
     */
    private void validar(Producto p) {
        if (p.getNombre() == null || p.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        if (p.getCategoria() == null || p.getCategoria().trim().isEmpty()) {
            throw new IllegalArgumentException("La categoría es obligatoria.");
        }
        if (p.getPrecio() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        }
        if (p.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
    }
}
