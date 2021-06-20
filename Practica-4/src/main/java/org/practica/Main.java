package org.practica;

import io.javalin.Javalin;
import io.javalin.core.util.RouteOverviewPlugin;
import io.javalin.plugin.rendering.JavalinRenderer;
import io.javalin.plugin.rendering.template.JavalinThymeleaf;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.practica.controller.ProductController;
import org.practica.controller.ReceiptController;
import org.practica.controller.UserController;
import org.practica.models.Product;
import org.practica.models.User;
import org.practica.services.*;

import java.sql.SQLException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws SQLException {
        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("/public");
            config.registerPlugin(new RouteOverviewPlugin("/rutas"));
            config.enableCorsForAllOrigins();
            JavalinRenderer.register(JavalinThymeleaf.INSTANCE, ".html");
        }).start(7001);
        BootStrapService.startDb();
        ArrayList<Product> products = new ArrayList<>();
        products.add(new Product(null, "Milk", 10.00f, 3, "Product"));
        products.add(new Product(null, "Bread", 5.00f, 2,"Product"));
        products.add(new Product(null, "Soap", 2.00f, 3,"Product"));
        products.add(new Product(null, "Beer", 15.00f, 3,"Product"));
        products.add(new Product(null, "Paper", 15.00f, 0,"Product"));
        for (Product p: products) {
            ProductService.getInstance().create(p);
        }
        StrongPasswordEncryptor spe = new StrongPasswordEncryptor();
        UserService.getInstance().create(new User(null, "aliciac07", spe.encryptPassword("123")));






        app.get("/",ctx -> {
            ctx.redirect("product");
        });
        //app.get("/", ctx -> ctx.result("Hola Mundo en Javalin :-D"));

        new ProductController(app).applyRoutes();
        new UserController(app).applyRoutes();
        new ReceiptController(app).applyRoutes();


    }
}
