package com.panaderia.controller;

import com.panaderia.model.*;
import com.panaderia.service.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class PedidoController {

 // --- Elementos FXML: Formulario del Pedido ---
 @FXML
 private TextField txtCliente;

 @FXML
 private TextField txtTelefono;

 @FXML
 private DatePicker dpEntrega;

 @FXML
 private TextArea txtObs;

 @FXML
 private ComboBox<Producto> cmbProducto;

 @FXML
 private TextField txtCantidad;

 // --- Elementos FXML: Filtros de Búsqueda ---
 @FXML
 private TextField txtBuscar;

 @FXML
 private ComboBox<String> cmbEstado;

 @FXML
 private DatePicker dpDesde;

 @FXML
 private DatePicker dpHasta;

 // --- Elementos FXML: Tablas y Botones ---
 @FXML
 private TableView<DetallePedido> tablaDetalle;

 @FXML
 private TableColumn<DetallePedido, String> colDetProducto;

 @FXML
 private TableColumn<DetallePedido, Integer> colDetCantidad;

 @FXML
 private TableColumn<DetallePedido, Double> colDetPrecio;

 @FXML
 private TableColumn<DetallePedido, Double> colDetSubtotal;

 @FXML
 private TableView<Pedido> tablaPedidos;

 @FXML
 private TableColumn<Pedido, Integer> colId;

 @FXML
 private TableColumn<Pedido, String> colCliente;

 @FXML
 private TableColumn<Pedido, String> colTelefono;

 @FXML
 private TableColumn<Pedido, String> colFecha;

 @FXML
 private TableColumn<Pedido, String> colEstado;

 @FXML
 private Button btnEstado;

 // --- Atributos de Lógica y Capas de Servicio ---
 private final ProductoService ps = new ProductoService();
 private final PedidoService service = new PedidoService();

 private final ObservableList<DetallePedido> detalles = FXCollections.observableArrayList();
 private ObservableList<Pedido> pedidos = FXCollections.observableArrayList();

 private Pedido sel;
 private String rol = "ADMIN";
 private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

 // --- Método de Inicialización ---
 @FXML
 public void initialize() {
  // 1. Configurar columnas de la tabla Detalle del Pedido
  colDetProducto.setCellValueFactory(new PropertyValueFactory<>("producto"));
  colDetCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
  colDetPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
  colDetSubtotal.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getSubtotal()));
  tablaDetalle.setItems(detalles);

  // 2. Configurar columnas de la tabla de Pedidos Generales
  colId.setCellValueFactory(new PropertyValueFactory<>("id"));
  colCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
  colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
  colFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaEntrega().format(fmt)));
  colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

  // 3. Configurar filtros del ComboBox de estados
  cmbEstado.getItems().addAll("Todos", "Pendiente", "En preparación", "Listo", "Entregado", "Cancelado");
  cmbEstado.getSelectionModel().selectFirst();

  // 4. Agregar escucha (Listener) para detectar filas seleccionadas en los pedidos
  tablaPedidos.getSelectionModel().selectedItemProperty().addListener((o, a, b) -> sel = b);

  // 5. Cargas iniciales de datos
  cargarProductos();
  cargarPedidos();
 }

 /**
  * Define los accesos del usuario activo en la pantalla de pedidos.
  */
 public void configurarRol(String r) {
  rol = r;
  btnEstado.setDisable(!(r.equals("ADMIN") || r.equals("PRODUCCION")));
 }

 /**
  * Carga el catálogo de productos disponibles en el ComboBox de ventas.
  */
 private void cargarProductos() {
  try {
   cmbProducto.setItems(FXCollections.observableArrayList(ps.listarTodos()));
  } catch (Exception e) {
   error("No se pudieron cargar productos: " + e.getMessage());
  }
 }

 // --- Acciones del Formulario del Pedido ---

 /**
  * Agrega un ítem al carrito de detalles. Si el producto ya existe, acumula la cantidad.
  */
 @FXML
 private void agregar() {
  try {
   Producto p = cmbProducto.getValue();
   if (p == null) {
    throw new IllegalArgumentException("Seleccione un producto.");
   }

   int n = Integer.parseInt(txtCantidad.getText().trim());
   if (n <= 0) {
    throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
   }

   // Si el producto ya está en el detalle, sumamos la cantidad
   for (DetallePedido d : detalles) {
    if (d.getIdProducto() == p.getId()) {
     d.setCantidad(d.getCantidad() + n);
     tablaDetalle.refresh();
     txtCantidad.clear();
     return;
    }
   }

   // Si es un producto nuevo, lo agregamos a la lista observable
   detalles.add(new DetallePedido(p.getId(), p.getNombre(), n, p.getPrecio()));
   txtCantidad.clear();

  } catch (NumberFormatException e) {
   error("La cantidad debe ser un número entero mayor a 0.");
  } catch (Exception e) {
   error(e.getMessage());
  }
 }

 /**
  * Remueve la línea seleccionada en el detalle de la orden.
  */
 @FXML
 private void quitar() {
  DetallePedido d = tablaDetalle.getSelectionModel().getSelectedItem();
  if (d == null) {
   error("Seleccione una línea del pedido.");
   return;
  }
  detalles.remove(d);
 }

 /**
  * Envía el pedido y sus respectivas líneas de detalle a la capa de servicios.
  */
 @FXML
 private void guardar() {
  try {
   Pedido p = new Pedido();
   p.setCliente(txtCliente.getText());
   p.setTelefono(txtTelefono.getText());
   p.setFechaEntrega(dpEntrega.getValue());
   p.setObservaciones(txtObs.getText());

   info(service.guardar(p, new ArrayList<>(detalles)));

   limpiar();
   cargarPedidos();
  } catch (Exception e) {
   error(e.getMessage());
  }
 }

 /**
  * Avanza de forma secuencial el estado lógico de producción del pedido seleccionado.
  */
 @FXML
 private void cambiarEstado() {
  try {
   if (sel == null) {
    throw new IllegalArgumentException("Seleccione un pedido.");
   }

   String actual = sel.getEstado();
   String nuevo = actual.equals("Pendiente") ? "En preparación"
           : actual.equals("En preparación") ? "Listo"
           : actual.equals("Listo") ? "Entregado" : null;

   if (nuevo == null) {
    throw new IllegalArgumentException("Este pedido ya no puede avanzar de estado.");
   }

   info(service.cambiarEstado(sel, nuevo));
   cargarPedidos();

  } catch (Exception e) {
   error(e.getMessage());
  }
 }

 // --- Sistema Integrado de Filtrado y Búsqueda ---

 /**
  * Filtra reactivamente la lista de pedidos en base a criterios de texto, estados y rango de fechas.
  */
 @FXML
 private void filtrar() {
  String q = (txtBuscar.getText() == null) ? "" : txtBuscar.getText().trim().toLowerCase();
  String e = cmbEstado.getValue();
  LocalDate d1 = dpDesde.getValue();
  LocalDate d2 = dpHasta.getValue();

  // Validación preventiva del rango de fechas
  if (d1 != null && d2 != null && d1.isAfter(d2)) {
   error("La fecha desde no puede ser posterior a la fecha hasta.");
   return;
  }

  ObservableList<Pedido> f = pedidos.filtered(p -> {
   // Criterio 1: Coincidencia por cliente o teléfono
   boolean coincideBusqueda = q.isEmpty()
           || p.getCliente().toLowerCase().contains(q)
           || p.getTelefono().contains(q);

   // Criterio 2: Coincidencia por Estado del Pedido
   boolean coincideEstado = (e == null) || e.equals("Todos") || p.getEstado().equals(e);

   // Criterio 3: Coincidencia en Rango de Fechas
   boolean coincideFechaDesde = (d1 == null) || !p.getFechaEntrega().isBefore(d1);
   boolean coincideFechaHasta = (d2 == null) || !p.getFechaEntrega().isAfter(d2);

   return coincideBusqueda && coincideEstado && coincideFechaDesde && coincideFechaHasta;
  });

  tablaPedidos.setItems(f);

  if (f.isEmpty()) {
   tablaPedidos.setPlaceholder(new Label("No se encontraron pedidos con los filtros seleccionados."));
  }
 }

 /**
  * Restablece los filtros de búsqueda a su estado inicial.
  */
 @FXML
 private void limpiarFiltros() {
  txtBuscar.clear();
  cmbEstado.getSelectionModel().selectFirst();
  dpDesde.setValue(null);
  dpHasta.setValue(null);
  filtrar();
 }

 // --- Métodos de Soporte / Auxiliares ---

 private void cargarPedidos() {
  try {
   pedidos.setAll(service.listar());
   filtrar();
  } catch (Exception e) {
   error("No se pudieron cargar los pedidos: " + e.getMessage());
  }
 }

 private void limpiar() {
  txtCliente.clear();
  txtTelefono.clear();
  dpEntrega.setValue(null);
  txtObs.clear();
  detalles.clear();
 }
 private void info(String s) {
  new Alert(Alert.AlertType.INFORMATION, s).showAndWait();

 }private void error(String s) {
  new Alert(Alert.AlertType.ERROR, (s == null) ? "Ocurrió un error." : s).showAndWait();
 }
}