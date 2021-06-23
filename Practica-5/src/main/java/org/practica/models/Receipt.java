package org.practica.models;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
public class Receipt implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private LocalDate date;
    @ManyToOne
    @JoinColumn(name = "email_client")
    private Client client;
    @ManyToOne
    @JoinColumn(name = "salesman_id")
    private User salesman;

    @Column
    private Float total;

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL)
    private List<ReceiptDetail> receiptDetails;

    public Receipt() {
    }

    public Receipt(Integer id, LocalDate date, Client client, User salesman,  Float total) {
        this.id = id;
        this.date = date;
        this.client = client;
        this.salesman = salesman;

        this.total = total;
    }
}
