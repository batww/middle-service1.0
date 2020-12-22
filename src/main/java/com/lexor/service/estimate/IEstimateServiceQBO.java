package com.lexor.service.estimate;

import com.intuit.ipp.data.Estimate;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.DataService;
import com.lexor.model.OrderQuickBook;

import java.io.IOException;

public interface IEstimateServiceQBO {
    Estimate createEstimate(Object entity, DataService dataService) throws FMSException, IOException;
}
