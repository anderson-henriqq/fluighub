package com.fluig.service;

import com.fluig.exception.NoEndpointFoundException;
import com.fluig.model.MoveStartProcessModel;
import com.fluig.model.ResponseGeneralModel;
import com.fluig.utils.BuilderURI;
import com.fluig.utils.HttpRequestUtil;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MoveStartProcessService {
    public ResponseGeneralModel executeService(MoveStartProcessModel request) throws NoEndpointFoundException, URISyntaxException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (Objects.equals(request.getMethod().toLowerCase(), "post")) {
            return postHttpRequest(request);
        } else {
            return new ResponseGeneralModel("Método não suportado", true, 405);
        }
    }

    public ResponseGeneralModel postHttpRequest(MoveStartProcessModel request) throws NoEndpointFoundException, URISyntaxException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        URI uri = getUri(request);
        StringEntity stringEntity = new StringEntity(request.getParams(), "UTF-8");
        try{
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        HttpPost httpPost = HttpRequestUtil.createHttpPost(uri, headers,stringEntity);
        return HttpRequestUtil.executeHttpRequest(httpPost);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseGeneralModel("Não foi possível interagir com endpoint " + e, true, 500);
        }
    }

    private URI getUri(MoveStartProcessModel request) throws NoEndpointFoundException, URISyntaxException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        URI uri = null;
        if (request.getEndpoint().equals("start")) {
            uri = new BuilderURI().URIstartProcess(request);
        } else if (request.getEndpoint().equals("moveprocess")) {
            uri = new BuilderURI().URImoveProcess(request);
        }
        return uri;
    }
}
