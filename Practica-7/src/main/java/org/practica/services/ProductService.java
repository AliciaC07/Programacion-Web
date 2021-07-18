package org.practica.services;

import org.hibernate.Session;
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
        Query query = entityManager.createQuery("SELECT p from Product p WHERE p.active = true", Product.class);
        List<Product> products = query.getResultList();
        return products;
    }
    public List<Product> findAllByActiveTruePagination(int pageSize, int lastPageNumber){
        EntityManager entityManager = getEntityManager();
        Query selectQuery = entityManager.createQuery("select p from Product p where p.active = true");
        selectQuery.setFirstResult((lastPageNumber - 1) * pageSize);
        selectQuery.setMaxResults(pageSize);
        List<Product> lastPage = selectQuery.getResultList();
        return lastPage;

    }
    public Product findProductByActiveTrue(Integer id){
        EntityManager entityManager = getEntityManager();
        Query query = entityManager.createNamedQuery("Product.findUserById");
        query.setParameter("id", id);
        List<Product> products = query.getResultList();
        return products.get(0);
    }
    public Product findByCommentId(Integer id){
        EntityManager entityManager = getEntityManager();
        Query query = entityManager.createNativeQuery("SELECT PRODUCT_ID FROM PRODUCT_COMMENTS  WHERE COMMENTS_ID = ?");
        query.setParameter(1, id);
        List<Integer> idP = query.getResultList();
        Product product = findProductByActiveTrue(idP.get(0));
        return product;

    }





}
