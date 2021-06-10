package org.practica.services;

import org.practica.models.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductService {

    ///Create product
    public Boolean createProduct(Product product){
        Connection connection = DataBaseService.getInstance().getConnection();
        boolean status = false;
        String sql_create = "INSERT INTO PRODUCT (NAME, PRICE, AMOUNT) VALUES (?,?,?);";
        try{
            PreparedStatement ps = connection.prepareStatement(sql_create);
            ps.setString(1, product.getName());
            ps.setFloat(2, product.getPrice());
            ps.setInt(3, product.getAmount());
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

    public Product findProductById(Integer id){
        Connection connection = DataBaseService.getInstance().getConnection();
        Product product = null;
        try{
            String sql_find = "SELECT * FROM PRODUCT WHERE ID = ?;";
            PreparedStatement ps = connection.prepareStatement(sql_find);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                product = new Product();
                product.setId(rs.getInt("ID"));
                product.setName(rs.getString("NAME"));
                product.setPrice(rs.getFloat("PRICE"));
                product.setAmount(rs.getInt("AMOUNT"));
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
        return product;
    }

    public ArrayList<Product> findAllProducts(){
        ArrayList<Product> products = new ArrayList<>();
        Connection connection = DataBaseService.getInstance().getConnection();

        try {
            String sql_all = "SELECT * FROM PRODUCT";
            PreparedStatement ps = connection.prepareStatement(sql_all);
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                Product product = new Product(
                        rs.getInt("ID"),
                        rs.getString("NAME"),
                        rs.getFloat("PRICE"),
                        rs.getInt("AMOUNT")
                );
                products.add(product);
            }
            ps.close();
            rs.close();
        }catch (SQLException e){
            Logger.getLogger(ProductService.class.getName()).log(Level.SEVERE, null, e);
        }finally {
            try{
                connection.close();
            }catch (SQLException e){
                System.out.println("Occurred an error closing the connection");
            }
        }
        return products;

    }
    public Boolean updateProduct(Product product){
        Product productFound = findProductById(product.getId());
        Connection connection = DataBaseService.getInstance().getConnection();
        Boolean status = false;
        if (productFound != null){
            String sql_update = "UPDATE PRODUCT SET NAME=?, PRICE=?, AMOUNT=?";
            try{
                PreparedStatement ps = connection.prepareStatement(sql_update);
                ps.setString(1, product.getName());
                ps.setFloat(2, product.getPrice());
                ps.setInt(3, product.getAmount());
                int row = ps.executeUpdate();
                status = row > 0;
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
        return status;

    }


}
