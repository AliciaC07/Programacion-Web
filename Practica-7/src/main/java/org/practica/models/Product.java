package org.practica.models;

import jdk.jfr.Name;
import lombok.Getter;
import lombok.Setter;
import org.practica.services.PictureService;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@NamedQueries({@NamedQuery(name = "Product.findUserById", query = "select u from Product u where u.id = :id and u.active = true")})
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private String name;
    @Column
    private Float price;
    @Column
    private Integer amount;
    @Column
    private Boolean active=true;
    @Column
    private String description;

    @OneToMany(mappedBy = "product")
    private List<ReceiptDetail> receiptDetails;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Picture> pictures = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER)
    private List<Comments> comments = new ArrayList<>();



    public Product(Integer id, String name, Float price, Integer amount, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.amount = amount;
        this.description = description;
    }


    public Product() {
    }
    public void PictureAdd(Picture picture){
        pictures.add(picture);
    }


}
