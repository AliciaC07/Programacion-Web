package org.practica.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
public class Picture implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String mimeType;
    @Lob
    private String pictureType;

    public Picture() {
    }

    public Picture(Long id, String name, String mimeType, String pictureType) {
        this.id = id;
        this.name = name;
        this.mimeType = mimeType;
        this.pictureType = pictureType;
    }
}
