# Panificadora Molinos de Oro — Sistema de Gestión

Sistema de gestión de **productos, inventario y pedidos de pasteles** para la Panificadora y Pastelería Molinos de Oro (Quito, Ecuador).

Proyecto integrador de **Diseño de Software** — Instituto Superior Tecnológico CENESTUR.

**Repositorio:** [https://github.com/Erigudis/Panificadora-Molinos-De-Oro](https://github.com/Erigudis/Panificadora-Molinos-De-Oro)

---

## Descripción

Aplicación de escritorio en **JavaFX** orientada al personal del local (Administrador, Producción y Mostrador), con un **sitio web público** embebido para que los clientes vean el catálogo de pasteles y envíen pedidos por WhatsApp.

Nace de un problema real del negocio: pedidos por WhatsApp y papel que se perdían, stock controlado “a ojo” y sin trazabilidad digital.

---

## Novedades de la versión final (v3)

Respecto a las versiones anteriores (v1 catálogo básico y v2 con búsqueda/filtros), en esta entrega se incorporó:

| Módulo / mejora | Descripción |
|-----------------|-------------|
| **Login por roles** | Acceso diferenciado: Administrador, Producción y Mostrador |
| **Categorías** | Alta, edición y eliminación de tipos de producto |
| **Movimientos de stock** | Entradas y salidas con historial y validación (no stock negativo) |
| **Pedidos de pasteles** | Registro de cliente, productos, fecha de entrega y flujo de estados |
| **Estados de pedido** | Pendiente → En preparación → Listo → Entregado (también Cancelado) |
| **Descuento automático** | Al pasar un pedido a **Listo**, se descuenta stock y se registra el movimiento |
| **Consulta rápida** | Vista con colores: verde (ok), amarillo (bajo), rojo (agotado) |
| **Sitio web público** | Catálogo de pasteles, carrito y contacto en `http://localhost:8080` |
| **Inicialización de BD** | Las tablas se crean solas al arrancar si no existen |
| **Empaquetado JAR** | `shaded.jar` con JavaFX y dependencias para ejecutar sin IDE |
| **Interfaz renovada** | Identidad visual café / dorado / crema alineada a la marca |

---

## Requisitos

- **Java 17** o superior  
- **Maven 3.8+**  
- **XAMPP** (MySQL/MariaDB + phpMyAdmin)  
- Sistema operativo: Windows o Linux  

---

## Cómo ejecutar (desarrollo)

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/Erigudis/Panificadora-Molinos-De-Oro.git
   cd Panificadora-Molinos-De-Oro
   ```

2. Abrir **XAMPP** e iniciar el módulo **MySQL** (puerto `3306`).

3. (Opcional) Revisar la base en phpMyAdmin:  
   [http://localhost/phpmyadmin](http://localhost/phpmyadmin)

4. Ajustar usuario/contraseña en `ConexionDB.java` si es necesario  
   (por defecto: `root` sin contraseña, típico de XAMPP).

5. Ejecutar la aplicación:
   ```bash
   mvn clean javafx:run
   ```

6. Sitio web público de pasteles:  
   [http://localhost:8080](http://localhost:8080)

> Si aparece `Address already in use: bind`, el puerto 8080 está ocupado. Cierra otras instancias de la app o cambia el puerto en la clase del servidor web.

---

## Empaquetado JAR (distribución)

```bash
mvn clean package
```

Se generan en `target/`:

| Archivo | Uso |
|---------|-----|
| `panificadora-molinos-de-oro-2.0.0.jar` | Solo clases del proyecto |
| **`panificadora-molinos-de-oro-2.0.0-shaded.jar`** | **Recomendado** — incluye JavaFX, MySQL connector y todo |
| `original-panificadora-molinos-de-oro-2.0.0.jar` | Respaldo interno de Maven |

Ejecutar el shaded JAR (con Java 17 instalado):

```bash
java -jar target/panificadora-molinos-de-oro-2.0.0-shaded.jar
```

---

## Credenciales de prueba

| Rol | Usuario | Contraseña |
|-----|---------|------------|
| Administrador | `admin` | `admin123` |
| Producción | `produccion` | `1234` |
| Mostrador | `mostrador` | `1234` |

### Permisos por módulo

| Módulo | Admin | Producción | Mostrador |
|--------|:-----:|:----------:|:---------:|
| Productos | Sí | Sí | Sí |
| Categorías | Sí | — | — |
| Movimientos de stock | Sí | Sí | — |
| Pedidos de pasteles | Sí | Sí | Sí |
| Consulta rápida | Sí | Sí | Sí |

---

## Arquitectura

```
Presentación (JavaFX + FXML)
        ↓
Lógica de negocio (Services + validaciones)
        ↓
Acceso a datos (DAO / JDBC)
        ↓
MySQL (XAMPP) — base panaderia_inventario
```

**Paquetes principales:**

```
com.panaderia
├── App.java / Launcher.java
├── controller/     → pantallas JavaFX
├── service/        → reglas de negocio + servidor web
├── dao/            → CRUD JDBC
├── model/          → entidades del dominio
└── util/           → ConexionDB, DatabaseInitializer, ServidorWebPedidos
```

---

## Módulos del sistema

- **Productos** — CRUD, búsqueda, filtros, precio y stock  
- **Categorías** — mantenimiento del catálogo de tipos  
- **Movimientos de stock** — entradas / salidas + historial  
- **Pedidos de pasteles** — alta, listado y cambio de estado  
- **Consulta rápida** — disponibilidad con códigos de color  
- **Sitio web** — inicio, galería de pasteles, carrito, contacto y WhatsApp  

---

## Base de datos

Motor: **MySQL** vía **XAMPP**.  
Herramienta de administración: **phpMyAdmin**.

Tablas principales:

- `usuario`
- `categoria`
- `producto`
- `movimiento_stock`
- `pedido`
- `detalle_pedido`

La aplicación puede crear la base y las tablas al arrancar (`DatabaseInitializer`).

---

## Evolución del proyecto

| Versión | Contenido |
|---------|-----------|
| **v1** | CRUD de productos, arquitectura por capas, MySQL, primer push a GitHub |
| **v2** | Búsqueda, filtros, ajuste rápido de stock, validaciones reforzadas |
| **v3 (final)** | Roles, categorías, movimientos, pedidos, consulta rápida, sitio web, JAR shaded |

---

## Tecnologías

- Java 17  
- JavaFX 21 (FXML + CSS)  
- MySQL 8 + JDBC  
- **XAMPP** (MySQL + phpMyAdmin)  
- Maven 3.8+  
- Git + GitHub  

---

## Autora

**Erika María Gudiño Santander**  
Diseño de Software — CENESTUR  
Docente: MSc. Yadira Franco  

---

## Licencia

Proyecto académico — uso educativo.

