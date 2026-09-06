package com.panaderia.model;

import java.time.LocalDate;

public class Pedido {

    // --- Atributos de la Clase ---
    private int id;
    private String cliente;
    private String telefono;
    private LocalDate fechaEntrega;
    private String estado; // Ejemplo: "PENDIENTE", "ENTREGADO", "CANCELADO"
    private String observaciones;

    // --- Constructor Vacío ---
    public Pedido() {
    }

    // --- Constructor Completo (Mapeo de Base de Datos) ---
    public Pedido(int id, String cliente, String telefono, LocalDate fechaEntrega, String estado, String observaciones) {
        this.id = id;
        this.cliente = cliente;
        this.telefono = telefono;
        this.fechaEntrega = fechaEntrega;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    // --- Métodos Getter y Setter (Encapsulamiento Completo) ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDate getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDate fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
