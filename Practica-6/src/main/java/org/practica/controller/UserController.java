package org.practica.controller;

import io.javalin.Javalin;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.practica.models.Client;
import org.practica.models.CookieVerification;
import org.practica.models.User;
import org.practica.services.ClientService;
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
                    model.put("logged", false);
                    if (ctx.cookie("userToken") != null){
                        ctx.redirect("/product");
                    }else if (ctx.sessionAttribute("user") != null){
                        ctx.redirect("/product");
                    }
                    ctx.render("/public/login.html",model);
                });
                get("/register", ctx -> {
                    Map<String, Object> model = new HashMap<>();
                    model.put("title", "Register");
                    if (ctx.cookie("userToken") != null){
                        ctx.redirect("/product");
                    }else if (ctx.sessionAttribute("user") != null){
                        ctx.redirect("/product");
                    }
                    ctx.render("/public/register.html",model);
                });
                post("/register", ctx -> {
                    Map<String, Object> model = new HashMap<>();
                    String userName = ctx.formParam("userName");
                    String password = ctx.formParam("password");
                    String name = ctx.formParam("name");
                    String lastName = ctx.formParam("lastName");
                    User newUser = new User();
                    newUser.setUserName(userName);
                    StrongPasswordEncryptor spe = new StrongPasswordEncryptor();
                    newUser.setPassword(spe.encryptPassword(password));
                    newUser.setRol("User");
                    userService.create(newUser);
                    Client client = new Client();
                    client.setName(name);
                    client.setLastName(lastName);
                    client.setEmail(userName);
                    ClientService.getInstance().create(client);
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


                    }else {
                        ctx.redirect("/user");
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
