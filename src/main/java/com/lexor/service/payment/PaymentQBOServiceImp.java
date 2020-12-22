package com.lexor.service.payment;

import com.intuit.ipp.data.*;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.services.QueryResult;
import com.intuit.ipp.util.DateUtils;
import com.lexor.service.IQBOService;
import com.lexor.service.invoice.IInvoiceQBOService;
import com.lexor.service.invoice.InvoiceQBOServiceImp;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author auphan
 */


public class PaymentQBOServiceImp implements IPaymentQBOService, IQBOService {


    @Override
    public Payment createPaymentQBO(String idOrder,Double amount ,DataService dataService) throws FMSException {
        Payment payment = new Payment();
        try {
            payment.setTxnDate(DateUtils.getCurrentDateTime());
        } catch (ParseException e) {
            throw new FMSException("ParseException while getting current date.");
        }

        IInvoiceQBOService invoiceQBOService = new InvoiceQBOServiceImp();
        Invoice invoice = invoiceQBOService.findInvoiceByDocNumber(idOrder,dataService) != null ?
                invoiceQBOService.findInvoiceByDocNumber(idOrder,dataService):
                new Invoice();
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
        line1.setAmount(new BigDecimal("11.00"));
        line1.setDetailType(LineDetailTypeEnum.ACCOUNT_BASED_EXPENSE_LINE_DETAIL);

        ReferenceType taxCodeRef = new ReferenceType();
        taxCodeRef.setValue("NON");

        line1.setLinkedTxn(linkedTxnList);

        Line line2 = new Line();
        line2.setAmount(new BigDecimal("22.00"));
        line2.setDetailType(LineDetailTypeEnum.ACCOUNT_BASED_EXPENSE_LINE_DETAIL);

        line2.setLinkedTxn(linkedTxnList);

        List<Line> lineList = new ArrayList<Line>();
        lineList.add(line1);
        lineList.add(line2);
        payment.setLine(lineList);

        payment.setCustomerRef(invoice.getCustomerRef());

        CreditCardPayment creditCardPayment=new CreditCardPayment();

        CreditChargeInfo creditChargeInfo=new CreditChargeInfo();
        creditChargeInfo.setNameOnAcct("osssiuyyee");
        creditChargeInfo.setNumber("5111005111051128");
        creditChargeInfo.setType("Commercial Credit");
        creditChargeInfo.setCcExpiryMonth(12);
        creditChargeInfo.setCcExpiryYear(2020);
        creditChargeInfo.setAmount(BigDecimal.valueOf(3000));
        creditCardPayment.setCreditChargeInfo(creditChargeInfo);


        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setName("Credit card");
        paymentMethod.setType(PaymentTypeEnum.CREDIT_CARD.name());
        PaymentMethod paymentMethod1 = dataService.add(paymentMethod);


        ReferenceType paymentMethodRef = new ReferenceType();
        paymentMethodRef.setValue(paymentMethod1.getId());

        payment.setPaymentMethodRef(paymentMethodRef);

        payment.setCreditCardPayment(creditCardPayment);
        payment.setPaymentType(PaymentTypeEnum.CREDIT_CARD);
        payment.setTotalAmt(BigDecimal.valueOf(amount));

        return dataService.add(payment);
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
