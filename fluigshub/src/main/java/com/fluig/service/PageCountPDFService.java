package com.fluig.service;

import com.fluig.exception.NoEndpointFoundException;
import com.fluig.model.GenericModel;
import com.fluig.model.ResponseGeneralModel;
import com.fluig.oauthhub.OAuthClientInstance;
import com.fluig.utils.BuilderURI;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class PageCountPDFService {

    public ResponseGeneralModel executeService(GenericModel request) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, NoEndpointFoundException, URISyntaxException, IOException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
        InputStream inputStream = GetStreamFromAPI(request.getString());
        int pages = countPdfPages(inputStream);
        return new ResponseGeneralModel("Number pages: " + pages, false, 200);
    }

    public InputStream GetStreamFromAPI(String documentId) throws NoEndpointFoundException, NoSuchPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, URISyntaxException,
            InvalidKeyException, IOException {


        try(CloseableHttpClient httpClient = HttpClientBuilder.create().build()){
            URI URI = new BuilderURI().URIcountPages(documentId);
            HttpGet httpGet = new HttpGet(URI);
            OAuthRequest oAuthRequest = getoAuthRequest(URI);
            String authorizationHeader = oAuthRequest.getHeaders().get("Authorization");
            if (authorizationHeader != null) {
                httpGet.setHeader("Authorization", authorizationHeader);
            }

            try (CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(httpGet)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        return entity.getContent();
                    }
                } else {
                    System.err.println("Failed to download file. HTTP error code: " + response.getStatusLine().getStatusCode());
                }
                return null;
            } catch (IOException | UnsupportedOperationException e) {
                throw new RuntimeException(e);
            }
        }

    }


    private static OAuthRequest getoAuthRequest(URI URI) {
        OAuthClientInstance oAuthClient = OAuthClientInstance.getInstance();
        OAuth10aService service = oAuthClient.getService();
        OAuth1AccessToken accessToken = oAuthClient.getAccessToken();

        // Cria a requisição OAuth com o ScribeJava
        OAuthRequest oAuthRequest = new OAuthRequest(Verb.GET, URI.toString());
        oAuthRequest.addHeader("Accept", "application/json"); // Ou qualquer outro cabeçalho necessário

        // Assina a requisição
        service.signRequest(accessToken, oAuthRequest);
        return oAuthRequest;
    }

    public int countPdfPages(InputStream inputStream) throws IOException {
        byte[] bytes = convertInputStreamToByteArray(inputStream);
        try (PDDocument document = Loader.loadPDF(bytes)) {
            return document.getNumberOfPages();
        }
    }

    private byte[] convertInputStreamToByteArray(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream.toByteArray();
        }
    }

}
