package com.fluig.controller;

import com.fluig.model.ResponseGeneralModel;
import com.fluig.model.ZipFilesModel;
import com.fluig.service.ZipFilesService;
import com.fluig.utils.CheckJson;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ConcurrentModificationException;

@Path("service")
public class ZipFilesController {

    private final Gson gson = new Gson();


    @POST
    @Path("execute/zipfiles")
    @Produces(MediaType.APPLICATION_JSON)
    public Response execute(String jsonValues) {

        ZipFilesModel requestMessage = parseJsonToZipFilesModel(jsonValues);
        if (requestMessage == null || !isValidRequestMessage(requestMessage)) {
            return buildErrorResponse(Response.Status.BAD_REQUEST, "Não foi possível criar o Objeto a partir do JSON");
        }

        ResponseGeneralModel responseMessage;
        try {
            responseMessage = new ZipFilesService().executeService(requestMessage);
            System.out.println("ARQUIVOS ZIPADOS COM SUCESSO");
        } catch (Exception e) {
            System.out.println("ERRO - K31 " + e);
            responseMessage = new ResponseGeneralModel(e.getMessage(), true, 500);
        }

        return Response.status(responseMessage.getCode()).entity(gson.toJson(responseMessage)).build();
    }


    private ZipFilesModel parseJsonToZipFilesModel(String jsonValues) {
        try {
            return gson.fromJson(jsonValues, ZipFilesModel.class);
        } catch (JsonSyntaxException | IllegalArgumentException | UnsupportedOperationException | ConcurrentModificationException e) {
            return null;
        }
    }

    private boolean isValidRequestMessage(ZipFilesModel requestMessage) {
        return requestMessage.getBody() != null && requestMessage.getBody().getAttachments() != null && !requestMessage.getBody().getAttachments().isEmpty() && requestMessage.getEndpoint() != null && !requestMessage.getEndpoint().isEmpty() && requestMessage.getPathId() != null && !requestMessage.getPathId().isEmpty() && requestMessage.getNamefile() != null && !requestMessage.getNamefile().isEmpty() && requestMessage.getMethod() != null && !requestMessage.getMethod().isEmpty();
    }

    private Response buildErrorResponse(Response.Status status, String message) {
        return Response.status(status).entity(message).build();
    }
}
