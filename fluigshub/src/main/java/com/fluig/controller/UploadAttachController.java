package com.fluig.controller;

import com.fluig.exception.NoEndpointFoundException;
import com.fluig.model.ResponseGeneralModel;
import com.fluig.model.UploadAttachModel;
import com.fluig.service.UploadAttachService;
import com.google.gson.Gson;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import static com.fluig.utils.CheckJson.checkInputPartsIsNullOrEmpty;

@Path("service")
public class UploadAttachController {

    private static void UpdateModelWithFormData(UploadAttachModel uploadAttachModel, String filename, InputPart inputPart) {
        uploadAttachModel.setFilename(filename);
        uploadAttachModel.setFile(inputPart);
    }

    @POST
    @Path("execute/uploadanexo")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response execute(MultipartFormDataInput input) {
        final UploadAttachModel uploadAttachModel = new UploadAttachModel();


        try {

            Map<String, List<InputPart>> uploadForm = input.getFormDataMap();

            List<InputPart> inputParts = uploadForm.get("file");
            if (checkInputPartsIsNullOrEmpty(inputParts))
                return Response.status(Response.Status.BAD_REQUEST).entity("Nenhum arquivo foi enviado").build();
            InputPart inputPart = inputParts.get(0);


            List<InputPart> filenameParts = uploadForm.get("fileName");
            if (checkInputPartsIsNullOrEmpty(filenameParts)) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Nome de arquivo não informado").build();
            }
            String filename = filenameParts.get(0).getBody(String.class, null);


            UpdateModelWithFormData(uploadAttachModel, filename, inputPart);

            ResponseGeneralModel responseMessage = new UploadAttachService().executeService(uploadAttachModel);

            return Response.status(responseMessage.getCode()).entity(new Gson().toJson(responseMessage)).build();


        } catch (IOException e) {
            return Response.serverError().entity("Erro ao processar PDFs: " + e.getMessage()).build();
        } catch (NoEndpointFoundException | NoSuchPaddingException | IllegalBlockSizeException | URISyntaxException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }


}
