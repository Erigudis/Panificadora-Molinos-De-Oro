package com.panaderia.controller;

import com.panaderia.model.*;
import com.panaderia.service.*;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.format.DateTimeFormatter;

public class StockController {

 @FXML
 private ComboBox<Producto> cmbProducto;

 @FXML
 private ComboBox<String> cmbTipo, cmbFiltroTipo;

 @FXML
 private TextField txtCantidad, txtObservacion, txtBuscar;

 @FXML
 private TableView<MovimientoStock> tabla;

 @FXML
 private TableColumn<MovimientoStock, Integer> colId, colCantidad;

 @FXML
 private TableColumn<MovimientoStock, String> colProducto, colTipo, colFecha, colObs;

 private final ProductoService ps = new ProductoService();
 private final StockService ss = new StockService();

 private final ObservableList<MovimientoStock> datos = FXCollections.observableArrayList();
 private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

 @FXML
 public void initialize() {
  // Configuración de columnas
  colId.setCellValueFactory(new PropertyValueFactory<>("id"));
  colProducto.setCellValueFactory(new PropertyValueFactory<>("producto"));
  colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
  colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
  colFecha.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getFecha().format(fmt)));
  colObs.setCellValueFactory(new PropertyValueFactory<>("observacion"));

  // ComboBox
  cmbTipo.getItems().addAll("ENTRADA", "SALIDA");
  cmbTipo.getSelectionModel().selectFirst();

  cmbFiltroTipo.getItems().addAll("Todos", "ENTRADA", "SALIDA");
  cmbFiltroTipo.getSelectionModel().selectFirst();

  cargarProductos();
  cargar();
 }

 private void cargarProductos() {
  try {
   cmbProducto.setItems(FXCollections.observableArrayList(ps.listarTodos()));
  } catch (Exception e) {
   error("No se pudieron cargar productos: " + e.getMessage());
  }
 }

 @FXML
 private void registrar() {
  try {
   Producto p = cmbProducto.getValue();
   if (p == null) {
    throw new IllegalArgumentException("Seleccione un producto.");
   }

   String tipo = cmbTipo.getValue();
   if (tipo == null) {
    throw new IllegalArgumentException("Seleccione el tipo de movimiento.");
   }

   int n = Integer.parseInt(txtCantidad.getText().trim());
   if (n <= 0) {
    throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
   }

   ss.registrar(p.getId(), tipo, n, txtObservacion.getText().trim());
   info("Movimiento registrado correctamente.");

   txtCantidad.clear();
   txtObservacion.clear();
   cargarProductos();
   cargar();
  } catch (NumberFormatException e) {
   error("La cantidad debe ser un número entero mayor a 0.");
  } catch (Exception e) {
   error(e.getMessage());
  }
 }

 @FXML
 private void filtrar() {
  String q = txtBuscar.getText() == null ? "" : txtBuscar.getText().trim().toLowerCase();
  String t = cmbFiltroTipo.getValue();

  FilteredList<MovimientoStock> fl = datos.filtered(m ->
          (q.isEmpty() || m.getProducto().toLowerCase().contains(q)) &&
                  (t == null || t.equals("Todos") || m.getTipo().equals(t)));

  tabla.setItems(fl);

  if (fl.isEmpty()) {
   tabla.setPlaceholder(new Label("No se encontraron movimientos con los filtros seleccionados."));
  }
 }

 @FXML
 private void limpiar() {
  txtBuscar.clear();
  cmbFiltroTipo.getSelectionModel().selectFirst();
  filtrar();
 }

 private void cargar() {
  try {
   datos.setAll(ss.listar());
   filtrar();
  } catch (Exception e) {
   error("No se pudo cargar el historial: " + e.getMessage());
  }
 }

 private void info(String s) {
  new Alert(Alert.AlertType.INFORMATION, s).showAndWait();
 }

 private void error(String s) {
  new Alert(Alert.AlertType.ERROR, s == null ? "Ocurrió un error." : s).showAndWait();
 }
}