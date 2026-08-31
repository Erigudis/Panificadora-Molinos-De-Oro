# Panificadora Molinos de Oro – Sistema de gestión de productos e inventario

**Versión 2 (v2)** – Catálogo de productos + búsqueda + filtro + ajuste de stock  
**Estudiante:** Erika María Gudiño Santander  
**Asignatura:** Diseño de Software – 7547  
**Institución:** Instituto Superior Tecnológico CENESTUR  

---

## Descripción

Aplicación de escritorio en **Java 17 + JavaFX 21** para gestionar el catálogo de productos de la Panificadora Molinos de Oro.

### Cambios de la v2 respecto a la v1

| Funcionalidad | v1 | v2 |
|---------------|----|----|
| CRUD de productos | Sí | Sí |
| Validaciones (nombre, categoría, precio, stock) | Sí | Reforzadas (precio > 0) |
| Búsqueda por nombre | No | **Sí** |
| Filtro por categoría | No | **Sí** |
| Ajuste de stock (entrada / salida) | No | **Sí** |
| Control de stock negativo en salidas | No | **Sí** |

---

## Tecnologías

| Tecnología       | Versión / uso                          |
|------------------|----------------------------------------|
| Java             | 17                                     |
| JavaFX           | 21                                     |
| Maven            | 3.8+                                   |
| MySQL            | 8.x (local o nube)                     |
| JDBC             | mysql-connector-j 8.3                  |

---

## Arquitectura de paquetes

```
com.panaderia
├── App.java                 → Punto de entrada
├── model/Producto.java      → Entidad del dominio
├── dao/ProductoDAO.java     → Acceso a datos (JDBC) + actualizarStock
├── service/ProductoService.java → Validaciones + ajustarStock
├── controller/ProductoController.java → Controlador JavaFX (CRUD + filtros + stock)
└── util/ConexionDB.java     → Conexión a MySQL
```

**Flujo:** Vista (FXML) → Controller → Service → DAO → MySQL

---

## Requisitos previos

1. **Java 17** instalado (`java -version`)
2. **Maven** instalado (`mvn -v`) o IntelliJ IDEA
3. **MySQL** en ejecución

---

## Instalación y ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/Erigudis/Panificadora-Molinos-De-Oro.git
cd Panificadora-Molinos-De-Oro
```

### 2. Crear la base de datos

Ejecuta el script SQL en MySQL Workbench o:

```bash
mysql -u root -p < docs/script_mysql_DBPanaderia.sql
```

### 3. Configurar la conexión

Edita `src/main/java/com/panaderia/util/ConexionDB.java` y pon tu contraseña de MySQL:

```java
private static final String PASS = "tu_contraseña";
```

### 4. Ejecutar

```bash
mvn clean javafx:run
```

O en IntelliJ: pestaña Maven → Plugins → javafx → **javafx:run**.

---

## Funciones de la v2

- Listar productos en tabla
- Registrar / editar / eliminar productos
- Buscar por nombre (en tiempo real)
- Filtrar por categoría
- Entrada de stock (+)
- Salida de stock (−) con validación de stock insuficiente
- Validaciones de negocio con mensajes al usuario

---

## Repositorio

https://github.com/Erigudis/Panificadora-Molinos-De-Oro
