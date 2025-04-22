package com.fluig.utils;

import com.fluig.configuration.ConfigurationHub;
import com.fluig.exception.NoEndpointFoundException;
import com.fluig.model.*;
import org.apache.http.client.utils.URIBuilder;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class BuilderURI {
    private final URIBuilder builder;

    public BuilderURI() {

        builder = new URIBuilder();
        String scheme = ConfigurationHub.getInstance().getProperty("SCHEME");
        builder.setScheme(scheme);

        String domain = ConfigurationHub.getInstance().getProperty("DOMAIN");
        builder.setHost(domain);

    }

    public URI URIdataSearch(DatasetSearchModel request) throws NoEndpointFoundException, URISyntaxException, UnsupportedEncodingException {
        String endpoint = new Endpoint().getEndpoint(request.getEndpoint());
        builder.setPath(endpoint);

        if (Objects.equals(request.getMethod().toLowerCase(), "get")) {
            String queryString = request.getParams();
            builder.setCustomQuery(queryString);
        }

        return builder.build();
    }


    public URI URIuploadFileZip(ZipFilesModel request) throws URISyntaxException, NoEndpointFoundException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String endpoint = new Endpoint().getEndpointWithParams(request.getEndpoint(), request.getPathId(), request.getNamefile() + ".zip");
        builder.setPath(endpoint);

        return builder.build();
    }

    public URI URImoveProcess(MoveStartProcessModel request) throws URISyntaxException, NoEndpointFoundException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String endpoint = new Endpoint().getEndpointWithParams(request.getEndpoint(), request.getProcess(), null);
        builder.setPath(endpoint);

        return builder.build();
    }

    public URI URIstartProcess(MoveStartProcessModel request) throws NoEndpointFoundException, URISyntaxException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String endpoint = new Endpoint().getEndpointWithParams(request.getEndpoint(), request.getProcess(), null);
        builder.setPath(endpoint);

        return builder.build();
    }

    public URI URIuploadPdfFile(HtmlToPdfModel request) throws URISyntaxException, NoEndpointFoundException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String endpoint = new Endpoint().getEndpointWithParams("uploadstream", request.getPathId(), request.getFileName() + ".pdf");
        builder.setPath(endpoint);

        return builder.build();
    }

    public URI URIcreateFolderGeneric(CreateFolderModel request) throws URISyntaxException, NoEndpointFoundException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String endpoint = new Endpoint().getEndpointWithParams("folder", request.getParentFolderId(), null);
        builder.setPath(endpoint);

        return builder.build();
    }

    public URI URIreturndocuments(CreateFolderModel request) throws URISyntaxException, NoEndpointFoundException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, UnsupportedEncodingException {
        String endpoint = new Endpoint().getEndpointWithParams("getfolder", request.getParentFolderId(), null);
        int LIMIT_PAGES = 1000;
        builder.setPath(endpoint);
        String documentDescription = URLEncoder.encode(request.getDocumentDescription(), StandardCharsets.UTF_8);
        builder.setCustomQuery("order=documentDescription&pageSize=" + LIMIT_PAGES + "&documentDescription=" + documentDescription);

        return builder.build();
    }

    public URI URIuploadFile(UploadFileModel request) throws NoEndpointFoundException, URISyntaxException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String nome = request.getNameFile();
        String filename = request.getNameFile();
        String endpoint = new Endpoint().getEndpointWithParams("uploadstream", request.getPathId(), filename);
        builder.setPath(endpoint);

        return builder.build();
    }

    public URI URIattachFile() throws NoEndpointFoundException, URISyntaxException {
        String endpoint = new Endpoint().getEndpoint("attachfile");
        builder.setPath(endpoint);

        return builder.build();
    }

    public URI URIuploadAttach(String filename) throws NoEndpointFoundException, URISyntaxException {
        String endpoint = new Endpoint().getEndpoint("uploadattach");
        builder.setPath(endpoint);
        builder.setCustomQuery("fileName=" + filename);

        return builder.build();
    }

    public URI URIcountPages(String documentId) throws NoEndpointFoundException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, URISyntaxException {
        String endpoint = new Endpoint().getEndpointWithParams("countpages", documentId, null);
        builder.setPath(endpoint);

        return builder.build();
    }

    public URI URIdeleteAttach() throws NoEndpointFoundException, URISyntaxException {
        String endpoint = new Endpoint().getEndpoint("deleteattach");
        builder.setPath(endpoint);

        return builder.build();
    }

    public URI URIstrategihub() throws URISyntaxException {
        builder.setHost("strategiconsultoria176588.fluig.cloudtotvs.com.br:2450");
        String endpoint = "/fluighub/rest/service/execute/datasearch";
        builder.setPath(endpoint);

        return builder.build();
    }

    public URI URICreateCard() throws NoEndpointFoundException, URISyntaxException {
        String endpoint = new Endpoint().getEndpoint("card");
        builder.setPath(endpoint);

        return builder.build();
    }
}
