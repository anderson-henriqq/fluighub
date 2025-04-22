package com.fluig.utils;

import com.fluig.configuration.ConfigurationHub;
import com.fluig.exception.DatasetNotAllowedException;

import java.util.*;
import java.util.stream.Collectors;

public class AllowedDatasets {
    private static AllowedDatasets instance;
    ConfigurationHub configurationHub;
    private final HashSet<String> datasets;
    private final List<String> loginDatasets;
    private final String permitedDataset;
//    private long lastUpdateTimestamp;
//    private static final long UPDATE_INTERVAL_10min = 10 * 60 * 200;

    public AllowedDatasets() {
        this.configurationHub = ConfigurationHub.getInstance();
        this.datasets = new HashSet<>();
        this.loginDatasets = new ArrayList<>();
        this.permitedDataset = configurationHub.getProperty("datasets");


        selectPermitedDatasets();
    }

    public static AllowedDatasets getInstance() {
        if (instance == null) {
            instance = new AllowedDatasets();
        }
        return instance;
    }

    public void check(String params) throws DatasetNotAllowedException {
        String datasetEscolhido = "datasetId=" + getParameterValue(params, "datasetId");
        System.out.println("DATASET ESCOLHIDO: " + datasetEscolhido);
        if (isCalledFromLoginService()) {
            System.out.println("K31 CHAMADA DE LOGIN");
            selectLoginDatasets();
            boolean allowedForLogin = loginDatasets.contains(datasetEscolhido);
            if (!allowedForLogin) {
                throw new DatasetNotAllowedException("Dataset indisponível ou não existe para login.");
            }
        } else {
        boolean allowedOne = datasets.contains(datasetEscolhido);
        if(Objects.equals(permitedDataset, "fluighubstrategi")) {

            allowedOne = true;
        }
        if (!allowedOne) {
            System.out.println("DATASETS PERMITIDOS: " + this.datasets);
            System.out.println("DATASET ESCOLHIDO NÃO PRESENTE: " + datasetEscolhido);
            throw new DatasetNotAllowedException("Dataset indisponível ou não existe");
            }
        }
    }

//
//    private ResponseGeneralModel getJsonResponseDirectForStrategi() {
//        try {
//            String requestString = "datasetId=dsRetornoDatasetsPermitidos";
//            DatasetSearchModel datasetSearch = new DatasetSearchModel("dataset", requestString, "get");
//            URI uri = new BuilderURI().URIdataSearch(datasetSearch);
//            HttpGet httpGet = HttpRequestUtil.createHttpGet(uri, null);
//            return HttpRequestUtil.executeHttpRequest(httpGet);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseGeneralModel("Erro ao executar datasets permitidos", false, 500);
//        }
//    }
//
//    private ResponseGeneralModel getJsonResponseDirect() {
//        try {
//             String jsonPayload = "{\n" +
//                    "    \"endpoint\": \"dataset\",\n" +
//                    "    \"method\": \"get\",\n" +
//                    "    \"params\": \"datasetId=dsRetornoDatasetsPermitidos\"\n" +
//                    "}";
//
//             StringEntity jsonEntity = new StringEntity(jsonPayload);
//            URI uri = new BuilderURI().URIstrategihub();
//            System.out.println("KKKKKKKKKKKKKURI: " + uri);
//            HttpPost httpPost = HttpRequestUtil.createHttpPost(uri, null, jsonEntity);
//            return HttpRequestUtil.executeHttpRequest(httpPost);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseGeneralModel("Erro ao executar datasets permitidos", false, 500);
//        }
//    }
//
//    private void updateCacheIfNeeded() {
//        long currentTime = System.currentTimeMillis();
//        System.out.println("K31 - currentTime " + currentTime);
//        System.out.println("K31 - time to atualization " + (currentTime-lastUpdateTimestamp));
//        if (currentTime - lastUpdateTimestamp >= UPDATE_INTERVAL_10min) {
//            selectDinamicDatasets();
//            lastUpdateTimestamp = currentTime;
//        }
//    }
//
//
//    private void updateCacheWithDatasets(Set<String> newDatasets) {
//        datasets.retainAll(newDatasets);
//
//        datasets.addAll(newDatasets);
//
//        System.out.println("K31 - Datasets: " + datasets);
//    }
//
//    private void selectDinamicDatasets() {
//        Gson gson = new Gson();
//        ResponseGeneralModel response = null;
//
//        if (configurationHub.getProperty("datasets").equalsIgnoreCase("fluighubstrategi")) {
//            response = getJsonResponseDirectForStrategi();
//        } else {
//            response = getJsonResponseDirect();
//        }
//
//        // Obtendo a resposta em JSON
//        String jsonResponse = response.getMessage();
//        System.out.println("K31 - JsonResponseDirect: " + jsonResponse);
//
//        try {
//            if (jsonResponse.trim().startsWith("{")) {
//                Map<String, Object> messageMap = gson.fromJson(jsonResponse, Map.class);
//
//                Object innerJsonResponseObj;
//                Map<String, Object> innerMap;
//
//                if (messageMap.containsKey("message")) {
//                    innerJsonResponseObj = messageMap.get("message");
//
//                    if (innerJsonResponseObj instanceof String) {
//                        String innerJsonResponse = (String) innerJsonResponseObj;
//                        innerMap = gson.fromJson(innerJsonResponse, Map.class);
//                    } else {
//                        innerMap = (Map<String, Object>) innerJsonResponseObj;
//                    }
//                } else {
//                    innerMap = messageMap;
//                }
//
//                List<Map<String, String>> values = (List<Map<String, String>>) innerMap.get("values");
//                System.out.println("K31 - Values: " + values);
//
//                Set<String> newDatasets = new HashSet<>();
//                for (Map<String, String> value : values) {
//                    if (value.get("nome_fluig").equalsIgnoreCase(permitedDataset)) {
//                        String[] datasetsArray = value.get("nome_dataset").split(",");
//                        for (String dataset : datasetsArray) {
//                            System.out.println("KKKKKKKKKKKK DATASET: " + dataset);
//                            newDatasets.add(dataset.trim());
//                        }
//                    }
//                }
//                if (!newDatasets.isEmpty()) {
//                    updateCacheWithDatasets(newDatasets);
//                }
//            } else {
//                System.out.println(" K31 - Resposta da TOTVS para o dataset " + permitedDataset + " não é um JSON válido: " + jsonResponse);
//            }
//        } catch (JsonSyntaxException e) {
//            System.out.println("Erro ao interpretar o JSON: " + e.getMessage());
//        }
//    }

    private void selectPermitedDatasets() {
            System.out.println(Arrays.toString(configurationHub.getProperty("datasets.permitidos").trim().split(",")));
            String[] dataset = configurationHub.getProperty("datasets.permitidos").trim().split(",");
            datasets.addAll(Arrays.stream(dataset).map(String::trim).collect(Collectors.toList()));
            System.out.println(" KKKKKKKKKKKKKKKKK PREENCHIDOS: " + datasets);
    }


    private void selectLoginDatasets() {
        if (permitedDataset.equalsIgnoreCase("fluighubelastri")) {
            loginDatasets.add("datasetId=dsformRegistrosFornecedores");
        } else if (permitedDataset.equalsIgnoreCase("fluighubsebraeam")) {
            loginDatasets.add("datasetId=dsformInternoAutenticacaoFornecedores");
        } else if (permitedDataset.equalsIgnoreCase("fluighubdoisa")) {
            loginDatasets.add("datasetId=dsFormRegistrosClientesCustomizacao");
        }
    }

    private boolean isCalledFromLoginService() {
        // Verifica a stack trace para determinar se a chamada é do serviço de login
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            if (element.getClassName().equals("com.fluig.service.UserLoginService")) {
                return true;
            }
        }
        return false;
    }

    public Map<String, String> selectAuthenticationDataset() {
        Map<String, String> authParams = new HashMap<>();
        if (permitedDataset.equalsIgnoreCase("fluighubelastri")) {
            authParams.put("datasetId", "dsformRegistrosFornecedores");
            authParams.put("loginField", "cnpj");
            authParams.put("passwordField", "senha");
        } else if (permitedDataset.equalsIgnoreCase("fluighubsebraeam")) {
            authParams.put("datasetId", "dsformInternoAutenticacaoFornecedores");
            authParams.put("loginField", "cpf_cnpj");
            authParams.put("passwordField", "senhaEncriptada");
        } else if (permitedDataset.equalsIgnoreCase("fluighubdoisa")) {
            authParams.put("datasetId", "dsFormRegistrosClientesCustomizacao");
            authParams.put("loginField", "cpf_cnpj");
            authParams.put("passwordField", "senha");
        }
        return authParams;
    }

    private String getParameterValue(String query, String paramName) {

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2 && keyValue[0].equals(paramName)) {
                return keyValue[1];
            }
        }
        return null;
    }
}
