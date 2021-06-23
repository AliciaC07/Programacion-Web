package org.practica.services;

import org.practica.models.Client;
import org.practica.models.Comments;
import org.practica.repository.Repository;

public class CommentService extends Repository<Comments> {

    private static CommentService commentService;

    public CommentService() {
        super(Comments.class);
    }
    public static CommentService getInstance(){
        if(commentService == null){
            commentService = new CommentService();
        }
        return commentService;
    }
}
