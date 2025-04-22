package com.fluig.controller;

import com.fluig.model.GenericModel;
import com.fluig.model.ResponseGeneralModel;
import com.fluig.service.PageCountPDFService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ConcurrentModificationException;


@Path("service")
public class PageCountPDFController {

    private final Gson gson = new Gson();

    @POST
    @Path("execute/countpages")
    @Produces(MediaType.APPLICATION_JSON)
    public Response execute(String jsonValues) {

        if (isJsonValuesNullOrEmpty(jsonValues)) {
            return buildErrorResponse(Response.Status.BAD_REQUEST, "Não foi possível criar o Objeto a partir do JSON");
        }

        GenericModel requestMessage = parseJsonToGenericModel(jsonValues);
        if (requestMessage == null || !isValidRequestMessage(requestMessage)) {
            return buildErrorResponse(Response.Status.BAD_REQUEST, "Não foi possível criar o Objeto a partir do JSON");
        }

        ResponseGeneralModel responseMessage;
        try {
            responseMessage = new PageCountPDFService().executeService(requestMessage);
        } catch (Exception e) {
            e.printStackTrace();
            responseMessage = new ResponseGeneralModel(e.getMessage(), true, 500);
        }

        return Response.status(responseMessage.getCode()).entity(gson.toJson(responseMessage)).build();
    }

    private boolean isJsonValuesNullOrEmpty(String jsonValues) {
        return jsonValues == null || jsonValues.isEmpty();
    }

    private GenericModel parseJsonToGenericModel(String jsonValues) {
        try {
            return gson.fromJson(jsonValues, GenericModel.class);
        } catch (JsonSyntaxException | IllegalArgumentException | UnsupportedOperationException | ConcurrentModificationException e) {
            return null;
        }
    }

    private boolean isValidRequestMessage(GenericModel requestMessage) {
        return requestMessage.getString() != null && !requestMessage.getString().isEmpty();
    }

    private Response buildErrorResponse(Response.Status status, String message) {
        return Response.status(status).entity(message).build();
    }
}
