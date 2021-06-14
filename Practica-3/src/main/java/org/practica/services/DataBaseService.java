package org.practica.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseService {
    private static DataBaseService dataBaseService;
    private String URL_DATABASE = "jdbc:h2:tcp://localhost/~/easy_shop";

    public static DataBaseService getInstance(){
        if (dataBaseService == null){
            dataBaseService = new DataBaseService();
        }
        return dataBaseService;
    }
    private  DataBaseService(){
        driverRegister();
    }

    private void driverRegister() {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException ex) {
            System.out.println("Couldn't register driver");
        }
    }

    public Connection getConnection(){
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL_DATABASE, "sa", "");
        } catch (SQLException ex) {
            System.out.println("Couldn't connect to the database");
        }
        return connection;

    }
    public void testConexion() {
        try {
            getConnection().close();
            System.out.println("Conexión realizado con exito...");
        } catch (SQLException ex) {
            System.out.println("The test connection fail");
        }
    }

}
