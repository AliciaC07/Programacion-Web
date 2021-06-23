package org.practica.controller;

import io.javalin.Javalin;
import org.practica.services.ReceiptService;

import java.util.HashMap;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.path;

public class ReceiptController {
    private final Javalin app;
    private ReceiptService receiptService = ReceiptService.getInstance();

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
                    if ( ctx.cookie("userToken") != null ){
                        model.put("isLogged", true);
                    }else if (ctx.sessionAttribute("user") != null){
                        model.put("isLogged", true);
                    }else {
                        model.put("isLogged", false);
                        ctx.redirect("/user");
                    }
                    model.put("receipts", receiptService.findAllReceipt());

                    ctx.render("/public/sales.html", model);


                });
            });
        });
    }
}
