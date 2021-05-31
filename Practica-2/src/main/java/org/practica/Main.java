package org.practica;

import io.javalin.Javalin;
import io.javalin.core.util.RouteOverviewPlugin;
import io.javalin.plugin.rendering.JavalinRenderer;
import io.javalin.plugin.rendering.template.JavalinThymeleaf;
import org.practica.controller.ProductController;
import org.practica.controller.UserController;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("/public");
            config.registerPlugin(new RouteOverviewPlugin("/rutas"));
            config.enableCorsForAllOrigins();
            JavalinRenderer.register(JavalinThymeleaf.INSTANCE, ".html");
        }).start(7001);

        app.get("/",ctx -> {
            ctx.redirect("product");
        });
        //app.get("/", ctx -> ctx.result("Hola Mundo en Javalin :-D"));

        new ProductController(app).applyRoutes();
        new UserController(app).applyRoutes();


    }
}
