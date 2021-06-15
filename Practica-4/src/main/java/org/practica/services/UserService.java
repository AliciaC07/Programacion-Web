package org.practica.services;

import org.jasypt.util.password.StrongPasswordEncryptor;
import org.practica.models.Product;
import org.practica.models.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
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
            System.out.println("Occurred an error inserting the user");
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
    public User findUserbyId(Integer id){
        Connection connection = DataBaseService.getInstance().getConnection();
        User user = null;
        try{
            String sql_find = "SELECT * FROM USER WHERE ID = ?";
            PreparedStatement ps = connection.prepareStatement(sql_find);
            ps.setInt(1, id);
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

    public void insertCookieVerification(String userName, String token){
        Connection connection = DataBaseService.getInstance().getConnection();
        String sql_create = "INSERT INTO COOKIE_VERIFICATION (USERNAME, TOKEN, DATE_CREATED) VALUES (?,?,?);";
        try{
            PreparedStatement ps = connection.prepareStatement(sql_create);
            ps.setString(1, userName);
            ps.setString(2, token);
            ps.setDate(3, Date.valueOf(LocalDate.now()));
            ps.executeUpdate();

        }catch (SQLException e){
            System.out.println("Occurred an error inserting the Cookie validation");
        }finally {
            try{
                connection.close();
            }catch (SQLException e){
                System.out.println("Occurred an error closing the connection");
            }
        }
    }
    public void updateCookieVerification(String userName, String token){
        Connection connection = DataBaseService.getInstance().getConnection();
        String sql_create = "UPDATE COOKIE_VERIFICATION SET TOKEN = ?, DATE_UPDATED = ? WHERE USERNAME = ?";
        try{
            PreparedStatement ps = connection.prepareStatement(sql_create);
            ps.setString(1, token);
            ps.setDate(2, Date.valueOf(LocalDate.now()));
            ps.setString(3, userName);
            ps.executeUpdate();

        }catch (SQLException e){
            System.out.println("Occurred an error inserting the Cookie validation");
        }finally {
            try{
                connection.close();
            }catch (SQLException e){
                System.out.println("Occurred an error closing the connection");
            }
        }
    }


    public Map<String, String> findCookie(String token){
        Connection connection = DataBaseService.getInstance().getConnection();
        Map<String, String> cookie = new HashMap<>();
        try{
            String sql_find = "SELECT * FROM COOKIE_VERIFICATION WHERE TOKEN = ?";
            PreparedStatement ps = connection.prepareStatement(sql_find);
            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
               cookie.put("token", rs.getString("TOKEN"));
               cookie.put("user", rs.getString("USERNAME"));

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
        return cookie;
    }
    public Map<String, String> findCookieByUsername(String userName){
        Connection connection = DataBaseService.getInstance().getConnection();
        Map<String, String> cookie = new HashMap<>();
        try{
            String sql_find = "SELECT * FROM COOKIE_VERIFICATION WHERE USERNAME = ?";
            PreparedStatement ps = connection.prepareStatement(sql_find);
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                cookie.put("token", rs.getString("TOKEN"));
                cookie.put("user", rs.getString("USERNAME"));

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
        return cookie;
    }
    public void disableCookie(String token){
        Connection connection = DataBaseService.getInstance().getConnection();
        String sql_update = "DELETE FROM COOKIE_VERIFICATION WHERE TOKEN = ? ";
        try{
            PreparedStatement ps = connection.prepareStatement(sql_update);
            ps.setString(1, token);

            ps.executeUpdate();

            ps.close();
        }catch (SQLException e){
            System.out.println("Occurred an error updating the product");
        }finally {
            try{
                connection.close();
            }catch (SQLException e){
                System.out.println("Occurred an error closing the connection");
            }
        }
    }
}
