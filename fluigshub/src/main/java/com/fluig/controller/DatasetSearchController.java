package com.fluig.controller;

import com.fluig.model.DatasetSearchModel;
import com.fluig.model.ResponseGeneralModel;
import com.fluig.service.DataSearchService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ConcurrentModificationException;

@Path("service")
public class DatasetSearchController {

    private final Gson gson = new Gson();
    private final DataSearchService dataSearchService = new DataSearchService();

    @POST
    @Path("execute/datasearch")
    @Produces(MediaType.APPLICATION_JSON)
    public Response execute(String jsonValues) {

        DatasetSearchModel requestMessage = parseJsonToDatasetSearchModel(jsonValues);
        if (requestMessage == null || !isValidRequestMessage(requestMessage)) {
            return buildErrorResponse(Response.Status.BAD_REQUEST, "Não foi possível criar o Objeto a partir do JSON");
        }

        ResponseGeneralModel responseMessage;
        try {
            responseMessage = dataSearchService.executeService(requestMessage);
            System.out.println("RETORNO DO DATASET::::::::: " + responseMessage);
        } catch (Exception e) {
            System.out.println("ERROR - K31" + e);
            responseMessage = new ResponseGeneralModel(e.getMessage(), true, 500);
        }

        return Response.status(responseMessage.getCode()).entity(gson.toJson(responseMessage)).build();
    }

    private DatasetSearchModel parseJsonToDatasetSearchModel(String jsonValues) {
        try {
            return gson.fromJson(jsonValues, DatasetSearchModel.class);
        } catch (JsonSyntaxException | IllegalArgumentException | UnsupportedOperationException | ConcurrentModificationException e) {
            return null;
        }
    }

    private boolean isValidRequestMessage(DatasetSearchModel requestMessage) {
        return requestMessage.getParams() != null && !requestMessage.getParams().isEmpty() && requestMessage.getEndpoint() != null && !requestMessage.getEndpoint().isEmpty() && requestMessage.getMethod() != null && !requestMessage.getMethod().isEmpty();
    }

    private Response buildErrorResponse(Response.Status status, String message) {
        return Response.status(status).entity(message).build();
    }
}
