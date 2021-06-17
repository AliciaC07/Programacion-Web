package org.practica.controller;

import io.javalin.Javalin;
import org.practica.models.*;
import org.practica.services.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.*;

public class ProductController {
    private final Javalin app;
    private Shop shop = Shop.getInstance();
    private ProductService productService = new ProductService();
    private ClientService clientService = new ClientService();
    private UserService userService = new UserService();
    private ReceiptService receiptService = new ReceiptService();
    public ProductController(Javalin app ){
        this.app = app;
    }

    public void applyRoutes() {
        app.routes(() -> {
            path("/product", () -> {


                get("/", ctx -> {
                    Map<String, Object> model = new HashMap<>();
                    model.put("title", "Home");
                    model.put("products", productService.findAllProducts());
                    ShoppingCart shoppingCart = ctx.sessionAttribute("cart");
                    if (shoppingCart == null){
                        model.put("cartCount", "Cart(0)");
                    }else {
                        model.put("cartCount", "Cart("+shoppingCart.getProducts().size()+")");
                    }
                    if ( ctx.cookie("userToken") != null){

                        Map<String, String> cookie  = userService.findCookie(ctx.cookie("userToken"));

                        if (shop.tokenVerify(cookie.get("user"), (cookie.get("token")))){
                            model.put("isLogged", true);
                        }else{
                            model.put("isLogged", false);
                        }

                    }else if (ctx.sessionAttribute("user") != null){
                        model.put("isLogged", true);
                    }else {
                        model.put("isLogged", false);
                    }
                    ctx.render("/public/home.html", model);

                });

                get("/create", ctx -> {
                    Map<String, Object> model = new HashMap<>();
                    model.put("title", "Create Product");
                    model.put("action_form", "/product/create");
                    model.put("action", "Create Product");
                    model.put("buying", false);
                    model.put("edit", false);
                    if (ctx.cookie("userToken") != null){
                        model.put("isLogged", true);
                    }else if (ctx.sessionAttribute("user") != null){
                        model.put("isLogged", true);
                    }else {
                        model.put("isLogged", false);
                        ctx.redirect("/user");
                    }

                    ctx.render("/public/product.html", model);
                });
                get("/edit/:id", ctx -> {
                    Product product = productService.findProductByIdAndActive(ctx.pathParam("id", Integer.class).get());

                    Map<String, Object> model = new HashMap<>();
                    if (product == null){
                        ctx.redirect("/product");
                    }
                    model.put("product", product);
                    model.put("title", "Edit Product");
                    model.put("action", "Edit Product");
                    model.put("action_form", "/product/edit/"+product.getId());
                    model.put("buying", false);
                    model.put("edit", true);
                    if ( ctx.cookie("userToken") != null ){

                        Map<String, String> cookie  = userService.findCookie(ctx.cookie("userToken"));

                        if (shop.tokenVerify(cookie.get("user"), (cookie.get("token")))){
                            model.put("isLogged", true);
                        }else{
                            model.put("isLogged", false);
                        }

                    }else if (ctx.sessionAttribute("user") != null){
                        model.put("isLogged", true);
                    }else {
                        model.put("isLogged", false);
                        ctx.redirect("/user");
                    }

                    ctx.render("/public/product.html",model);


                });
                ///edit post done
                post("/edit/:id", ctx -> {
                    String name = ctx.formParam("name");
                    if (ctx.cookie("userToken") == null ){
                        ctx.redirect("/user");
                    }
                    Integer amount = Integer.parseInt(Objects.requireNonNull(ctx.formParam("amount")));
                    Float price = Float.parseFloat(Objects.requireNonNull(ctx.formParam("price")));
                    Boolean active = ctx.formParam("active", Boolean.class).get();
                    Product product = new Product(ctx.pathParam("id", Integer.class).get(),name,price,amount);
                    product.setActive(active);
                    productService.updateProduct(product);
                    ctx.redirect("/product");


                });
//done
                post("/create", ctx -> {
                    if ( ctx.cookie("userToken") == null ){
                        ctx.redirect("/product");
                    }
                    String name = ctx.formParam("name");
                    float price = Float.parseFloat(Objects.requireNonNull(ctx.formParam("price")));
                    int amount = Integer.parseInt(Objects.requireNonNull(ctx.formParam("amount")));
                    if (name == null || price == 0 || amount == 0){
                        ctx.redirect("/public/product.html");
                        return;
                    }
                    Product product = new Product(null,name,price,amount);
                    //Product product = shop.createProduct(name, price, amount);
                    productService.createProduct(product);
                    System.out.println(product);
                    ctx.redirect("/product");

                });
//done
                get("/delete/:id", ctx -> {

                    //shop.deleteProduct(ctx.pathParam("id", Integer.class).get());
                    productService.deleteProduct(ctx.pathParam("id", Integer.class).get());

                    ctx.redirect("/product");
                });

                get("/buy/remove/:id", ctx -> {
                    ShoppingCart shoppingCart = ctx.sessionAttribute("cart");
                    Integer idProduct = ctx.pathParam("id", Integer.class).get();
                    shoppingCart = shop.deleteFromCart(shoppingCart, idProduct);

                    ctx.sessionAttribute("cart", shoppingCart);

                    ctx.redirect("/product/shopping-cart");

                });
                get("/buy/remove-all", ctx -> {
                    ShoppingCart shoppingCart = ctx.sessionAttribute("cart");
                    System.out.println("entro");
                    shoppingCart = shop.removeAllFromCart(shoppingCart);
                    ctx.sessionAttribute("cart", shoppingCart);
                    ctx.redirect("/product/shopping-cart");

                });

///working on
                get("/buy/:id",ctx -> {
                    Product product = productService.findProductById(ctx.pathParam("id", Integer.class).get());
                    Map<String, Object> model = new HashMap<>();
                    model.put("title", "Buy Product");
                    model.put("action", "Buy Product");
                    model.put("buying", true);
                    model.put("edit", false);
                    model.put("action_form", "/product/buy/"+product.getId());
                    model.put("product", product);
                    ShoppingCart shoppingCart = ctx.sessionAttribute("cart");
                    if (shoppingCart == null){
                        model.put("cartCount", "Cart(0)");
                    }else {
                        model.put("cartCount", "Cart("+shoppingCart.getProducts().size()+")");
                    }
                    if ( ctx.cookie("userToken") != null ){
                        Map<String, String> cookie  = userService.findCookie(ctx.cookie("userToken"));
                        if (shop.tokenVerify(cookie.get("user"), (cookie.get("token")))){
                            model.put("isLogged", true);
                            ctx.redirect("/product");
                        }else{
                            model.put("isLogged", false);

                        }

                    }else if (ctx.sessionAttribute("user") != null){
                        model.put("isLogged", true);
                        ctx.redirect("/product");
                    }else {
                        model.put("isLogged", false);
                    }
                    ctx.render("/public/product.html", model);
                });
///dique done
                post("/buy/:id", ctx -> {
                    ArrayList<Product> productsCart = new ArrayList<>();
                    ShoppingCart shoppingCart = new ShoppingCart();
                    Product product = productService.findProductByIdAndActive(ctx.pathParam("id", Integer.class).get());
                    Product productselected = new Product();
                    productselected.setId(product.getId());
                    productselected.setName(product.getName());
                    productselected.setPrice(product.getPrice());
                    productselected.setActive(product.getActive());
                    Integer amountSelected = ctx.formParam("quantity", Integer.class).get();
                    productselected.setAmount(amountSelected);
                    product.setAmount(product.getAmount()- amountSelected);
                    //shop.updateProduct(product.getName(),product.getAmount(), product.getPrice(),product.getId());
                    productService.updateProduct(product);

                    if (ctx.sessionAttribute("cart") == null){
                        shop.createShoppingCart(shoppingCart);
                        productsCart.add(productselected);
                        shoppingCart.setProducts(productsCart);
                       ctx.sessionAttribute("cart", shoppingCart);
//

                    }else {
                        ShoppingCart shoppingCartFound = ctx.sessionAttribute("cart");
                        shoppingCart = shop.findShoppingCartById(shoppingCartFound.getId());

                        for (Product aux :  shoppingCart.getProducts()) {
                            if (aux.getId().equals(productselected.getId())){
                                aux.setAmount(aux.getAmount() + productselected.getAmount());
                                ctx.sessionAttribute("cart", shoppingCart);
                                ctx.redirect("/product");
                                return;
                            }
                        }
                        shoppingCart.getProducts().add(productselected);
                        ctx.sessionAttribute("cart", shoppingCart);



                    }
                    ctx.redirect("/product");

                });

                get("/shopping-cart", ctx -> {
                    Map<String, Object> model = new HashMap<>();
                    model.put("title", "ShoppingCart");
                    model.put("cartList", "Car List");

                    if ( ctx.cookie("userToken") != null){

                        Map<String, String> cookie  = userService.findCookie(ctx.cookie("userToken"));
                        if (shop.tokenVerify(cookie.get("user"), (cookie.get("token")))){
                            model.put("isLogged", true);
                            ctx.redirect("/product");
                        }else{
                            model.put("isLogged", false);
                        }

                    }else if (ctx.sessionAttribute("user") != null){
                        model.put("isLogged", true);
                        ctx.redirect("/product");
                    }else {
                        model.put("isLogged", false);
                    }
                    if (ctx.sessionAttribute("cart") != null){
                        ShoppingCart shoppingCart = ctx.sessionAttribute("cart");
                        ArrayList<Product> products = shoppingCart.getProducts();

                        if (products.size() == 0){
                            model.put("emptyList", true);
                        }

                        model.put("cartCount", "Cart("+shoppingCart.getProducts().size()+")");

                        model.put("cartProducts", products);
                        model.put("total", shoppingCart.getTotal());
                    }else {
                        ArrayList<Product> products = new ArrayList<>();
                        model.put("cartProducts", products);
                        model.put("emptyList", true);
                        model.put("total", "00");
                        model.put("cartCount", "Cart(0)");
                    }

                    ctx.render("/public/shoppingCart.html", model);

                });

                post("/shopping-cart/buy", ctx -> {
                    String name = ctx.formParam("nameClient");
                    String lastName = ctx.formParam("lastNameClient");
                    String email =  ctx.formParam("emailClient");
                    Client client = clientService.findClientByEmail(email);
                    ShoppingCart shoppingCart = ctx.sessionAttribute("cart");
                    Receipt receipt = new Receipt();
                    Map<String, String> cookie  = userService.findCookie(ctx.cookie("userToken"));
                    if (shop.tokenVerify(cookie.get("user"), (cookie.get("token")))){
                        ctx.redirect("/product");
                    }
                    if (client.getEmail() == null){
                        Client newClient = new Client();
                        newClient.setLastName(lastName);
                        newClient.setName(name);
                        newClient.setEmail(email);
                        clientService.createClient(newClient);
                        receipt.setClient(newClient);
                        //receipt.setShoppingCart(shoppingCart);
                        receipt.setProducts(shoppingCart.getProducts());
                        User user = userService.findUserbyUsername("aliciac07");
                        receipt.setSalesman(user);
                        receipt.setDate(LocalDate.now());
                        receipt.setTotal(shoppingCart.getTotal());
                        //shop.createReceipt(receipt);
                        receiptService.createReceipt(receipt);
                        for (Product pr: shoppingCart.getProducts()) {
                            receiptService.insertProductReceipt(receipt, pr);
                        }

                        ctx.sessionAttribute("cart", null);
                        ctx.redirect("/product");


                    }else{
                        receipt.setClient(client);
                        //receipt.setShoppingCart(shoppingCart);
                        receipt.setProducts(shoppingCart.getProducts());
                        User user = userService.findUserbyUsername("aliciac07");
                        receipt.setSalesman(user);
                        receipt.setDate(LocalDate.now());
                        receipt.setTotal(shoppingCart.getTotal());
                        //shop.createReceipt(receipt);
                        receiptService.createReceipt(receipt);
                        for (Product pr: shoppingCart.getProducts()) {
                            receiptService.insertProductReceipt(receipt, pr);
                        }

                        ctx.sessionAttribute("cart", null);
                        //System.out.println(receipt.getShoppingCart().getTotal());
                        ctx.redirect("/product");

                    }

                });


            });


        });


    }


}
