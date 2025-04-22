package com.fluig.service;

import com.fluig.exception.NoEndpointFoundException;
import com.fluig.model.ResponseGeneralModel;
import com.fluig.utils.BuilderURI;

import java.net.URI;
import java.net.URISyntaxException;

public class DeleteAttachService {
    private final AttachFileService attachFileService = new AttachFileService();

    public ResponseGeneralModel executeService(String jsonValues) throws NoEndpointFoundException, URISyntaxException {
        String request = attachFileService.attCurrentMovtoInJson(jsonValues);
        URI uri = new BuilderURI().URIdeleteAttach();
        return attachFileService.getResponseToAPI(request, uri);
    }

}
