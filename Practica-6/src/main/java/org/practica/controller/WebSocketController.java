package org.practica.controller;

import io.javalin.Javalin;
import org.eclipse.jetty.websocket.api.Session;
import org.practica.services.Shop;

import java.io.IOException;

public class WebSocketController {
    private final Javalin app;
    private Shop shop = Shop.getInstance();

    public WebSocketController(Javalin app) {
        this.app = app;
    }
    public void applyRoutes(){
        app.ws("/", ws -> {
//guardar el usuario

            ws.onConnect(ctx -> {
                System.out.println("Conexión Iniciada - "+ctx.getSessionId());
                shop.addUserConnected(ctx.session);
                sendInfoClientDisconnect();
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
                commentRemove(ctx.message());



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

    }
    public void commentRemove(String id){
        System.out.println("esa vaina"+id);
        for(Session sesionConectada : shop.getUserConnected()){
            try {
                sesionConectada.getRemote().sendString("Respuesta server:"+id);
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
