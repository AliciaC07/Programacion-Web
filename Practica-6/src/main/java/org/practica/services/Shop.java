//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.practica.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.eclipse.jetty.websocket.api.Session;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.practica.config.JwtGen;
import org.practica.models.Client;
import org.practica.models.Product;
import org.practica.models.Receipt;
import org.practica.models.ReceiptDetail;
import org.practica.models.ShoppingCart;
import org.practica.models.User;

public class Shop {
    private static Shop shop = null;
    private ArrayList<Client> clients = new ArrayList();
    private ArrayList<Product> products = new ArrayList();
    private ArrayList<User> users = new ArrayList();
    private ArrayList<Receipt> receipts = new ArrayList();
    private ArrayList<ShoppingCart> shoppingCarts = new ArrayList();
    StrongPasswordEncryptor spe = new StrongPasswordEncryptor();
    ArrayList<Session> userConnected = new ArrayList();

    private Shop() {
        this.products.add(new Product(1, "Milk", 10.0F, 3, "Product"));
        this.products.add(new Product(2, "Bread", 5.0F, 2, "Product"));
        this.products.add(new Product(3, "Soap", 2.0F, 3, "Product"));
        this.products.add(new Product(4, "Beer", 15.0F, 3, "Product"));
        this.products.add(new Product(5, "Paper", 15.0F, 0, "Product"));
        this.users.add(new User(1, "aliciac07", "123", "Admin"));
    }

    public static Shop getInstance() {
        if (shop == null) {
            shop = new Shop();
        }

        return shop;
    }

    public void addUserConnected(Session session) {
        this.userConnected.add(session);
    }

    public void removeUserConnected(Session session) {
        this.userConnected.remove(session);
    }

    public ArrayList<Session> getUserConnected() {
        return this.userConnected;
    }

    public String tokenCreated(User user) {
        String jwtId = "PacoFish";
        String jwtIssuer = "JWT Gen";
        String jwtSubject = user.getUserName();
        int jwtTimeToLive = 620000;
        return JwtGen.createJWT(jwtId, jwtIssuer, jwtSubject, (long)jwtTimeToLive);
    }

    public Boolean tokenVerify(String userName, String jwt) {
        String jwtId = "PacoFish";
        String jwtIssuer = "JWT Gen";

        try {
            if (jwt != null) {
                Claims claims = JwtGen.decodeJWT(jwt);
                if (claims.getId().equals(jwtId) && claims.getIssuer().equals(jwtIssuer) && claims.getSubject().equals(userName)) {
                    return true;
                }

                return false;
            }
        } catch (JwtException var6) {
            System.out.println("Token is invalid");
        }

        return false;
    }

    public Product updateProduct(String name, Integer amount, Float price, Integer id) {
        Product productFound = this.FindProductById(id);
        if (productFound != null) {
            productFound.setAmount(amount);
            productFound.setPrice(price);
            productFound.setName(name);
            return productFound;
        } else {
            throw new NoSuchElementException("This product doesn't exists");
        }
    }

    public void deleteProduct(Integer id) {
        Product product = this.FindProductById(id);
        if (product != null) {
            this.products.remove(product);
        } else {
            throw new NoSuchElementException("This product doesn't exists");
        }
    }

    public List<ReceiptDetail> buildReceiptDetail(ShoppingCart shoppingCart, Receipt receipt) {
        List<ReceiptDetail> receiptDetails = new ArrayList();
        Iterator var4 = shoppingCart.getProducts().iterator();

        while(var4.hasNext()) {
            Product p = (Product)var4.next();
            ReceiptDetail receiptDetail = new ReceiptDetail();
            receiptDetail.setReceipt(receipt);
            receiptDetail.setProduct(p);
            receiptDetail.setPrice(p.getPrice());
            receiptDetail.setQuantity(p.getAmount());
            receiptDetails.add(receiptDetail);
        }

        return receiptDetails;
    }

    public ShoppingCart createShoppingCart(ShoppingCart shoppingCart) {
        if (this.shoppingCarts == null) {
            shoppingCart.setId(1);
        } else {
            shoppingCart.setId(this.shoppingCarts.size() + 1);
        }

        this.shoppingCarts.add(shoppingCart);
        return shoppingCart;
    }

    public void AddProducts(Product product) {
        if (this.products == null) {
            product.setId(1);
        } else {
            product.setId(this.products.size() + 1);
        }

        this.products.add(product);
    }

    public void AddClient(Client client) {
        this.clients.add(client);
    }

    public User authUser(String userName, String password) {
        User user = this.FindCUserByUsername(userName);
        if (user == null) {
            return null;
        } else {
            return user.getPassword().equalsIgnoreCase(password) ? user : null;
        }
    }

    public ArrayList<User> getallUser() {
        return this.users;
    }

    public User createUser(String userName, String password) {
        User newUser = new User();
        if (this.users == null) {
            newUser.setId(1);
            newUser.setUserName(userName);
            newUser.setPassword(password);
            this.users.add(newUser);
            return newUser;
        } else {
            newUser.setId(this.users.size() + 1);
            newUser.setUserName(userName);
            newUser.setPassword(password);
            return newUser;
        }
    }

    public ShoppingCart deleteFromCart(ShoppingCart shoppingCart, Integer idProduct) {
        ProductService productService = ProductService.getInstance();
        Product product = productService.findProductByActiveTrue(idProduct);
        Iterator var5 = shoppingCart.getProducts().iterator();

        Product aux;
        do {
            if (!var5.hasNext()) {
                return null;
            }

            aux = (Product)var5.next();
        } while(!aux.getId().equals(idProduct));

        product.setAmount(product.getAmount() + aux.getAmount());
        productService.edit(product);
        shoppingCart.getProducts().remove(aux);
        return shoppingCart;
    }

    public ShoppingCart removeAllFromCart(ShoppingCart shoppingCart) {
        for(int i = shoppingCart.getProducts().size() - 1; i >= 0; --i) {
            this.deleteFromCart(shoppingCart, ((Product)shoppingCart.getProducts().get(i)).getId());
        }

        return shoppingCart;
    }

    public ArrayList<Product> getAllProducts() {
        return this.products;
    }

    public ArrayList<Receipt> getAllReceipts() {
        return this.receipts;
    }

    public Product FindProductById(Integer id) {
        Iterator var2 = this.products.iterator();

        Product aux;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            aux = (Product)var2.next();
        } while(!aux.getId().equals(id));

        return aux;
    }

    public ShoppingCart findShoppingCartById(Integer id) {
        Iterator var2 = this.shoppingCarts.iterator();

        ShoppingCart cart;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            cart = (ShoppingCart)var2.next();
        } while(!cart.getId().equals(id));

        return cart;
    }

    public User FindCUserByUsername(String userName) {
        Iterator var2 = this.users.iterator();

        User aux;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            aux = (User)var2.next();
        } while(!aux.getUserName().equals(userName));

        return aux;
    }
}
