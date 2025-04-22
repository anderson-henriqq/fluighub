package com.fluig.controller;

import com.fluig.model.QRcodeModel;
import com.fluig.model.ResponseGeneralModel;
import com.fluig.service.QRcodeService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ConcurrentModificationException;


@Path("service")
public class QRcodeController {

    @POST
    @Path("execute/qrcode")
    @Produces(MediaType.APPLICATION_JSON)
    public Response execute(String jsonValues) {


        if (jsonValues == null || jsonValues.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("JSON nulo ou vazio").build();
        }

        QRcodeModel requestMessage;

        try {
            requestMessage = new Gson().fromJson(jsonValues, QRcodeModel.class);
        } catch (JsonSyntaxException | IllegalArgumentException | UnsupportedOperationException | ConcurrentModificationException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Não foi possível criar o Objeto a partir do JSON").build();
        }

        if (requestMessage.getTexttobase64() == null || requestMessage.getTexttobase64().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Parâmetros obrigatórios não preenchidos, visite a documentação").build();
        }

        ResponseGeneralModel responseMessage;

        try {
            responseMessage = new QRcodeService().executeService(requestMessage);
        } catch (Exception e) {
            e.printStackTrace();
            responseMessage = new ResponseGeneralModel(e.getMessage(), true, 500);
        }

        return Response.status(responseMessage.getCode()).entity(new Gson().toJson(responseMessage)).build();
    }

}
