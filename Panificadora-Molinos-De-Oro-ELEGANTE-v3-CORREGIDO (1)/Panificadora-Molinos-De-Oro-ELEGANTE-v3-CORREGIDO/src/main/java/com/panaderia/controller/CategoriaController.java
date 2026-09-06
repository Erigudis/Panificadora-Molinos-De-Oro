package com.panaderia.controller;

import com.panaderia.model.Categoria;
import com.panaderia.service.CategoriaService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class CategoriaController {

 // --- Elementos de la Interfaz Gráfica (FXML) ---
 @FXML
 private TableView<Categoria> tabla;

 @FXML
 private TableColumn<Categoria, Integer> colId;

 @FXML
 private TableColumn<Categoria, String> colNombre;

 @FXML
 private TableColumn<Categoria, String> colDescripcion;

 @FXML
 private TextField txtNombre;

 @FXML
 private TextArea txtDescripcion;

 // --- Atributos de Lógica y Servicio ---
 private final CategoriaService service = new CategoriaService();
 private Categoria sel;

 // --- Método de Inicialización ---
 @FXML
 public void initialize() {
  // Mapeo de columnas con las propiedades del modelo Categoria
  colId.setCellValueFactory(new PropertyValueFactory<>("id"));
  colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
  colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

  // Oyente (Listener) para detectar cuando el usuario hace clic en una fila de la tabla
  tabla.getSelectionModel().selectedItemProperty().addListener((o, a, b) -> {
   if (b != null) {
    sel = b;
    txtNombre.setText(b.getNombre());
    txtDescripcion.setText(b.getDescripcion());
   }
  });

  // Cargar los datos iniciales de la base de datos
  cargar();
 }

 // --- Acciones de los Botones ---

 @FXML
 private void nuevo() {
  sel = null;
  txtNombre.clear();
  txtDescripcion.clear();
  tabla.getSelectionModel().clearSelection();
  txtNombre.requestFocus();
 }

 @FXML
 private void guardar() {
  try {
   Categoria c = new Categoria();
   if (sel != null) {
    c.setId(sel.getId());
   }
   c.setNombre(txtNombre.getText());
   c.setDescripcion(txtDescripcion.getText());

   // Llama al servicio y muestra el mensaje que este retorne
   info(service.guardar(c));

   // Refresca la tabla y limpia el formulario
   cargar();
   nuevo();
  } catch (Exception e) {
   error(e.getMessage());
  }
 }

 @FXML
 private void eliminar() {
  if (sel == null) {
   error("Seleccione una categoría.");
   return;
  }

  Alert a = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar la categoría seleccionada?", ButtonType.OK, ButtonType.CANCEL);
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

 // --- Métodos de Soporte / Auxiliares ---

 private void cargar() {
  try {
   tabla.setItems(FXCollections.observableArrayList(service.listar()));
  } catch (Exception e) {
   error("No se pudo cargar categorías: " + e.getMessage());
  }
 }

 private void info(String s) {
  new Alert(Alert.AlertType.INFORMATION, s).showAndWait();
 }

 private void error(String s) {
  new Alert(Alert.AlertType.ERROR, s == null ? "Ocurrió un error." : s).showAndWait();
 }
}
