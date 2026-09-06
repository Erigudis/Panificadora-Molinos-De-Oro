# Panificadora Molinos de Oro

Aplicación de escritorio desarrollada con **Java 17**, **JavaFX 21**, **Maven** y **MySQL** para gestionar productos, categorías, movimientos de stock y pedidos de pasteles.

## Módulos

- **Acceso por rol**: Administrador, Producción y Mostrador.
- **Productos**: registrar, editar, eliminar, buscar y filtrar.
- **Categorías**: crear, editar y eliminar con validación de dependencias.
- **Movimientos de stock**: entradas, salidas e historial.
- **Pedidos de pasteles**: datos del cliente, productos, cantidades, fecha y estado.
- **Consulta rápida**: búsqueda y filtros de disponibilidad.

## Roles y credenciales

| Rol            | Usuario     | Contraseña |
|----------------|-------------|------------|
| Administrador  | admin       | admin123   |
| Producción     | produccion  | 1234       |
| Mostrador      | mostrador   | 1234       |

### Permisos por rol

| Módulo      | Administrador | Producción | Mostrador |
|-------------|:-------------:|:----------:|:---------:|
| Productos   | ✓             | ✓          | ✓         |
| Categorías  | ✓             | —          | —         |
| Stock       | ✓             | ✓          | —         |
| Pedidos     | ✓             | ✓          | ✓         |
| Consulta    | ✓             | ✓          | ✓         |

## Validaciones

La aplicación valida campos obligatorios, números, valores no negativos, stock insuficiente, teléfonos, fechas, líneas de pedido, transiciones de estado y filtros de consulta. Cuando un filtro no encuentra coincidencias se informa al usuario sin interrumpir la aplicación.

### Flujo de estados de pedido

```
Pendiente → En preparación → Listo → Entregado
     ↘         ↘
   Cancelado  Cancelado
```

Al pasar a **Listo** se descuenta automáticamente el stock y se registra un movimiento de salida.

## Base de datos

- Nombre: `DBPanaderia`
- Ejecutar (opcional) `docs/script_mysql_DBPanaderia.sql` en MySQL.
- La aplicación **crea automáticamente** la base de datos y las tablas al arrancar si no existen.
- Configura usuario/contraseña en `src/main/java/com/panaderia/util/ConexionDB.java` (por defecto: `root` / vacío).

## Requisitos

- JDK 17 o superior
- Maven 3.8+
- MySQL 8.x en `localhost:3306`

## Ejecución

```bash
mvn clean javafx:run
```

La interfaz utiliza una identidad visual inspirada en el logo de Molino de Oro: café oscuro, dorado, crema y rojo, con tarjetas, navegación lateral, tablas y formularios de estilo moderno.
