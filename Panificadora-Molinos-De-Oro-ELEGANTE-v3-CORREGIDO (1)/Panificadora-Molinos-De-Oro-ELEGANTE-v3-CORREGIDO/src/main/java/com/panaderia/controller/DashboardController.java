package com.panaderia.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class DashboardController {

 @FXML private StackPane contenido;
 @FXML private Label lblRol;
 @FXML private Label lblUsuario;
 @FXML private Button btnProductos, btnCategorias, btnStock, btnPedidos, btnConsulta;

 private String rol;

 public void configurarRol(String rol, String nombre) {
  this.rol = rol;
  lblRol.setText(rol);
  lblUsuario.setText(nombre);

  btnProductos.setDisable(false);
  btnCategorias.setDisable(!rol.equals("ADMIN"));
  btnStock.setDisable(rol.equals("MOSTRADOR"));
  btnPedidos.setDisable(false);
  btnConsulta.setDisable(false);

  mostrar("productos.fxml", btnProductos);
 }

 @FXML
 private void productos() {
  mostrar("productos.fxml", btnProductos);
 }

 @FXML
 private void categorias() {
  mostrar("categorias.fxml", btnCategorias);
 }

 @FXML
 private void stock() {
  mostrar("stock.fxml", btnStock);
 }

 @FXML
 private void pedidos() {
  mostrar("pedidos.fxml", btnPedidos);
 }

 @FXML
 private void consulta() {
  mostrar("consulta.fxml", btnConsulta);
 }

 private void mostrar(String vista, Button activo) {
  try {
   FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/panaderia/" + vista));
   Parent parent = loader.load();

   Object controller = loader.getController();
   if (controller instanceof ProductoController pc) {
    pc.configurarRol(rol);
   }
   if (controller instanceof PedidoController pec) {
    pec.configurarRol(rol);
   }

   contenido.getChildren().setAll(parent);

   for (Button b : new Button[] { btnProductos, btnCategorias, btnStock, btnPedidos, btnConsulta }) {
    b.getStyleClass().remove("active");
   }
   activo.getStyleClass().add("active");

  } catch (Exception e) {
   new Alert(Alert.AlertType.ERROR, "No se pudo cargar el módulo: " + e.getMessage())
           .showAndWait();
   e.printStackTrace();
  }
 }

 @FXML
 private void salir() {
  try {
   FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/panaderia/login.fxml"));
   Parent parent = loader.load();

   Stage stage = (Stage) contenido.getScene().getWindow();
   Scene scene = new Scene(parent, 980, 650);
   scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
   stage.setScene(scene);
   stage.setTitle("Molino de Oro · Acceso");
   stage.setMinWidth(900);
   stage.setMinHeight(600);
  } catch (Exception e) {
   e.printStackTrace();
  }
 }
}