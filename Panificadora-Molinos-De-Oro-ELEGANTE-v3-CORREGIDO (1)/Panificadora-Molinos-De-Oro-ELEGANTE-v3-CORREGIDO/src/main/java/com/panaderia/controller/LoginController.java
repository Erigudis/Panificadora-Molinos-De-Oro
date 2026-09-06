package com.panaderia.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

 // --- Elementos de la Interfaz Gráfica (FXML) ---
 @FXML
 private TextField txtUsuario;

 @FXML
 private PasswordField txtClave;

 @FXML
 private ComboBox<String> cmbRol;

 @FXML
 private Label lblError;

 // --- Método de Inicialización ---
 @FXML
 public void initialize() {
  // Cargar los roles disponibles en el sistema de la panadería
  cmbRol.getItems().addAll("Administrador", "Producción", "Mostrador");
  cmbRol.getSelectionModel().selectFirst();
 }

 // --- Eventos de Control de Acceso ---

 /**
  * Valida las credenciales ingresadas por el usuario y realiza la autenticación
  * basándose en el rol seleccionado en la interfaz.
  */
 @FXML
 private void onEntrar() {
  // 1. Limpieza y preparación de variables de entrada
  String u = (txtUsuario.getText() == null) ? "" : txtUsuario.getText().trim();
  String c = (txtClave.getText() == null) ? "" : txtClave.getText();
  String r = cmbRol.getValue();

  // 2. Validación de campos obligatorios
  if (u.isEmpty() || c.isEmpty() || r == null) {
   lblError.setText("Complete usuario, contraseña y rol.");
   return;
  }

  // 3. Mapeo de la interfaz de usuario con los códigos de rol (Roles lógicos)
  String rol = r.equals("Administrador") ? "ADMIN"
          : r.equals("Producción") ? "PRODUCCION"
          : "MOSTRADOR";

  // 4. Configuración de credenciales esperadas "en duro" (Hardcoded)
  String esperado = rol.equals("ADMIN") ? "admin"
          : rol.equals("PRODUCCION") ? "produccion"
          : "mostrador";

  String pass = rol.equals("ADMIN") ? "admin123" : "1234";

  // 5. Verificación de las credenciales ingresadas contra los valores esperados
  if (!u.equalsIgnoreCase(esperado) || !c.equals(pass)) {
   lblError.setText("Los datos de acceso no corresponden al rol seleccionado.");
   return;
  }

  // 6. Carga dinámica del Dashboard principal tras el inicio de sesión exitoso
  try {
   FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/panaderia/dashboard.fxml"));
   Parent root = loader.load();

   // Inyectar la configuración del rol y nombre al controlador del panel
   DashboardController dc = loader.getController();
   dc.configurarRol(rol, r);

   // Reconfigurar la ventana (Stage) actual con las dimensiones del panel general
   Stage s = (Stage) txtUsuario.getScene().getWindow();
   Scene scene = new Scene(root, 1200, 760);

   // Agregar las hojas de estilo de la interfaz gráfica
   scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());

   s.setScene(scene);
   s.setTitle("Molino de Oro · Panel de gestión");
   s.setMinWidth(1080);
   s.setMinHeight(700);
   s.show();

  } catch (Exception e) {
   lblError.setText("No se pudo abrir la aplicación: " + e.getMessage());
  }
 }
}

