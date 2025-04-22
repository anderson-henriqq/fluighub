package com.fluig.service;

import com.fluig.model.CoordenatesModel;
import com.fluig.model.ResponseGeneralWithDataModel;
import com.fluig.utils.ApiKeyUtil;
import com.fluig.utils.Coordinates;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoordinatesService {

    private static final String API_KEY_PROPERTY = "API_KEY";

    public ResponseGeneralWithDataModel executeService(CoordenatesModel request) throws Exception {
        List<String> cities = request.getCidades();
        System.out.println("NOME DA CIDADE: " + cities);
        Map<String, Coordinates> results = new HashMap<>();

        for (String city : cities) {
            Coordinates coordenadas = seachCoordenates(city);
            if (coordenadas != null) {
                System.out.println("COORDENADAS: " + coordenadas);
                results.put(city, coordenadas);
            } else {
                System.out.println("Nenhuma coordenada encontrada para a cidade: " + city);
            }
        }
        System.out.println("RESULTS: " + results);
        return new ResponseGeneralWithDataModel(results, false, 200);
    }


    public static String seachCoordenatesString(String cidade) throws Exception {
        Coordinates coordinates = fetchCoordinates(cidade);
        if (coordinates != null) {
            return "Latitude: " + coordinates.getLatitude() + ", Longitude: " + coordinates.getLongitude();
        }
        return "Nenhum resultado encontrado";
    }

    public static Coordinates seachCoordenates(String cidade) throws Exception {
        return fetchCoordinates(cidade);
    }

    private static Coordinates fetchCoordinates(String cidade) throws Exception {

        String apiKey = ApiKeyUtil.getApiKey();
        if (apiKey == null) {
            throw new RuntimeException("Chave API não encontrada no arquivo api.properties");
        }

        URI uri = buildUri(cidade, apiKey);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(uri);
            request.addHeader("Subscription-Key", apiKey);

            try (CloseableHttpResponse response = client.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    System.out.println("Erro ao chamar API. Status: " + statusCode);
                    return null;
                }

                String jsonResponse = EntityUtils.toString(response.getEntity());
                return parseCoordinates(jsonResponse);
            }
        } catch (Exception e) {
            System.err.println("Erro ao chamar API. Status: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static URI buildUri(String cidade, String apiKey) throws Exception {
        URIBuilder builder = new URIBuilder("https://atlas.microsoft.com/search/address/json");
        builder.addParameter("api-version", "1.0")
                .addParameter("query", URLEncoder.encode(cidade, StandardCharsets.UTF_8));
        return builder.build();
    }

    private static Coordinates parseCoordinates(String jsonResponse) {
        Gson gson = new Gson();
        JsonObject responseObject = gson.fromJson(jsonResponse, JsonObject.class);

        if (responseObject.has("results") && responseObject.get("results").isJsonArray()) {
            JsonArray resultsArray = responseObject.getAsJsonArray("results");

            if (!resultsArray.isEmpty()) {
                JsonObject firstResult = resultsArray.get(0).getAsJsonObject();
                JsonObject position = firstResult.getAsJsonObject("position");
                double latitude = position.get("lat").getAsDouble();
                double longitude = position.get("lon").getAsDouble();

                String localName = firstResult.getAsJsonObject("address").get("freeformAddress").getAsString();
                return new Coordinates(localName, latitude, longitude);
            }
        }
        return null;
    }



}