package com.fluig.controller;


import com.fluig.exception.NoEndpointFoundException;
import com.fluig.model.GenericModel;
import com.fluig.model.ResponseGeneralModel;
import com.fluig.model.SunlineModel;
import com.fluig.service.CryptoService;
import com.fluig.service.SunlineService;
import com.google.gson.Gson;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


@Path("service")
public class SunlineController {

    @POST
    @Path("execute/sunline")
    @Produces(MediaType.APPLICATION_JSON)
    public Response execute(String jsonValues) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, NoEndpointFoundException, OAuthMessageSignerException, OAuthExpectationFailedException, URISyntaxException, OAuthCommunicationException {


        if (jsonValues == null || jsonValues.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("JSON nulo ou vazio").build();
        }

        SunlineModel model; //Mudar model

        try {
            model = new Gson().fromJson(jsonValues, SunlineModel.class);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Não foi possível criar o Objeto a partir do JSON" + e.toString()).build();
        }
        ResponseGeneralModel responseMessage;

        responseMessage = new SunlineService().executeService(model);
//        String response = SunlineService.executeService();




        return Response.status(Response.Status.OK).entity(new Gson().toJson(responseMessage)).build();
    }
}
