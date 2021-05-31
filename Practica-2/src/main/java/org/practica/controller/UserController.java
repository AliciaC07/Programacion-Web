package org.practica.controller;

import io.javalin.Javalin;
import org.practica.models.User;
import org.practica.services.Shop;

import java.util.HashMap;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.*;

public class UserController {
    private final Javalin app;
    private Shop shop = Shop.getInstance();

    public UserController(Javalin app) {
        this.app = app;
    }

    public void applyRoutes(){
        app.routes(() -> {
            path("/user", () -> {
                get("/", ctx -> {
                    Map<String, Object> model = new HashMap<>();
                    model.put("title", "Shop");
                    ctx.render("/public/login.html",model);
                });

                post("/login", ctx -> {
                    Map<String, Object> model = new HashMap<>();
                    String userName = ctx.formParam("userName");
                    String password = ctx.formParam("password");
                    if (userName == null || password == null){
                        ctx.redirect("/public/login.html");
                        return;
                    }
                    User userLog = shop.authUser(userName, password);
                    if (userLog != null){
                        if (ctx.formParam("signed").equals("on")){
                            ctx.cookie("userName", userLog.getUserName(), 400000);
                        }
                        ctx.sessionAttribute("user", userLog);
                    }else{
                        //crear user
                        System.out.println(ctx.formParam("signed"));
                        User newUser = shop.createUser(userName, password);
                        ctx.sessionAttribute("user", newUser);


                    }
                    ctx.redirect("/product");

                });

            });
        });
    }

}
