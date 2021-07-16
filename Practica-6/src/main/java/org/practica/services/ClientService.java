package org.practica.services;

import org.practica.models.Client;
import org.practica.models.User;
import org.practica.repository.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

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
    public Client findClientByEmail(String email){
        EntityManager entityManager = getEntityManager();
        Query query = entityManager.createQuery("select c from Client c where c.email = :email");
        query.setParameter("email", email);
        List<Client> user = query.getResultList();
        if (user.isEmpty()){
            System.out.println("Nada");
            return null;
        }
        return  user.get(0);

    }

}
