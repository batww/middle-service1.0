package com.lexor.controller;


import javax.annotation.security.PermitAll;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;


public class ProductController {

    private static final String SUCCESS = "Success";
    private static final String ERROR = "Error";

    @POST
    @Path("product")
    @PermitAll
    public Response sendProductToQuickBooks(@FormParam("product") String product){

        return Response.status(Response.Status.OK).entity(SUCCESS).build();

    }
}
