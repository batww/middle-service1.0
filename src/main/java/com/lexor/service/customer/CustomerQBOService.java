package com.lexor.service.customer;

import com.intuit.ipp.data.Customer;
import com.intuit.ipp.data.IntuitEntity;
import com.intuit.ipp.data.ReferenceType;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.services.QueryResult;
import com.intuit.ipp.util.Logger;
import com.lexor.config.QBODataService;
import com.lexor.service.IQBOService;

import java.io.IOException;
import java.util.List;

/**
 * @author auphan
 */

public class CustomerQBOService implements  IQBOService {


    private static final org.slf4j.Logger LOG = Logger.getLogger();


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
    public IntuitEntity isEntityActive(String id, DataService dataService) throws FMSException, IOException {
        String sql = "select * from customer";
        DataService service = QBODataService.initConfigQuickBook();
        QueryResult queryResult = service.executeQuery(sql);
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

    public IntuitEntity findCustomerByNote(String id,DataService  dataService) throws FMSException, IOException {
        LOG.debug("findCustomerByNote = " +id);
        String sql = "select * from customer where id = '" +id+ "'";
        QueryResult queryResult = dataService.executeQuery(sql);
        try {
            Customer customers = (Customer)queryResult.getEntities().get(0);
            if(customers != null) return customers;
        }catch (Exception exception){
            return null;
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
