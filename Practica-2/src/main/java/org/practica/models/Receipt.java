package org.practica.models;


import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class Receipt {

    Integer id;
    LocalDate date;
    Client client;
    User salesman;
    ShoppingCart shoppingCart;

    public Receipt() {
    }

    public Receipt(Integer id, LocalDate date, Client client, User salesman, ShoppingCart shoppingCart) {
        this.id = id;
        this.date = date;
        this.client = client;
        this.salesman = salesman;
        this.shoppingCart = shoppingCart;
    }
}
