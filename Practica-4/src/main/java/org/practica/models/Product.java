package org.practica.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Product {

    Integer id;
    String name;
    Float price;
    Integer amount;
    Boolean active;

    public Product(Integer id, String name, Float price, Integer amount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.amount = amount;
    }

    public Product() {
    }


}
