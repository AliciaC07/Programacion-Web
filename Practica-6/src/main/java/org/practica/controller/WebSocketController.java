package org.practica.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.javalin.Javalin;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.jetty.util.ajax.JSON;
import org.eclipse.jetty.websocket.api.Session;
import org.practica.models.Client;
import org.practica.models.Comments;
import org.practica.models.Product;
import org.practica.services.ClientService;
import org.practica.services.CommentService;
import org.practica.services.ProductService;
import org.practica.services.Shop;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class WebSocketController {
    private final Javalin app;
    private ArrayList<Session> us = new ArrayList<>();
    private ArrayList<Session> usA = new ArrayList<>();
    private Shop shop = Shop.getInstance();

    public WebSocketController(Javalin app) {
        this.app = app;
    }
    public void applyRoutes(){
        app.ws("/view-delete", ws -> {
            ws.onConnect(ctx -> {
                System.out.println("Conexión Iniciada - "+ctx.getSessionId());
                us.add(ctx.session);
            });

            ws.onMessage(ctx -> {
                //Puedo leer los header, parametros entre otros.
                ctx.headerMap();
                ctx.pathParamMap();
                ctx.queryParamMap();
                //
                System.out.println("Mensaje Recibido de "+ctx.getSessionId()+" ====== ");
                System.out.println("Mensaje: "+ctx.message());
                System.out.println("================================");
                //
                commentRemove(Integer.parseInt(ctx.message()));

            });
            ws.onClose(ctx -> {
                System.out.println("Conexión Iniciada - "+ctx.getSessionId());
                us.remove(ctx.session);
            });


            ws.onError(ctx -> {
                System.out.println("Ocurrió un error en el WS");
            });

        });
        app.ws("/users-conected", ws -> {
//guardar el usuario

            ws.onConnect(ctx -> {
                System.out.println("Conexión Iniciada - "+ctx.getSessionId());
                shop.addUserConnected(ctx.session);
                sendInfoClientDisconnect();
            });


            ws.onClose(ctx -> {
                System.out.println("Conexión Cerrada - "+ctx.getSessionId());
                shop.removeUserConnected(ctx.session);
                sendInfoClientDisconnect();
            });

            ws.onError(ctx -> {
                System.out.println("Ocurrió un error en el WS");
            });
        });
        app.ws("/add-comment", ws ->{
            ws.onConnect(ctx -> {
                System.out.println("Conexión Iniciada - "+ctx.getSessionId());
                usA.add(ctx.session);
            });

            ws.onMessage(ctx -> {
                //Puedo leer los header, parametros entre otros.
                ctx.headerMap();
                ctx.pathParamMap();
                ctx.queryParamMap();
                //
                System.out.println("Mensaje Recibido de "+ctx.getSessionId()+" ====== ");
                System.out.println("Mensaje: "+ctx.message());
                System.out.println("================================");
                //
                Gson gson = new Gson();
                commentParse commentParse = gson.fromJson(ctx.message(),commentParse.class);
                commentAdd(commentParse);




            });
            ws.onClose(ctx -> {
                System.out.println("Conexión Iniciada - "+ctx.getSessionId());
                usA.remove(ctx.session);
            });


            ws.onError(ctx -> {
                System.out.println("Ocurrió un error en el WS");
            });

        });

    }
    public void commentAdd(commentParse commentParse){
        Product product = ProductService.getInstance().findProductByActiveTrue(commentParse.getProduct());

        Client client = ClientService.getInstance().findClientByEmail(commentParse.getEmail());
        Comments comments = new Comments();
        comments.setComment(commentParse.getCommentText());
        comments.setDate(LocalDate.now());
        comments.setTime(LocalTime.now());

        if (client == null){
            comments.setEmail(commentParse.getClientName());
        }else {
            comments.setEmail(commentParse.getEmail());
        }
        product.getComments().add(comments);
        CommentService.getInstance().create(comments);
        ProductService.getInstance().edit(product);
        for(Session sessionConnected : usA){
            try {
                sessionConnected.getRemote().sendString(new Gson().toJson(comments));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
    @Getter
    @Setter
    public static class commentParse{
        private String email;
        private String clientName;
        private String commentText;
        private Integer product;
    }

    public void commentRemove(Integer id){

        Product product = ProductService.getInstance().findByCommentId(id);
        Comments comments = CommentService.getInstance().find(id);
        System.out.println("esa vaina"+id);
        System.out.println(product.getId());

        for (int i = product.getComments().size() - 1; i >= 0; i--) {
            if (product.getComments().get(i).getId().equals(comments.getId())){
                product.getComments().remove(product.getComments().get(i));
            }
        }
        ProductService.getInstance().edit(product);
        CommentService.getInstance().delete(id);

        for(Session sessionConnected : us){
            try {
                sessionConnected.getRemote().sendString(id.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    public void  sendInfoClientDisconnect(){
        for(Session sesionConectada : shop.getUserConnected()){
            try {
                sesionConectada.getRemote().sendString("Users connected: "+ shop.getUserConnected().size());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
