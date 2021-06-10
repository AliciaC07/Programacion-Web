package org.practica.services;

import org.practica.models.Product;
import org.practica.models.Receipt;
import org.practica.models.ShoppingCart;

import java.sql.*;

public class ReceiptService {

    public Receipt createReceipt(Receipt receipt){
        Connection connection = DataBaseService.getInstance().getConnection();


        String sql_create = "INSERT INTO RECEIPT (DATE, EMAIL_CLIENT, ID_USER, TOTAL) VALUES (?,?,?,?);";
        String sql_giveId = "SELECT SCOPE_IDENTITY() as ID";
        String sql_setProducts = "INSERT INTO RECEIPT_PRODUCT (ID_RECEIPT, ID_PRODUCT, AMOUNT) VALUES (?,?,?)";
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
            System.out.println("Occurred an error inserting the product");
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

        String sql_setProducts = "INSERT INTO RECEIPT_PRODUCT (ID_RECEIPT, ID_PRODUCT, AMOUNT) VALUES (?,?,?)";
        try{
            PreparedStatement ps = connection.prepareStatement(sql_setProducts);
            ps.setInt(1, receipt.getId());
            ps.setInt(2, product.getId());
            ps.setInt(3, product.getAmount());
            int row = ps.executeUpdate();

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

}
