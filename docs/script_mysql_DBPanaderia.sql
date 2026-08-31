-- ============================================================
-- Script SQL – Panificadora Molinos de Oro (v1)
-- Base de datos: DBPanaderia
-- Ejecutar en MySQL / MariaDB antes de arrancar la aplicación
-- ============================================================

CREATE DATABASE IF NOT EXISTS DBPanaderia
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE DBPanaderia;

-- Tabla de productos (módulo catálogo v1)
DROP TABLE IF EXISTS producto;

CREATE TABLE producto (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  nombre      VARCHAR(100)  NOT NULL,
  categoria   VARCHAR(50)   NOT NULL,
  precio      DECIMAL(10,2) NOT NULL CHECK (precio >= 0),
  stock       INT           NOT NULL DEFAULT 0 CHECK (stock >= 0),
  descripcion VARCHAR(255)  NULL,
  fecha_alta  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Datos de ejemplo para pruebas
INSERT INTO producto (nombre, categoria, precio, stock, descripcion) VALUES
('Pan francés',     'Pan',     0.50, 120, 'Pan crujiente de barra'),
('Torta chocolate', 'Pastel', 15.00,   8, 'Torta de 8 porciones'),
('Galleta avena',   'Galleta', 0.80,  45, 'Galleta integral'),
('Pan integral',    'Pan',     0.70,  80, 'Pan de trigo integral'),
('Pastel vainilla', 'Pastel', 12.00,   5, 'Pastel clásico'),
('Bollo dulce',     'Otro',    0.60,  30, 'Bollo de mantequilla');

-- Verificar
SELECT * FROM producto;
