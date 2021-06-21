package org.practica.controller;

import io.javalin.Javalin;
import org.practica.models.CookieVerification;
import org.practica.models.User;
import org.practica.services.CookieVerificationService;
import org.practica.services.Shop;
import org.practica.services.UserService;

import java.util.HashMap;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.*;

public class UserController {
    private final Javalin app;
    Shop shop = Shop.getInstance();
    private UserService userService = UserService.getInstance();
    private CookieVerificationService cookieVerificationService = CookieVerificationService.getInstance();

    public UserController(Javalin app) {
        this.app = app;
    }

    public void applyRoutes(){

        app.routes(() -> {
            path("/user", () ->{
                get("/", ctx -> {
                    Map<String, Object> model = new HashMap<>();
                    model.put("title", "Loging");
                    if (ctx.cookie("userToken") != null){
                        ctx.redirect("/product");
                    }else if (ctx.sessionAttribute("user") != null){
                        ctx.redirect("/product");
                    }
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
                    User userLog = userService.authUser(userName,password);
                    if (userLog != null){

                        if (ctx.formParam("signed") != null){
                            String tokenCookie = shop.tokenCreated(userLog);
                            ctx.cookie("userToken", tokenCookie);
                            Map<String, String> cookieFound = cookieVerificationService.findByCookieUsername(userLog.getUserName());
                            if (cookieFound.get("user") != null){
                                CookieVerification cookieVerification = new CookieVerification();
                                cookieVerification.setUsername(userLog.getUserName());
                                cookieVerification.setToken(tokenCookie);
                                cookieVerificationService.edit(cookieVerification);
                            }else {
                                CookieVerification cookieVerification = new CookieVerification();
                                cookieVerification.setUsername(userLog.getUserName());
                                cookieVerification.setToken(tokenCookie);
                                cookieVerificationService.create(cookieVerification);
                            }
                        }
                        ctx.sessionAttribute("user", userLog);


                    }else{
                        //crear user

                        User newUser = new User();
                        newUser.setUserName(userName);
                        newUser.setPassword(password);
                        userService.create(newUser);
                        if (ctx.formParam("signed") != null){
                            String tokenCookie = shop.tokenCreated(newUser);
                            ctx.cookie("userToken", tokenCookie);
                            Map<String, String> cookieFound = cookieVerificationService.findByCookieUsername(newUser.getUserName());
                            if (cookieFound.get("user") != null){
                                CookieVerification cookieVerification = new CookieVerification();
                                cookieVerification.setUsername(newUser.getUserName());
                                cookieVerification.setToken(tokenCookie);
                                cookieVerificationService.edit(cookieVerification);
                            }else {
                                CookieVerification cookieVerification = new CookieVerification();
                                cookieVerification.setUsername(newUser.getUserName());
                                cookieVerification.setToken(tokenCookie);
                                cookieVerificationService.create(cookieVerification);
                            }
                        }
                        ctx.sessionAttribute("user", newUser);
                    }
                    ctx.redirect("/product");

                });
                get("/logout", ctx -> {

                    if (ctx.cookie("userToken") != null){

                        cookieVerificationService.delete(cookieVerificationService.findByCookieTokenVeri(ctx.cookie("userToken")).getId());
                        ctx.removeCookie("userToken","/");
                    }
                    if (ctx.sessionAttribute("user") != null){
                        ctx.req.getSession().invalidate();
                    }
                    ctx.redirect("/product");
                });
            });
        });



    };
}
