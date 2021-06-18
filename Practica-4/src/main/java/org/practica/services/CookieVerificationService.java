package org.practica.services;

import org.practica.models.CookieVerification;
import org.practica.models.Product;
import org.practica.models.User;
import org.practica.repository.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CookieVerificationService extends Repository<CookieVerification> {

    private static  CookieVerificationService cookieVerificationService;

    public CookieVerificationService() {
        super(CookieVerification.class);
    }
    public static CookieVerificationService getInstance(){
        if(cookieVerificationService == null){
            cookieVerificationService = new CookieVerificationService();
        }
        return cookieVerificationService;
    }
    public Map<String, String> findByCookieToken(String token){
        EntityManager entityManager = getEntityManager();
        Query query = entityManager.createQuery("select * from CookieVerification where token = :token");
        query.setParameter("token", token+"%");
        List<CookieVerification> cookieVerifications = query.getResultList();
        if (cookieVerifications.isEmpty()){
            System.out.println("Nada");
            return null;
        }
       Map<String, String> cookie = new HashMap<>();
       cookie.put("user", cookieVerifications.get(0).getUsername());
       cookie.put("token", cookieVerifications.get(0).getToken());
       return cookie;

    }
}
