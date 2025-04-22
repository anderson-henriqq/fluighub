package com.fluig.controller;

import com.fluig.model.DistanceRequestModel;
import com.fluig.model.ResponseGeneralWithDataModel;
import com.fluig.service.DistanceService;
import com.google.gson.Gson;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("service")

public class DistanceController {
    @POST
    @Path("execute/distancia")
    @Produces(MediaType.APPLICATION_JSON)
    public Response execute(String jsonValues) throws IOException {


        if (jsonValues == null || jsonValues.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("JSON nulo ou vazio").build();
        }

        DistanceRequestModel requestMessage;
        ResponseGeneralWithDataModel response;
        try {
            requestMessage = new Gson().fromJson(jsonValues, DistanceRequestModel.class);
            response = new DistanceService().executeService(requestMessage);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Não foi possível criar o Objeto a partir do JSON").build();
        }


        return Response.status(Response.Status.OK).entity(new Gson().toJson(response)).build();
    }
}