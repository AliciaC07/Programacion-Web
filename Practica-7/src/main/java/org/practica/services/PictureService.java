package org.practica.services;

import org.practica.models.Client;
import org.practica.models.Picture;
import org.practica.repository.Repository;

public class PictureService extends Repository<Picture> {
    private static PictureService pictureService;

    public PictureService() {
        super(Picture.class);
    }
    public static PictureService getInstance(){
        if(pictureService == null){
            pictureService = new PictureService();
        }
        return pictureService;
    }
}
