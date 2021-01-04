package com.lexor.controller;


import com.intuit.ipp.data.*;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.util.StringUtils;
import com.lexor.config.ConfigProperties;
import com.lexor.config.QBODataService;
import com.lexor.model.CreditCardData;
import com.lexor.model.OrderQuickBook;
import com.lexor.model.Product;
import com.lexor.model.PurchaseOrderDto;
import com.lexor.payload.PayloadPayment;
import com.lexor.service.IQBOService;
import com.lexor.service.SecurityService;
import com.lexor.service.estimate.EstimateServiceQBOImp;
import com.lexor.service.item.ItemQBOService;

import com.lexor.service.payment.IPaymentQBOService;
import com.lexor.service.payment.PaymentQBOServiceImp;
import com.lexor.service.purchaseorder.PurchaseOrderQBOServiceImp;
import com.lexor.service.queue.QueueService;
import com.lexor.service.until.*;
import org.slf4j.LoggerFactory;


import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;

import java.text.ParseException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * @author auphan
 */

@Path("/quickbook")
public class QuickBookController {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(QuickBookController.class);

    static final String SIGNATURE = "intuit-signature";
    private static final String SUCCESS = "Success";
    private static final String ERROR = "Error";

    @GET
    @Path("/test")
    public String test() throws FMSException, IOException {
//      PaymentQBOServiceImp service = new PaymentQBOServiceImp();
//      service.createPaymentQBO("30", 1000.0,QBODataService.initConfigQuickBook());

        return "..";
    }

    @POST
    @Path("payment")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response payment(PayloadPayment info) throws FMSException, IOException {

        LOG.info("call payment api");
        PaymentQBOServiceImp paymentQBOServiceImp = new PaymentQBOServiceImp();
        String urlResponse = paymentQBOServiceImp.handleRedirectPayment(info,QBODataService.initConfigQuickBook());

        return !urlResponse.equals("") ? Response.status(Response.Status.OK).entity(urlResponse).build() :
                Response.status(Response.Status.NOT_FOUND).entity("").build();
    }


    @GET
    @Path("/payment-credit")
    public Response paymentCredit() throws FMSException, IOException {

       // CreditCardData cardData = (CreditCardData) convertData(creditCardData,new ProxyCreditCard());

        IPaymentQBOService service = new PaymentQBOServiceImp();
        CreditCardData creditCardData1 = new CreditCardData();
        creditCardData1.setIdOrder(30);
        creditCardData1.setCardNumber("5111005111051128");
        creditCardData1.setExpMonth(12);
        creditCardData1.setExpYear(2020);
        creditCardData1.setAmount(300);
        Payment payment =  service.createPaymentQBO(creditCardData1,QBODataService.initConfigQuickBook());
        return payment != null ?
                Response.status(Response.Status.OK).entity(SUCCESS).build() :
                Response.status(Response.Status.FORBIDDEN).entity(ERROR).build();
    }

    @PUT
    @Path("product")
    public Response updateProduct(@FormParam("product") String product) throws ExecutionException, InterruptedException {
        Product product1 = (Product) convertData(product,new ProxyProduct());
        ItemQBOService service = new ItemQBOService();
        return service.updateItemAsync(product1) ? Response.status(Response.Status.OK).entity(SUCCESS).build() :
                Response.status(Response.Status.FORBIDDEN).entity(ERROR).build();
    }


    @Path("product")
    @POST
    @PermitAll
    public Response sendProductToQuickBooks(@FormParam("product")String product) throws ExecutionException, InterruptedException, ParseException, IOException, FMSException {
        Product product1 = (Product) convertData(product, new ProxyProduct());

        ItemQBOService  itemQBOService = new ItemQBOService();

        return itemQBOService.createEntityQBO(product1) != null ? Response.status(Response.Status.OK).entity(SUCCESS).build() :
                Response.status(Response.Status.FORBIDDEN).entity(ERROR).build();
    }


    // convert from json to object
    private Object convertData(String param, IProxy proxy){
        return proxy.readJsonParam(param);
    }


    @Path("purchaseOrder")
    @POST
    @PermitAll
    public Response createPO(@FormParam("purchaseOrder")String purchase) throws ExecutionException, InterruptedException {
        PurchaseOrderDto purchaseOrderDto = (PurchaseOrderDto) convertData(purchase,new ProxyPurchaseOrder());
        IQBOService service = new PurchaseOrderQBOServiceImp();
        return service.sendAsynQBO(purchaseOrderDto) ? Response.status(Response.Status.OK).entity(SUCCESS).build() :
                Response.status(Response.Status.FORBIDDEN).entity(ERROR).build();
    }

    @PUT
    @Path("purchaseOrder")
    public Response updatePO(@FormParam("purchaseOrder") String purchase) throws FMSException, IOException {
        PurchaseOrderDto purchaseOrderDto = (PurchaseOrderDto) convertData(purchase,new ProxyPurchaseOrder());
        IQBOService service = new PurchaseOrderQBOServiceImp();
        service.updateEntityQBO(purchaseOrderDto);
        return Response.status(Response.Status.OK).build();
    }


    @Path("order-sales")
    @POST
    @PermitAll
    public Response sendOrderToQuickBook(@FormParam("order")String order) throws ExecutionException, InterruptedException {

        OrderQuickBook orderQuickBook = (OrderQuickBook) convertData(order, new ProxyOrder());

        IQBOService estimateServiceQBO = new EstimateServiceQBOImp();
        boolean estimate = estimateServiceQBO.sendAsynQBO(orderQuickBook);
        if(estimate)
            return Response.status(Response.Status.CREATED).entity(SUCCESS).build();

        return null;
    }


    @PermitAll
    @Path("/webhooks")
    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response webhooks(@HeaderParam(SIGNATURE) String signature, String payload) throws IOException {

        Properties config = ConfigProperties.loadConfig();

        if (!StringUtils.hasText(signature)) {
            return Response.status(Response.Status.FORBIDDEN).entity(ERROR).build();
        }
        // if payload is empty, don't do anything
        if (!StringUtils.hasText(payload)) {
            return Response.status(Response.Status.FORBIDDEN).entity(ERROR).build();
        }

        final String verifierToken = config.getProperty("webhooks.verifier.token");
        final String encrytionKey =config.getProperty("encryption.key");

        boolean resultValid = SecurityService.getInstance().isRequestValid(signature, payload,verifierToken,encrytionKey);

        if (resultValid) {
            QueueService queueService = new QueueService();
            queueService.add(payload);
            return Response.status(Response.Status.OK).entity(SUCCESS).build();

        } else {
            return Response.status(Response.Status.FORBIDDEN).entity(ERROR).build();
        }
    }

}
