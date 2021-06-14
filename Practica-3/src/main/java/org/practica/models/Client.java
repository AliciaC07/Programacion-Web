package org.practica.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Client {
    Integer id;
    String name;
    String lastName;
    String email;

    public Client() {
    }
}
