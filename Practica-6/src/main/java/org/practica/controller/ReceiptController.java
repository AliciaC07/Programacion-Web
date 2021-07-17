package org.practica.controller;

import io.javalin.Javalin;
import org.practica.models.User;
import org.practica.services.CookieVerificationService;
import org.practica.services.ReceiptService;
import org.practica.services.Shop;
import org.practica.services.UserService;

import java.util.HashMap;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.path;

public class ReceiptController {
    private final Javalin app;
    private ReceiptService receiptService = ReceiptService.getInstance();
    private CookieVerificationService cookieVerificationService = CookieVerificationService.getInstance();
    private UserService userService = UserService.getInstance();
    private Shop shop = Shop.getInstance();
    public ReceiptController(Javalin app) {
        this.app = app;
    }

    public void applyRoutes(){
        app.routes(() ->{
            path("/receipt", ()->{
                get("/", ctx -> {
                    Map<String, Object> model = new HashMap<>();
                    model.put("title", "Sales");
                    model.put("class", "nav-link active");
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
                        }

                    }else {
                        ctx.redirect("/user");
                        return;
                    }
                    model.put("receipts", receiptService.findAllReceipt());

                    ctx.render("/public/sales.html", model);


                });
            });
        });
    }
}
