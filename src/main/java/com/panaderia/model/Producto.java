package com.panaderia.model;

/**
 * Entidad del dominio - Producto de la panadería.
 * Atributos alineados con RF-1 y el DER de la Tarea 2.
 */
public class Producto {

    private int id;
    private String nombre;
    private String categoria;
    private double precio;
    private int stock;
    private String descripcion;

    public Producto() {
    }

    public Producto(String nombre, String categoria, double precio, int stock, String descripcion) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
        this.descripcion = descripcion;
    }

    public Producto(int id, String nombre, String categoria, double precio, int stock, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
        this.descripcion = descripcion;
    }

    // --- Getters y Setters ---

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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Producto{id=" + id + ", nombre='" + nombre + "', categoria='" + categoria
                + "', precio=" + precio + ", stock=" + stock + "}";
    }
}
