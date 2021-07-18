package org.practica.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "product_receipt")
public class ReceiptDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recceipt_detail_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @ManyToOne
    @JoinColumn(name = "receipt_id")
    private Receipt receipt;
    @Column
    private Integer quantity;
    @Column
    private Float price;

}
