package com.panaderia.service;

import com.panaderia.dao.CategoriaDAO;
import com.panaderia.model.Categoria;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public class CategoriaService {

    // --- Atributos de Acceso a Datos (DAO) ---
    private final CategoriaDAO dao = new CategoriaDAO();

    // --- Métodos de la Capa de Negocio ---

    /**
     * Obtiene la lista completa de categorías desde la base de datos.
     */
    public List<Categoria> listar() throws SQLException {
        return dao.listar();
    }

    /**
     * Procesa las reglas de negocio para registrar o actualizar una categoría.
     */
    public String guardar(Categoria c) throws SQLException {
        // Regla 1: Validar que el nombre no esté vacío
        if (c.getNombre() == null || c.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio.");
        }

        // Limpiar espacios en blanco innecesarios
        c.setNombre(c.getNombre().trim());

        // Regla 2: Si el ID es 0 o menor, es un registro nuevo (Inserción)
        if (c.getId() <= 0) {
            int id = dao.insertar(c);
            return "Categoría registrada correctamente (ID " + id + ").";
        }

        // Regla 3: Si tiene ID, es una actualización
        if (!dao.actualizar(c)) {
            throw new SQLException("No se pudo actualizar la categoría.");
        }

        return "Categoría actualizada correctamente.";
    }

    /**
     * Elimina una categoría solo si no tiene productos asociados.
     */
    public String eliminar(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Seleccione una categoría.");
        }
        // Verificar productos que usan el nombre de esta categoría
        Categoria cat = null;
        for (Categoria c : dao.listar()) {
            if (c.getId() == id) {
                cat = c;
                break;
            }
        }
        if (cat == null) {
            throw new SQLException("No se encontró la categoría.");
        }
        try (var cn = com.panaderia.util.ConexionDB.getConnection();
             var ps = cn.prepareStatement("SELECT COUNT(*) FROM producto WHERE categoria = ?")) {
            ps.setString(1, cat.getNombre());
            try (var rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new IllegalArgumentException(
                            "No se puede eliminar la categoría porque tiene productos asociados.");
                }
            }
        }
        if (!dao.eliminar(id)) {
            throw new SQLException("No se pudo eliminar la categoría.");
        }
        return "Categoría eliminada correctamente.";
    }
}
