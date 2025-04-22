package com.fluig.controller;

import com.fluig.model.ResponseGeneralModel;
import com.fluig.model.UploadFileModel;
import com.fluig.service.UploadFileService;
import com.google.gson.Gson;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.fluig.utils.CheckJson.checkInputPartsIsNullOrEmpty;


@Path("service")
public class UploadFileController {

    private static void UpdateModelWithFormData(UploadFileModel uploadFileModel, InputPart inputPart, String pathId, String nameFile) {
        uploadFileModel.setFile(inputPart);
        uploadFileModel.setPathId(pathId);
        uploadFileModel.setNameFile(nameFile);
    }

    @POST
    @Path("execute/uploadfile")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response execute(MultipartFormDataInput input) {

        try {
            UploadFileModel uploadFileModel = new UploadFileModel();
            Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
            List<InputPart> inputParts = uploadForm.get("file");

            if (checkInputPartsIsNullOrEmpty(inputParts)) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Nenhum arquivo foi enviado").build();
            }

            InputPart inputPart = inputParts.get(0);


            List<InputPart> pathIdParts = uploadForm.get("pathId");
            if (checkInputPartsIsNullOrEmpty(pathIdParts)) {
                return Response.status(Response.Status.BAD_REQUEST).entity("O campo pathId está ausente").build();
            }
            String pathId = pathIdParts.get(0).getBody(String.class, null);


            List<InputPart> nameFileParts = uploadForm.get("nameFile");
            if (nameFileParts == null || nameFileParts.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("O campo nameFile está ausente").build();
            }
            String nameFile;

            InputPart inputPartnameFile = nameFileParts.get(0);

            InputStream streamOfBytesNameFile = inputPartnameFile.getBody(InputStream.class, null);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(streamOfBytesNameFile, StandardCharsets.UTF_8))) {
                nameFile = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            } catch (IOException e) {

                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao processar o campo nameFile").build();
            }

            UpdateModelWithFormData(uploadFileModel, inputPart, pathId, nameFile);
            ResponseGeneralModel responseMessage = new UploadFileService().executeService(uploadFileModel);

            return Response.status(responseMessage.getCode()).entity(new Gson().toJson(responseMessage)).build();
        } catch (Exception e) {
            System.out.println("ERROR - K31" + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao processar o arquivo").build();
        }
    }

}
