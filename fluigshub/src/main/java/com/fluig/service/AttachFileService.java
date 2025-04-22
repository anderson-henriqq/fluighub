package com.fluig.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fluig.exception.DatasetNotAllowedException;
import com.fluig.exception.NoEndpointFoundException;
import com.fluig.model.DatasetSearchModel;
import com.fluig.model.ResponseGeneralModel;
import com.fluig.oauthhub.OAuthClientInstance;
import com.fluig.utils.BuilderURI;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AttachFileService {
    public ResponseGeneralModel executeService(String jsonValues) throws NoEndpointFoundException, URISyntaxException {
        String request = attCurrentMovtoInJson(jsonValues);
        System.out.println("K31 - " + request);
        URI uri = new BuilderURI().URIattachFile();
        return getResponseToAPI(request, uri);
    }


    public ResponseGeneralModel getResponseToAPI(String request, URI uri) throws NoEndpointFoundException, URISyntaxException {
        HttpPost httpPost = new HttpPost(uri);
        StringEntity entity = new StringEntity(request, "UTF-8");
        entity.setContentType("application/json; charset=UTF-8");
        httpPost.setEntity(entity);

        ResponseGeneralModel responseMessage = null;

        OAuthClientInstance oAuthClient = OAuthClientInstance.getInstance();
        OAuth10aService service = oAuthClient.getService();
        OAuth1AccessToken accessToken = oAuthClient.getAccessToken();

        OAuthRequest oAuthRequest = new OAuthRequest(Verb.POST, uri.toString());
        oAuthRequest.addHeader("Content-Type", "application/json; charset=UTF-8");
        oAuthRequest.setPayload(request);

        service.signRequest(accessToken, oAuthRequest);

        String authorizationHeader = oAuthRequest.getHeaders().get("Authorization");
        if (authorizationHeader != null) {
            httpPost.setHeader("Authorization", authorizationHeader);
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == 302) {
                String newLocation = httpResponse.getFirstHeader("Location").getValue();
                System.out.println("KKKKKKKKKK New location: " + newLocation);
                URI redirectUri = new URI(newLocation);
                System.out.println("KKKKKKKKKK Redirect URI: " + redirectUri);
                HttpPost redirectPost = new HttpPost(redirectUri);
                redirectPost.setEntity(entity);
                redirectPost.setHeader("Authorization", authorizationHeader);
                redirectPost.setHeader("Content-Type", "application/json; charset=UTF-8");
                httpResponse = httpClient.execute(redirectPost);
            }

            HttpEntity entityResponse = httpResponse.getEntity();
            String result = EntityUtils.toString(entityResponse);
            responseMessage = new ResponseGeneralModel(result, false, httpResponse.getStatusLine().getStatusCode());

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR - K31 " + e.getMessage());
        }
        return responseMessage;
    }

    public String attCurrentMovtoInJson(String jsonValues) throws NoEndpointFoundException, URISyntaxException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonValues);
            String processInstanceId = jsonNode.get("processInstanceId").asText();
            Object[] ValueOfcurrentMovto = getCurrentMovto(processInstanceId);


            ((ObjectNode) jsonNode).put("currentMovto", (int) ValueOfcurrentMovto[0]);
            ((ObjectNode) jsonNode).put("selectedState", (int) ValueOfcurrentMovto[1]);
            ((ObjectNode) jsonNode).put("processId", (String) ValueOfcurrentMovto[2]);


            return objectMapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException | DatasetNotAllowedException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public Object[] getCurrentMovto(String processID) throws NoEndpointFoundException, URISyntaxException, DatasetNotAllowedException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, JsonProcessingException, UnsupportedEncodingException {
        Object[] currentMovto = new Object[3];
        String params = "datasetId=dsConsultaNumSeqMvto&constraintsField=fluigid&constraintsInitialValue=" + processID;
        System.out.println(params);
        ResponseGeneralModel response = new DataSearchService().executeService(new DatasetSearchModel("dataset", params, "get"));
        System.out.println("K31 - " + response.getMessage());
        System.out.println("Dataset encontrado\n");
        JsonObject jsonObject = new Gson().fromJson(response.getMessage(), JsonObject.class);

        if (jsonObject.has("values") && jsonObject.get("values").isJsonArray()) {
            JsonArray valuesArray = jsonObject.getAsJsonArray("values");
            if (!valuesArray.isEmpty()) {
                JsonObject firstValueObject = valuesArray.get(0).getAsJsonObject();
                if (firstValueObject.has("NUM_SEQ_MOVTO")) {
                    currentMovto[0] = firstValueObject.get("NUM_SEQ_MOVTO").getAsInt();
                }
                if (firstValueObject.has("SELECTED_STATE")) {
                    currentMovto[1] = firstValueObject.get("SELECTED_STATE").getAsInt();
                }
                if (firstValueObject.has("PROCESS_ID")) {
                    currentMovto[2] = firstValueObject.get("PROCESS_ID").getAsString();
                }
                System.out.println("Número de sequencia: " + currentMovto[0] + "e número de estado: " + currentMovto[1]);
                return currentMovto;
            }
        }
        throw new IllegalArgumentException("ERROR - K31 - NUM_SEQ_MOVTO ou SELECTED_STATE não encontrado no JSON");
    }

}


