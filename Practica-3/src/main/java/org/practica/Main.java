package org.practica;

import io.javalin.Javalin;
import io.javalin.core.util.RouteOverviewPlugin;
import io.javalin.plugin.rendering.JavalinRenderer;
import io.javalin.plugin.rendering.template.JavalinThymeleaf;
import org.practica.controller.ProductController;
import org.practica.controller.SalesController;
import org.practica.controller.UserController;
import org.practica.models.Product;
import org.practica.services.BootStrapService;
import org.practica.services.DataBaseService;
import org.practica.services.ProductService;
import org.practica.services.Shop;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("/public");
            config.registerPlugin(new RouteOverviewPlugin("/rutas"));
            config.enableCorsForAllOrigins();
            JavalinRenderer.register(JavalinThymeleaf.INSTANCE, ".html");
        }).start(7001);
        BootStrapService.startDb();
        DataBaseService.getInstance().testConexion();
        BootStrapService.createTables();
        ProductService productService = new ProductService();
        for (Product pr: Shop.getInstance().getAllProducts()) {
            if (productService.findProductById(pr.getId()) == null){
                productService.createProduct(pr);
            }
        }
        for (Product p: productService.findAllProducts()) {
            System.out.println(p.getName());
        }


        app.get("/",ctx -> {
            ctx.redirect("product");
        });
        //app.get("/", ctx -> ctx.result("Hola Mundo en Javalin :-D"));

        new ProductController(app).applyRoutes();
        new UserController(app).applyRoutes();
        new SalesController(app).applyRoutes();


    }
}
