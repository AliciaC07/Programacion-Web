package org.practica.services;

import org.jasypt.util.password.StrongPasswordEncryptor;
import org.practica.models.Product;
import org.practica.models.User;
import org.practica.repository.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class UserService  extends Repository<User> {
    private static  UserService userService;
    StrongPasswordEncryptor spe = new StrongPasswordEncryptor();

    public UserService() {
        super(User.class);
    }
    public static UserService getInstance(){
        if(userService == null){
            userService = new UserService();
        }
        return userService;
    }
    public User findByUserName(String username){
        EntityManager entityManager = getEntityManager();
        Query query = entityManager.createQuery("select * from User where userName = :username");
        query.setParameter("username", username+"%");
        List<User> user = query.getResultList();
        if (user.isEmpty()){
            System.out.println("Nada");
            return null;
        }
        return  user.get(0);

    }
    public User loginUser(String userName, String password){
        User user = findByUserName(userName);
        if (user == null){
            return null;
        }
        if (spe.checkPassword(password, user.getPassword())){
            return user;
        }
        return null;

    }

}
