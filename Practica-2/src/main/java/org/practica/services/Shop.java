package org.practica.services;

import org.practica.models.Client;
import org.practica.models.Product;
import org.practica.models.Receipt;
import org.practica.models.User;

import java.util.ArrayList;

public class Shop {
    private static Shop shop = null;
    private ArrayList<Client> clients = new ArrayList<>();
    private ArrayList<Product> products = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<Receipt> receipts = new ArrayList<>();

    private Shop() {
        products.add(new Product(1, "Milk", 10.00f, 3));
        products.add(new Product(1, "Bread", 5.00f, 2));
        products.add(new Product(1, "Soap", 2.00f, 3));
        products.add(new Product(1, "Beer", 15.00f, 3));
        users.add(new User(1, "aliciac07", "123"));

    }

    public static Shop getInstance(){
        if(shop == null){
            shop = new Shop();
        }
        return shop;
    }

    public void AddProducts(Product product){
        if (products == null){
            product.setId(1);

        }else {
            product.setId(products.size()+1);
        }
        products.add(product);
    }
    public void AddClient(Client client){
        clients.add(client);
    }
    public User authUser(String userName, String password){
        User user = FindCUserByUsername(userName);
        if (user == null){
            return null;
        }else if (user.getPassword().equalsIgnoreCase(password)){
            return user;
        }
        return null;
    }
    public User createUser(String userName, String password){
        User newUser = new User();
        if (users == null){
            newUser.setId(1);
            newUser.setUserName(userName);
            newUser.setPassword(password);
            users.add(newUser);
            return newUser;
        }else {
            newUser.setId(users.size()+1);
            newUser.setUserName(userName);
            newUser.setPassword(password);
        }
        return newUser;
    }

    public ArrayList<Product> getAllProducts(){
        return products;
    }

    public Product FindProductById(Integer id){

        for (Product aux: products) {
            if (aux.getId().equals(id)){
                return aux;
            }
        }
        return null;
    }

    public Product createProduct(String name, Float price, Integer amount){
        Product product = new Product(1, name, price, amount);
        if (products == null){
            products.add(product);
            return product;
        }else {
            products.add(product);
            return product;
        }
    }

    public Client FindClientByEmail(String email){

        for (Client aux: clients) {
            if (aux.getEmail().equals(email)){
                return aux;
            }
        }
        return null;
    }
    public User FindCUserByUsername(String userName){
        for (User aux: users) {
            if (aux.getUserName().equals(userName)){
                return aux;
            }
        }
        return null;
    }



}
