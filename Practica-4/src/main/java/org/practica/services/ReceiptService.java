package org.practica.services;

import org.practica.models.Product;
import org.practica.models.Receipt;
import org.practica.repository.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class ReceiptService extends Repository<Receipt> {
    private static ReceiptService receiptService;
    public ReceiptService() {
        super(Receipt.class);
    }
    public static ReceiptService getInstance(){
        if(receiptService == null){
            receiptService = new ReceiptService();
        }
        return receiptService;
    }

    public List<Receipt> findAllReceipt(){
        EntityManager entityManager = getEntityManager();
        Query query = entityManager.createQuery("SELECT r FROM Receipt r");
        List<Receipt> receipt = query.getResultList();
        return receipt;
    }

}
