package com.panaderia.util;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ServidorWebPedidos {

    public static void iniciarServidor() {
        try {
            // Crea el servidor escuchando en el puerto 8080 configurado en tu HTML
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            server.createContext("/api/pedidos", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    // Configuración de CORS obligatoria para que el navegador no bloquee la petición
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

                    // Resolver peticiones previas de control (Preflight)
                    if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                        exchange.sendResponseHeaders(204, -1);
                        return;
                    }

                    if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                        // 1. Leer el JSON entrante del index.html
                        InputStream is = exchange.getRequestBody();
                        String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                        // 2. Extraer de forma exacta las propiedades del JSON de tu HTML
                        String nombre = extraerCampoJson(json, "nombre");
                        String telefono = extraerCampoJson(json, "telefono");
                        String direccion = extraerCampoJson(json, "direccion");
                        String tipo = extraerCampoJson(json, "tipo");
                        String mensaje = extraerCampoJson(json, "mensaje");

                        // 3. Guardar los datos en tu base de datos MySQL usando ConexionDB
                        boolean guardadoExitoso = guardarPedidoEnMySQL(nombre, telefono, direccion, tipo, mensaje);

                        // 4. Responder al index.html con un estado HTTP adecuado
                        String respuesta = guardadoExitoso ? "{\"status\":\"success\"}" : "{\"status\":\"error\"}";
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(guardadoExitoso ? 200 : 500, respuesta.length());

                        OutputStream os = exchange.getResponseBody();
                        os.write(respuesta.getBytes());
                        os.close();

                        // 5. Notificación para refrescar la interfaz gráfica de JavaFX
                        if (guardadoExitoso) {
                            System.out.println("✅ Pedido guardado en MySQL: " + nombre + " - " + tipo);

                            // Aquí se fuerza a que la UI de JavaFX refresque el listado en pantalla
                            javafx.application.Platform.runLater(() -> {
                                // Aquí llamaremos al método de recarga de tu PedidoController
                            });
                        }

                    } else {
                        exchange.sendResponseHeaders(405, -1); // Método No Permitido
                    }
                }
            });

            server.setExecutor(null);
            server.start();
            System.out.println("🚀 Servidor HTTP de Molino de Oro corriendo en http://localhost:8080");
        } catch (IOException e) {
            System.err.println("❌ Error al levantar el servidor: " + e.getMessage());
        }
    }

    // Método encargado de realizar la inserción JDBC en MySQL
    private static boolean guardarPedidoEnMySQL(String nombre, String telefono, String direccion, String tipo, String mensaje) {
        // Asegúrate de mapear los nombres exactos de tu tabla en MySQL
        String query = "INSERT INTO pedidos (cliente_nombre, telefono, direccion, tipo_pedido, mensaje, estado, fecha) " +
                "VALUES (?, ?, ?, ?, ?, 'Pendiente', NOW())";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, nombre);
            ps.setString(2, telefono);
            ps.setString(3, direccion);
            ps.setString(4, tipo);
            ps.setString(5, mensaje);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error en la consulta SQL de inserción: " + e.getMessage());
            return false;
        }
    }

    // Extractor nativo simple para leer cadenas desde un JSON plano
    private static String extraerCampoJson(String json, String campo) {
        try {
            String clave = "\"" + campo + "\":\"";
            int inicio = json.indexOf(clave);
            if (inicio == -1) return ""; // Retorna vacío si el campo no se encuentra

            inicio += clave.length();
            int fin = json.indexOf("\"", inicio);
            return json.substring(inicio, fin);
        } catch (Exception e) {
            return "";
        }
    }
}
