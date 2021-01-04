package com.lexor.service.vendor;


import com.intuit.ipp.data.IntuitEntity;
import com.intuit.ipp.data.ReferenceType;
import com.intuit.ipp.data.Vendor;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.services.QueryResult;
import com.lexor.config.QBODataService;
import com.lexor.service.IQBOService;
import com.lexor.service.queue.QueueProcessor;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.logging.Logger;


/**
 * @author auphan
 */
public class VendorQBOService implements IQBOService {

    private static final Logger LOG = Logger.getLogger (QueueProcessor.class.getName());


    @Override
    public Object createEntityQBO(Object entity) throws FMSException, IOException {
        LOG.info("Create new vendor");
        Vendor vendor = new Vendor();
        // Mandatory Fields
        vendor.setDisplayName((String) entity);
        vendor.setCompanyName((String) entity);
//        // Optional Fields
//        vendor.setCompanyName("ABC Corp");
//        vendor.setTitle(RandomStringUtils.randomAlphanumeric(7));
//        vendor.setGivenName(RandomStringUtils.randomAlphanumeric(8));
//        vendor.setMiddleName(RandomStringUtils.randomAlphanumeric(1));
//        vendor.setFamilyName(RandomStringUtils.randomAlphanumeric(8));
//        vendor.setSuffix("Sr.");
//        vendor.setPrintOnCheckName("MS");
//
//        vendor.setBillAddr(Address.getPhysicalAddress());
//
//        vendor.setTaxIdentifier("1111111");
//
//        vendor.setPrimaryEmailAddr(Email.getEmailAddress());
//
//        vendor.setPrimaryPhone(Telephone.getPrimaryPhone());
//        vendor.setAlternatePhone(Telephone.getAlternatePhone());
//        vendor.setMobile(Telephone.getMobilePhone());
//        vendor.setFax(Telephone.getFax());
//
//        vendor.setWebAddr(Address.getWebSiteAddress());
//
//        vendor.setDomain("QBO");
//
//        Term term = TermHelper.getTerm(service);
//
//        vendor.setTermRef(TermHelper.getTermRef(term));
//
//        vendor.setAcctNum("11223344");
//        vendor.setBalance(new BigDecimal("0"));
//        try {
//            vendor.setOpenBalanceDate(DateUtils.getCurrentDateTime());
//        } catch (ParseException e) {
//            throw new FMSException("ParseException while getting current date.");
//        }
        LOG.info("Info vendor" + vendor.getDisplayName());
        return QBODataService.initConfigQuickBook().add(vendor);
    }

    @Override
    public ReferenceType getRef(IntuitEntity entity) {
        Vendor vendor = (Vendor) entity;
        ReferenceType vendorRef = new ReferenceType();
        vendorRef.setName(vendor.getDisplayName());
        vendorRef.setValue(vendor.getId());
        return vendorRef;
    }

    @Override
    public IntuitEntity isEntityActive(String id, DataService dataService) throws FMSException {
        String sql = "select * from vendor where displayname = '"+id+"'";
        QueryResult queryResult = dataService.executeQuery(sql);
        LOG.info("Get vendor by displayname" + id);
        try {
            Vendor vendors =(Vendor) queryResult.getEntities().get(0);

            return vendors;
        }catch (Exception exception){
            return null;
        }
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

    public  Vendor getVendor(DataService service) throws FMSException, ParseException, IOException {
        List<Vendor> vendors = (List<Vendor>) service.findAll(new Vendor());

        if (!vendors.isEmpty()) {
            return vendors.get(0);
        }
        return (Vendor) createEntityQBO(null);
    }
}
