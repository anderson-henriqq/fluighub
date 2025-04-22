package com.fluig.controller;

import com.fluig.configuration.ConfigurationHub;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("service")
public class VersionController {

    @GET
    @Path("execute/version")
    @Produces(MediaType.APPLICATION_JSON)
    public Response execute() {
        ConfigurationHub config = ConfigurationHub.getInstance();
        final String FLUIG_NAME = config.getProperty("service.allow");
        final String DATE = config.getVersion();
        System.out.println("K31" + DATE);

        return Response.status(200).entity("{\"name\":\"" + FLUIG_NAME + "\",\"date\":\"" + DATE + "\"}").build();
    }


}
