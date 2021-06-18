package org.practica.services;

import org.practica.models.Client;
import org.practica.models.Receipt;
import org.practica.models.User;
import org.practica.repository.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientService extends Repository<Client> {

    private static ClientService clientService;

    public ClientService() {
        super(Client.class);
    }
    public static ClientService getInstance(){
        if(clientService == null){
            clientService = new ClientService();
        }
        return clientService;
    }

}
