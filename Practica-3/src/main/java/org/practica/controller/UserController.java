package org.practica.controller;

import io.javalin.Javalin;
import org.jasypt.util.password.StrongPasswordEncryptor;
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
                    if (ctx.cookie("userToken") != null){
                        ctx.redirect("/product");
                    }
                    ctx.render("/public/login.html",model);
                });

                get("/logout", ctx -> {

                    if (ctx.cookie("userToken") != null){
                        userService.disableCookie(ctx.cookie("userToken"));
                        ctx.removeCookie("userToken","/");
                    }


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

                        if (ctx.formParam("signed") != null){
                            String tokenCookie = shop.tokenCreated(userLog);
                            ctx.cookie("userToken", tokenCookie);
                            Map<String, String> cookieFound = userService.findCookieByUsername(userLog.getUserName());
                            if (cookieFound.get("user") != null){
                                userService.updateCookieVerification(userLog.getUserName(), tokenCookie);
                            }else {
                                userService.insertCookieVerification(userLog.getUserName(), tokenCookie);
                            }


                        }

                    }else{
                        //crear user

                        User newUser = new User();
                        newUser.setUserName(userName);
                        newUser.setPassword(password);
                        userService.createUser(newUser);
                        if (ctx.formParam("signed") != null){
                            String tokenCookie = shop.tokenCreated(newUser);
                            ctx.cookie("userToken", tokenCookie);
                            Map<String, String> cookieFound = userService.findCookieByUsername(newUser.getUserName());
                            if (cookieFound.get("user") != null){
                                userService.updateCookieVerification(newUser.getUserName(), tokenCookie);
                            }else {
                                userService.insertCookieVerification(newUser.getUserName(), tokenCookie);
                            }
                        }



                    }
                    ctx.redirect("/product");

                });

            });
        });
    }

}
