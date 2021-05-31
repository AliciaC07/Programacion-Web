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

}
