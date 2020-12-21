package com.lexor.service.payment;

import com.intuit.ipp.data.Payment;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.DataService;

public interface IPaymentQBOService {
    Payment createPaymentQBO(String idOrder, Double aDouble,DataService dataService) throws FMSException;
}
