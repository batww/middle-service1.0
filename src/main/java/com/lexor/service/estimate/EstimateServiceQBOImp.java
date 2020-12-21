package com.lexor.service.estimate;

import com.intuit.ipp.data.*;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.util.DateUtils;
import com.lexor.model.KafkaOrderDetailDataPost;
import com.lexor.model.OrderQuickBook;
import com.lexor.service.customer.CustomerQBOService;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class EstimateServiceQBOImp implements IEstimateServiceQBO{

    @Override
    public Estimate createEstimate(OrderQuickBook orderQuickBook, DataService dataService) throws FMSException, IOException {

        Estimate estimate =new Estimate();
        try {
            //  create date estimate
            estimate.setTxnDate(DateUtils.getCurrentDateTime());
            // create date due of estimate
            estimate.setExpirationDate(orderQuickBook.getListScheduleShipping().get(0).getRequestedShip());
            // create ship date
            estimate.setShipDate(orderQuickBook.getListScheduleShipping().get(0).getRequestedShip());
        } catch (ParseException e) {
            throw new FMSException("ParseException while getting current date.");
        }

        CustomerQBOService customerQBOService = new CustomerQBOService();
        final String idCustomer = String.valueOf(orderQuickBook.getOrderInfo().getIdCustomer());
        Customer existCustomer = (Customer) customerQBOService.isEntityActive(idCustomer,dataService);

        final String firstName = orderQuickBook.getOrderInfo().getFirstName();
        final String lastName =  orderQuickBook.getOrderInfo().getLastName();
        final StringBuilder displayName = new StringBuilder(firstName + " " + lastName);
        final String companyName =  orderQuickBook.getOrderInfo().getCompany();
        final String emailCustomer = orderQuickBook.getOrderInfo().getEmail();
        final String phone = orderQuickBook.getOrderInfo().getPhone();
        final String shippingTo = orderQuickBook.getOrderInfo().getAddressShipSelected();
        final String idOrder =String.valueOf(orderQuickBook.getOrderInfo().getIdOrderERP());
        final String addressSelect = orderQuickBook.getOrderInfo().getAddressSelected();
        //create email address
        EmailAddress emailAddress =new EmailAddress();
        emailAddress.setAddress(emailCustomer);

        estimate.setBillEmail(emailAddress);

        if(existCustomer != null){
            estimate.setCustomerRef(customerQBOService.getRef(existCustomer));
        }else{

            Customer customer = new Customer();

            customer.setDisplayName(displayName.toString());
            customer.setCompanyName(companyName);
            customer.setNotes(idCustomer);
            customer.setPrimaryEmailAddr(emailAddress);

            //create telephone number
            TelephoneNumber telephoneNumber =  new TelephoneNumber();

            telephoneNumber.setFreeFormNumber(phone);

            customer.setPrimaryPhone(telephoneNumber);

            Customer saveCustomer = (Customer) customerQBOService.createEntityQBO(customer);

            estimate.setCustomerRef(customerQBOService.getRef(saveCustomer));

        }

        PhysicalAddress physicalAddress = new PhysicalAddress();

        physicalAddress.setCountry(shippingTo);
        estimate.setShipAddr(physicalAddress);

        physicalAddress.setCountry(addressSelect);

        estimate.setBillAddr(physicalAddress);

        List<KafkaOrderDetailDataPost> listOrderDetail = (List<KafkaOrderDetailDataPost>) orderQuickBook.getListOrderDetail();
        List<Line> lineList = new ArrayList<>();
        for (KafkaOrderDetailDataPost detail: listOrderDetail) {

            Line line = new Line();
            final BigDecimal amount = BigDecimal.valueOf(detail.getQuantity() * detail.getPrice());
            line.setDetailType(LineDetailTypeEnum.SALES_ITEM_LINE_DETAIL);
            line.setAmount(amount);
            line.setDescription("Sales order ");

            SalesItemLineDetail salesItemLineDetail = new SalesItemLineDetail();

            final BigDecimal quantity = BigDecimal.valueOf(detail.getQuantity());
            final BigDecimal price = BigDecimal.valueOf(detail.getPrice());

            salesItemLineDetail.setQty(quantity);
            salesItemLineDetail.setUnitPrice(price);

            ReferenceType taxCodeRef = new ReferenceType();
            taxCodeRef.setValue("NON");

            salesItemLineDetail.setTaxCodeRef(taxCodeRef);

            line.setSalesItemLineDetail(salesItemLineDetail);

            lineList.add(line);
        }
        estimate.setLine(lineList);
        estimate.setDocNumber(idOrder);

        return dataService.add(estimate);
    }
}
