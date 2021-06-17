package org.practica.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class Product {

    @Id
    private Integer id;
    @Column
    private String name;
    @Column
    private Float price;
    @Column
    private Integer amount;
    @Column
    private Boolean active;
    @ManyToOne
    @JoinColumn(name = "receipt_id")
    private Receipt receipt;


    public Product(Integer id, String name, Float price, Integer amount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.amount = amount;
    }

    public Product() {
    }


}
