package org.practica.services;

import org.practica.models.Product;
import org.practica.repository.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class ProductService extends Repository<Product> {

    private static  ProductService productService;

    public ProductService() {
        super(Product.class);
    }
    public static ProductService getInstance(){
        if(productService == null){
            productService = new ProductService();
        }
        return productService;
    }

    public List<Product> findAllByActiveTrue(){
        EntityManager entityManager = getEntityManager();
        Query query = entityManager.createNativeQuery("SELECT * from PRODUCT WHERE ACTIVE = true", Product.class);
        List<Product> products = query.getResultList();
        return products;
    }
    public Product findProductByActiveTrue(Integer id){
        EntityManager entityManager = getEntityManager();
        Query query = entityManager.createNamedQuery("Product.findUserById");
        query.setParameter("id", id);
        List<Product> products = query.getResultList();
        return products.get(0);
    }



}
