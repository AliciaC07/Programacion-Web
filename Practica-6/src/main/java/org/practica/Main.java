//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.practica;

import io.javalin.Javalin;
import io.javalin.core.util.RouteOverviewPlugin;
import io.javalin.plugin.rendering.JavalinRenderer;
import io.javalin.plugin.rendering.template.JavalinThymeleaf;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.practica.controller.ProductController;
import org.practica.controller.ReceiptController;
import org.practica.controller.UserController;
import org.practica.controller.WebSocketController;
import org.practica.models.Product;
import org.practica.models.User;
import org.practica.services.BootStrapService;
import org.practica.services.ProductService;
import org.practica.services.UserService;

public class Main {
    public Main() {
    }

    public static void main(String[] args) throws SQLException {
        Javalin app = Javalin.create((config) -> {
            config.addStaticFiles("/public");
            config.registerPlugin(new RouteOverviewPlugin("/rutas"));
            config.enableCorsForAllOrigins();
            JavalinRenderer.register(JavalinThymeleaf.INSTANCE, new String[]{".html"});
        }).start(7001);
        BootStrapService.startDb();
        ArrayList<Product> products = new ArrayList();
        products.add(new Product((Integer)null, "Milk", 10.0F, 3, "Product"));
        products.add(new Product((Integer)null, "Bread", 5.0F, 2, "Product"));
        products.add(new Product((Integer)null, "Soap", 2.0F, 3, "Product"));
        products.add(new Product((Integer)null, "Beer", 15.0F, 3, "Product"));
        products.add(new Product((Integer)null, "Paper", 15.0F, 0, "Product"));
        products.add(new Product((Integer)null, "Apple", 2.0F, 3, "Product"));
        products.add(new Product((Integer)null, "Pear", 15.0F, 3, "Product"));
        products.add(new Product((Integer)null, "Banana", 15.0F, 3, "Product"));
        products.add(new Product((Integer)null, "Lemon", 2.0F, 3, "Product"));
        products.add(new Product((Integer)null, "Juice", 2.0F, 3, "Product"));
        Iterator var3 = products.iterator();

        while(var3.hasNext()) {
            Product p = (Product)var3.next();
            ProductService.getInstance().create(p);
        }

        StrongPasswordEncryptor spe = new StrongPasswordEncryptor();
        UserService.getInstance().create(new User((Integer)null, "aliciac07", spe.encryptPassword("123"), "Admin"));
        app.get("/", (ctx) -> {
            ctx.redirect("product");
        });
        (new ProductController(app)).applyRoutes();
        (new UserController(app)).applyRoutes();
        (new ReceiptController(app)).applyRoutes();
        (new WebSocketController(app)).applyRoutes();
    }
}
