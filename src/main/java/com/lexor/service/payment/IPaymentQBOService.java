package com.lexor.service.payment;

import com.intuit.ipp.data.Payment;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.DataService;
import com.lexor.model.CreditCardData;
import com.lexor.payload.PayloadPayment;

import java.io.IOException;

public interface IPaymentQBOService {
    Payment createPaymentQBO(CreditCardData creditCardData, DataService dataService) throws FMSException, IOException;
    String handleRedirectPayment(PayloadPayment payloadPayment,DataService dataService) throws FMSException, IOException;
}
