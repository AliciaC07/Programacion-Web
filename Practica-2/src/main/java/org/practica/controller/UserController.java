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

                get("/logout", ctx -> {
//                    String id = ctx.req.getSession().getId();
//                    System.out.println(id);
                    //ctx.req.getSession().invalidate();
                    ctx.sessionAttribute("user", null);

                    ctx.redirect("/product");
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

                        ctx.sessionAttribute("user", userLog);
                    }else{
                        //crear user

                        User newUser = shop.createUser(userName, password);

                        ctx.sessionAttribute("user", newUser);


                    }
                    ctx.redirect("/product");

                });

            });
        });
    }

}
