package com.fluig.controller;

import com.fluig.model.GenericModel;
import com.fluig.service.CheckTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.Objects;

@Path("service")
public class CheckTokenController {

    private final Gson gson = new Gson();

    @POST
    @Path("execute/checklogin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response execute(String jsonValues) throws IOException {

        GenericModel requestMessage = parseJsonToGenericModel(jsonValues);
        assert requestMessage != null;
        if (!isValidRequestMessage(requestMessage)) {
            return buildErrorResponse(Response.Status.BAD_REQUEST, "Não foi possível criar o Objeto a partir do JSON");
        }

        return new CheckTokenService().executeService(requestMessage);
    }

    private GenericModel parseJsonToGenericModel(String jsonValues) {
        try {
            return gson.fromJson(jsonValues, GenericModel.class);
        } catch (JsonSyntaxException | IllegalArgumentException | UnsupportedOperationException | ConcurrentModificationException e) {
            return null;
        }
    }

    private boolean isValidRequestMessage(GenericModel requestMessage) {
        return requestMessage.getString() != null && !requestMessage.getString().isEmpty() && !Objects.equals(requestMessage.getString(), "");
    }

    private Response buildErrorResponse(Response.Status status, String message) {
        return Response.status(status).entity(message).build();
    }
}