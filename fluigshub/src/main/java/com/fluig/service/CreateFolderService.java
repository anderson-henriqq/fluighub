package com.fluig.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fluig.exception.DatasetNotAllowedException;
import com.fluig.exception.NoEndpointFoundException;
import com.fluig.model.CreateFolderModel;
import com.fluig.model.ResponseGeneralModel;
import com.fluig.utils.BuilderURI;
import com.fluig.utils.HttpRequestUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.util.HashMap;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


public class CreateFolderService {

    public ResponseGeneralModel executeService(CreateFolderModel request) throws NoEndpointFoundException, DatasetNotAllowedException, URISyntaxException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, IOException {
        try {
            String documentid = createFolder(request);
            return new ResponseGeneralModel(documentid, false, 200);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseGeneralModel("Não foi possível executar o método - K31 " + e, true, 500);
        }

    }

    public static String createFolder(CreateFolderModel request) throws IOException, URISyntaxException, NoEndpointFoundException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
        String verify = verifyExistentFolder(request);
        if (verify.equalsIgnoreCase("not found")) {

            URI uri = new BuilderURI().URIcreateFolderGeneric(request);
            ObjectMapper objectMapper = new ObjectMapper();
            AliasRequest aliasRequest = new AliasRequest(request.getDocumentDescription());
            String json = objectMapper.writeValueAsString(aliasRequest);


            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
            HttpPost httpPost = HttpRequestUtil.createHttpPost(uri, headers,entity);

            ResponseGeneralModel response = HttpRequestUtil.executeHttpRequest(httpPost);
            System.out.println("PASTA CRIADA COM SUCESSO!");
            JsonObject jsonObject = JsonParser.parseString(response.getMessage()).getAsJsonObject();
            return jsonObject.get("documentId").getAsString();
        }
        return verify;
    }

    private static String verifyExistentFolder(CreateFolderModel request) throws NoEndpointFoundException, URISyntaxException, IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
        URI uri = new BuilderURI().URIreturndocuments(request);
        String foldername = request.getDocumentDescription();
        HttpGet httpGet = HttpRequestUtil.createHttpGet(uri, null);
        ResponseGeneralModel response = HttpRequestUtil.executeHttpRequest(httpGet);
        String json = response.getMessage();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(json);
        ArrayNode invdataArray = (ArrayNode) rootNode.get("invdata");
        if (invdataArray != null) {
            for (JsonNode documentNode : invdataArray) {
                String documentDescription = documentNode.get("documentDescription").asText();
                System.out.println(documentDescription);
                if (documentDescription != null && documentDescription.equals(foldername)) {
                    System.out.println("Documento encontrado");
                    return documentNode.get("documentId").asText();
                }
            }
        }
        return "not found";
    }

    static class AliasRequest {
        private String alias;

        public AliasRequest(String alias) {
            this.alias = alias;
        }


        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }
    }
}
