package com.fluig.controller;

import com.fluig.model.CoordenatesModel;
import com.fluig.model.ResponseGeneralWithDataModel;
import com.fluig.service.CoordinatesService;
import com.google.gson.Gson;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("service")
public class CoordinatesController {
    @POST
    @Path("execute/coordenadas")
    @Produces(MediaType.APPLICATION_JSON)
    public Response execute(String jsonValues) throws IOException {


        if (jsonValues == null || jsonValues.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("JSON nulo ou vazio").build();
        }

        CoordenatesModel requestMessage;
        ResponseGeneralWithDataModel response;
        try {
            requestMessage = new Gson().fromJson(jsonValues, CoordenatesModel.class);
            response = new CoordinatesService().executeService(requestMessage);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Não foi possível criar o Objeto a partir do JSON").build();
        }


        return Response.status(Response.Status.OK).entity(new Gson().toJson(response)).build();
    }
}