package com.fluig.controller;

import com.fluig.service.MergePdfService;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.fluig.utils.CheckJson.checkInputPartsIsNullOrEmpty;

@Path("service")
public class MergePdfController {

    private static void AddFilesInInputPartList(List<InputPart> inputParts, List<InputStream> inputStreams) throws IOException {
        for (InputPart inputPart : inputParts) {
            InputStream inputStream = inputPart.getBody(InputStream.class, null);
            inputStreams.add(inputStream);
        }
    }

    @POST
    @Path("execute/mergepdf")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response execute(MultipartFormDataInput input) {

        try {
            Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
            List<InputPart> inputParts = uploadForm.get("pdfFiles");

            if (checkInputPartsIsNullOrEmpty(inputParts)) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Nenhum arquivo PDF foi enviado").build();
            }
            List<InputStream> inputStreams = new ArrayList<>();

            AddFilesInInputPartList(inputParts, inputStreams);
            byte[] mergedPdfBytes = new MergePdfService().executeService(inputStreams);


            return Response.ok(mergedPdfBytes, MediaType.APPLICATION_OCTET_STREAM_TYPE).header("Content-Disposition", "attachment; filename=merged.pdf").build();
        } catch (IOException e) {
            e.printStackTrace();
            return Response.serverError().entity("K31 - Erro ao processar PDFs: " + e.getMessage()).build();
        }
    }
}
