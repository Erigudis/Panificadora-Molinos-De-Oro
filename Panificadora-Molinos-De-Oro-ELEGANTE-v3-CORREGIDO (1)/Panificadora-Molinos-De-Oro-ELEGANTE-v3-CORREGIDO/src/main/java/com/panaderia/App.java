package com.panaderia;

import com.panaderia.util.DatabaseInitializer;
import com.panaderia.util.ServidorWebPedidos; // Importamos tu nuevo servidor de pedidos
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        try {
            DatabaseInitializer.initialize();
        } catch (Exception e) {
            System.err.println("Aviso de base de datos: " + e.getMessage());
        }

        // 🚀 INICIAR SERVIDOR WEB EN SEGUNDO PLANO
        // Usamos un Thread para que escuche la web sin congelar la interfaz de JavaFX
        new Thread(() -> {
            try {
                ServidorWebPedidos.iniciarServidor();
            } catch (Exception e) {
                System.err.println("Error al iniciar el servidor de pedidos web: " + e.getMessage());
            }
        }).start();

        Parent root = FXMLLoader.load(getClass().getResource("/com/panaderia/login.fxml"));

        Scene scene = new Scene(root, 980, 650);
        scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Molino de Oro · Acceso");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
