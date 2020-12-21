package com.lexor.config;

import com.intuit.ipp.core.Context;
import com.intuit.ipp.core.ServiceType;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.security.OAuth2Authorizer;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.util.Config;

import java.io.IOException;

public class QBODataService {

    public static DataService initConfigQuickBook() throws FMSException, IOException {

        Config.setProperty(Config.BASE_URL_QBO,ConfigProperties.loadConfig().getProperty("baseURL.qbo"));

        //create oauth object
        OAuth2Authorizer oauth = new OAuth2Authorizer(ConfigProperties.loadConfig().getProperty("oauth2.accessToken"));
        //create context
        Context context = new Context(oauth, ServiceType.QBO, ConfigProperties.loadConfig().getProperty("company.id"));

        return new DataService(context);
    }
}
