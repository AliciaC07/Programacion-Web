package org.practica.controller;

import io.javalin.Javalin;
import org.practica.models.*;
import org.practica.services.Shop;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.*;

public class ProductController {
    private final Javalin app;
    private Shop shop = Shop.getInstance();

    public ProductController(Javalin app ){
        this.app = app;
    }

    public void applyRoutes() {
        app.routes(() -> {
            path("/product", () -> {
                get("/", ctx -> {
                    Map<String, Object> model = new HashMap<>();
                    model.put("title", "Home");
                    model.put("class", "nav-link active");
                    model.put("products", shop.getAllProducts());
                    if (ctx.cookie("userName") != null || ctx.sessionAttribute("user") != null){
                        model.put("isLogged", true);
                    }else {
                        model.put("isLogged", false);
                    }
                    ctx.render("/public/home.html", model);

                });

                get("/create", ctx -> {
                    Map<String, Object> model = new HashMap<>();
                    Product product = new Product();
                    model.put("title", "Create Product");
                    model.put("action_form", "/product/create");
                    model.put("action", "Create Product");
                    model.put("buying", false);
                    if (ctx.cookie("userName") != null || ctx.sessionAttribute("user") != null){
                        model.put("isLogged", true);
                    }else {
                        model.put("isLogged", false);
                    }
                    ctx.render("/public/product.html", model);
                });
                get("/edit/:id", ctx -> {
                    Product product = shop.FindProductById(ctx.pathParam("id", Integer.class).get());

                    Map<String, Object> model = new HashMap<>();
                    if (product == null){
                        ctx.redirect("/product");
                    }
                    model.put("product", product);
                    model.put("title", "Edit Product");
                    model.put("action", "Edit Product");
                    model.put("action_form", "/product/edit/"+product.getId());
                    model.put("buying", false);
                    if (ctx.cookie("userName") != null || ctx.sessionAttribute("user") != null){
                        model.put("isLogged", true);
                    }else {
                        model.put("isLogged", false);
                    }

                    ctx.render("/public/product.html",model);


                });
                ///edit post
                post("/edit/:id", ctx -> {
                    String name = ctx.formParam("name");
                    Integer amount = Integer.parseInt(Objects.requireNonNull(ctx.formParam("amount")));
                    Float price = Float.parseFloat(Objects.requireNonNull(ctx.formParam("price")));
                    Product productEdited = shop.updateProduct(name, amount, price, ctx.pathParam("id", Integer.class).get());
                    System.out.println(shop.FindProductById(ctx.pathParam("id", Integer.class).get()).getAmount());
                    ctx.redirect("/product");


                });

                post("/create", ctx -> {
                    String name = ctx.formParam("name");
                    float price = Float.parseFloat(Objects.requireNonNull(ctx.formParam("price")));
                    int amount = Integer.parseInt(Objects.requireNonNull(ctx.formParam("amount")));
                    if (name == null || price == 0 || amount == 0){
                        ctx.redirect("/public/product.html");
                        return;
                    }
                    Product product = shop.createProduct(name, price, amount);
                    System.out.println(product);
                    ctx.redirect("/product");

                });

                get("/delete/:id", ctx -> {
                    shop.deleteProduct(ctx.pathParam("id", Integer.class).get());
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

                get("/buy/:id",ctx -> {
                    Product product = shop.FindProductById(ctx.pathParam("id", Integer.class).get());
                    Map<String, Object> model = new HashMap<>();
                    model.put("title", "Buy Product");
                    model.put("action", "Buy Product");
                    model.put("buying", true);
                    model.put("action_form", "/product/buy/"+product.getId());
                    model.put("product", product);
                    if (ctx.cookie("userName") != null || ctx.sessionAttribute("user") != null){
                        model.put("isLogged", true);
                    }else {
                        model.put("isLogged", false);
                    }
                    ctx.render("/public/product.html", model);


                });

                post("/buy/:id", ctx -> {
                    ArrayList<Product> productsCart = new ArrayList<>();
                    ShoppingCart shoppingCart = new ShoppingCart();
                    Product product = shop.FindProductById(ctx.pathParam("id", Integer.class).get());
                    Product productselected = new Product();
                    productselected.setId(product.getId());
                    productselected.setName(product.getName());
                    productselected.setPrice(product.getPrice());
                    Integer amountSelected = ctx.formParam("quantity", Integer.class).get();
                    productselected.setAmount(amountSelected);
                    product.setAmount(product.getAmount()- amountSelected);
                    shop.updateProduct(product.getName(),product.getAmount(), product.getPrice(),product.getId());
                    if (ctx.sessionAttribute("cart") == null){
                       shop.createShoppingCart(shoppingCart);
                       productsCart.add(productselected);
                       shoppingCart.setProducts(productsCart);
                        ctx.sessionAttribute("cart", shoppingCart);

                        for (Product aux: shoppingCart.getProducts()) {
                            System.out.println(aux.getName());
                        }

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

//                        for (Product aux: shoppingCart.getProducts()) {
//                            System.out.println(aux.getName());
//                        }

                    }
                    ctx.redirect("/product");

                });

                get("/shopping-cart", ctx -> {
                    Map<String, Object> model = new HashMap<>();
                    model.put("title", "ShoppingCart");
                    model.put("cartList", "Car List");
                    if (ctx.cookie("userName") != null || ctx.sessionAttribute("user") != null){
                        model.put("isLogged", true);
                    }else {
                        model.put("isLogged", false);
                    }
                    if (ctx.sessionAttribute("cart") != null){
                        ShoppingCart shoppingCart = ctx.sessionAttribute("cart");
                        ArrayList<Product> products = shoppingCart.getProducts();
                        for (Product aux: products) {
                            System.out.println(aux.getName() + aux.getId() + aux.getPrice() + aux.getAmount());
                        }
                        if (products.size() == 0){
                            model.put("emptyList", true);
                        }
                        model.put("cartProducts", products);
                        model.put("total", shoppingCart.getTotal());
                    }else {
                        ArrayList<Product> products = new ArrayList<>();
                        model.put("cartProducts", products);
                        model.put("emptyList", true);
                        model.put("total", "00");
                    }

                    ctx.render("/public/shoppingCart.html", model);

                });

                post("/shopping-cart/buy", ctx -> {
                    String name = ctx.formParam("nameClient");
                    String lastName = ctx.formParam("lastNameClient");
                    String email =  ctx.formParam("emailClient");
                    Client client = shop.FindClientByEmail(email);
                    ShoppingCart shoppingCart = ctx.sessionAttribute("cart");
                    Receipt receipt = new Receipt();
                    if (client == null){
                        Client newClient = new Client();
                        newClient.setLastName(lastName);
                        newClient.setName(name);
                        newClient.setEmail(email);
                        receipt.setClient(newClient);
                        receipt.setShoppingCart(shoppingCart);
                        User user = shop.FindCUserByUsername("aliciac07");
                        receipt.setSalesman(user);
                        receipt.setDate(LocalDate.now());
                        receipt.setTotal(shoppingCart.getTotal());
                        shop.createReceipt(receipt);
                        ArrayList<Product> products = new ArrayList<>();
                        ShoppingCart shoppingCartNew = new ShoppingCart();
                        shoppingCartNew.setProducts(products);
                        ctx.req.getSession().invalidate();
                        shop.createShoppingCart(shoppingCart);

                        ctx.redirect("/product");
                        System.out.println(receipt.getShoppingCart().getTotal());

                    }else{
                        receipt.setClient(client);
                        receipt.setShoppingCart(shoppingCart);
                        User user = shop.FindCUserByUsername("aliciac07");
                        receipt.setSalesman(user);
                        receipt.setDate(LocalDate.now());
                        receipt.setTotal(shoppingCart.getTotal());
                        shop.createReceipt(receipt);
                        ArrayList<Product> products = new ArrayList<>();
                        ShoppingCart shoppingCartNew = new ShoppingCart();
                        shoppingCartNew.setProducts(products);
                        ctx.req.getSession().invalidate();
                        shop.createShoppingCart(shoppingCart);
                        System.out.println(receipt.getShoppingCart().getTotal());

                        ctx.redirect("/product");

                    }

                });


            });


        });


    }


}
