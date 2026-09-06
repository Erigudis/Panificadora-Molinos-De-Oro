package com.panaderia.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Crea la base de datos y las tablas si no existen.
 * Se ejecuta al arrancar la aplicación.
 */
public final class DatabaseInitializer {

    private DatabaseInitializer() {}

    public static void initialize() throws Exception {
        // 1) Crear la base de datos si no existe (conexión sin schema)
        String baseUrl = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        try (Connection cn = DriverManager.getConnection(baseUrl, "root", "");
             Statement st = cn.createStatement()) {
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS DBPanaderia CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        }

        // 2) Crear tablas e insertar datos base
        try (Connection cn = ConexionDB.getConnection(); Statement st = cn.createStatement()) {
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS producto (
                  id INT AUTO_INCREMENT PRIMARY KEY,
                  nombre VARCHAR(100) NOT NULL,
                  categoria VARCHAR(50) NOT NULL,
                  precio DECIMAL(10,2) NOT NULL DEFAULT 0,
                  stock INT NOT NULL DEFAULT 0,
                  stock_minimo INT NOT NULL DEFAULT 10,
                  descripcion VARCHAR(255)
                ) ENGINE=InnoDB
                """);

            // Por si la tabla ya existía sin stock_minimo
            try {
                st.executeUpdate("ALTER TABLE producto ADD COLUMN stock_minimo INT NOT NULL DEFAULT 10");
            } catch (Exception ignored) {}

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS categoria (
                  id INT AUTO_INCREMENT PRIMARY KEY,
                  nombre VARCHAR(80) NOT NULL UNIQUE,
                  descripcion VARCHAR(255)
                ) ENGINE=InnoDB
                """);

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS movimiento_stock (
                  id INT AUTO_INCREMENT PRIMARY KEY,
                  id_producto INT NOT NULL,
                  tipo VARCHAR(20) NOT NULL,
                  cantidad INT NOT NULL,
                  fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                  observacion VARCHAR(255),
                  FOREIGN KEY (id_producto) REFERENCES producto(id) ON DELETE RESTRICT
                ) ENGINE=InnoDB
                """);

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS pedido (
                  id INT AUTO_INCREMENT PRIMARY KEY,
                  nombre_cliente VARCHAR(120) NOT NULL,
                  telefono VARCHAR(30) NOT NULL,
                  fecha_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                  fecha_entrega DATE NOT NULL,
                  estado VARCHAR(30) NOT NULL DEFAULT 'Pendiente',
                  observaciones VARCHAR(500),
                  id_usuario_registro INT NULL
                ) ENGINE=InnoDB
                """);

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS detalle_pedido (
                  id INT AUTO_INCREMENT PRIMARY KEY,
                  id_pedido INT NOT NULL,
                  id_producto INT NOT NULL,
                  cantidad INT NOT NULL,
                  precio_unitario DECIMAL(10,2) NOT NULL,
                  FOREIGN KEY (id_pedido) REFERENCES pedido(id) ON DELETE CASCADE,
                  FOREIGN KEY (id_producto) REFERENCES producto(id) ON DELETE RESTRICT
                ) ENGINE=InnoDB
                """);

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS usuario (
                  id INT AUTO_INCREMENT PRIMARY KEY,
                  nombre VARCHAR(120) NOT NULL,
                  usuario_login VARCHAR(60) NOT NULL UNIQUE,
                  clave VARCHAR(120) NOT NULL,
                  rol VARCHAR(30) NOT NULL
                ) ENGINE=InnoDB
                """);

            st.executeUpdate("""
                INSERT IGNORE INTO categoria(nombre, descripcion) VALUES
                ('Pan', 'Panes y productos de panadería'),
                ('Pastel', 'Pasteles y tortas'),
                ('Galleta', 'Galletas'),
                ('Otro', 'Otros productos')
                """);

            st.executeUpdate("""
                INSERT IGNORE INTO usuario(nombre, usuario_login, clave, rol) VALUES
                ('Administrador', 'admin', 'admin123', 'ADMIN'),
                ('Producción', 'produccion', '1234', 'PRODUCCION'),
                ('Mostrador', 'mostrador', '1234', 'MOSTRADOR')
                """);

            // Datos de ejemplo solo si la tabla producto está vacía
            try (var rs = st.executeQuery("SELECT COUNT(*) FROM producto")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    st.executeUpdate("""
                        INSERT INTO producto (nombre, categoria, precio, stock, stock_minimo, descripcion) VALUES
                        ('Pan francés', 'Pan', 0.50, 120, 20, 'Pan crujiente de barra'),
                        ('Torta chocolate', 'Pastel', 15.00, 8, 3, 'Torta de 8 porciones'),
                        ('Galleta avena', 'Galleta', 0.80, 45, 15, 'Galleta integral'),
                        ('Pan integral', 'Pan', 0.70, 80, 20, 'Pan de trigo integral'),
                        ('Pastel vainilla', 'Pastel', 12.00, 5, 2, 'Pastel clásico'),
                        ('Bollo dulce', 'Otro', 0.60, 30, 10, 'Bollo de mantequilla')
                        """);
                }
            }
        }
    }
}
