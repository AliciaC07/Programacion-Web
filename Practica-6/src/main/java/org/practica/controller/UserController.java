package org.practica.controller;

import com.google.gson.Gson;
import io.javalin.Javalin;
import org.h2.engine.Mode;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.practica.models.Client;
import org.practica.models.CookieVerification;
import org.practica.models.User;
import org.practica.services.*;

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
                get("/dashboard", ctx -> {
                    Map<String, Object> model = new HashMap<>();
                    if ( ctx.cookie("userToken") != null){
                        Map<String, String> cookie  = cookieVerificationService.findByCookieToken(ctx.cookie("userToken"));
                        User user = userService.findByUserName(cookie.get("user"));
                        if (cookie.get("user") == null){
                            ctx.redirect("/user");
                            return;
                        }else {
                            if (shop.tokenVerify(cookie.get("user"), (cookie.get("token")))){
                                if (user.getRol().equalsIgnoreCase("Admin")){
                                    model.put("isLogged", true);
                                    model.put("logged", true);
                                }else if (user.getRol().equalsIgnoreCase("User")){
                                    model.put("isLogged", false);
                                    model.put("logged", true);
                                    ctx.redirect("/user");
                                    return;
                                }
                            }else{
                                ctx.redirect("/user");
                                return;
                            }
                        }

                    }else if (ctx.sessionAttribute("user") != null){
                        User user = ctx.sessionAttribute("user");
                        if (user.getRol().equalsIgnoreCase("Admin")){
                            model.put("isLogged", true);
                            model.put("logged", true);
                        }else if (user.getRol().equalsIgnoreCase("User")){
                            model.put("isLogged", false);
                            model.put("logged", true);
                            ctx.redirect("/user");
                            return;
                        }

                    }else {
                        ctx.redirect("/user");
                        return;
                    }
                    model.put("title", "Dashboard");
                    model.put("totalSales", ReceiptService.getInstance().getTotalSales());
                    model.put("graph", new Gson().toJson(ReceiptService.getInstance().getQuantitySold()));
                    ctx.render("public/dashboard.html", model);
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
