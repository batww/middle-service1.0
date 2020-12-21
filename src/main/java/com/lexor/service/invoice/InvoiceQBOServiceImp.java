package com.lexor.service.invoice;

import com.intuit.ipp.data.*;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.services.QueryResult;
import com.intuit.ipp.util.DateUtils;
import com.lexor.config.QBODataService;
import com.lexor.model.KafkaOrderDetailDataPost;
import com.lexor.model.OrderQuickBook;
import com.lexor.service.IQBOService;
import com.lexor.service.customer.CustomerQBOService;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author auphan
 */


public class InvoiceQBOServiceImp implements IInvoiceQBOService, IQBOService {

    @Override
    public Invoice findInvoiceByDocNumber(String docNumber, DataService dataService) throws FMSException {

        String sql = "select * from invoice where docnumber = '" + docNumber + "'";
        QueryResult queryResult = dataService.executeQuery(sql);
        return (Invoice)queryResult.getEntities().get(0);
    }

    @Override
    public Object createEntityQBO(Object entity) throws FMSException, IOException {
        Invoice invoice = new Invoice();
        OrderQuickBook orderQuickBook = (OrderQuickBook) entity;
        final String idOrder = String.valueOf(orderQuickBook.getOrderInfo().getIdOrderERP());

        List <CustomField> invoiceCustomFields = new ArrayList <> ();

        CustomField customField = new CustomField();
        customField.setDefinitionId("1");
        customField.setName("Id Order");
        customField.setType(CustomFieldTypeEnum.STRING_TYPE);
        customField.setStringValue(idOrder);

        invoiceCustomFields.add(customField);

        invoice.setCustomField(invoiceCustomFields);

        try {
            invoice.setTxnDate(DateUtils.getCurrentDateTime());
        } catch (ParseException e) {
            throw new FMSException("ParseException while getting current date.");
        }

        CustomerQBOService customerQBOService = new CustomerQBOService();

        final String idCustomer = String.valueOf(orderQuickBook.getOrderInfo().getIdCustomer());
        Customer customerExist = (Customer) customerQBOService.isEntityActive(idCustomer, QBODataService.initConfigQuickBook());
        final String firstName = orderQuickBook.getOrderInfo().getFirstName();
        final String lastName =  orderQuickBook.getOrderInfo().getLastName();
        final StringBuilder displayName = new StringBuilder(firstName + " " + lastName);
        final String companyName =  orderQuickBook.getOrderInfo().getCompany();
        final String emailCustomer = orderQuickBook.getOrderInfo().getEmail();
        final String phone = orderQuickBook.getOrderInfo().getPhone();
        final String shippingTo = orderQuickBook.getOrderInfo().getAddressShipSelected();
        final String addressSelected = orderQuickBook.getOrderInfo().getAddressSelected();
        //create email address
        EmailAddress emailAddress =new EmailAddress();
        emailAddress.setAddress(emailCustomer);

        invoice.setBillEmail(emailAddress);

        if(customerExist != null){
            invoice.setCustomerRef(customerQBOService.getRef(customerExist));
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

            invoice.setCustomerRef(customerQBOService.getRef(saveCustomer));

        }
        PhysicalAddress physicalAddress = new PhysicalAddress();
        physicalAddress.setCountry(shippingTo);
        physicalAddress.setCountry(addressSelected);

        invoice.setShipAddr(physicalAddress);
        invoice.setBillAddr(physicalAddress);
        invoice.setPrivateNote("Testing");
        invoice.setTxnStatus("Payable");

        List<KafkaOrderDetailDataPost> listOrder = (List<KafkaOrderDetailDataPost>) orderQuickBook.getListOrderDetail();
        List<Line> lineList = new ArrayList<>();
        for (KafkaOrderDetailDataPost detail: listOrder) {

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
        invoice.setLine(lineList);

        return QBODataService.initConfigQuickBook().add(invoice);
    }


    @Override
    public ReferenceType getRef(IntuitEntity entity) {
        return null;
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


}
