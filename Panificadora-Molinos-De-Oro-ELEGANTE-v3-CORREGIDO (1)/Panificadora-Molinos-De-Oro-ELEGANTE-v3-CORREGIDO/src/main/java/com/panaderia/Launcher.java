package com.panaderia;

public class Launcher {
    public static void main(String[] args) {
        // ACTIVAR EL SERVIDOR WEB AQUÍ:
        com.panaderia.service.ServidorWebPedidos.iniciarServidor();

        // Arranca tu ventana de JavaFX
        App.main(args);
    }
}

