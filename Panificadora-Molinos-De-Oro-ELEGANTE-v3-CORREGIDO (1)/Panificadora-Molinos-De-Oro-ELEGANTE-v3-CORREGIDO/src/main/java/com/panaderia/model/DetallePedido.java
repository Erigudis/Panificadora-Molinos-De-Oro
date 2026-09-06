package com.panaderia.model;

public class DetallePedido {

    private int idProducto;
    private String producto;
    private int cantidad;
    private double precioUnitario;

    public DetallePedido() {
    }

    public DetallePedido(int idProducto, String producto, int cantidad, double precioUnitario) {
        this.idProducto = idProducto;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    /** Subtotal = cantidad × precio unitario */
    public double getSubtotal() {
        return cantidad * precioUnitario;
    }
}
