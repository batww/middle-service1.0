package com.lexor.service.purchaseorder;


import com.intuit.ipp.data.*;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.util.DateUtils;
import com.lexor.config.QBODataService;
import com.lexor.model.Product;
import com.lexor.model.PurchaseOrderDetails;
import com.lexor.model.PurchaseOrderDto;
import com.lexor.service.IQBOService;
import com.lexor.service.account.AccountQBOService;
import com.lexor.service.address.AddressOBOService;
import com.lexor.service.email.EmailQBOService;
import com.lexor.service.item.ItemQBOService;
import com.lexor.service.vendor.VendorQBOService;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;


/**
 * @author auphan
 */
public class PurchaseOrderQBOServiceImp implements IQBOService
{
    private static final Logger LOG = Logger.getLogger (PurchaseOrderQBOServiceImp.class.getName());

    private final ScheduledExecutorService executorService;
    private static final int COUNT_THREAD_POOL = 20;
    public PurchaseOrderQBOServiceImp() {
        LOG.info("init executor");
        executorService = Executors.newScheduledThreadPool(COUNT_THREAD_POOL);
    }
    @Override
    public boolean sendAsynQBO(Object entity) throws ExecutionException, InterruptedException {

            LOG.info("send PO asyn to QBO");
            Future<Boolean> isSynSuccess = executorService.submit(new Callable<Boolean>(){
                @Override
                public Boolean call() {
                    LOG.info("Runnable");
                    try {
                        createEntityQBO(entity);
                        return true;
                    } catch (FMSException | IOException | ParseException fmsException) {
                        fmsException.printStackTrace();
                        return false;
                    }
                }
            });
            executorService.shutdown();
        return isSynSuccess.get();

    }


    @Override
    public Object createEntityQBO(Object entity) throws FMSException, ParseException, IOException {
        LOG.info("create PO");
        PurchaseOrderDto purchaseOrderDto = (PurchaseOrderDto) entity;

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        VendorQBOService vendorQBOService = new VendorQBOService();
        Vendor vendor = (Vendor) vendorQBOService.isEntityActive(purchaseOrderDto.getVendor(),QBODataService.initConfigQuickBook());
        if(vendor != null)
            purchaseOrder.setVendorRef(vendorQBOService.getRef(vendor));
        else {
            Vendor vendor1 = (Vendor) vendorQBOService.createEntityQBO(purchaseOrderDto.getVendor());
            purchaseOrder.setVendorRef(vendorQBOService.getRef(vendor1));
        }
        purchaseOrder.setTxnDate(DateUtils.getCurrentDateTime());
        purchaseOrder.setDocNumber(String.valueOf(purchaseOrderDto.getIdPurchaseOrder()));

        AccountQBOService accountQBOService = new AccountQBOService();

        Account account = accountQBOService.getLiabilityBankAccount(QBODataService.initConfigQuickBook());
        purchaseOrder.setAPAccountRef(accountQBOService.getRef(account));

        purchaseOrder.setMemo("For Internal usage");

        Line line1 = new Line();
        line1.setAmount(BigDecimal.valueOf(0));
        line1.setDetailType(LineDetailTypeEnum.ACCOUNT_BASED_EXPENSE_LINE_DETAIL);

        AccountBasedExpenseLineDetail detail = new AccountBasedExpenseLineDetail();

        Account account1 = AccountQBOService.getExpenseBankAccount(QBODataService.initConfigQuickBook());
        detail.setAccountRef(accountQBOService.getRef(account1));
        line1.setAccountBasedExpenseLineDetail(detail);

        List<Line> lineList = new ArrayList<>();

        lineList.add(line1);

        double amount = 0;
        for (PurchaseOrderDetails purchaseOrderDetails: purchaseOrderDto.getPurchaseOrderDetails()) {
            Line line = new Line();
            line.setDetailType(LineDetailTypeEnum.ITEM_BASED_EXPENSE_LINE_DETAIL);

            ItemQBOService service = new ItemQBOService();

            Product product = new Product();
            product.setId(purchaseOrderDetails.getIdProduct());
            product.setPrice(purchaseOrderDetails.getPrice());
            product.setENName(purchaseOrderDetails.getName());

            Item item = (Item) service.isEntityActive(purchaseOrderDetails.getIdProduct() +product.getENName(),QBODataService.initConfigQuickBook());

            ItemBasedExpenseLineDetail itemBasedExpenseLineDetail = new ItemBasedExpenseLineDetail();

            if(item != null)
                itemBasedExpenseLineDetail.setItemRef(service.getRef(item));
            else{
                Item item1 = service.createItem(product,purchaseOrderDetails.getQuantity());
                if(item1 != null)
                    itemBasedExpenseLineDetail.setItemRef(service.getRef(item1));
            }
            itemBasedExpenseLineDetail.setQty(BigDecimal.valueOf(purchaseOrderDetails.getQuantity()));
            itemBasedExpenseLineDetail.setUnitPrice(BigDecimal.valueOf(purchaseOrderDetails.getPrice()));
            line.setItemBasedExpenseLineDetail(itemBasedExpenseLineDetail);

            line.setAmount(BigDecimal.valueOf(purchaseOrderDetails.getQuantity() * purchaseOrderDetails.getPrice()));
            amount += amount + ( Double.parseDouble(String.valueOf(line.getAmount())));
            lineList.add(line);
        }
        purchaseOrder.setTotalAmt(BigDecimal.valueOf(amount));
        PhysicalAddress address = new PhysicalAddress();
        address.setLine1(purchaseOrderDto.getShipTo());
        ReferenceType  referenceType = new ReferenceType();
        referenceType.setValue(address.getId());
        referenceType.setName(address.getLine1());
        purchaseOrder.setShipTo(referenceType);

        purchaseOrder.setLine(lineList);

        purchaseOrder.setPOEmail(EmailQBOService.getEmailAddress());

        purchaseOrder.setDomain("QBO");

        purchaseOrder.setGlobalTaxCalculation(GlobalTaxCalculationEnum.NOT_APPLICABLE);

        purchaseOrder.setReplyEmail(EmailQBOService.getEmailAddress());

        purchaseOrder.setShipAddr(AddressOBOService.getPhysicalAddress());

        purchaseOrder.setTxnDate(DateUtils.getCurrentDateTime());

        return QBODataService.initConfigQuickBook().add(purchaseOrder);
    }


    @Override
    public ReferenceType getRef(IntuitEntity entity) {
        PurchaseOrder purchaseOrder = (PurchaseOrder) entity;
        ReferenceType purchaseOrderRef = new ReferenceType();
        purchaseOrderRef.setValue(purchaseOrder.getId());
        return purchaseOrderRef;
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


}
