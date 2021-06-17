package org.practica.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class Client {
    @Id
    private Integer id;
    @Column
    private String name;
    @Column
    private String lastName;
    @Column
    private String email;

    public Client() {
    }
}
