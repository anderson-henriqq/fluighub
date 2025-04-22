package com.fluig.controller;

import com.fluig.model.HtmlToPdfModel;
import com.fluig.model.ResponseGeneralModel;
import com.fluig.service.HtmlToPdfService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ConcurrentModificationException;

@Path("service")
public class HtmlToPdfController {
    private final Gson gson = new Gson();

    @POST
    @Path("execute/topdf")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Response execute(String jsonValues) throws IOException {
        HtmlToPdfModel requestMessage = parseJsonToHtmlToPdfModel(jsonValues);

        if (requestMessage != null && isValidRequestMessage(requestMessage)) {
            return buildErrorResponse(Response.Status.BAD_REQUEST, "Não foi possível criar o Objeto a partir do JSON");
        }

        if (requestMessage != null && requestMessage.getService() != null && requestMessage.getService().equalsIgnoreCase("bytes")) {
            byte[] pdfBytes = new HtmlToPdfService().executeServiceToReturnBytes(requestMessage);
            MultipartFormDataOutput output = new MultipartFormDataOutput();
            output.addFormData("pdfFile", new ByteArrayInputStream(pdfBytes), MediaType.APPLICATION_OCTET_STREAM_TYPE, requestMessage.getFileName());


            return Response.ok(output).build();

        }

        ResponseGeneralModel responseMessage;
        assert requestMessage != null;
        try {
            responseMessage = new HtmlToPdfService().executeService(requestMessage);

        } catch (Exception e) {
            e.printStackTrace();
            responseMessage = new ResponseGeneralModel(e.getMessage(), true, 500);
        }

        return Response.status(responseMessage.getCode()).entity(new Gson().toJson(responseMessage)).build();
    }

    private HtmlToPdfModel parseJsonToHtmlToPdfModel(String jsonValues) {
        try {
            return gson.fromJson(jsonValues, HtmlToPdfModel.class);
        } catch (JsonSyntaxException | IllegalArgumentException | UnsupportedOperationException | ConcurrentModificationException e) {
            return null;
        }
    }

    private boolean isValidRequestMessage(HtmlToPdfModel requestMessage) {
        return requestMessage.getFileName() != null && !requestMessage.getFileName().isEmpty() && requestMessage.getParams() != null && !requestMessage.getParams().isEmpty() && requestMessage.getService() != null && requestMessage.getService().isEmpty() && requestMessage.getPathId() != null && !requestMessage.getPathId().isEmpty();
    }

    private Response buildErrorResponse(Response.Status status, String message) {
        try(Response response = Response.status(status).entity(message).build()) {
            return response;
        }
    }

}