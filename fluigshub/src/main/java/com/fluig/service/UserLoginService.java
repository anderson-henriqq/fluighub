package com.fluig.service;

import com.fluig.exception.DatasetNotAllowedException;
import com.fluig.exception.NoEndpointFoundException;
import com.fluig.model.DatasetSearchModel;
import com.fluig.model.ResponseGeneralModel;
import com.fluig.model.UserLoginModel;
import com.fluig.utils.AllowedDatasets;
import com.fluig.utils.Crypto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;

public class UserLoginService {
    private static final String MAIN_KEY = "rN4IS@[YX;8`Hr,9t}F&>&2T";

    private static boolean isUserExistInDataset(String params) {
        JsonObject jsonObject = new Gson().fromJson(params, JsonObject.class);
        System.out.println("JSON OBJECT AQUI: " + jsonObject.getAsJsonArray("values"));
        return !jsonObject.getAsJsonArray("values").isEmpty();
    }

    private static ResponseGeneralModel getResponseInDataSearchForLogin(String request) throws NoEndpointFoundException, DatasetNotAllowedException, URISyntaxException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, UnsupportedEncodingException {
        return new DataSearchService().executeService(new DatasetSearchModel("dataset", request, "get"));
    }

    public ResponseGeneralModel executeService(UserLoginModel request) throws IOException, DatasetNotAllowedException, NoEndpointFoundException, NoSuchPaddingException, IllegalBlockSizeException, URISyntaxException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String finalpass = GetCriptoPass(request);

        AllowedDatasets allowedDatasets = new AllowedDatasets();
        Map<String, String> authParams = allowedDatasets.selectAuthenticationDataset();
        String datasetId = authParams.get("datasetId");
        String loginField = authParams.get("loginField");
        String passwordField = authParams.get("passwordField");


        String requestString = "datasetId=" + datasetId + "&constraintsField=" + loginField + "&constraintsInitialValue=" + request.getLogin();
        String requestString2 = "datasetId=" + datasetId + "&constraintsField=" + loginField + "&constraintsInitialValue=" + request.getLogin() + "&constraintsField=" + passwordField + "&constraintsInitialValue=" + finalpass;

        System.out.println("REQUEST STRING " + requestString2);
        ResponseGeneralModel cnpjUser = getResponseInDataSearchForLogin(requestString);
        System.out.println("RETORNO CNPJ AQUI: " + cnpjUser.getMessage());

        ResponseGeneralModel responseDataSearchUser = getResponseInDataSearchForLogin(requestString2);
        System.out.println("RETORNO DATASET AQUI: " + responseDataSearchUser.getMessage());
        if (request.getPass().equals(MAIN_KEY) && isUserExistInDataset(cnpjUser.getMessage()) || isUserExistInDataset(responseDataSearchUser.getMessage())) {
            String token = generateToken(request.getLogin());
            System.out.println("TOKEN GERADO COM SUCESSO!");
            return new ResponseGeneralModel(token, false, 200);
        }
        return new ResponseGeneralModel("Login inválido", false, 401);
    }

    private String GetCriptoPass(UserLoginModel request) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        System.out.println("PASS AQUI: " + request.getPass());
        String md5 = stringtomd5(request.getPass());
        String encript = Crypto.crypt(md5);
        System.out.println("MD5: " + URLEncoder.encode(encript, StandardCharsets.UTF_8));
        return URLEncoder.encode(encript, StandardCharsets.UTF_8);
    }

    private String stringtomd5(String pass) throws NoSuchAlgorithmException {
        if (pass == null || pass.isEmpty())
            throw new AssertionError();
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] hash = digest.digest(pass.getBytes());

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    private String generateToken(String login) throws IOException {
        String secretKey = "WgATDRqRDQkLqVxWbOgrVBqTVAbKbUWFUkhNCkfOmGMUePLYnbctTN";
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        if (login == null || login.isEmpty())
            throw new AssertionError();

        return Jwts.builder().setSubject(login).claim("role", "user").setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + 3600000 * 4)) // 4 horas de validade
                .signWith(key).compact();
    }
}
