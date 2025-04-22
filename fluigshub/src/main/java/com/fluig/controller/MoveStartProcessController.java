package com.fluig.controller;

import com.fluig.model.MoveStartProcessModel;
import com.fluig.model.ResponseGeneralModel;
import com.fluig.service.MoveStartProcessService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ConcurrentModificationException;

@Path("service")
public class MoveStartProcessController {

    private final Gson gson = new Gson();


    @POST
    @Path("execute/movestart-process")
    @Produces(MediaType.APPLICATION_JSON)
    public Response execute(String jsonValues) {
        if (isJsonValuesNullOrEmpty(jsonValues)) {
            return buildErrorResponse(Response.Status.BAD_REQUEST, "JSON nulo ou vazio");
        }

        MoveStartProcessModel requestMessage = parseJsonToMoveStartProcessModel(jsonValues);
        if (requestMessage == null || !isValidRequestMessage(requestMessage)) {
            return buildErrorResponse(Response.Status.BAD_REQUEST, "Não foi possível criar o Objeto a partir do JSON");
        }

        ResponseGeneralModel responseMessage;
        try {
            responseMessage = new MoveStartProcessService().executeService(requestMessage);
        } catch (Exception e) {
            e.printStackTrace();
            responseMessage = new ResponseGeneralModel(e.getMessage(), true, 500);
        }

        return Response.status(responseMessage.getCode()).entity(gson.toJson(responseMessage)).build();
    }

    private boolean isJsonValuesNullOrEmpty(String jsonValues) {
        return jsonValues == null || jsonValues.isEmpty();
    }

    private MoveStartProcessModel parseJsonToMoveStartProcessModel(String jsonValues) {
        try {
            return gson.fromJson(jsonValues, MoveStartProcessModel.class);
        } catch (JsonSyntaxException | IllegalArgumentException | UnsupportedOperationException | ConcurrentModificationException e) {
            return null;
        }
    }

    private boolean isValidRequestMessage(MoveStartProcessModel requestMessage) {
        return requestMessage.getParams() != null && !requestMessage.getParams().isEmpty() && requestMessage.getEndpoint() != null && !requestMessage.getEndpoint().isEmpty() && requestMessage.getMethod() != null && !requestMessage.getMethod().isEmpty() && requestMessage.getProcess() != null && !requestMessage.getProcess().isEmpty();
    }

    private Response buildErrorResponse(Response.Status status, String message) {
        return Response.status(status).entity(message).build();
    }
}
