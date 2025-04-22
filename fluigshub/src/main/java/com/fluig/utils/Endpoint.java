package com.fluig.utils;

import com.fluig.exception.NoEndpointFoundException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Endpoint {

    private final Map<String, String> endpoint;


    public Endpoint() {
        this.endpoint = new HashMap<>();
        InitializeEndpoints(null, null);
    }


    public void InitializeEndpoints(String param, String genericValue) {
        endpoint.put("dataset", "/dataset/api/v2/dataset-handle/search");
        endpoint.put("createDocument", "/api/public/ecm/document/createDocument");
        endpoint.put("attachfile", "/ecm/api/rest/ecm/workflowView/saveAttachments");
        endpoint.put("deleteattach", "/ecm/api/rest/ecm/workflowView/deleteAttachments");
        endpoint.put("uploadattach", "/api/public/2.0/contentfiles/upload");
        endpoint.put("card", "/api/public/2.0/cards/create");

        if (genericValue != null && param != null) {
            endpoint.put("uploadstream", "/content-management/api/v2/documents/upload/" + genericValue + "/" + param + "/publish");
        }

        if (param != null) {
            endpoint.put("folder", "/content-management/api/v2/folders/" + param);
            endpoint.put("start", "/process-management/api/v2/processes/" + param + "/start");
            endpoint.put("getfolder", "/content-management/api/v2/folders/" + param + "/documents");
            endpoint.put("moveprocess", "/process-management/api/v2/requests/" + param + "/move");
            endpoint.put("countpages", "/content-management/api/v2/documents/" + param + "/stream");
        }
    }


    public String getEndpointWithParams(String servico, String param, String generic) throws NoEndpointFoundException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (servico.equalsIgnoreCase("moveprocess") || servico.equalsIgnoreCase("start")) {
            param = Crypto.decrypt(param);
        }
        InitializeEndpoints(param, generic);
        return getEndpoint(servico);
    }

    public String getEndpoint(String servico) throws NoEndpointFoundException {
        String selectedEndpoint = endpoint.get(servico);
        if (selectedEndpoint == null) {
            throw new NoEndpointFoundException("Endpoint indisponível");
        }
        return selectedEndpoint;
    }
}