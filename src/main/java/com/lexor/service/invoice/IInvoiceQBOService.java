package com.lexor.service.invoice;

import com.intuit.ipp.data.Invoice;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.DataService;
/**
 * @author auphan
 */

public interface IInvoiceQBOService {
    Invoice findInvoiceByDocNumber(String docNumber, DataService dataService) throws FMSException;
}
