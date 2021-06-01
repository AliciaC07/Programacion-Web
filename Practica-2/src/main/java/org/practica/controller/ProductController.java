package org.practica.controller;

import io.javalin.Javalin;
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

                post("/create", ctx -> {
                    Map<String, Object> model = new HashMap<>();


                });
            });


        });


    }


}
