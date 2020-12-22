package com.lexor.controller;


import com.intuit.ipp.data.*;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.services.QueryResult;
import com.intuit.ipp.util.StringUtils;
import com.lexor.config.ConfigProperties;
import com.lexor.config.QBODataService;
import com.lexor.model.OrderQuickBook;
import com.lexor.model.Product;
import com.lexor.model.PurchaseOrderDto;
import com.lexor.service.IQBOService;
import com.lexor.service.SecurityService;
import com.lexor.service.estimate.EstimateServiceQBOImp;
import com.lexor.service.item.ItemQBOService;

import com.lexor.service.purchaseorder.PurchaseOrderQBOServiceImp;
import com.lexor.service.queue.QueueService;
import com.lexor.service.until.IProxy;
import com.lexor.service.until.ProxyOrder;
import com.lexor.service.until.ProxyProduct;
import com.lexor.service.until.ProxyPurchaseOrder;


import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * @author auphan
 */



@Path("/quickbook")
public class QuickBookController {

    static final String SIGNATURE = "intuit-signature";
    private static final String SUCCESS = "Success";
    private static final String ERROR = "Error";

    @GET
    @Path("/test")
    public String test() throws FMSException, IOException {
        DataService dataService =QBODataService.initConfigQuickBook();
        String sql = "select * from payment";
        QueryResult queryResult = dataService.executeQuery(sql);
        List<Payment> payments =(List<Payment>) queryResult.getEntities();

        System.out.println(payments.get(0).getPaymentMethodRef().getValue());

        return "..";
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
    public Response sendProductToQuickBooks(@FormParam("product")String product) throws ExecutionException, InterruptedException {
        Product product1 = (Product) convertData(product, new ProxyProduct());

        ItemQBOService  itemQBOService = new ItemQBOService();

        return itemQBOService.createItemAsync(product1) ? Response.status(Response.Status.OK).entity(SUCCESS).build() :
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
