package org.practica.services;

import org.practica.models.Product;
import org.practica.models.Receipt;
import org.practica.models.ShoppingCart;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReceiptService {
    private ClientService clientService = new ClientService();
    private UserService userService = new UserService();

    public Receipt createReceipt(Receipt receipt){
        Connection connection = DataBaseService.getInstance().getConnection();


        String sql_create = "INSERT INTO RECEIPT (DATE, EMAIL_CLIENT, ID_USER, TOTAL) VALUES (?,?,?,?);";
        String sql_giveId = "SELECT SCOPE_IDENTITY() as ID";

        try{
            PreparedStatement ps = connection.prepareStatement(sql_create);
            ps.setDate(1, Date.valueOf(receipt.getDate()));
            ps.setString(2, receipt.getClient().getEmail());
            ps.setInt(3, receipt.getSalesman().getId());
            ps.setFloat(4, receipt.getTotal());
            int row = ps.executeUpdate();
            ps = connection.prepareStatement(sql_giveId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                receipt.setId(rs.getInt("ID"));
            }

        }catch (SQLException e){
            System.out.println("Occurred an error inserting the receipt");
        }finally {
            try{
                connection.close();
            }catch (SQLException e){
                System.out.println("Occurred an error closing the connection");
            }
        }
        return receipt;
    }
    public void insertProductReceipt(Receipt receipt, Product product){
        Connection connection = DataBaseService.getInstance().getConnection();

        String sql_setProducts = "INSERT INTO RECEIPT_PRODUCT (ID_RECEIPT, ID_PRODUCT, AMOUNT, PRICE) VALUES (?,?,?,?)";
        try{
            PreparedStatement ps = connection.prepareStatement(sql_setProducts);
            ps.setInt(1, receipt.getId());
            ps.setInt(2, product.getId());
            ps.setInt(3, product.getAmount());
            ps.setFloat(4, product.getPrice());
            int row = ps.executeUpdate();

        }catch (SQLException e){
            System.out.println("Occurred an error inserting the receipt");
        }finally {
            try{
                connection.close();
            }catch (SQLException e){
                System.out.println("Occurred an error closing the connection");
            }
        }

    }public ArrayList<Receipt> findAllReceipts(){
        ArrayList<Receipt> receipts = new ArrayList<>();
        Connection connection = DataBaseService.getInstance().getConnection();

        try {
            String sql_all = "SELECT * FROM RECEIPT";
            PreparedStatement ps = connection.prepareStatement(sql_all);
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                Receipt receipt = new Receipt();
                receipt.setId(rs.getInt("ID"));
                receipt.setClient(clientService.findClientByEmail(rs.getString("EMAIL_CLIENT")));
                receipt.setDate(rs.getDate("DATE").toLocalDate());
                receipt.setSalesman(userService.findUserbyId(rs.getInt("ID_USER")));
                receipt.setProducts(findProductsRecipt(rs.getInt("ID")));
                receipts.add(receipt);


            }
            ps.close();
            rs.close();
        }catch (SQLException e){
            Logger.getLogger(Receipt.class.getName()).log(Level.SEVERE, null, e);
        }finally {
            try{
                connection.close();
            }catch (SQLException e){
                System.out.println("Occurred an error closing the connection");
            }
        }
        return receipts;

    }
    public List<Product> findProductsRecipt(Integer id){
        Connection connection = DataBaseService.getInstance().getConnection();
        ShoppingCart shoppingCart = new ShoppingCart();
        ProductService productService = new ProductService();
        ArrayList<Product> products = new ArrayList<>();
        try{
            String sql_find = "SELECT * from RECEIPT_PRODUCT\n" +
                    "WHERE ID_RECEIPT = ?";
            PreparedStatement ps = connection.prepareStatement(sql_find);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                shoppingCart.setId(rs.getInt("ID_RECEIPT"));
                Product product = productService.findProductById(rs.getInt("ID_PRODUCT"));
                product.setAmount(rs.getInt("AMOUNT"));
                product.setPrice(rs.getFloat("PRICE"));
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
        return products;
    }

}
