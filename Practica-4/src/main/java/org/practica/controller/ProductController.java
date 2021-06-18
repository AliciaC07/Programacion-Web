package org.practica.controller;

import io.javalin.Javalin;
import org.practica.models.Picture;
import org.practica.models.Product;
import org.practica.models.ShoppingCart;
import org.practica.services.PictureService;
import org.practica.services.ProductService;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.*;

public class ProductController {
    private final Javalin app;
    private ProductService productService = ProductService.getInstance();
    private PictureService pictureService = PictureService.getInstance();

    public ProductController(Javalin app) {
        this.app = app;
    }

    public void applyRoutes() {
        app.routes(() -> {
            path("/product", () ->{
                get("/", ctx -> {
                    Map<String, Object> model = new HashMap<>();
                    model.put("title", "Home");
                    model.put("products", productService.findAllByActiveTrue());
                    ShoppingCart shoppingCart = ctx.sessionAttribute("cart");
                    if (shoppingCart == null){
                        model.put("cartCount", "Cart(0)");
                    }else {
                        model.put("cartCount", "Cart("+shoppingCart.getProducts().size()+")");
                    }
//                    if ( ctx.cookie("userToken") != null){
//
//                        Map<String, String> cookie  = userService.findCookie(ctx.cookie("userToken"));
//
//                        if (shop.tokenVerify(cookie.get("user"), (cookie.get("token")))){
//                            model.put("isLogged", true);
//                        }else{
//                            model.put("isLogged", false);
//                        }
//
//                    }else if (ctx.sessionAttribute("user") != null){
//                        model.put("isLogged", true);
//                    }else {
//                        model.put("isLogged", false);
//                    }
                    model.put("isLogged", true);
                    ctx.render("/public/home.html", model);

                });
                get("/create", ctx -> {
                    Map<String, Object> model = new HashMap<>();
                    model.put("title", "Create Product");
                    model.put("action_form", "/product/create");
                    model.put("action", "Create Product");
                    model.put("buying", false);
                    model.put("edit", false);
//                    if (ctx.cookie("userToken") != null){
//                        model.put("isLogged", true);
//                    }else if (ctx.sessionAttribute("user") != null){
//                        model.put("isLogged", true);
//                    }else {
//                        model.put("isLogged", false);
//                        ctx.redirect("/user");
//                    }
                    model.put("isLogged", true);

                    ctx.render("/public/product.html", model);
                });
                post("/create", ctx -> {
//                    if ( ctx.cookie("userToken") == null ){
//                        ctx.redirect("/product");
//                    }
                    String name = ctx.formParam("name");
                    float price = Float.parseFloat(Objects.requireNonNull(ctx.formParam("price")));
                    int amount = Integer.parseInt(Objects.requireNonNull(ctx.formParam("amount")));
                    String description = ctx.formParam("description", String.class).get();
                    if (name == null || price == 0 || amount == 0){
                        ctx.redirect("/public/product.html");
                        return;
                    }
                    Product product = new Product(null,name,price,amount);
                    product.setDescription(description);
                    var file = ctx.uploadedFiles("picture");
                    ctx.uploadedFiles("picture").forEach(uploadedFile -> {
                        try {
                            byte[] bytes = uploadedFile.getContent().readAllBytes();
                            String encodedString = Base64.getEncoder().encodeToString(bytes);
                            Picture picture = new Picture(null, uploadedFile.getFilename(), uploadedFile.getContentType(), encodedString);
                            pictureService.create(picture);
                            product.PictureAdd(picture);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    });
                    productService.create(product);

                    System.out.println(product);
                    ctx.redirect("/product");

                });
                get("/edit/:id", ctx -> {
                    Product product = productService.findProductByActiveTrue(ctx.pathParam("id", Integer.class).get());

                    Map<String, Object> model = new HashMap<>();
//                    if (product == null){
//                        ctx.redirect("/product");
//                    }
                    model.put("product", product);
                    model.put("title", "Edit Product");
                    model.put("action", "Edit Product");
                    model.put("action_form", "/product/edit/"+product.getId());
                    model.put("buying", false);
                    model.put("edit", true);
//                    if ( ctx.cookie("userToken") != null ){
//
//                        Map<String, String> cookie  = userService.findCookie(ctx.cookie("userToken"));
//
//                        if (shop.tokenVerify(cookie.get("user"), (cookie.get("token")))){
//                            model.put("isLogged", true);
//                        }else{
//                            model.put("isLogged", false);
//                        }
//
//                    }else if (ctx.sessionAttribute("user") != null){
//                        model.put("isLogged", true);
//                    }else {
//                        model.put("isLogged", false);
//                        ctx.redirect("/user");
//                    }
                    model.put("isLogged", true);

                    ctx.render("/public/product.html",model);


                });
                post("/edit/:id", ctx -> {
                    String name = ctx.formParam("name");
//                    if (ctx.cookie("userToken") == null ){
//                        ctx.redirect("/user");
//                    }
                    Integer amount = Integer.parseInt(Objects.requireNonNull(ctx.formParam("amount")));
                    Float price = Float.parseFloat(Objects.requireNonNull(ctx.formParam("price")));
                    Boolean active = ctx.formParam("active", Boolean.class).get();
                    String description = ctx.formParam("description", String.class).get();
                    Product product = new Product(ctx.pathParam("id", Integer.class).get(),name,price,amount);
                    product.setDescription(description);
                    product.setActive(active);
                    productService.edit(product);
                    ctx.redirect("/product");


                });

            });
        });
    }
}
