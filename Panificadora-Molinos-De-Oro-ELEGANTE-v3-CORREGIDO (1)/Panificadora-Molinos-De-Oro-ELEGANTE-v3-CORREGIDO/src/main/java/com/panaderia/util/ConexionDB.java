package com.panaderia.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utilidad de conexión a MySQL.
 * Cambia URL / usuario / contraseña según tu entorno local o de nube.
 * NO subas credenciales reales al repositorio público.
 */
public class ConexionDB {

    // --- Configuración (ajusta a tu MySQL local) ---
    private static final String URL = "jdbc:mysql://192.168.1.2:3306/panaderia_inventario";
    private static final String USER = "root";
    private static final String PASS = "";


    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No se encontró el driver MySQL", e);
        }
    }

    /**
     * Obtiene una conexión nueva a la base de datos.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
