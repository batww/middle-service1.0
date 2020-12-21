package com.lexor.service;

import com.intuit.ipp.services.WebhooksService;
import com.intuit.ipp.util.Config;
import com.intuit.ipp.util.Logger;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class SecurityService {

    private static final org.slf4j.Logger LOG = Logger.getLogger();

    private static SecretKeySpec secretKey;

    private static volatile SecurityService securityService;
    private static final Object APP_LOCK = new Object();

    public static SecurityService getInstance() {

        SecurityService result = securityService;
        if (result == null) {
            synchronized(APP_LOCK) {
                result = securityService;
                if (result == null) {
                    securityService = result = new SecurityService();
                }
            }
        }
        return result;
    }


    /**
     * Validates the payload with the intuit-signature hash
     *
     * @param signature
     * @param payload
     * @return
     */

    public boolean isRequestValid(String signature, String payload,String verifierKey, String encryKey) {
        LOG.info("Inner validate payload webhook");
        secretKey = new SecretKeySpec(encryKey.getBytes(StandardCharsets.UTF_8), "AES");
        // set custom config
        Config.setProperty(Config.WEBHOOKS_VERIFIER_TOKEN,verifierKey);

        // create webhooks service
        WebhooksService service = new WebhooksService();
        return service.verifyPayload(signature, payload);
    }
}
