package com.panaderia.service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class ServidorWebPedidos {

    public static void iniciarServidor() {
        try {
            // Levanta el servidor en el puerto 8080
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            // Asigna el manejador para la raíz principal y archivos estáticos
            server.createContext("/", new StaticFilesHandler());

            server.setExecutor(null);
            server.start();
            System.out.println("🚀 Servidor HTTP de Molino de Oro corriendo en http://localhost:8080");
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor web: " + e.getMessage());
        }
    }

    private static class StaticFilesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();

            // Si entran a la raíz, cargamos el index.html
            if (path.equals("/")) {
                path = "/index.html";
            }

            // Busca el archivo de forma dinámica dentro de tu carpeta resources/static del .jar
            InputStream is = ServidorWebPedidos.class.getResourceAsStream("/static" + path);

            if (is == null) {
                // Si no encuentra el archivo (ej. una imagen que falte), manda error 404
                String response = "404 Not Found";
                exchange.sendResponseHeaders(404, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                // Configura el tipo de archivo (Content-Type) para que el navegador aplique los estilos
                if (path.endsWith(".css")) {
                    exchange.getResponseHeaders().set("Content-Type", "text/css; charset=UTF-8");
                } else if (path.endsWith(".html")) {
                    exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                } else if (path.endsWith(".png")) {
                    exchange.getResponseHeaders().set("Content-Type", "image/png");
                } else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
                    exchange.getResponseHeaders().set("Content-Type", "image/jpeg");
                }

                // Envía el archivo al navegador
                exchange.sendResponseHeaders(200, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                }
                is.close();
            }
        }
    }
}

