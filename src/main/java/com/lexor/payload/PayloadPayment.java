package com.lexor.payload;


public class PayloadPayment {
    private int idCustomer = 0;
    private int idInvoice = 0;

    public PayloadPayment(int idCustomer, int idInvoice) {
        this.idCustomer = idCustomer;
        this.idInvoice = idInvoice;
    }

    public PayloadPayment() {
    }

    public int getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(int idCustomer) {
        this.idCustomer = idCustomer;
    }

    public int getIdInvoice() {
        return idInvoice;
    }

    public void setIdInvoice(int idInvoice) {
        this.idInvoice = idInvoice;
    }
}
