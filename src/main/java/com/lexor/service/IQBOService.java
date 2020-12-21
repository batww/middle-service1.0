package com.lexor.service;

import com.intuit.ipp.data.IntuitEntity;
import com.intuit.ipp.data.ReferenceType;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.DataService;

import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.ExecutionException;

public interface IQBOService {
    Object createEntityQBO(Object entity) throws FMSException, ParseException, IOException;

    ReferenceType getRef(IntuitEntity entity);

    IntuitEntity isEntityActive(String id, DataService dataService) throws FMSException;

    Object updateEntityQBO(Object entity) throws FMSException, IOException;

    Object deleteEntityOBO(String id, DataService dataService) throws FMSException;

    public boolean sendAsynQBO(Object entity) throws ExecutionException, InterruptedException;

}
