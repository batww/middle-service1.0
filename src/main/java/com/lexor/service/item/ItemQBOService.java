package com.lexor.service.item;

import com.intuit.ipp.data.*;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.services.QueryResult;
import com.lexor.config.QBODataService;
import com.lexor.model.Product;
import com.lexor.service.IQBOService;
import com.lexor.service.account.AccountQBOService;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.*;
;

/**
 * @author auphan
 */

public class ItemQBOService implements IItemQBOService, IQBOService {


    private final ScheduledExecutorService executorService;
    private final static int COUNT_THREAD_POOL = 20;
    public ItemQBOService() {
        executorService = Executors.newScheduledThreadPool(COUNT_THREAD_POOL);
    }

    public static ReferenceType getItemRef(Item item) {
        ReferenceType itemRef = new ReferenceType();
        itemRef.setName(item.getName());
        itemRef.setValue(item.getId());
        return itemRef;
    }

    private ReferenceType createRef(IntuitEntity entity) {
        ReferenceType referenceType = new ReferenceType();
        referenceType.setValue(entity.getId());
        return referenceType;
    }

    public Item getItemById(String id) {
        return null;
    }

    @Override
    public Object createEntityQBO(Object entity) throws FMSException, ParseException, IOException {
        Product product = (Product) entity;
        Item item = new Item();
        item.setType(ItemTypeEnum.INVENTORY);
        item.setName(String.valueOf(product.getId()));
        item.setInvStartDate(new Date());

        // Start with 10 items
        item.setQtyOnHand(BigDecimal.valueOf(1));
        item.setTrackQtyOnHand(true);
        item.setUnitPrice(BigDecimal.valueOf(product.getPrice()));
        Account incomeBankAccount = AccountQBOService.getIncomeBankAccount(QBODataService.initConfigQuickBook());
        item.setIncomeAccountRef(createRef(incomeBankAccount));

        Account expenseBankAccount = AccountQBOService.getExpenseBankAccount(QBODataService.initConfigQuickBook());
        item.setExpenseAccountRef(createRef(expenseBankAccount));

        Account assetAccount = AccountQBOService.getAssetAccount(QBODataService.initConfigQuickBook());
        item.setAssetAccountRef(createRef(assetAccount));
        return QBODataService.initConfigQuickBook().add(item);
    }
    public boolean createItemAsync(Object entity) throws ExecutionException, InterruptedException {
        Future<Boolean> isSendSuccess = executorService.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    createItemAsync(entity);
                    return true;
                }catch (Exception exception){
                    exception.printStackTrace();
                    return false;
                }
            }
        });
        return isSendSuccess.get();
    }

    public Item createItem(Object entity,int quantity) throws FMSException, IOException {
        Product product = (Product) entity;
        Item item = new Item();
        item.setType(ItemTypeEnum.INVENTORY);
        item.setName(String.valueOf(product.getId()));
        item.setInvStartDate(new Date());

        // Start with 10 items
        item.setQtyOnHand(BigDecimal.valueOf(quantity));
        item.setTrackQtyOnHand(true);
        item.setUnitPrice(BigDecimal.valueOf(product.getPrice()));
        Account incomeBankAccount = AccountQBOService.getIncomeBankAccount(QBODataService.initConfigQuickBook());
        item.setIncomeAccountRef(createRef(incomeBankAccount));

        Account expenseBankAccount = AccountQBOService.getExpenseBankAccount(QBODataService.initConfigQuickBook());
        item.setExpenseAccountRef(createRef(expenseBankAccount));

        Account assetAccount = AccountQBOService.getAssetAccount(QBODataService.initConfigQuickBook());
        item.setAssetAccountRef(createRef(assetAccount));
        return QBODataService.initConfigQuickBook().add(item);
    }
    @Override
    public ReferenceType getRef(IntuitEntity entity) {
        Item item = (Item) entity;
        ReferenceType referenceType =  new ReferenceType();
        referenceType.setValue(item.getId());
        referenceType.setName(item.getName());
        return referenceType;
    }

    @Override
    public IntuitEntity isEntityActive(String id, DataService dataService) throws FMSException {
        String sql = "select * from item where name = '"+id+"'";
        QueryResult queryResult = dataService.executeQuery(sql);
        try {
            Item item =(Item) queryResult.getEntities().get(0);
            return item;
        }catch (Exception exception){
            return null;
        }
    }

    @Override
    public Object updateEntityQBO(Object entity) throws FMSException, IOException {
        Product product = (Product) entity;
        IQBOService service = new ItemQBOService();
        Item item = (Item) service.isEntityActive(String.valueOf(product.getId()),QBODataService.initConfigQuickBook());
        if (item != null) {
            item.setType(ItemTypeEnum.INVENTORY);
            item.setInvStartDate(new Date());

            // Start with 10 items
            item.setQtyOnHand(BigDecimal.valueOf(10));
            item.setTrackQtyOnHand(true);
            item.setUnitPrice(BigDecimal.valueOf(product.getPrice()));
            Account incomeBankAccount = AccountQBOService.getIncomeBankAccount(QBODataService.initConfigQuickBook());
            item.setIncomeAccountRef(createRef(incomeBankAccount));

            Account expenseBankAccount = AccountQBOService.getExpenseBankAccount(QBODataService.initConfigQuickBook());
            item.setExpenseAccountRef(createRef(expenseBankAccount));

            Account assetAccount = AccountQBOService.getAssetAccount(QBODataService.initConfigQuickBook());
            item.setAssetAccountRef(createRef(assetAccount));
        }else return null;
        return QBODataService.initConfigQuickBook().update(item);
    }

    public boolean updateItemAsync(Object entity) throws ExecutionException, InterruptedException {
        Future<Boolean> isSendSuccess = executorService.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    updateEntityQBO(entity);
                    return true;
                }catch (FMSException exception){
                    exception.printStackTrace();
                    return false;
                }
            }
        });
        return isSendSuccess.get();
    }

    @Override
    public Object deleteEntityOBO(String id, DataService dataService) {
        return null;
    }

    @Override
    public boolean sendAsynQBO(Object entity) {
        return false;
    }
}
