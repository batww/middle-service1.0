package com.lexor.controller;

import com.lexor.config.ConfigProperties;
import com.lexor.service.KafkaServiceImp;


import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.*;

@Path("/")
public class OrderController {

    private static final String SUCCESS = "Success";
    private static final String ERROR = "Error";

    @POST
    @Path("order")
    public Response sendOrderToKafka(@FormParam("order")String order) throws IOException {
        Properties config = ConfigProperties.loadConfig();

        final String topic = config.getProperty("topic");

        return KafkaServiceImp.instance.sendKafkaMessage(topic,"order",order) ?
                Response.status(Response.Status.OK).entity(SUCCESS).build():
                Response.status(Response.Status.FORBIDDEN).entity(ERROR).build();
    }

    @GET
    @Produces("text/plain")
    public String getClichedMessage() {
        return "Hello, World!";
    }
}
