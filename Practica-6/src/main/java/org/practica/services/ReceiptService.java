package org.practica.services;

import lombok.Getter;
import lombok.Setter;
import org.practica.models.Product;
import org.practica.models.Receipt;
import org.practica.repository.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

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
    public Double getTotalSales(){
        EntityManager entityManager = getEntityManager();
        Query query = entityManager.createQuery("SELECT SUM(r.total) as TOTALSALES FROM Receipt r");
        List<Double> total = query.getResultList();
        if (total.get(0) != null){
            Double ca = 0.0;
            ca = Double.parseDouble(total.get(0).toString());
            System.out.println(ca);
            return ca;
        }else {
            return 0.0;
        }

    }
    public List<Quantity> getQuantitySold(){
        EntityManager entityManager = getEntityManager();
        Query query = entityManager.createNativeQuery("SELECT SUM(r.QUANTITY) as total, r.PRODUCT_ID as product FROM PRODUCT_RECEIPT r group by r.PRODUCT_ID");
        List<Object[]> consult = query.getResultList();
        List<Quantity> quantities = new ArrayList<>();
        if (consult.size() == 0){
            Quantity q = new Quantity();
            q.setProduct("None");
            q.setTotal(0);
            quantities.add(q);
        }else {
            for (Object[] o : consult) {
                Quantity q = new Quantity();
                q.setTotal(Integer.parseInt(o[0].toString()));
                Product p = ProductService.getInstance().find(Integer.parseInt(o[1].toString()));
                q.setProduct(p.getName());
                quantities.add(q);
            }
        }

        return quantities;

    }
    @Getter
    @Setter
    public static class Quantity{
        private Integer total;
        private String product;
    }

}
