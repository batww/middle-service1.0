import com.intuit.ipp.core.Context;
import com.intuit.ipp.core.ServiceType;
import com.intuit.ipp.data.Customer;
import com.intuit.ipp.data.Invoice;
import com.intuit.ipp.data.Vendor;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.security.OAuth2Authorizer;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.services.QueryResult;
import com.intuit.ipp.util.Config;
import com.lexor.config.QBODataService;
import com.lexor.model.OrderQuickBook;
import com.lexor.model.PurchaseOrderDto;
import com.lexor.payload.PayloadPayment;
import com.lexor.service.IQBOService;
import com.lexor.service.customer.CustomerQBOService;
import com.lexor.service.invoice.InvoiceQBOServiceImp;
import com.lexor.service.item.ItemQBOService;
import com.lexor.service.payment.PaymentQBOServiceImp;
import com.lexor.service.purchaseorder.PurchaseOrderQBOServiceImp;
import com.lexor.service.until.IProxy;
import com.lexor.service.until.ProxyOrder;
import com.lexor.service.until.ProxyPurchaseOrder;
import com.lexor.service.until.QBRequest;
import com.lexor.service.vendor.VendorQBOService;
import org.apache.http.HttpResponse;
import org.codehaus.jettison.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.junit.Assert.*;

public class QBOTest {

//    DataService dataService = null;
//
//    @Before
//    public void setUp() throws FMSException {
//        Config.setProperty(Config.BASE_URL_QBO, "https://sandbox-quickbooks.api.intuit.com/v3/company");
//
//        //create oauth object
//        OAuth2Authorizer oauth = new OAuth2Authorizer(
//                "eyJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..0NFQt3kGrcH2lfLJevnlKQ.gaRl5Bj8g95cRB2dCLCJgVgGUDXwgLcWmtiRV6uPRqI4O2ueE1H7-I-UyrzAbcceL6hVSr_Dwpg0VzEBJvDJ6WTw3anX8N19t3EvyjnKvUbfsY45LW6q3olPESDKck4jc6H_DbOrdxIYpP_s3E9n4V2Rsz_TgY5BSeZhE9v7MI5nbO1ssuZ-vWhQNVNVFBhUBoRj1FbTxvFy0W_Hd76qLP0XkshxdGHzbnq9uWQyeJN-4pwhEj4HH3jtrXLb96R1GYytTZh-0vArjTt3flraF-ha4BaatCWerNZtv7M15r1n4HU105Dwrgk2I7Mt90VivYa734m32z63cpmtJKx6e1KYdiCDIUM3XX6rXZuUA3ljxIwDcCbIDGqs3YKI0WrUL-XJABB68iK9G5OUxOwcvv43uTFJaRJm2vn0iTEO0WQKXzMtxnBdvvZOv4ed2dNYqNDxVxmGtuDZOE1A36eqWDVVuV-okhTshir05mBl_gIgDjKP3B1yM56AkXoWhG-RMFoHrr3Ozwi9R08A5QQ24MTj68GawdRloBHy3kLbO8RGugI2VNMwMc6Q--kg8RMPTtw4Cdmc1Aw9S8InMDntQyOI6oo3n12LovKpMUQ6jFVdlwG-3dqRN3b33YsOlW0MhNVixoiPQYm82OgxFzdqHcW50EK4ghJJAUTD3GCM_7aIHYjdi7GnzB8gw6aKqztgwLSkUhaHi5sALE4YWi4As2LR4rARLLgRJ3BOGMrJUfSRx0iNebXtiQAyfpDPvMo6GL-FsGTwTolLuVg9X9Bqzj_O5M7c0ylp38wCIwh3S88H40CpPVSgSHzsnttMZAkR_enYm6Jgud6nOJX_-NSRPWQclpvXNeJ2pM1LIdPpgM4BCiJ2Oel_EpuJNlIKiF0NtW2rzjX3hmsFcpd88MiJXA.syuf9os7GkspBLaXy9CO7w"
//        );
//        //create context
//        Context context = new Context(oauth, ServiceType.QBO, "4620816365153243980");
//
//         dataService= new DataService(context);
//    }
//
//    @Test
//    public void getVendorIs() throws FMSException, IOException {
//        VendorQBOService vendorQBOService = new VendorQBOService();
//        assertEquals((Vendor)vendorQBOService.isEntityActive("Lac a1212",dataService), Vendor.class);
//    }
//    @Test
//    public void getVendor() throws FMSException {
//        Config.setProperty(Config.BASE_URL_QBO, "https://sandbox-quickbooks.api.intuit.com/v3/company");
//
//        //create oauth object
//        OAuth2Authorizer oauth = new OAuth2Authorizer("eyJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..faaFYm2ViROeYbj5bSsZZw.hFC2MlV0wV_NKWi0Krb1TIHm4DBrROlCN4JGoKDHh4yS-2b71ymfrZc_aHd_MIA-YZ3LNLNy3pcWCZ6DHLzhT1DRhi4wb4SkAovVdiWTPHp6FyRRvQDTlh9IJAtCj4l4ikSKAf3fbqhzwxmg90y_6hG14L73tV9ZW0C5qcNPlsdNJYK57gNqw0sYOImkE583ezwQnrJbkY6gw5WJAilq7X3Fwb8ffI398FcAYd2gwGext7lUii2z9lN1AYxV_jlUYb367nXUiNS1h06Xlw_t3lDsrUnagSNH4--3ePFqhJEv5N1JlN3Yn1rtNWXh9YOyf5Y569ovnlQjGTfo7U-G92iMDBld_u9CB3ApP9zphOfJ7R_2M62InzUhUokQ60CduWEOtamzkdPEeUqKi8lXGZJt9YtGqvYMD-Oe4qCElJBp_C196T8tIctDfKb-Do6jZBPUHnb3kIuRoBRegOeM9fPZTYda9z5TWz6Pv4DTgVZmq7x9_n2rFxR5--h_4gTFDdS5jalaCaJwVa8Q3BVY2IHCLUK1D1qocKo9HLxMLmD6CQv9g-an07LyA3k3_1Cz2dEkucmCD8FWEO_LwXZH4qZqh7KHcMOYv1JDF-EFznnIUoP4EGPaAk66K44Fv-gi1YemOHyYx9mge9rjvygjyyTGxr2vOty4OdpyAnXoghPSS4QStNz1jeMt-AEEv4W7tvL4R50mGLubNE0wRr-0Gt7PhfruELIn6kzPNSn5GVVFHmUglbfo-EsGbwzunLGDRgGVsVyUWSv3anTaGA3xKsH-vt4Z_MBj7KSC6WUts7YYwjhKCw2mkS0h2A-51-q4Q39ndHT3Qjyq8EJYnU_PABIoXZukVTKvZ7RpVglv5nJbxTlC_Z7n3Wy6jwVgvsfBqHUx7hhoTgKt1s-gGgABMw.boG9tYgvjqRWSl7vLL4GFg");
//        //create context
//        Context context = new Context(oauth, ServiceType.QBO, "4620816365153243980");
//
//        assertNotNull(context);
//
//    }
//
//    @Test
//    public void createPO() throws FMSException, ParseException {
//        PurchaseOrderQBOServiceImp purchaseOrderQBOServiceImp =new PurchaseOrderQBOServiceImp();
//        IProxy convertFunc = new ProxyPurchaseOrder();
//        PurchaseOrderDto purchaseOrderDto = (PurchaseOrderDto) convertFunc.readJsonParam("{\"approvedDate\": \"12/17/2020\",\n" +
//                "\"approver\": \"dieu thuyen\",\n" +
//                "\"createdDate\": \"12/17/2020\",\n" +
//                "\"description\": \"\",\n" +
//                "\"idPurchaseOrder\": 13,\n" +
//                "\"idPurchaseOrderStatus\": 3,\n" +
//                "\"issuedBy\": \"PR\",\n" +
//                "\"purchaseOrderDetails\": [\n" +
//                "{\n" +
//                "\"amount\": 80000.0,\n" +
//                "\"idProduct\": 41212426,\n" +
//                "\"image\": \"\",\n" +
//                "\"name\": \"Elite/Prestige Versatile Footrest 18.0\",\n" +
//                "\"price\": 10000.0,\n" +
//                "\"quantity\":4\n" +
//                "},\n" +
//                "{\n" +
//                "\"amount\": 80000.0,\n" +
//                "\"idProduct\": 421263,\n" +
//                "\"image\": \"\",\n" +
//                "\"name\": \"Elite/Prestige Versatile Footrest 18.0\",\n" +
//                "\"price\": 10000.0,\n" +
//                "\"quantity\":4\n" +
//                "},\n" +
//                "{\n" +
//                "\"amount\": 60000.0,\n" +
//                "\"idProduct\": 18332,\n" +
//                "\"image\": \"\",\n" +
//                "\"name\": \"Elite Arm Cushion Set 19.0 \",\n" +
//                "\"price\": 15000.0,\n" +
//                "\"quantity\": 4\n" +
//                "}\n" +
//                "],\n" +
//                "\"references\": [\n" +
//                "{\n" +
//                "\"idPurchaseRequisition\": 1\n" +
//                "}\n" +
//                "],\n" +
//                "\"shipTo\": \"CA\",\n" +
//                "\"shipVia\": \"Super Cargo Service\",\n" +
//                "\"shippingAddress\": \"7400 W Hazard Ave, Westminster CA 92683\",\n" +
//                "\"status\": \"Shipped\",\n" +
//                "\"totalAmount\": 140000.0,\n" +
//                "\"vendor\": \"Lac Long\"}");
//
//        assertNotNull(purchaseOrderQBOServiceImp.sendAsynQBO(purchaseOrderDto,dataService));
//       // assertNotNull(purchaseOrderQBOServiceImp.createEntityQBO(purchaseOrderDto,dataService));
//       // assertNotNull(purchaseOrderQBOServiceImp.createEntityQBO(purchaseOrderDto,dataService));
//
//    }
//
//    @Test
//    public void getItem() throws FMSException {
//        IQBOService service = new ItemQBOService();
//
//         assertEquals("128",service.isEntityActive("182",dataService).getId());
//    }'

//    @Test
//    public void findCustomer() throws FMSException, IOException {
//        CustomerQBOService customerQBOService = new CustomerQBOService();
//
//        Customer customer = (Customer) customerQBOService.findCustomerByNote("303", dataService);
//        System.out.println(customer.getDisplayName());
//        Assert.assertNotNull(customer);
//    }
//
//    @Test
//    public void genericUrl() throws FMSException, IOException {
//        PayloadPayment payloadPayment = new PayloadPayment();
//        payloadPayment.setIdCustomer(303);
//        payloadPayment.setIdInvoice(1315);
//        PaymentQBOServiceImp paymentQBOServiceImp = new PaymentQBOServiceImp();
//
//        InvoiceQBOServiceImp invoiceQBOServiceImp = new InvoiceQBOServiceImp();
//        Invoice invoice = (Invoice) invoiceQBOServiceImp.isEntityActive(String.valueOf(payloadPayment.getIdInvoice()),dataService);
//
//
//        assertEquals("https://app.sandbox.qbo.intuit.com/app/recvpayment?srcTxnId=1315&nameId=03",paymentQBOServiceImp.handleRedirectPayment(payloadPayment,dataService));
//    }
//
//
//    @Test
//    public void createES(){
//        IProxy iProxy = new ProxyOrder();
//        OrderQuickBook orderQuickBook  = (OrderQuickBook) iProxy.readJsonParam("{\"orderInfo\":{\"idCustomer\":0,\"company\":\"abc\",\"firstName\":\"first\",\"lastName\":\"last\",\"sex\":true,\"phone\":\"1234 56789\",\"cellPhone\":\"1234 56789\",\"email\":\"nguyenxuananhtri@gmail.com\",\"address\":\"shipping\",\"addressSelected\":\"District 3, HCMC\",\"city\":\"\",\"companyShip\":\"abc\",\"firstNameShip\":\"first\",\"lastNameShip\":\"last\",\"addressShip\":\"shipping\",\"addressShipSelected\":\"District 3, HCMC\",\"postalCodeShip\":\"0\",\"cityShipping\":\"\",\"idOrderSource\":4,\"idOrderType\":1,\"idOrderERP\":30,\"isPOX\":false},\"listOrderDetail\":[{\"idOrder\":53,\"idProduct\":1641,\"quantity\":1,\"idColor1\":277,\"idColor2\":0,\"price\":0,\"idProductParent\":1834,\"idPromotionOption\":0,\"idOrderDetailReact\":113},{\"idOrder\":53,\"idProduct\":1679,\"quantity\":1,\"idColor1\":260,\"idColor2\":0,\"price\":0,\"idProductParent\":1834,\"idPromotionOption\":0,\"idOrderDetailReact\":113},{\"idOrder\":53,\"idProduct\":1689,\"quantity\":1,\"idColor1\":277,\"idColor2\":0,\"price\":0,\"idProductParent\":1834,\"idPromotionOption\":0,\"idOrderDetailReact\":113},{\"idOrder\":53,\"idProduct\":1162,\"quantity\":1,\"idColor1\":0,\"idColor2\":0,\"price\":0,\"idProductParent\":1834,\"idPromotionOption\":0,\"idOrderDetailReact\":113},{\"idOrder\":53,\"idProduct\":1219,\"quantity\":1,\"idColor1\":0,\"idColor2\":0,\"price\":0,\"idProductParent\":1834,\"idPromotionOption\":0,\"idOrderDetailReact\":113},{\"idOrder\":53,\"idProduct\":20,\"quantity\":1,\"idColor1\":0,\"idColor2\":0,\"price\":0,\"idProductParent\":1834,\"idPromotionOption\":0,\"idOrderDetailReact\":113},{\"idOrder\":53,\"idProduct\":131,\"quantity\":1,\"idColor1\":0,\"idColor2\":0,\"price\":-300,\"idProductParent\":1834,\"idPromotionOption\":391,\"idOrderDetailReact\":113},{\"idOrder\":53,\"idProduct\":1834,\"quantity\":1,\"idColor1\":0,\"idColor2\":0,\"price\":2595,\"idProductParent\":0,\"idPromotionOption\":0,\"idOrderDetailReact\":113},{\"idOrder\":53,\"idProduct\":1641,\"quantity\":1,\"idColor1\":100,\"idColor2\":0,\"price\":0,\"idProductParent\":1835,\"idPromotionOption\":0,\"idOrderDetailReact\":108},{\"idOrder\":53,\"idProduct\":1744,\"quantity\":1,\"idColor1\":260,\"idColor2\":0,\"price\":0,\"idProductParent\":1835,\"idPromotionOption\":0,\"idOrderDetailReact\":108},{\"idOrder\":53,\"idProduct\":1689,\"quantity\":1,\"idColor1\":100,\"idColor2\":0,\"price\":0,\"idProductParent\":1835,\"idPromotionOption\":0,\"idOrderDetailReact\":108},{\"idOrder\":53,\"idProduct\":1835,\"quantity\":5,\"idColor1\":0,\"idColor2\":0,\"price\":2595,\"idProductParent\":0,\"idPromotionOption\":0,\"idOrderDetailReact\":108}],\"listScheduleShipping\":[{\"idOrder\":53,\"idShippingMethod\":1,\"idDeliveryMethod\":7,\"ShippingQuote\":0,\"requestedShip\":\"2020-12-17 00:00:00.0\",\"idLocation_Department\":17,\"distanceDelivery\":100,\"isInternational\":false,\"idSCheduleShippingStatus\":1,\"idScheduleShippingStatusBeforeRollBack\":1,\"isShowShippingQuoteDiscount\":true,\"isECONumber\":true,\"isSpecialWarehouse\":false,\"idECO\":63,\"listDetail\":[{\"idOrderDetailReact\":108,\"idShippingDeliveryMethod\":1},{\"idOrderDetailReact\":113,\"idShippingDeliveryMethod\":1}]}]})");
//        System.out.println(orderQuickBook.getListOrderDetail().size());
//    }


//    @Test
//    public void testCALLAPI() throws JSONException, IOException {
//        QBRequest qbRequest = new QBRequest();
//        HttpResponse o = (HttpResponse) qbRequest.callAPIGET("https://sandbox-quickbooks.api.intuit.com/v3/company/4620816365153243980/customer/303?minorversion=55",
//                "eyJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..hMv5jDvn-moockaqFCzBUw.ACbHeWdxP8Lxjy2BQGMIj1lVyidk3GKPCkmTEP7SNa6uMbzRRiIkqb_iklvQ5zlWOO4zjtNdrwCpizZDllJU3GGDpHZ3ZrFaxCHBXhwhgBJ3EgdqMSzUozpeRF4nn4ceURf-bNVXtNLN015UsXJEdVsE9aeHTHy-sPlFB28vrHIAWcGqwt3PmZd18n4teJQrWW8F8bUH4Z6COZy9iWibg2uWBaWReWfW_9pkPIX--cLfpJbm6Ihp5YCiynfXEC6-_lx-YKzhwn8HCUD0bnkGLnvZCKUNHdBidSh8SBO3Q5s-RQXnoS9otJjEAO0YCv61PDVmvrx_C1FDNB1lADcxGlWh5D_t-fkrMAVtK-BUGfpS0kdqizeScdm5aiEiKMZSz7SPRKDRg_OWWA4JDA4jWGs5iif79fuJkcECnqXEOtMb7nZJNkuHyUSvWe5G0nrEP99pC6ZZ3kPo59b9cHIsp7rNDrwnrI1UYrB3WkRUOLM1Bc_GcKh2NzjtB1lV8Y5CgmW1p3kB2W6KnK_itA9tdDC6GZc6SYzpp1NJTS2CqqoPs2ZyDm6oYCgAjNc8BepQWrokqZe-nJiTwsOFGOWS03mHyZlEvksXSrscjnrU6dZ5jxBTvi_ifmCp-CWeVet9v4WPaEHfW5rR_9y36w7dhO-Z6cugtqJ2dK3Nxc6AXTiCHFDSadFJPZJCU75VpejUaF3Paq0CwhLMzwerSJpmqvkcUK8hwca9vCWsmwv0ZI9n0Jg-vr4Hb-_XBU41xVV1C7hWDnorx8W7jYUqAwXx390xXdzhvV1o-kNV_astMuIgAgjCJc1WDuu6OXUDmA3UAksrn94Xn3cx6TqTE-R0E2q1bamjnPfS3x88bmGE2dSi_DzOX4l3U-HG6GfgDzZ4Sy8IJHwCbl_38I_78eietA.iVpZD6isH1srFZoCEfPYXQ");
//    }
}
