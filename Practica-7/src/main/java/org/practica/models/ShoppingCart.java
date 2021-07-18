package org.practica.models;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;

@Getter
@Setter
public class ShoppingCart {

    Integer id;
    ArrayList<Product> products;


    public ShoppingCart() {
    }

    public ShoppingCart(Integer id, ArrayList<Product> products) {
        this.id = id;
        this.products = products;
    }
    public Float getTotal(){
        Float total = 0f;
        for (Product aux: products) {
            total += aux.getPrice() * aux.getAmount();
        }
        return total;
    }

}
