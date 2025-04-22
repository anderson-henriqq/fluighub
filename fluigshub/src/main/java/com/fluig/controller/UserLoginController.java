package com.fluig.controller;

import com.fluig.model.ResponseGeneralModel;
import com.fluig.model.UserLoginModel;
import com.fluig.service.UserLoginService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ConcurrentModificationException;
import java.util.Objects;

@Path("service")
public class UserLoginController {

    private final Gson gson = new Gson();

    @POST
    @Path("execute/loginportal")
    @Produces(MediaType.APPLICATION_JSON)
    public Response execute(String jsonValues) {

        if (isJsonValuesNullOrEmpty(jsonValues)) {
            return buildErrorResponse(Response.Status.BAD_REQUEST, "Não foi possível criar o Objeto a partir do JSON");
        }

        UserLoginModel requestMessage = parseJsonToUserLoginModel(jsonValues);
        assert requestMessage != null;
        if (requestMessage.getLogin() == null || requestMessage.getPass() == null || Objects.equals(requestMessage.getLogin(), "") || requestMessage.getPass().isEmpty() || requestMessage.getLogin().isEmpty()) {
            return buildErrorResponse(Response.Status.BAD_REQUEST, "Não foi possível criar o Objeto a partir do JSON");
        }

        ResponseGeneralModel responseMessage;
        try {
            responseMessage = new UserLoginService().executeService(requestMessage);
        } catch (Exception e) {
            responseMessage = new ResponseGeneralModel(e.getMessage(), true, 500);
        }
        System.out.println(responseMessage.toString());
        return Response.status(responseMessage.getCode()).entity(gson.toJson(responseMessage)).build();
    }

    private boolean isJsonValuesNullOrEmpty(String jsonValues) {
        return jsonValues == null || jsonValues.isEmpty();
    }

    private UserLoginModel parseJsonToUserLoginModel(String jsonValues) {
        try {
            return gson.fromJson(jsonValues, UserLoginModel.class);
        } catch (JsonSyntaxException | IllegalArgumentException | UnsupportedOperationException | ConcurrentModificationException e) {
            return null;
        }
    }

    private Response buildErrorResponse(Response.Status status, String message) {
        return Response.status(status).entity(message).build();
    }
}