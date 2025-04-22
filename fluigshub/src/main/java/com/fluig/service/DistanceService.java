package com.fluig.service;

import com.fluig.model.DistanceRequestModel;
import com.fluig.model.DistanceResponseModel;
import com.fluig.model.ResponseGeneralWithDataModel;
import com.fluig.utils.ApiKeyUtil;
import com.fluig.utils.Coordinates;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.fluig.service.CoordinatesService.seachCoordenates;


public class DistanceService {

    private static final String AZURE_API_KEY = ApiKeyUtil.getApiKey();

    public ResponseGeneralWithDataModel executeService(DistanceRequestModel request) throws Exception {

        List<DistanceRequestModel.DistancePair> data = request.getData();

        List<DistanceResponseModel> responseList = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(data.size());

        List<Future<DistanceResponseModel>> futures = new ArrayList<>();


        for (int i = 0; i < data.size(); i++) {
            final int index = i;
            futures.add(executor.submit(() -> {
                DistanceRequestModel.DistancePair pair = data.get(index);
                String origem = pair.getOrigem();
                String destino = pair.getDestino();

                Coordinates origemCoord = buscarCoordenadas(origem);
                Coordinates destinoCoord = buscarCoordenadas(destino);

                if (origemCoord != null && destinoCoord != null) {
                    Double distancia = calcularDistanciaEntreCoordenadas(origemCoord, destinoCoord);
                    return new DistanceResponseModel(origem, destino, distancia);
                } else {
                    return new DistanceResponseModel(origem, destino, null);
                }
            }));
        }

        for (Future<DistanceResponseModel> future : futures) {
            responseList.add(future.get());
        }

        executor.shutdown();


        System.out.println("LISTA DE DISTANCIAS");
        System.out.println(responseList);
        return new ResponseGeneralWithDataModel(responseList, false, 200);
    }

    private Coordinates buscarCoordenadas(String cidade) throws Exception {

        return seachCoordenates(cidade);
    }

    private Double calcularDistanciaEntreCoordenadas(Coordinates origem, Coordinates destino) throws Exception {
        String url = String.format(Locale.US,
                "https://atlas.microsoft.com/route/directions/json?api-version=1.0&query=%f,%f:%f,%f",
                origem.getLatitude(), origem.getLongitude(),
                destino.getLatitude(), destino.getLongitude()
        );

        System.out.println(origem.toString() + " " + destino.toString() + " ");


        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            request.addHeader("Subscription-Key", AZURE_API_KEY);


            try (CloseableHttpResponse response = client.execute(request)) {

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    throw new RuntimeException("Falha ao chamar o endpoint: " + statusCode);
                }


                String jsonResponse = EntityUtils.toString(response.getEntity());
                System.out.println("RESPOSTA API AZURE DISTANCIA");
                System.out.println(jsonResponse);

                JsonObject responseObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
                JsonArray routesArray = responseObject.getAsJsonArray("routes");

                if (!routesArray.isEmpty()) {
                    JsonObject routeSummary = routesArray.get(0).getAsJsonObject().get("summary").getAsJsonObject();
                    double distanciaEmMetros = routeSummary.get("lengthInMeters").getAsDouble();
                    return distanciaEmMetros / 1000; // Converte para quilômetros
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao calcular distância: " + e.getMessage());
            throw new RuntimeException(e);
        }

        return null;
    }

}