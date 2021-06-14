package org.practica.controller;

import io.javalin.Javalin;
import org.practica.models.Receipt;
import org.practica.services.ReceiptService;
import org.practica.services.Shop;

import java.util.HashMap;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class SalesController {
    private final Javalin app;
    private Shop shop = Shop.getInstance();
    private ReceiptService receiptService = new ReceiptService();

    public SalesController(Javalin app) {
        this.app = app;
    }
    public void applyRoutes(){
        app.routes(() -> {
            path("/sales", () -> {
                get("/", ctx -> {
                    Map<String, Object> model = new HashMap<>();
                    model.put("title", "Sales");
                    model.put("class", "nav-link active");
                    if ( ctx.cookie("userToken") != null ){
                        model.put("isLogged", true);
                    }else {
                        model.put("isLogged", false);
                        ctx.redirect("/user");
                    }
                    model.put("receipts", receiptService.findAllReceipts());

                    ctx.render("/public/sales.html", model);


                });
            });
        });
    }
}
