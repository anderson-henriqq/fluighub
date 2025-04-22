package com.fluig.service;

import com.fluig.exception.NoEndpointFoundException;
import com.fluig.model.CryptoModel;
import com.fluig.model.ResponseGeneralModel;
import com.fluig.model.SunlineModel;
import com.fluig.utils.BuilderURI;
import com.fluig.utils.Crypto;
import com.fluig.utils.HttpRequestUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SunlineService {


    public ResponseGeneralModel executeService(SunlineModel request) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, NoEndpointFoundException, URISyntaxException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
        try {
            String message = convertToJSON(request);
            ResponseGeneralModel response = sendToInternForm(message);
            return response;
        }
        catch(Exception e){
                return new ResponseGeneralModel("Error: " + e.getMessage(), false, 500);
            }

    }


        private ResponseGeneralModel sendToInternForm (String message) throws
                NoEndpointFoundException, URISyntaxException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException, IOException {
            URI uri = new BuilderURI().URICreateCard();
            StringEntity stringEntity = new StringEntity(message, "UTF-8");
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            System.out.println(EntityUtils.toString(stringEntity));
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            try {
                HttpPost httpPost = HttpRequestUtil.createHttpPost(uri, headers, stringEntity);
                System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
                System.out.println(httpPost);
                return HttpRequestUtil.executeHttpRequest(httpPost);
            } catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException e) {
                e.printStackTrace();
                System.out.println("ERROR endpoint card - K31 [Cheque se os parâmetros estão sendo passados corretamente no JSON] " + e.getMessage() + " endpoint /api/public/2.0/cards/create");
                return new ResponseGeneralModel("ERROR endpoint card - K31 [Cheque se os parâmetros estão sendo passados corretamente no JSON] " + e.getMessage() + " endpoint /api/public/2.0/cards/create " + e.getMessage(), false, 500);
            }
        }


        public static String convertToJSON (SunlineModel sunline){
            Gson gson = new Gson();
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("documentDescription", "VIAGEM AÉREA - " + sunline.getNomePassageiro() + " - " + sunline.getLocalizador());
            jsonObject.addProperty("parentDocumentId", 155574);
            jsonObject.addProperty("version", 0);
            jsonObject.addProperty("inheritSecurity", true);

            JsonArray formData = new JsonArray();

            formData.add(createFormField("nome_passageiro", sunline.getNomePassageiro()));
            formData.add(createFormField("sobrenome_passageiro", sunline.getSobrenomePassageiro()));
            formData.add(createFormField("telefone_solicitante", sunline.getTelefone()));
            formData.add(createFormField("aeroporto_origem", sunline.getAeroportoOrigem()));
            formData.add(createFormField("aeroporto_destino", sunline.getAeroportoDestino()));
            formData.add(createFormField("localizador", sunline.getLocalizador()));
            formData.add(createFormField("sistema", sunline.getSistema()));
            formData.add(createFormField("status", sunline.getStatus()));
            formData.add(createFormField("forma_pagamento", sunline.getFormaPagamento()));
            formData.add(createFormField("data_viagem", sunline.getDataPartida()));
            formData.add(createFormField("numero_bilhete", sunline.getNumeroBilhete()));
            formData.add(createFormField("tipo_pax", sunline.getTipoPax()));
            formData.add(createFormField("tarifa", sunline.getTarifa()));
            formData.add(createFormField("taxa_embarque", sunline.getTaxaEmbarque()));
            formData.add(createFormField("taxa_de_servico", sunline.getTaxaServico()));
            formData.add(createFormField("valor_total", sunline.getTotal()));
            formData.add(createFormField("numero_voo_ida", sunline.getNumeroVooIda()));
            formData.add(createFormField("numero_voo_volta", sunline.getNumeroVooVolta()));

            jsonObject.add("formData", formData);

            return gson.toJson(jsonObject);
        }

        private static JsonObject createFormField (String name, Object value){
                JsonObject field = new JsonObject();
                field.addProperty("name", name);
                field.addProperty("value", value != null ? value.toString() : ""); // Evita valores nulos
                return field;
            }
        }
