package org.practica.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class CookieVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cookie_ve_id")
    private Integer id;
    @Column
    private String username;
    @Column
    private String token;
}
