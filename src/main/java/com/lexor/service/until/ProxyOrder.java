package com.lexor.service.until;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.lexor.model.OrderQuickBook;
import com.lexor.service.queue.QueueProcessor;

import java.io.StringReader;
import java.util.logging.Logger;

/**
 * @author auphan
 */


public class ProxyOrder implements IProxy{
    private static final String DATE_PARTTERN = "yyyy-MM-dd HH:mm:ss.S";
    private static final Logger LOG = Logger.getLogger (QueueProcessor.class.getName());

    @Override
    public Object readJsonParam(String param) {

        Gson gson = new GsonBuilder().setDateFormat(DATE_PARTTERN).create();
        JsonReader jsonReader = new JsonReader(new StringReader(param));
        LOG.info("Convert to Order");
        return gson.fromJson(jsonReader, OrderQuickBook.class);
    }
}
