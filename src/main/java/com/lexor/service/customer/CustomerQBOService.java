package com.lexor.service.customer;

import com.intuit.ipp.data.Customer;
import com.intuit.ipp.data.IntuitEntity;
import com.intuit.ipp.data.ReferenceType;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.services.QueryResult;
import com.lexor.config.QBODataService;
import com.lexor.service.IQBOService;

import java.io.IOException;
import java.util.List;

/**
 * @author auphan
 */

public class CustomerQBOService implements  IQBOService {


    @Override
    public Object createEntityQBO(Object entity) throws FMSException, IOException {
        Customer customer = (Customer) entity;
        return QBODataService.initConfigQuickBook().add(customer);
    }

    @Override
    public ReferenceType getRef(IntuitEntity entity) {
        Customer customer = (Customer) entity;
        ReferenceType customerRef = new ReferenceType();
        customerRef.setName(customer.getDisplayName());
        customerRef.setValue(customer.getId());
        return customerRef;
    }

    @Override
    public IntuitEntity isEntityActive(String id, DataService dataService) throws FMSException {
        String sql = "select * from customer";
        QueryResult queryResult = dataService.executeQuery(sql);
        List<Customer> customers =(List<Customer>) queryResult.getEntities();

        if(!customers.isEmpty()){
            for (Customer customer: customers) {
                if ( customer.getNotes() != null){
                    if(customer.getNotes().equals(id))
                        return customer;
                }
            }
        }
        return null;
    }

    @Override
    public Object updateEntityQBO(Object entity) throws FMSException {
        return null;
    }

    @Override
    public Object deleteEntityOBO(String id, DataService dataService) throws FMSException {
        return null;
    }

    @Override
    public boolean sendAsynQBO(Object entity) {
        return false;
    }
}
