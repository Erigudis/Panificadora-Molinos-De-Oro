package com.panaderia;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Punto de entrada de la aplicación JavaFX.
 * Panificadora Molinos de Oro – v2 (Catálogo + búsqueda + ajuste de stock).
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/panaderia/productos.fxml")
        );
        Parent root = loader.load();

        Scene scene = new Scene(root, 980, 620);
        stage.setTitle("Panificadora Molinos de Oro – Catálogo de productos (v2)");
        stage.setScene(scene);
        stage.setMinWidth(850);
        stage.setMinHeight(550);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
