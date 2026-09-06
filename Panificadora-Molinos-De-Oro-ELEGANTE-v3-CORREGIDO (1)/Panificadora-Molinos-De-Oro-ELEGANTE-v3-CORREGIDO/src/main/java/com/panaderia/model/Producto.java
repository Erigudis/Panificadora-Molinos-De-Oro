package com.panaderia.model;

public class Producto {
    private int id; private String nombre; private String categoria; private double precio; private int stock; private int stockMinimo=10; private String descripcion;
    public Producto(){}
    public Producto(String nombre,String categoria,double precio,int stock,int stockMinimo,String descripcion){this.nombre=nombre;this.categoria=categoria;this.precio=precio;this.stock=stock;this.stockMinimo=stockMinimo;this.descripcion=descripcion;}
    public Producto(int id,String nombre,String categoria,double precio,int stock,int stockMinimo,String descripcion){this.id=id;this.nombre=nombre;this.categoria=categoria;this.precio=precio;this.stock=stock;this.stockMinimo=stockMinimo;this.descripcion=descripcion;}
    public int getId(){return id;} public void setId(int v){id=v;} public String getNombre(){return nombre;} public void setNombre(String v){nombre=v;} public String getCategoria(){return categoria;} public void setCategoria(String v){categoria=v;} public double getPrecio(){return precio;} public void setPrecio(double v){precio=v;} public int getStock(){return stock;} public void setStock(int v){stock=v;} public int getStockMinimo(){return stockMinimo;} public void setStockMinimo(int v){stockMinimo=v;} public String getDescripcion(){return descripcion;} public void setDescripcion(String v){descripcion=v;}
    public boolean isStockBajo(){return stock<=stockMinimo;}
    @Override public String toString(){return nombre;}
}
