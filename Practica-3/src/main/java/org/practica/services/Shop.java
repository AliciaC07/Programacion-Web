package org.practica.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.practica.config.JwtGen;
import org.practica.models.*;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class Shop {
    private static Shop shop = null;

    private ArrayList<Client> clients = new ArrayList<>();
    private ArrayList<Product> products = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<Receipt> receipts = new ArrayList<>();
    private ArrayList<ShoppingCart> shoppingCarts = new ArrayList<>();
    StrongPasswordEncryptor spe = new StrongPasswordEncryptor();

    private Shop() {
        products.add(new Product(1, "Milk", 10.00f, 3));
        products.add(new Product(2, "Bread", 5.00f, 2));
        products.add(new Product(3, "Soap", 2.00f, 3));
        products.add(new Product(4, "Beer", 15.00f, 3));
        products.add(new Product(5, "Paper", 15.00f, 0));
        users.add(new User(1, "aliciac07", "123"));

    }

    public static Shop getInstance(){
        if(shop == null){
            shop = new Shop();
        }
        return shop;
    }
    public String tokenCreated(User user){
        String jwtId = "PacoFish";
        String jwtIssuer = "JWT Gen";
        String jwtSubject = user.getUserName();
        int jwtTimeToLive = 620000;

        return JwtGen.createJWT(
                jwtId, // claim = jti
                jwtIssuer, // claim = iss
                jwtSubject, // claim = sub
                jwtTimeToLive // used to calculate expiration (claim = exp)
        );
    }
    public Boolean tokenVerify(String userName, String jwt){
        String jwtId = "PacoFish";
        String jwtIssuer = "JWT Gen";
        try{
            if (jwt != null){
                Claims claims = JwtGen.decodeJWT(jwt);
                if (claims.getId().equals(jwtId) && claims.getIssuer().equals(jwtIssuer)
                        && claims.getSubject().equals(userName)){
                    return true;
                }else {
                    return false;
                }
            }


        }catch (JwtException e){
            System.out.println("Token is invalid");
        }

        return false;
    }
    public Product createProduct(String name, Float price, Integer amount){
        Product product = new Product(1, name, price, amount);
        if (products == null){
            products.add(product);
            return product;
        }else {
            product.setId(products.size()+1);
            products.add(product);
            return product;
        }
    }

    public Product updateProduct(String name, Integer amount, Float price, Integer id){
        Product productFound = FindProductById(id);
        if (productFound != null){
            productFound.setAmount(amount);
            productFound.setPrice(price);
            productFound.setName(name);

        }else {
            throw new NoSuchElementException("This product doesn't exists");
        }
        return productFound;

    }
    public void deleteProduct(Integer id){
        Product product = FindProductById(id);
        if (product != null){
            products.remove(product);

        }else {
            throw new NoSuchElementException("This product doesn't exists");
        }
    }
    public ShoppingCart createShoppingCart(ShoppingCart shoppingCart){
        if (shoppingCarts == null){
            shoppingCart.setId(1);

        }else{
            shoppingCart.setId(shoppingCarts.size()+1);
        }
        shoppingCarts.add(shoppingCart);
        return shoppingCart;
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
    public ArrayList<User> getallUser(){
        return users;
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
    public ShoppingCart deleteFromCart(ShoppingCart shoppingCart, Integer idProduct){
        ProductService productService = new ProductService();
        Product product = productService.findProductByIdAndActive(idProduct);
        for (Product aux: shoppingCart.getProducts()) {
            if (aux.getId().equals(idProduct)){
                product.setAmount(product.getAmount() + aux.getAmount());
                productService.updateProduct(product);
                shoppingCart.getProducts().remove(aux);
                return shoppingCart;
            }
        }
        return null;
    }
    public ShoppingCart removeAllFromCart(ShoppingCart shoppingCart){
        for (int i = shoppingCart.getProducts().size() - 1; i >= 0; i--) {
            deleteFromCart(shoppingCart, shoppingCart.getProducts().get(i).getId());
        }
        return shoppingCart;
    }

    public ArrayList<Product> getAllProducts(){
        return products;
    }

    public ArrayList<Receipt> getAllReceipts(){
        return receipts;
    }

    public Product FindProductById(Integer id){

        for (Product aux: products) {
            if (aux.getId().equals(id)){
                return aux;
            }
        }
        return null;
    }

    public ShoppingCart findShoppingCartById(Integer id){
        for (ShoppingCart cart: shoppingCarts) {
            if (cart.getId().equals(id)){
                return cart;
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
