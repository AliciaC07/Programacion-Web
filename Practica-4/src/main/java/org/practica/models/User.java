package org.practica.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {

    Integer id;
    String userName;
    String password;

    public User() {
    }

    public User(Integer id, String userName, String password) {
        this.id = id;
        this.userName = userName;
        this.password = password;
    }


}
