package com.panaderia.controller;

import com.panaderia.model.Categoria;
import com.panaderia.model.Producto;
import com.panaderia.service.CategoriaService;
import com.panaderia.service.ProductoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ConsultaController {

 // --- Elementos de la Interfaz Gráfica (FXML) ---
 @FXML
 private TextField txtBuscar;

 @FXML
 private ComboBox<String> cmbCategoria;

 @FXML
 private ComboBox<String> cmbEstado;

 @FXML
 private TableView<Producto> tabla;

 @FXML
 private TableColumn<Producto, String> colProducto;

 @FXML
 private TableColumn<Producto, String> colCategoria;

 @FXML
 private TableColumn<Producto, Integer> colStock;

 @FXML
 private TableColumn<Producto, Double> colPrecio;

 // --- Atributos de Lógica y Datos ---
 private final ProductoService service = new ProductoService();
 private final CategoriaService cs = new CategoriaService();
 private final ObservableList<Producto> datos = FXCollections.observableArrayList();

 // --- Método de Inicialización ---
 @FXML
 public void initialize() {
  // 1. Vincular columnas con las propiedades del modelo Producto
  colProducto.setCellValueFactory(new PropertyValueFactory<>("nombre"));
  colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
  colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
  colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

  // 2. Cargar categorías de la base de datos en el ComboBox
  cmbCategoria.getItems().add("Todas");
  try {
   for (Categoria c : cs.listar()) {
    cmbCategoria.getItems().add(c.getNombre());
   }
  } catch (Exception ignored) {
   // Se ignoran errores de conexión inicial para no romper la vista
  }

  // 3. Configurar ComboBox de estados de inventario
  cmbEstado.getItems().addAll("Todos", "Disponible", "Stock bajo", "Agotado");

  // Seleccionar primeros elementos por defecto
  cmbCategoria.getSelectionModel().selectFirst();
  cmbEstado.getSelectionModel().selectFirst();

  // 4. Cargar los productos desde el servicio
  try {
   datos.setAll(service.listarTodos());
  } catch (Exception e) {
   error("No se pudo cargar el stock: " + e.getMessage());
  }

  // 5. Aplicar filtros iniciales
  filtrar();
 }

 // --- Acciones de Filtrado y Control ---

 /**
  * Filtra dinámicamente los productos de la tabla basándose en texto, categoría y estado.
  */
 @FXML
 private void filtrar() {
  String q = (txtBuscar.getText() == null) ? "" : txtBuscar.getText().trim().toLowerCase();
  String c = cmbCategoria.getValue();
  String e = cmbEstado.getValue();

  ObservableList<Producto> f = datos.filtered(p -> {
   // Filtro 1: Búsqueda por coincidencia de texto en el nombre
   boolean coincideTexto = q.isEmpty() || p.getNombre().toLowerCase().contains(q);

   // Filtro 2: Coincidencia de Categoría
   boolean coincideCategoria = (c == null) || c.equals("Todas") || p.getCategoria().equalsIgnoreCase(c);

   // Filtro 3: Coincidencia de Estado de Inventario (Agotado, Bajo o Disponible)
   boolean coincideEstado = (e == null) || e.equals("Todos")
           || (e.equals("Agotado") && p.getStock() == 0)
           || (e.equals("Stock bajo") && p.getStock() > 0 && p.isStockBajo())
           || (e.equals("Disponible") && p.getStock() > p.getStockMinimo());

   return coincideTexto && coincideCategoria && coincideEstado;
  });

  // Asignar la lista filtrada a la tabla
  tabla.setItems(f);

  // Mensaje personalizado si la búsqueda queda vacía
  if (f.isEmpty()) {
   tabla.setPlaceholder(new Label("No se encontraron productos. Pruebe otros filtros."));
  }
 }

 /**
  * Limpia los controles de búsqueda y restablece la tabla.
  */
 @FXML
 private void limpiar() {
  txtBuscar.clear();
  cmbCategoria.getSelectionModel().selectFirst();
  cmbEstado.getSelectionModel().selectFirst();
  filtrar();
 }

 // --- Métodos de Soporte ---
 private void error(String s) {
  new Alert(Alert.AlertType.ERROR, s).showAndWait();
 }
}
