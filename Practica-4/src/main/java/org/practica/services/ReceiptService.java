package org.practica.services;

import org.practica.models.Receipt;
import org.practica.repository.Repository;

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

}
