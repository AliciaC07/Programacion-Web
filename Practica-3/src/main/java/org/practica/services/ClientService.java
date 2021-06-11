package org.practica.services;

import org.practica.models.Client;
import org.practica.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientService {

    public void createClient(Client client){
        Connection connection = DataBaseService.getInstance().getConnection();
        boolean status = false;

        String sql_create = "INSERT INTO CLIENT (NAME, LAST_NAME, EMAIL) VALUES (?,?,?);";

        try{
            PreparedStatement ps = connection.prepareStatement(sql_create);

            ps.setString(1, client.getName());
            ps.setString(2, client.getLastName());
            ps.setString(3, client.getEmail());
            int row = ps.executeUpdate();
            status = row > 0;
        }catch (SQLException e){
            System.out.println("Occurred an error inserting the product");
        }finally {
            try{
                connection.close();
            }catch (SQLException e){
                System.out.println("Occurred an error closing the connection");
            }
        }

    }

    public Client findClientByEmail(String email){
        Connection connection = DataBaseService.getInstance().getConnection();
        Client client = new Client();
        String sql_create = "SELECT * FROM CLIENT WHERE EMAIL = ?";
        try{
            PreparedStatement ps = connection.prepareStatement(sql_create);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                client.setEmail(rs.getString("EMAIL"));
                client.setName(rs.getString("NAME"));
                client.setLastName(rs.getString("LAST_NAME"));
                client.setId(rs.getInt("ID"));
            }


        }catch (SQLException e){
            System.out.println("Occurred an error searching the client");
        }finally {
            try{
                connection.close();
            }catch (SQLException e){
                System.out.println("Occurred an error closing the connection");
            }
        }
        return client;
    }

}
