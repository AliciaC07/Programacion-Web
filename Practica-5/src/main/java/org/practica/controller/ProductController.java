package org.practica.controller;

import io.javalin.Javalin;
import org.practica.models.*;
import org.practica.services.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.get;

public class ProductController {
    private final Javalin app;
    private ProductService productService = ProductService.getInstance();
    private PictureService pictureService = PictureService.getInstance();
    private CookieVerificationService cookieVerificationService = CookieVerificationService.getInstance();
    private ClientService clientService = ClientService.getInstance();
    private UserService userService = UserService.getInstance();
    private ReceiptService receiptService = ReceiptService.getInstance();
    private CommentService commentService = CommentService.getInstance();

    private Shop shop = Shop.getInstance();

    public ProductController(Javalin app) {
        this.app = app;
    }

    public void applyRoutes() {
        app.routes(() -> {
            path("/product", () ->{
                get("/", ctx -> {
                    Map<String, Object> model = new HashMap<>();
                    model.put("title", "Home");
                    Integer currentPage = 1;
                    Integer pageIndex = 0;
                    if (ctx.queryParam("pag") != null){
                        pageIndex = ctx.queryParam("pag",Integer.class).get();
                    }else {
                        pageIndex = currentPage;
                    }
                    Integer pageSize = 4;
                    Integer total = (productService.findAllByActiveTrue().size()+(pageSize-1))/pageSize;
                    System.out.println(total);
                    ArrayList<Integer> pages = new ArrayList<>();
                    for (int i =0; i < total; i++){
                        pages.add(i+1);
                    }
                    if (ctx.queryParam("pag") != null){
                        double div = Math.ceil((pageIndex.doubleValue()-1)/pageSize.doubleValue());
                        currentPage = Double.valueOf(div).intValue()+1;
                        if (currentPage < ctx.queryParam("pag",Integer.class).get()){
                            currentPage = ctx.queryParam("pag",Integer.class).get();
                        }
                    }else {
                        currentPage = 1;
                    }
                    model.put("currentPage",currentPage);
                    model.put("pages", pages);
                    model.put("products", productService.findAllByActiveTruePagination(pageSize, currentPage));
                    model.put("totalPages", total);
                    List<Product> p = productService.findAllByActiveTruePagination(pageSize,currentPage);
                    System.out.println(p.size());
                    System.out.println(p.get(0).getName());
                    ShoppingCart shoppingCart = ctx.sessionAttribute("cart");
                    if (shoppingCart == null){
                        model.put("cartCount", "Cart(0)");
                    }else {
                        model.put("cartCount", "Cart("+shoppingCart.getProducts().size()+")");
                    }
                    if ( ctx.cookie("userToken") != null){

                        Map<String, String> cookie  = cookieVerificationService.findByCookieToken(ctx.cookie("userToken"));
                        if (cookie.get("user") == null){
                            model.put("isLogged", false);
                        }else {
                            if (shop.tokenVerify(cookie.get("user"), (cookie.get("token")))){
                                model.put("isLogged", true);
                            }else{
                                model.put("isLogged", false);
                            }
                        }

                    }else if (ctx.sessionAttribute("user") != null){
                        model.put("isLogged", true);
                    }else {
                        model.put("isLogged", false);
                    }

                    ctx.render("/public/home.html", model);

                });
                get("/create", ctx -> {
                    Map<String, Object> model = new HashMap<>();
                    model.put("title", "Create Product");
                    model.put("action_form", "/product/create");
                    model.put("action", "Create Product");
                    model.put("buying", false);
                    model.put("edit", false);
                    if (ctx.cookie("userToken") != null){
                        model.put("isLogged", true);
                    }else if (ctx.sessionAttribute("user") != null){
                        model.put("isLogged", true);
                    }else {
                        model.put("isLogged", false);
                        ctx.redirect("/user");
                    }


                    ctx.render("/public/NewProduct.html", model);
                });
                post("/create", ctx -> {
                    if ( ctx.cookie("userToken") == null ){
                        ctx.redirect("/product");
                    }
                    String name = ctx.formParam("name");
                    float price = Float.parseFloat(Objects.requireNonNull(ctx.formParam("price")));
                    int amount = Integer.parseInt(Objects.requireNonNull(ctx.formParam("amount")));
                    String description = ctx.formParam("description", String.class).get();
                    if (name == null || price == 0 || amount == 0){
                        ctx.redirect("/public/product.html");
                        return;
                    }
                    Product product = new Product(null,name,price,amount, description);
                    var file = ctx.uploadedFiles("picture");
                    ctx.uploadedFiles("picture").forEach(uploadedFile -> {
                        try {
                            byte[] bytes = uploadedFile.getContent().readAllBytes();
                            String encodedString = Base64.getEncoder().encodeToString(bytes);
                            Picture picture = new Picture(null, uploadedFile.getFilename(), uploadedFile.getContentType(), encodedString);
                            if (!uploadedFile.getFilename().isBlank()){
                                pictureService.create(picture);
                                product.PictureAdd(picture);
                            }

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
                    if (product == null){
                        ctx.redirect("/product");
                    }
                    model.put("product", product);
                    model.put("title", "Edit Product");
                    model.put("action", "Edit Product");
                    model.put("action_form", "/product/edit/"+product.getId());
                    model.put("buying", false);
                    model.put("edit", true);
                    if ( ctx.cookie("userToken") != null ){

                        Map<String, String> cookie  = cookieVerificationService.findByCookieToken(ctx.cookie("userToken"));

                        if (shop.tokenVerify(cookie.get("user"), (cookie.get("token")))){
                            model.put("isLogged", true);
                        }else{
                            model.put("isLogged", false);
                        }

                    }else if (ctx.sessionAttribute("user") != null){
                        model.put("isLogged", true);
                    }else {
                        model.put("isLogged", false);
                        ctx.redirect("/user");
                    }

                    ctx.render("/public/product.html",model);


                });
                post("/edit/:id", ctx -> {
                    String name = ctx.formParam("name");
                    if (ctx.cookie("userToken") == null ){
                        ctx.redirect("/user");
                    }
                    Integer amount = Integer.parseInt(Objects.requireNonNull(ctx.formParam("amount")));
                    Float price = Float.parseFloat(Objects.requireNonNull(ctx.formParam("price")));
                    Boolean active = ctx.formParam("active", Boolean.class).get();
                    String description = ctx.formParam("description", String.class).get();
                    Product product = productService.findProductByActiveTrue(ctx.pathParam("id", Integer.class).get());
                    product.setDescription(description);
                    product.setPrice(price);
                    product.setActive(active);
                    product.setAmount(amount);

                    System.out.println(ctx.uploadedFiles("picture").size());
                        ctx.uploadedFiles("picture").forEach(uploadedFile -> {
                            try {
                                byte[] bytes = uploadedFile.getContent().readAllBytes();
                                String encodedString = Base64.getEncoder().encodeToString(bytes);
                                Picture picture = new Picture(null, uploadedFile.getFilename(), uploadedFile.getContentType(), encodedString);
                                if (!uploadedFile.getFilename().isBlank()){
                                    pictureService.create(picture);
                                    product.getPictures().add(picture);
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        });

                    productService.edit(product);
                    ctx.redirect("/product");


                });
                get("/delete/:id", ctx -> {

                    Product product  = productService.findProductByActiveTrue(ctx.pathParam("id", Integer.class).get());
                    product.setActive(false);
                    productService.edit(product);

                    ctx.redirect("/product");
                });
                get("/buy/:id",ctx -> {
                    Product product = productService.findProductByActiveTrue(ctx.pathParam("id", Integer.class).get());
                    Map<String, Object> model = new HashMap<>();
                    model.put("title", "Buy Product");
                    model.put("action", "Buy Product");
                    model.put("buying", true);
                    model.put("edit", false);
                    model.put("action_form", "/product/buy/"+product.getId());
                    model.put("product", product);
                    ShoppingCart shoppingCart = ctx.sessionAttribute("cart");
                    if (shoppingCart == null){
                        model.put("cartCount", "Cart(0)");
                    }else {
                        model.put("cartCount", "Cart("+shoppingCart.getProducts().size()+")");
                    }
                    if ( ctx.cookie("userToken") != null ){
                        Map<String, String> cookie  = cookieVerificationService.findByCookieToken(ctx.cookie("userToken"));
                        if (shop.tokenVerify(cookie.get("user"), (cookie.get("token")))){
                            model.put("isLogged", true);
                            ctx.redirect("/product");
                        }else{
                            model.put("isLogged", false);

                        }

                    }else if (ctx.sessionAttribute("user") != null){
                        model.put("isLogged", true);
                        ctx.redirect("/product");
                    }else {
                        model.put("isLogged", false);
                    }
                    ctx.render("/public/product.html", model);
                });
                get("/view/:id", ctx -> {
                    Product product = productService.findProductByActiveTrue(ctx.pathParam("id", Integer.class).get());
                    Map<String, Object> model = new HashMap<>();
                    model.put("title", "Buy Product");
                    model.put("action", "Buy Product");
                    model.put("buying", true);
                    model.put("edit", false);
                    model.put("action_form", "/product/comment/"+product.getId());
                    model.put("product", product);
                    ArrayList<Integer> count = new ArrayList<>();
                    for (int i = 0; i < product.getPictures().size(); i++){
                        count.add(i);
                    }

                    model.put("pictureSize", count);
                    ShoppingCart shoppingCart = ctx.sessionAttribute("cart");
                    if (shoppingCart == null){
                        model.put("cartCount", "Cart(0)");
                    }else {
                        model.put("cartCount", "Cart("+shoppingCart.getProducts().size()+")");
                    }
                    if ( ctx.cookie("userToken") != null ){
                        Map<String, String> cookie  = cookieVerificationService.findByCookieToken(ctx.cookie("userToken"));
                        if (shop.tokenVerify(cookie.get("user"), (cookie.get("token")))){
                            model.put("isLogged", true);

                        }else{
                            model.put("isLogged", false);

                        }

                    }else if (ctx.sessionAttribute("user") != null){
                        model.put("isLogged", true);

                    }else {
                        model.put("isLogged", false);
                    }
                    ctx.render("/public/viewProduct.html", model);

                });
                post("/comment/:id", ctx -> {
                    Product product = productService.findProductByActiveTrue(ctx.pathParam("id", Integer.class).get());
                    String name = ctx.formParam("nameClient");
                    String email =  ctx.formParam("emailClient");
                    String comment_given = ctx.formParam("comment");
                    Client client = clientService.findClientByEmail(email);
                    Comments comments = new Comments();
                    comments.setComment(comment_given);
                    comments.setDate(LocalDate.now());
                    comments.setTime(LocalTime.now());

                    if (client == null){
                        comments.setEmail(name);
                    }else {
                        comments.setEmail(email);
                    }
                    product.getComments().add(comments);
                    commentService.create(comments);
                    productService.edit(product);
                    ctx.redirect("/product/view/"+ctx.pathParam("id", Integer.class).get());


                });
                get("/comment/remove/:id/:idp", ctx -> {
                    Integer idComment = ctx.pathParam("id", Integer.class).get();
                    Integer idProduct = ctx.pathParam("idp", Integer.class).get();
                    Comments comments = commentService.find(idComment);
                    Product product = productService.findProductByActiveTrue(idProduct);

                    for (int i = product.getComments().size() - 1; i >= 0; i--) {
                        if (product.getComments().get(i).getId().equals(comments.getId())){
                            product.getComments().remove(product.getComments().get(i));
                        }
                    }
                    productService.edit(product);
                    commentService.delete(idComment);

                    ctx.redirect("/product/view/"+idProduct);
                });
                post("/buy/:id", ctx -> {
                    ArrayList<Product> productsCart = new ArrayList<>();
                    ShoppingCart shoppingCart = new ShoppingCart();
                    Product product = productService.findProductByActiveTrue(ctx.pathParam("id", Integer.class).get());
                    Product productselected = new Product();
                    productselected.setId(product.getId());
                    productselected.setName(product.getName());
                    productselected.setPrice(product.getPrice());
                    productselected.setActive(product.getActive());
                    Integer amountSelected = ctx.formParam("quantity", Integer.class).get();
                    productselected.setAmount(amountSelected);
                    product.setAmount(product.getAmount()- amountSelected);

                    productService.edit(product);

                    if (ctx.sessionAttribute("cart") == null){
                        shop.createShoppingCart(shoppingCart);
                        productsCart.add(productselected);
                        shoppingCart.setProducts(productsCart);
                        ctx.sessionAttribute("cart", shoppingCart);
//

                    }else {
                        ShoppingCart shoppingCartFound = ctx.sessionAttribute("cart");
                        shoppingCart = shop.findShoppingCartById(shoppingCartFound.getId());

                        for (Product aux :  shoppingCart.getProducts()) {
                            if (aux.getId().equals(productselected.getId())){
                                aux.setAmount(aux.getAmount() + productselected.getAmount());
                                ctx.sessionAttribute("cart", shoppingCart);
                                ctx.redirect("/product");
                                return;
                            }
                        }
                        shoppingCart.getProducts().add(productselected);
                        ctx.sessionAttribute("cart", shoppingCart);



                    }
                    ctx.redirect("/product");

                });
                get("/shopping-cart", ctx -> {
                    Map<String, Object> model = new HashMap<>();
                    model.put("title", "ShoppingCart");
                    model.put("cartList", "Car List");

                    if ( ctx.cookie("userToken") != null){

                        Map<String, String> cookie  = cookieVerificationService.findByCookieToken(ctx.cookie("userToken"));
                        if (shop.tokenVerify(cookie.get("user"), (cookie.get("token")))){
                            model.put("isLogged", true);
                            ctx.redirect("/product");
                        }else{
                            model.put("isLogged", false);
                        }

                    }else if (ctx.sessionAttribute("user") != null){
                        model.put("isLogged", true);
                        ctx.redirect("/product");
                    }else {
                        model.put("isLogged", false);
                    }
                    if (ctx.sessionAttribute("cart") != null){
                        ShoppingCart shoppingCart = ctx.sessionAttribute("cart");
                        ArrayList<Product> products = shoppingCart.getProducts();

                        if (products.size() == 0){
                            model.put("emptyList", true);
                        }

                        model.put("cartCount", "Cart("+shoppingCart.getProducts().size()+")");

                        model.put("cartProducts", products);
                        model.put("total", shoppingCart.getTotal());
                    }else {
                        ArrayList<Product> products = new ArrayList<>();
                        model.put("cartProducts", products);
                        model.put("emptyList", true);
                        model.put("total", "00");
                        model.put("cartCount", "Cart(0)");
                    }

                    ctx.render("/public/shoppingCart.html", model);

                });
                get("/buy/remove/:id", ctx -> {
                    ShoppingCart shoppingCart = ctx.sessionAttribute("cart");
                    Integer idProduct = ctx.pathParam("id", Integer.class).get();
                    shoppingCart = shop.deleteFromCart(shoppingCart, idProduct);

                    ctx.sessionAttribute("cart", shoppingCart);

                    ctx.redirect("/product/shopping-cart");

                });
                get("/remove-all", ctx -> {
                    ShoppingCart shoppingCart = ctx.sessionAttribute("cart");
                    System.out.println("entro");
                    shoppingCart = shop.removeAllFromCart(shoppingCart);
                    ctx.sessionAttribute("cart", shoppingCart);
                    ctx.redirect("/product/shopping-cart");

                });
                post("/shopping-cart/buy", ctx -> {
                    String name = ctx.formParam("nameClient");
                    String lastName = ctx.formParam("lastNameClient");
                    String email =  ctx.formParam("emailClient");
                    Client client = clientService.findClientByEmail(email);
                    ShoppingCart shoppingCart = ctx.sessionAttribute("cart");
                    Receipt receipt = new Receipt();
                    Map<String, String> cookie  = cookieVerificationService.findByCookieToken(ctx.cookie("userToken"));
                    if (shop.tokenVerify(cookie.get("user"), (cookie.get("token")))){
                        ctx.redirect("/product");
                    }
                    if (client == null){
                        Client newClient = new Client();
                        newClient.setLastName(lastName);
                        newClient.setName(name);
                        newClient.setEmail(email);
                        clientService.create(newClient);
                        receipt.setClient(newClient);
                        List<ReceiptDetail> receiptDetails = shop.buildReceiptDetail(shoppingCart, receipt);
                        receipt.setReceiptDetails(receiptDetails);
                        User user = userService.findByUserName("aliciac07");
                        receipt.setSalesman(user);
                        receipt.setDate(LocalDate.now());
                        receipt.setTotal(shoppingCart.getTotal());
                        receiptService.create(receipt);
                        ctx.sessionAttribute("cart", null);
                        ctx.redirect("/product");
                        System.out.println(shoppingCart.getTotal());

                    }else{
                        receipt.setClient(client);
                        List<ReceiptDetail> receiptDetails = shop.buildReceiptDetail(shoppingCart, receipt);
                        receipt.setReceiptDetails(receiptDetails);
                        User user = userService.findByUserName("aliciac07");
                        receipt.setSalesman(user);
                        receipt.setDate(LocalDate.now());
                        receipt.setTotal(shoppingCart.getTotal());
                        receiptService.create(receipt);
                        ctx.sessionAttribute("cart", null);
                        System.out.println(shoppingCart.getTotal());
                        ctx.redirect("/product");

                    }

                });


            });
        });
    }
}
