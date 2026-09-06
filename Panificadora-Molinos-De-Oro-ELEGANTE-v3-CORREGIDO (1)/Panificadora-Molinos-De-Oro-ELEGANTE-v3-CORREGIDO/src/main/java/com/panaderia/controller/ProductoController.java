package com.panaderia.controller;

import com.panaderia.model.*;
import com.panaderia.service.*;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ProductoController {

 @FXML
 private TableView<Producto> tabla;

 @FXML
 private TableColumn<Producto, Integer> colId, colStock;

 @FXML
 private TableColumn<Producto, String> colNombre, colCategoria, colDescripcion;

 @FXML
 private TableColumn<Producto, Double> colPrecio;

 @FXML
 private TextField txtBuscar, txtNombre, txtPrecio, txtStock, txtMinimo, txtCantidad;

 @FXML
 private ComboBox<String> cmbFiltro, cmbCategoria, cmbEstado;

 @FXML
 private TextArea txtDescripcion;

 @FXML
 private Button btnEliminar, btnGuardar, btnEntrada, btnSalida;

 private final ProductoService service = new ProductoService();
 private final CategoriaService cs = new CategoriaService();

 private final ObservableList<Producto> datos = FXCollections.observableArrayList();
 private FilteredList<Producto> filtrados;

 private Producto sel;
 private String rol = "ADMIN";

 @FXML
 public void initialize() {
  // Configuración de columnas
  colId.setCellValueFactory(new PropertyValueFactory<>("id"));
  colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
  colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
  colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
  colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
  colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

  // Inicializar ComboBox
  cmbEstado.getItems().addAll("Todos", "Disponible", "Stock bajo", "Agotado");
  cmbEstado.getSelectionModel().selectFirst();

  filtrados = new FilteredList<>(datos, p -> true);
  tabla.setItems(filtrados);

  // Listener para selección de producto
  tabla.getSelectionModel().selectedItemProperty().addListener((o, a, b) -> {
   if (b != null) {
    sel = b;
    cargar(b);
   }
  });

  cargarCategorias();
  cargar();
 }

 public void configurarRol(String r) {
  rol = r;
  boolean admin = "ADMIN".equals(r);

  btnEliminar.setDisable(!admin);
  txtPrecio.setDisable(!admin);
  btnGuardar.setDisable(!admin);
  btnEntrada.setDisable(rol.equals("MOSTRADOR"));
  btnSalida.setDisable(rol.equals("MOSTRADOR"));
 }

 private void cargarCategorias() {
  try {
   ObservableList<String> l = FXCollections.observableArrayList();
   for (Categoria c : cs.listar()) {
    l.add(c.getNombre());
   }
   cmbCategoria.setItems(l);

   ObservableList<String> f = FXCollections.observableArrayList("Todas");
   f.addAll(l);
   cmbFiltro.setItems(f);
   cmbFiltro.getSelectionModel().selectFirst();
  } catch (Exception e) {
   error("No se pudieron cargar las categorías: " + e.getMessage());
  }
 }

 private void cargar() {
  try {
   datos.setAll(service.listarTodos());
   filtrar();
  } catch (Exception e) {
   error("No se pudo cargar productos. Verifique MySQL y DBPanaderia.\n" + e.getMessage());
  }
 }

 @FXML
 private void filtrar() {
  String q = txtBuscar.getText() == null ? "" : txtBuscar.getText().trim().toLowerCase();
  String cat = cmbFiltro.getValue();
  String est = cmbEstado.getValue();

  filtrados.setPredicate(p -> {
   boolean a = q.isEmpty() || p.getNombre().toLowerCase().contains(q);
   boolean b = cat == null || cat.equals("Todas") || p.getCategoria().equalsIgnoreCase(cat);
   boolean c = est == null || est.equals("Todos") ||
           (est.equals("Agotado") && p.getStock() == 0) ||
           (est.equals("Stock bajo") && p.getStock() > 0 && p.isStockBajo()) ||
           (est.equals("Disponible") && p.getStock() > p.getStockMinimo());
   return a && b && c;
  });

  if (filtrados.isEmpty()) {
   tabla.setPlaceholder(new Label("No se encontraron productos con los filtros seleccionados."));
  }
 }

 @FXML
 private void limpiarFiltros() {
  txtBuscar.clear();
  cmbFiltro.getSelectionModel().selectFirst();
  cmbEstado.getSelectionModel().selectFirst();
  filtrar();
 }

 @FXML
 private void nuevo() {
  sel = null;
  txtNombre.clear();
  cmbCategoria.getSelectionModel().clearSelection();
  txtPrecio.clear();
  txtStock.clear();
  txtMinimo.setText("10");
  txtDescripcion.clear();
  txtCantidad.clear();
  tabla.getSelectionModel().clearSelection();
  txtNombre.requestFocus();
 }

 @FXML
 private void guardar() {
  try {
   if (!"ADMIN".equals(rol)) {
    throw new IllegalArgumentException("Solo el administrador puede modificar el catálogo.");
   }

   Producto p = new Producto();
   if (sel != null) {
    p.setId(sel.getId());
   }
   p.setNombre(txtNombre.getText());
   p.setCategoria(cmbCategoria.getValue());
   p.setPrecio(Double.parseDouble(txtPrecio.getText().trim().replace(',', '.')));
   p.setStock(Integer.parseInt(txtStock.getText().trim()));
   p.setStockMinimo(Integer.parseInt(txtMinimo.getText().trim()));
   p.setDescripcion(txtDescripcion.getText());

   info(service.guardar(p));
   cargar();
   nuevo();
  } catch (NumberFormatException e) {
   error("Precio, stock y stock mínimo deben contener valores numéricos válidos.");
  } catch (Exception e) {
   error(e.getMessage());
  }
 }

 @FXML
 private void eliminar() {
  if (sel == null) {
   error("Seleccione un producto.");
   return;
  }

  Alert a = new Alert(Alert.AlertType.CONFIRMATION,
          "¿Desea eliminar " + sel.getNombre() + "?",
          ButtonType.OK, ButtonType.CANCEL);
  a.showAndWait().ifPresent(b -> {
   if (b == ButtonType.OK) {
    try {
     info(service.eliminar(sel.getId()));
     cargar();
     nuevo();
    } catch (Exception e) {
     error(e.getMessage());
    }
   }
  });
 }

 @FXML
 private void entrada() {
  ajustar(true);
 }

 @FXML
 private void salida() {
  ajustar(false);
 }

 private void ajustar(boolean entrada) {
  try {
   if (sel == null) {
    throw new IllegalArgumentException("Seleccione un producto de la tabla.");
   }
   int n = Integer.parseInt(txtCantidad.getText().trim());
   info(service.ajustarStock(sel.getId(), n, entrada));
   cargar();
  } catch (NumberFormatException e) {
   error("Ingrese una cantidad entera mayor a 0.");
  } catch (Exception e) {
   error(e.getMessage());
  }
 }

 private void cargar(Producto p) {
  txtNombre.setText(p.getNombre());
  cmbCategoria.setValue(p.getCategoria());
  txtPrecio.setText(String.valueOf(p.getPrecio()));
  txtStock.setText(String.valueOf(p.getStock()));
  txtMinimo.setText(String.valueOf(p.getStockMinimo()));
  txtDescripcion.setText(p.getDescripcion() == null ? "" : p.getDescripcion());
 }

 private void info(String s) {
  new Alert(Alert.AlertType.INFORMATION, s).showAndWait();
 }

 private void error(String s) {
  new Alert(Alert.AlertType.ERROR, s == null ? "Ocurrió un error." : s).showAndWait();
 }
}
