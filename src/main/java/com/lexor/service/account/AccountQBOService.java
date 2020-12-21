package com.lexor.service.account;

import com.intuit.ipp.data.*;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.DataService;
import com.lexor.service.IQBOService;
import org.apache.commons.lang.RandomStringUtils;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

/**
 * @author auphan
 */
public class AccountQBOService implements IQBOService {

    @Override
    public Object createEntityQBO(Object entity) throws FMSException {
        Account account = new Account();
        account.setName("Equity" + RandomStringUtils.randomAlphabetic(5));
        account.setSubAccount(false);
        account.setFullyQualifiedName(account.getName());
        account.setActive(true);
        account.setClassification(AccountClassificationEnum.LIABILITY);
        account.setAccountType(AccountTypeEnum.ACCOUNTS_PAYABLE);
        account.setAccountSubType(AccountSubTypeEnum.ACCOUNTS_PAYABLE.value());
        account.setCurrentBalance(new BigDecimal("3000"));
        account.setCurrentBalanceWithSubAccounts(new BigDecimal("3000"));
        ReferenceType currencyRef = new ReferenceType();
        currencyRef.setName("United States Dollar");
        currencyRef.setValue("USD");
        account.setCurrencyRef(currencyRef);

        return account;
    }

    @Override
    public ReferenceType getRef(IntuitEntity entity) {
        Account account = (Account) entity;
        ReferenceType accountRef = new ReferenceType();
        accountRef.setValue(account.getId());
        return accountRef;
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


    public static Account getIncomeBankAccount(DataService service) throws FMSException {
        List<Account> accounts = (List<Account>) service.findAll(new Account());
        if (!accounts.isEmpty()) {
            Iterator<Account> itr = accounts.iterator();
            while (itr.hasNext()) {
                Account account = itr.next();
                if (account.getAccountType().equals(AccountTypeEnum.INCOME) && account.getAccountSubType().equals(AccountSubTypeEnum.SALES_OF_PRODUCT_INCOME)) {
                    return account;
                }
            }
        }
        return createIncomeBankAccount(service);
    }

    private static Account  createIncomeBankAccount(DataService service) throws FMSException {
        return service.add(getIncomeBankAccountFields());
    }

    public static Account getIncomeBankAccountFields(){

        Account account = new Account();
        account.setName("Incom" + RandomStringUtils.randomAlphabetic(5));
        account.setSubAccount(false);
        account.setFullyQualifiedName(account.getName());
        account.setActive(true);
        account.setClassification(AccountClassificationEnum.REVENUE);
        account.setAccountType(AccountTypeEnum.INCOME);
        account.setAccountSubType(AccountSubTypeEnum.SALES_OF_PRODUCT_INCOME.value());
        account.setCurrentBalance(new BigDecimal("0"));
        account.setCurrentBalanceWithSubAccounts(new BigDecimal("0"));
        ReferenceType currencyRef = new ReferenceType();
        currencyRef.setName("United States Dollar");
        currencyRef.setValue("USD");
        account.setCurrencyRef(currencyRef);

        return account;
    }

    public static Account getExpenseBankAccount(DataService service) throws FMSException {
        List<Account> accounts = (List<Account>) service.findAll(new Account());
        if (!accounts.isEmpty()) {
            Iterator<Account> itr = accounts.iterator();
            while (itr.hasNext()) {
                Account account = itr.next();
                if (account.getAccountType().equals(AccountTypeEnum.EXPENSE) && account.getAccountSubType().equals(AccountSubTypeEnum.SUPPLIES_MATERIALS_COGS)) {
                    return account;
                }
            }
        }
        return createExpenseBankAccount(service);
    }

    private static Account createExpenseBankAccount(DataService service) throws FMSException {
        return service.add(getExpenseBankAccountFields());
    }

    public static Account getExpenseBankAccountFields() {

        Account account = new Account();
        account.setName("Expense" + RandomStringUtils.randomAlphabetic(5));
        account.setSubAccount(false);
        account.setFullyQualifiedName(account.getName());
        account.setActive(true);
        account.setClassification(AccountClassificationEnum.EXPENSE);
        account.setAccountType(AccountTypeEnum.COST_OF_GOODS_SOLD);
        account.setAccountSubType(AccountSubTypeEnum.SUPPLIES_MATERIALS_COGS.value());
        account.setCurrentBalance(new BigDecimal("0"));
        account.setCurrentBalanceWithSubAccounts(new BigDecimal("0"));
        ReferenceType currencyRef = new ReferenceType();
        currencyRef.setName("United States Dollar");
        currencyRef.setValue("USD");
        account.setCurrencyRef(currencyRef);

        return account;
    }



    public static Account getAssetAccount(DataService service)  throws FMSException {
        List<Account> accounts = (List<Account>) service.findAll(new Account());
        if (!accounts.isEmpty()) {
            Iterator<Account> itr = accounts.iterator();
            while (itr.hasNext()) {
                Account account = itr.next();
                if (account.getAccountType().equals(AccountTypeEnum.OTHER_CURRENT_ASSET)
                        && account.getAccountSubType().equals(AccountSubTypeEnum.INVENTORY)) {
                    return account;
                }
            }
        }

        Account account1 = new Account();
        account1.setAccountType(AccountTypeEnum.ACCOUNTS_PAYABLE);

        return createOtherCurrentAssetAccount(service);
    }
    private static Account createOtherCurrentAssetAccount(DataService service) throws FMSException {
        Account account = new Account();
        account.setName("Other Current Asset " + RandomStringUtils.randomAlphanumeric(5));
        account.setAccountType(AccountTypeEnum.OTHER_CURRENT_ASSET);
        account.setAccountSubType(AccountSubTypeEnum.INVENTORY.value());

        return service.add(account);
    }





    //@Override


//    public static Account getExpenseBankAccount(DataService service) throws FMSException {
//        List<Account> accounts = (List<Account>) service.findAll(new Account());
//        if (!accounts.isEmpty()) {
//            Iterator<Account> itr = accounts.iterator();
//            while (itr.hasNext()) {
//                Account account = itr.next();
//                if (account.getAccountType().equals(AccountTypeEnum.EXPENSE)) {
//                    return account;
//                }
//            }
//        }
//        return createExpenseBankAccount(service);
//    }
//    private static Account createExpenseBankAccount(DataService service) throws FMSException {
//        return service.add(getExpenseBankAccountFields());
//    }
//    public static Account getExpenseBankAccountFields() throws FMSException {
//        Account account = new Account();
//        account.setName("Expense" + RandomStringUtils.randomAlphabetic(5));
//        account.setSubAccount(false);
//        account.setFullyQualifiedName(account.getName());
//        account.setActive(true);
//        account.setClassification(AccountClassificationEnum.EXPENSE);
//        account.setAccountType(AccountTypeEnum.EXPENSE);
//        account.setAccountSubType(AccountSubTypeEnum.ADVERTISING_PROMOTIONAL.value());
//        account.setCurrentBalance(new BigDecimal("0"));
//        account.setCurrentBalanceWithSubAccounts(new BigDecimal("0"));
//        ReferenceType currencyRef = new ReferenceType();
//        currencyRef.setName("United States Dollar");
//        currencyRef.setValue("USD");
//        account.setCurrencyRef(currencyRef);
//
//        return account;
//    }
    public Account getLiabilityBankAccount(DataService service) throws FMSException {
        List<Account> accounts = (List<Account>) service.findAll(new Account());
        if (!accounts.isEmpty()) {
            Iterator<Account> itr = accounts.iterator();
            while (itr.hasNext()) {
                Account account = itr.next();
                if (account.getAccountType().equals(AccountTypeEnum.ACCOUNTS_PAYABLE)
                        && account.getClassification().equals(AccountClassificationEnum.LIABILITY)) {
                    return account;
                }
            }
        }
        return (Account) createEntityQBO(null);
    }
}
