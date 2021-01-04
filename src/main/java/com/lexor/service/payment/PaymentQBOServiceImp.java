package com.lexor.service.payment;

import com.intuit.ipp.data.*;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.util.DateUtils;
import com.lexor.config.QBODataService;
import com.lexor.model.CreditCardData;
import com.lexor.payload.PayloadPayment;
import com.lexor.service.IQBOService;
import com.lexor.service.account.AccountQBOService;
import com.lexor.service.customer.CustomerQBOService;
import com.lexor.service.estimate.EstimateServiceQBOImp;
import com.lexor.service.invoice.InvoiceQBOServiceImp;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author auphan
 */


public class PaymentQBOServiceImp implements IPaymentQBOService, IQBOService {


    @Override
    public Payment createPaymentQBO(CreditCardData creditCardData, DataService dataService) throws FMSException, IOException {
        Payment payment = new Payment();
        try {
            payment.setTxnDate(DateUtils.getCurrentDateTime());
        } catch (ParseException e) {
            throw new FMSException("ParseException while getting current date.");
        }

        IQBOService iqboService = new EstimateServiceQBOImp();
        Estimate estimate = (Estimate) iqboService.isEntityActive(String.valueOf(creditCardData.getIdOrder()),dataService);
        if (estimate != null) {
            InvoiceQBOServiceImp service = new InvoiceQBOServiceImp();
            Invoice invoice = (Invoice) service.isEntityActive(String.valueOf(creditCardData.getIdOrder()),dataService);
            if(invoice == null){
                invoice = (Invoice) service.createInvoiceFromEstimate(estimate);
            }
            payment.setPrivateNote(invoice.getDocNumber());

            List<LinkedTxn> linkedTxnList = new ArrayList<>();

            LinkedTxn linkedTxn = new LinkedTxn();
            linkedTxn.setTxnId(invoice.getId());
            linkedTxn.setTxnLineId(invoice.getDocNumber());
            linkedTxn.setTxnType(TxnTypeEnum.INVOICE.value());

            payment.setPrivateNote(invoice.getDocNumber());

            linkedTxnList.add(linkedTxn);

            payment.setLinkedTxn(linkedTxnList);

            Line line1 = new Line();
//            line1.setAmount(new BigDecimal(creditCardData.getAmount()));
//            line1.setDetailType(LineDetailTypeEnum.ACCOUNT_BASED_EXPENSE_LINE_DETAIL);
                line1.setDetailType(LineDetailTypeEnum.PAYMENT_LINE_DETAIL);
//            AccountBasedExpenseLineDetail accountBasedExpenseLineDetail1 = new AccountBasedExpenseLineDetail();
//            Account expenseAccount1 = AccountQBOService.getExpenseBankAccount(QBODataService.initConfigQuickBook());
//            AccountQBOService accountQBOService = new AccountQBOService();
//            accountBasedExpenseLineDetail1.setAccountRef(accountQBOService.getRef(expenseAccount1));
//            ReferenceType taxCodeRef = new ReferenceType();
//            taxCodeRef.setValue("NON");
//            accountBasedExpenseLineDetail1.setTaxCodeRef(taxCodeRef);
//            accountBasedExpenseLineDetail1.setBillableStatus(BillableStatusEnum.NOT_BILLABLE);
//
//            line1.setAccountBasedExpenseLineDetail(accountBasedExpenseLineDetail1);
//            line1.setLinkedTxn(linkedTxnList);

    //		Line line2 = new Line();
    //		line2.setAmount(new BigDecimal("22.00"));
    //		line2.setDetailType(LineDetailTypeEnum.ACCOUNT_BASED_EXPENSE_LINE_DETAIL);
    //		AccountBasedExpenseLineDetail accountBasedExpenseLineDetail2 = new AccountBasedExpenseLineDetail();
    //		Account expenseAccount2 = AccountHelper.getExpenseBankAccount(service);
    //		accountBasedExpenseLineDetail2.setAccountRef(AccountHelper.getAccountRef(expenseAccount2));
    //		accountBasedExpenseLineDetail2.setBillableStatus(BillableStatusEnum.NOT_BILLABLE);
    //
    //		line2.setAccountBasedExpenseLineDetail(accountBasedExpenseLineDetail2);
    //		line2.setLinkedTxn(linkedTxnList);

            List<Line> lineList = new ArrayList<Line>();
            lineList.add(line1);
            payment.setLine(lineList);

            payment.setCustomerRef(invoice.getCustomerRef());

            CreditCardPayment creditCardPayment = new CreditCardPayment();

            CreditChargeInfo creditChargeInfo = new CreditChargeInfo();

            creditChargeInfo.setNumber(creditCardData.getCardName());
            creditChargeInfo.setCcExpiryMonth(creditCardData.getExpMonth());
            creditChargeInfo.setCcExpiryYear(creditCardData.getExpYear());
            creditChargeInfo.setAmount(BigDecimal.valueOf(creditCardData.getAmount()));
            //creditChargeInfo.setProcessPayment(true);
            creditCardPayment.setCreditChargeInfo(creditChargeInfo);
            payment.setCreditCardPayment(creditCardPayment);
            payment.setPaymentType(PaymentTypeEnum.CREDIT_CARD);

            List<PaymentMethod> paymentMethod2 = (List<PaymentMethod>) dataService.executeQuery("select * from paymentmethod").getEntities();

            for (PaymentMethod paymentMethod : paymentMethod2) {
                if (paymentMethod.getType().equals(PaymentTypeEnum.CREDIT_CARD.name())) {
                    ReferenceType paymentMethodRef = new ReferenceType();
                    paymentMethodRef.setValue(paymentMethod.getId());
                    payment.setPaymentMethodRef(paymentMethodRef);
                }
                break;
            }

            System.out.println(payment.getCreditCardPayment().getCreditChargeInfo().getNumber());
            payment.setTotalAmt(BigDecimal.valueOf(creditCardData.getAmount()));
        }
        return dataService.add(payment);
    }

    @Override
    public String handleRedirectPayment(PayloadPayment payloadPayment,DataService dataService){
        Customer customer = null;
        Invoice invoice = null;
        try {
            CustomerQBOService customerQBOService = new CustomerQBOService();
             customer = (Customer) customerQBOService.findCustomerByNote(String.valueOf(payloadPayment.getIdCustomer()),dataService);
            InvoiceQBOServiceImp invoiceQBOServiceImp = new InvoiceQBOServiceImp();
             invoice = (Invoice) invoiceQBOServiceImp.isEntityActive(String.valueOf(payloadPayment.getIdInvoice()),dataService);
        }catch (FMSException | IOException exception){
            exception.printStackTrace();
            return "";
        }
        return "https://app.sandbox.qbo.intuit.com/app/recvpayment?srcTxnId="+invoice.getId()+"&nameId="+customer.getId()+"";
    }

    @Override
    public Object createEntityQBO(Object entity) throws FMSException, ParseException {

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
