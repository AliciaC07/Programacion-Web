package org.practica.controller;

import io.javalin.Javalin;
import org.practica.models.User;
import org.practica.services.Shop;
import org.practica.services.UserService;

import java.util.HashMap;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.*;

public class UserController {
    private final Javalin app;
    private Shop shop = Shop.getInstance();
    private UserService userService = new UserService();

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

                    if (ctx.cookie("userName") != null){
                        ctx.removeCookie("userName","/");
                    }
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
                    User userLog = userService.authUser(userName,password);
                    if (userLog != null){

                        if (ctx.formParam("signed").equals("on")){
                            ctx.cookie("userName", userLog.getUserName(), 400000);
                        }

                        ctx.sessionAttribute("user", userLog);
                    }else{
                        //crear user

                        User newUser = new User();
                        newUser.setUserName(userName);
                        newUser.setPassword(password);
                        userService.createUser(newUser);
                        if (ctx.formParam("signed").equals("on")){
                            ctx.cookie("userName", newUser.getUserName(), 400000);
                        }
                        ctx.sessionAttribute("user", newUser);


                    }
                    ctx.redirect("/product");

                });

            });
        });
    }

}
