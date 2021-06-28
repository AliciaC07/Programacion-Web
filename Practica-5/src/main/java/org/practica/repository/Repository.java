package org.practica.repository;

import javax.persistence.criteria.CriteriaQuery;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class Repository <T> {
    private static EntityManagerFactory entityManagerFactory;

    private Class<T> entityClass;

    public Repository(Class<T> entityClass) {
        if(entityManagerFactory == null){
            entityManagerFactory = getConfiguracionBaseDatosHeroku();
        }
        this.entityClass = entityClass;
    }
    private EntityManagerFactory getConfiguracionBaseDatosHeroku(){
        //Leyendo la información de la variable de ambiente de Heroku
        String databaseUrl = System.getenv("DATABASE_URL");
        StringTokenizer st = new StringTokenizer(databaseUrl, ":@/");
        //Separando las información del conexión.
        String dbVendor = st.nextToken();
        String userName = st.nextToken();
        String password = st.nextToken();
        String host = st.nextToken();
        String port = st.nextToken();
        String databaseName = st.nextToken();
        //creando la jbdc String
        String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s?sslmode=require", host, port, databaseName);
        //pasando las propiedades.
        Map<String, String> properties = new HashMap<>();
        properties.put("javax.persistence.jdbc.url", jdbcUrl );
        properties.put("javax.persistence.jdbc.user", userName );
        properties.put("javax.persistence.jdbc.password", password );
        //
        return Persistence.createEntityManagerFactory("Heroku", properties);
    }

    public EntityManager getEntityManager(){
        return entityManagerFactory.createEntityManager();
    }

    public T create(T entity){
        EntityManager entityManager = getEntityManager();

        try{
            entityManager.getTransaction().begin();
            entityManager.persist(entity);
            entityManager.getTransaction().commit();

        }finally {
            entityManager.close();

        }
        return entity;
    }

    public T edit(T entity){
        EntityManager entityManager = getEntityManager();
        entityManager.getTransaction().begin();
        try {
            entityManager.merge(entity);
            entityManager.getTransaction().commit();
        }finally {
            entityManager.close();
        }
        return entity;
    }

    public boolean delete(Object entityId){
        boolean status = false;
        EntityManager entityManager = getEntityManager();
        entityManager.getTransaction().begin();
        try{
            T entity = entityManager.find(entityClass, entityId);
            entityManager.remove(entity);
            entityManager.getTransaction().commit();
            status = true;
        }finally {
            entityManager.close();
        }
        return status;
    }

    public T find(Object objectId){
        EntityManager entityManager = getEntityManager();
        try {
            return entityManager.find(entityClass, objectId);
        }finally {
            entityManager.close();
        }
    }

    public List<T> findAll() throws PersistenceException {
        EntityManager entityManager = getEntityManager();
        try{
            CriteriaQuery<T> criteriaQuery = entityManager.getCriteriaBuilder().createQuery(entityClass);
            criteriaQuery.select(criteriaQuery.from(entityClass));
            return entityManager.createQuery(criteriaQuery).getResultList();
        } finally {
            entityManager.close();
        }

    }

}
