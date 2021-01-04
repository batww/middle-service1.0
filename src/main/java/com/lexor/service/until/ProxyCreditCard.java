package com.lexor.service.until;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.lexor.model.CreditCardData;


import java.io.StringReader;
import java.util.logging.Logger;

public class ProxyCreditCard implements IProxy{
    private static final Logger LOG = Logger.getLogger (ProxyCreditCard.class.getName());

    @Override
    public Object readJsonParam(String param) {
        Gson gson = new Gson();
        JsonReader jsonReader = new JsonReader(new StringReader(param));
        LOG.info("Convert to Credit Card");
        return gson.fromJson(jsonReader, CreditCardData.class);
    }
}
