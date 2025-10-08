package com.fluig.controller;


import com.fluig.model.GenericModel;
import com.google.gson.Gson;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Base64;


@Path("service")
public class Base64Controller {

    @POST
    @Path("execute/encode")
    @Produces(MediaType.APPLICATION_JSON)
    
    public Response execute(String jsonValues) throws IOException {
        if (jsonValues == null || jsonValues.isEmpty()) {
    return Response.status(Response.Status.BAD_REQUEST).entity("JSON nulo ou vazio").build();
    }

        GenericModel requestMessage;

        try {
    requestMessage = new Gson().fromJson(jsonValues, GenericModel.class);
} catch (Exception e) {
return Response.status(Response.Status.BAD_REQUEST).entity("Não foi possível criar o Objeto a partir do JSON").build();
}

        String encoded = Base64.getEncoder().encodeToString(requestMessage.getString().getBytes());

        return Response.status(Response.Status.OK).entity(new Gson().toJson(encoded)).build();
    }
}
