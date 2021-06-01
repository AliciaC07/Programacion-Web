package org.practica.controller;

import io.javalin.Javalin;
import org.practica.models.Product;
import org.practica.services.Shop;

import java.util.HashMap;
import java.util.Map;

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
                    model.put("title", "Create Product");
                    model.put("action_form", "/product/create");
                    ctx.render("/public/product.html", model);
                });
                get("/edit:id", ctx -> {
                    Product product = shop.FindProductById(ctx.pathParam("id", Integer.class).get());
                    Map<String, Object> model = new HashMap<>();
                    if (product == null){
                        ctx.redirect("/product");
                    }
                    model.put("product", product);
                    model.put("title", "Edit Product");
                    model.put("action_form", "/product/edit");




                });

                post("/create", ctx -> {
                    String name = ctx.formParam("name");
                    float price = Float.parseFloat(ctx.formParam("price"));
                    int amount = Integer.parseInt(ctx.formParam("amount"));
                    if (name == null || price == 0 || amount == 0){
                        ctx.redirect("/public/product.html");
                        return;
                    }
                    Product product = shop.createProduct(name, price, amount);
                    System.out.println(product);
                    ctx.redirect("/product");

                });
            });


        });


    }


}
