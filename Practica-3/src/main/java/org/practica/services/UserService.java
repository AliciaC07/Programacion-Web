package org.practica.services;

import org.jasypt.util.password.StrongPasswordEncryptor;
import org.practica.models.Product;
import org.practica.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

public class UserService {
    StrongPasswordEncryptor spe = new StrongPasswordEncryptor();

    public Boolean createUser(User user){
        Connection connection = DataBaseService.getInstance().getConnection();
        boolean status = false;
        user.setPassword(spe.encryptPassword(user.getPassword()));
        String sql_create = "INSERT INTO USER (USER_NAME, PASSWORD) VALUES (?,?);";
        try{
            PreparedStatement ps = connection.prepareStatement(sql_create);

              ps.setString(1, user.getUserName());
              ps.setString(2, user.getPassword());
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
        return status;


    }

    public User findUserbyUsername(String userName){
        Connection connection = DataBaseService.getInstance().getConnection();
        User user = null;
        try{
            String sql_find = "SELECT * FROM USER WHERE USER_NAME = ?";
            PreparedStatement ps = connection.prepareStatement(sql_find);
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                user = new User();
                user.setUserName(rs.getString("USER_NAME"));
                user.setPassword(rs.getString("PASSWORD"));
                user.setId(rs.getInt("ID"));

            }
        }catch (SQLException e){
            System.out.println("Couldn't access the database");

        }finally {
            try{
                connection.close();
            }catch (SQLException e){
                System.out.println("Occurred an error closing the connection");
            }
        }
        return user;
    }

    public User authUser(String userName, String password){
        User userFound = findUserbyUsername(userName);
        System.out.println(spe.checkPassword(password, userFound.getPassword()));
        if (spe.checkPassword(password, userFound.getPassword())){
            return userFound;
        }else {
            throw new NoSuchElementException("This password is incorrect");
        }

    }
}
