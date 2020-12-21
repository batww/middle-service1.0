package com.lexor.service.until;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.lexor.model.PurchaseOrderDto;
import com.lexor.service.queue.QueueProcessor;

import java.io.StringReader;
import java.util.logging.Logger;

public class ProxyPurchaseOrder implements IProxy{
    private static final Logger LOG = Logger.getLogger (QueueProcessor.class.getName());

    @Override
    public Object readJsonParam(String param) {
        Gson gson = new Gson();
        JsonReader jsonReader = new JsonReader(new StringReader(param));
        LOG.info("Convert to PO");
        return gson.fromJson(jsonReader, PurchaseOrderDto.class);
    }
}
