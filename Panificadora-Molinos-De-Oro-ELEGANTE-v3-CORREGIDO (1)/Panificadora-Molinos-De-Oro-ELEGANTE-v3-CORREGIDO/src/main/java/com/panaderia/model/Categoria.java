package com.panaderia.model;

public class Categoria {

    // --- Atributos de la Clase ---
    private int id;
    private String nombre;
    private String descripcion;

    // --- Constructor Vacío (Obligatorio para frameworks e interfaces) ---
    public Categoria() {
    }

    // --- Constructor Completo (El que soluciona el error en tu CategoriaDAO) ---
    public Categoria(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // --- Métodos Getter y Setter (Encapsulamiento) ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
