package com.fluig.service;

import com.fluig.exception.DatasetNotAllowedException;
import com.fluig.exception.NoEndpointFoundException;
import com.fluig.model.DatasetSearchModel;
import com.fluig.model.ResponseGeneralModel;
import com.fluig.utils.AllowedDatasets;
import com.fluig.utils.BuilderURI;
import com.fluig.utils.HttpRequestUtil;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.apache.http.client.methods.HttpGet;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class DataSearchService {

    public ResponseGeneralModel executeService(DatasetSearchModel request) throws NoEndpointFoundException, DatasetNotAllowedException, URISyntaxException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, UnsupportedEncodingException {
        if (Objects.equals(request.getEndpoint().toLowerCase(), "dataset")) {
            AllowedDatasets.getInstance().check(request.getParams());
        }

        if (Objects.equals(request.getMethod().toLowerCase(), "get")) {
            System.out.println("REQUEST FEITO ");
            return getHttpRequest(request);
        } else {
            return new ResponseGeneralModel("Método não suportado", true, 405);
        }
    }

    public ResponseGeneralModel getHttpRequest(DatasetSearchModel request) throws NoEndpointFoundException, URISyntaxException, UnsupportedEncodingException {
        URI uri = new BuilderURI().URIdataSearch(request);
        try {
            HttpGet httpGet = HttpRequestUtil.createHttpGet(uri, null);
            return HttpRequestUtil.executeHttpRequest(httpGet);
        } catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException e) {
            e.printStackTrace();
            System.out.println("ERROR endpoint dataset - K31 " + e.getMessage());
        }

	    return null;
    }





}


