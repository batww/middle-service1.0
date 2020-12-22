package com.lexor.service.estimate;

import com.intuit.ipp.data.*;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.util.DateUtils;
import com.lexor.config.QBODataService;
import com.lexor.model.KafkaOrderDetailDataPost;
import com.lexor.model.OrderQuickBook;
import com.lexor.model.Product;
import com.lexor.service.IQBOService;
import com.lexor.service.customer.CustomerQBOService;
import com.lexor.service.item.ItemQBOService;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public class EstimateServiceQBOImp implements IEstimateServiceQBO, IQBOService {

    private final ScheduledExecutorService executorService;
    private final static int COUNT_THREAD_POOL = 20;

    public EstimateServiceQBOImp() {
        this.executorService = Executors.newScheduledThreadPool(COUNT_THREAD_POOL);
    }

    @Override
    public Estimate createEstimate(Object entity, DataService dataService) throws FMSException, IOException {
        OrderQuickBook orderQuickBook = (OrderQuickBook) entity;
        Estimate estimate = new Estimate();
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
        Customer existCustomer = (Customer) customerQBOService.isEntityActive(idCustomer,QBODataService.initConfigQuickBook());

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

            SalesItemLineDetail salesItemLineDetail = new SalesItemLineDetail();
            Line line = new Line();
            line.setDetailType(LineDetailTypeEnum.SALES_ITEM_LINE_DETAIL);

            ItemQBOService service = new ItemQBOService();

            Product product = new Product();
            product.setId(detail.getIdProduct());
            product.setPrice(detail.getPrice());

            Item item = (Item) service.isEntityActive(detail.getIdProduct() +product.getENName(), QBODataService.initConfigQuickBook());

            if(item != null)
                salesItemLineDetail.setItemRef(service.getRef(item));
            else{
                Item item1 = service.createItem(product,detail.getQuantity());
                if(item1 != null)
                    salesItemLineDetail.setItemRef(service.getRef(item1));
            }
            salesItemLineDetail.setQty(BigDecimal.valueOf(detail.getQuantity()));
            salesItemLineDetail.setUnitPrice(BigDecimal.valueOf(detail.getPrice()));
            line.setSalesItemLineDetail(salesItemLineDetail);

            line.setAmount(BigDecimal.valueOf(detail.getQuantity() * detail.getPrice()));

            lineList.add(line);

            lineList.add(line);
        }
        estimate.setLine(lineList);
        estimate.setDocNumber(idOrder);

        return QBODataService.initConfigQuickBook().add(estimate);
    }

    @Override
    public Object createEntityQBO(Object entity) throws FMSException, ParseException, IOException {
        return null;
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
    public Object updateEntityQBO(Object entity) throws FMSException, IOException {
        return null;
    }

    @Override
    public Object deleteEntityOBO(String id, DataService dataService) throws FMSException {
        return null;
    }

    @Override
    public boolean sendAsynQBO(Object entity) throws ExecutionException, InterruptedException {
        Future<Boolean> isSendSuccess = executorService.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    createEstimate(entity,QBODataService.initConfigQuickBook());
                    return true;
                }catch (FMSException exception){
                    exception.printStackTrace();
                    return false;
                }
            }
        });
        return isSendSuccess.get();
    }
}
