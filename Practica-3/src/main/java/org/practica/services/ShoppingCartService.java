package org.practica.services;

import org.practica.models.Product;
import org.practica.models.ShoppingCart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ShoppingCartService {

    public ShoppingCart createShoppingCart(ShoppingCart shoppingCart){
        Connection connection = DataBaseService.getInstance().getConnection();
        boolean status = false;

        String sql_create = "INSERT INTO SHOPPING_CART (NAME) VALUES (?);";
        String sql_giveId = "SELECT SCOPE_IDENTITY() as ID";
        try{
            PreparedStatement ps = connection.prepareStatement(sql_create);
            ps.setString(1,shoppingCart.getName());

            int row = ps.executeUpdate();
            status = row > 0;
            ps = connection.prepareStatement(sql_giveId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                shoppingCart.setId(rs.getInt("ID"));
            }
        }catch (SQLException e){
            System.out.println("Occurred an error inserting the product");
        }finally {
            try{
                connection.close();
            }catch (SQLException e){
                System.out.println("Occurred an error closing the connection");
            }
        }
        return shoppingCart;


    }
    public void updateShoppingCart(ShoppingCart shoppingCart, Product productSelected){
        ///ShoppingCart shoppingCartFound = findShoppingCartById(shoppingCart.getId());
        Connection connection = DataBaseService.getInstance().getConnection();

        if (shoppingCart != null){
            String sql_update = "INSERT INTO SHOPPING_CART_PRODUCT SET ID_SHOPPING_CART = ?, ID_PRODUCT = ?, AMOUNT = ?";
            try{
                PreparedStatement ps = connection.prepareStatement(sql_update);
                ps.setInt(1, shoppingCart.getId());
                ps.setInt(2, productSelected.getId());
                ps.setInt(3, productSelected.getAmount());
                int row = ps.executeUpdate();
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
    public void updateProductShoppingCart(ShoppingCart shoppingCart, Integer amount){
        Connection connection = DataBaseService.getInstance().getConnection();

        if (shoppingCart != null){
            String sql_update = "UPDATE SHOPPING_CART_PRODUCT SET AMOUNT = ? WHERE ID_SHOPPING_CART = ?";
            try{
                PreparedStatement ps = connection.prepareStatement(sql_update);
                ps.setInt(1, amount);
                ps.setInt(2, shoppingCart.getId());
                int row = ps.executeUpdate();
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
    public ShoppingCart findShoppingCartById(Integer id){
        Connection connection = DataBaseService.getInstance().getConnection();
        ShoppingCart shoppingCart = new ShoppingCart();
        ProductService productService = new ProductService();
        ArrayList<Product> products = new ArrayList<>();
        try{
            String sql_find = "SELECT * from SHOPPING_CART_PRODUCT\n" +
                    "WHERE ID_SHOPPING_CART = ?";
            PreparedStatement ps = connection.prepareStatement(sql_find);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                shoppingCart.setId(rs.getInt("ID_SHOPPING_CART"));
                Product product = productService.findProductByIdAndActive(rs.getInt("ID_PRODUCT"));
                product.setAmount(rs.getInt("AMOUNT"));
                products.add(product);

            }
            shoppingCart.setProducts(products);

        }catch (SQLException e){
            System.out.println("Couldn't access the database");

        }finally {
            try{
                connection.close();
            }catch (SQLException e){
                System.out.println("Occurred an error closing the connection");
            }
        }
        return shoppingCart;
    }

}
