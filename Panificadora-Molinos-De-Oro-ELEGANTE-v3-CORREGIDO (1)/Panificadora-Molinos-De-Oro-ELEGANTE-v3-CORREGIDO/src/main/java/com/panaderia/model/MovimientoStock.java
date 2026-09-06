package com.panaderia.model;

import java.time.LocalDateTime;

public class MovimientoStock {

    // --- Atributos de la Clase ---
    private int id;
    private int idProducto;
    private String producto;
    private String tipo; // Puede ser "INGRESO" o "EGRESO"
    private int cantidad;
    private LocalDateTime fecha;
    private String observacion;

    // --- Constructor Vacío ---
    public MovimientoStock() {
    }

    // --- Constructor Completo (Mapeo de Base de Datos) ---
    public MovimientoStock(int id, int idProducto, String producto, String tipo, int cantidad, LocalDateTime fecha, String observacion) {
        this.id = id;
        this.idProducto = idProducto;
        this.producto = producto;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.observacion = observacion;
    }

    // --- Métodos Getter (Encapsulamiento de Solo Lectura) ---

    public int getId() {
        return id;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public String getProducto() {
        return producto;
    }

    public String getTipo() {
        return tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getObservacion() {
        return observacion;
    }
}
