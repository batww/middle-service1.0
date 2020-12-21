import com.intuit.ipp.core.Context;
import com.intuit.ipp.core.ServiceType;
import com.intuit.ipp.data.Vendor;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.security.OAuth2Authorizer;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.util.Config;
import com.lexor.model.PurchaseOrderDto;
import com.lexor.service.IQBOService;
import com.lexor.service.item.ItemQBOService;
import com.lexor.service.purchaseorder.PurchaseOrderQBOServiceImp;
import com.lexor.service.until.IProxy;
import com.lexor.service.until.ProxyPurchaseOrder;
import com.lexor.service.vendor.VendorQBOService;
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
//        OAuth2Authorizer oauth = new OAuth2Authorizer("eyJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..sUKUeOjiGv7WuF0l-0FzLA.uw6Nh0kT3OBCiF6fUxRK7YD45iPeYVqXR5Av4slW_FZxYDLcC5nyeItlBFW1KiF_V7UlTIGgJWexlrpX2HbK2FWaxkl3qzvx079C5Zbve_KufzYKa5DPzKvx0I_taXMePpN1X1ZEx_WGuW1fdLj23fW-nuet1gDnRz22OpollpBqqvVL5Zp_xNW4oMe42AngRIqVcaNdhsIvmwdBMI9fnGqSIfhHUl5PrkwpAR6aLChV5KSr2ZaWQtmGCXICLOWnE8az1GwiHMtUK4_GriEa8m7mJs0pTENJzMf8kack-YcHY72tm4eCria8j4ayhqO8TOppvcgWu382ucBnCjnuOxfmKmsCLXq2ITMD8jw5j8D3zuB7mnTZazUI7kJAfNyIT-N8IuJGB4UB17zoWMmx2LinJp4vcyn0sEzu7dfzHnT7y21FBumQtrJi0ZBB0-0NjkUWJ-QkykUnsr0Y4Dl4KShLN6ulbAfb8hkqQYqZ2Kkk00YCDRqzEoiI7HBQQzS9RQLiRLd5ciHUw2J-pw3b9j8dXJwa4IqGVFsoOL1-8F3v3_zYW7KH8PX0O0jMBRmskzcPaoDZ0bj5V7zYK0efrxSmiRgfikNAzWNsiGzgAyY6CDZ7ivAPI2lTv5kCB0a7avCAdbveyrZFUPnTT9FxEjQ2QW04MgaiUjxzaoZ04-iCES142QM7nzf9iVblsti414FzTBnobQEScR7EIYP6jzcrWQvPG_VZqPGIPOMXUh6uoOyDwz06zTPQuX3Y9irL8duJF1ZLUz1pJo-ISRZvTzJ-WhwtsAU7Q3Jicr2-HmZ8J5tzJl40ABWG_1_Ecwo_pWXqj3yYqApBX_upn3HrqgfTb8QkQ9NZGeghRtChGdn287nsbGUDxoPR7lr8RrbOX9JBCgKJlBXSxj49do0dKw.B2B2AVmI6pg_Otb5W3fOvw");
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
//    }
}
