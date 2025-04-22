package com.fluig.controller;

import com.fluig.exception.NoEndpointFoundException;
import com.fluig.model.ResponseGeneralModel;
import com.fluig.service.DeleteAttachService;
import com.google.gson.Gson;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;

@Path("service")
public class DeleteAttachController {

    @POST
    @Path("execute/deleteattach")
    @Produces(MediaType.APPLICATION_JSON)
    public Response execute(String jsonValues) throws IOException, NoEndpointFoundException, URISyntaxException {

        ResponseGeneralModel responseMessage = new DeleteAttachService().executeService(jsonValues);

        return Response.status(responseMessage.getCode()).entity(new Gson().toJson(responseMessage)).build();
    }

}