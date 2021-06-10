package org.practica.models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class Client {

    String name;
    String lastName;
    String email;
    ArrayList<Receipt> receipts;

    public Client() {
    }
}
