-- ============================================================
-- Script SQL – Panificadora Molinos de Oro (v3)
-- Base de datos: DBPanaderia
-- Ejecutar en MySQL / MariaDB (opcional; la app también crea tablas)
-- ============================================================

CREATE DATABASE IF NOT EXISTS DBPanaderia
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE DBPanaderia;

-- Productos
CREATE TABLE IF NOT EXISTS producto (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  nombre      VARCHAR(100)  NOT NULL,
  categoria   VARCHAR(50)   NOT NULL,
  precio      DECIMAL(10,2) NOT NULL DEFAULT 0,
  stock       INT           NOT NULL DEFAULT 0,
  stock_minimo INT          NOT NULL DEFAULT 10,
  descripcion VARCHAR(255)  NULL,
  fecha_alta  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Categorías
CREATE TABLE IF NOT EXISTS categoria (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(80) NOT NULL UNIQUE,
  descripcion VARCHAR(255)
) ENGINE=InnoDB;

-- Usuarios (referencia; el login de la app usa credenciales fijas)
CREATE TABLE IF NOT EXISTS usuario (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(120) NOT NULL,
  usuario_login VARCHAR(60) NOT NULL UNIQUE,
  clave VARCHAR(120) NOT NULL,
  rol VARCHAR(30) NOT NULL
) ENGINE=InnoDB;

-- Movimientos de stock
CREATE TABLE IF NOT EXISTS movimiento_stock (
  id INT AUTO_INCREMENT PRIMARY KEY,
  id_producto INT NOT NULL,
  tipo VARCHAR(20) NOT NULL,
  cantidad INT NOT NULL,
  fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  observacion VARCHAR(255),
  FOREIGN KEY (id_producto) REFERENCES producto(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Pedidos
CREATE TABLE IF NOT EXISTS pedido (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre_cliente VARCHAR(120) NOT NULL,
  telefono VARCHAR(30) NOT NULL,
  fecha_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_entrega DATE NOT NULL,
  estado VARCHAR(30) NOT NULL DEFAULT 'Pendiente',
  observaciones VARCHAR(500),
  id_usuario_registro INT NULL
) ENGINE=InnoDB;

-- Detalle de pedidos
CREATE TABLE IF NOT EXISTS detalle_pedido (
  id INT AUTO_INCREMENT PRIMARY KEY,
  id_pedido INT NOT NULL,
  id_producto INT NOT NULL,
  cantidad INT NOT NULL,
  precio_unitario DECIMAL(10,2) NOT NULL,
  FOREIGN KEY (id_pedido) REFERENCES pedido(id) ON DELETE CASCADE,
  FOREIGN KEY (id_producto) REFERENCES producto(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Datos iniciales
INSERT IGNORE INTO categoria(nombre, descripcion) VALUES
('Pan', 'Panes y productos de panadería'),
('Pastel', 'Pasteles y tortas'),
('Galleta', 'Galletas'),
('Otro', 'Otros productos');

INSERT IGNORE INTO usuario(nombre, usuario_login, clave, rol) VALUES
('Administrador', 'admin', 'admin123', 'ADMIN'),
('Producción', 'produccion', '1234', 'PRODUCCION'),
('Mostrador', 'mostrador', '1234', 'MOSTRADOR');

-- Productos de ejemplo (solo si no hay ninguno)
INSERT INTO producto (nombre, categoria, precio, stock, stock_minimo, descripcion)
SELECT * FROM (
  SELECT 'Pan francés' AS nombre, 'Pan' AS categoria, 0.50 AS precio, 120 AS stock, 20 AS stock_minimo, 'Pan crujiente de barra' AS descripcion
  UNION ALL SELECT 'Torta chocolate', 'Pastel', 15.00, 8, 3, 'Torta de 8 porciones'
  UNION ALL SELECT 'Galleta avena', 'Galleta', 0.80, 45, 15, 'Galleta integral'
  UNION ALL SELECT 'Pan integral', 'Pan', 0.70, 80, 20, 'Pan de trigo integral'
  UNION ALL SELECT 'Pastel vainilla', 'Pastel', 12.00, 5, 2, 'Pastel clásico'
  UNION ALL SELECT 'Bollo dulce', 'Otro', 0.60, 30, 10, 'Bollo de mantequilla'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM producto LIMIT 1);
