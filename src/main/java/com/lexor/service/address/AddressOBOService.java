package com.lexor.service.address;

import com.intuit.ipp.data.IntuitEntity;
import com.intuit.ipp.data.PhysicalAddress;
import com.intuit.ipp.data.ReferenceType;
import com.intuit.ipp.data.WebSiteAddress;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.DataService;
import com.lexor.service.IQBOService;

import java.text.ParseException;

public class AddressOBOService implements IQBOService {


    @Override
    public Object createEntityQBO(Object entity) throws FMSException, ParseException {
        return null;
    }

    @Override
    public ReferenceType getRef(IntuitEntity entity) {

        ReferenceType referenceType = new ReferenceType();
        return referenceType;
    }

    @Override
    public IntuitEntity isEntityActive(String id, DataService dataService) throws FMSException {
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


    public static PhysicalAddress getPhysicalAddress() {
        PhysicalAddress billingAdd = new PhysicalAddress();
        billingAdd.setLine1("123 Main St");
        billingAdd.setCity("Mountain View");
        billingAdd.setCountry("United States");
        billingAdd.setCountrySubDivisionCode("CA");
        billingAdd.setPostalCode("94043");
        return billingAdd;
    }

    public static WebSiteAddress getWebSiteAddress() {
        WebSiteAddress webSite = new WebSiteAddress();
        webSite.setURI("http://abccorp.com");
        webSite.setDefault(true);
        webSite.setTag("Business");
        return webSite;
    }

    public static PhysicalAddress getAddressForAST() {
        PhysicalAddress billingAdd = new PhysicalAddress();
        billingAdd.setLine1("2700 Coast Ave");
        billingAdd.setLine2("MountainView, CA 94043");
        return billingAdd;
    }
}
