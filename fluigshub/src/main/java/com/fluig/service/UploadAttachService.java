package com.fluig.service;

import com.fluig.exception.NoEndpointFoundException;
import com.fluig.model.ResponseGeneralModel;
import com.fluig.model.UploadAttachModel;
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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

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

public class UploadAttachService {
    private static final HttpClient HTTP_CLIENT = HttpClientBuilder.create().build();

    static HttpResponse getResponseGeneralModel(HttpPost httpPost) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException, IOException {
        return HTTP_CLIENT.execute(httpPost);
    }

    public ResponseGeneralModel executeService(UploadAttachModel file) throws IOException, NoEndpointFoundException, NoSuchPaddingException, IllegalBlockSizeException, URISyntaxException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return postHttpRequest(file);
    }

    private byte[] filebytes(UploadAttachModel files) {
        try (InputStream inputStream = files.getFile().getBody(InputStream.class, null); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            if (inputStream == null) {
                throw new NullPointerException("InputStream is null");
            }

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            return baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Falha ao enviar arquivo: " + e);
        }
    }

    public ResponseGeneralModel postHttpRequest(UploadAttachModel request) throws NoEndpointFoundException, URISyntaxException {
        return postHttpRequestRecursive(request, 0);
    }

    private ResponseGeneralModel postHttpRequestRecursive(UploadAttachModel request, int requestCount) throws NoEndpointFoundException, URISyntaxException {
        if (requestCount > 3) {
            throw new RuntimeException("Falha ao enviar arquivo: Número máximo de tentativas excedido");
        }

        ResponseGeneralModel responseMessage = null;
        URI uri = new BuilderURI().URIuploadAttach(request.getFilename());
        HttpPost httpPost = new HttpPost(uri);


        OAuthClientInstance oAuthClient = OAuthClientInstance.getInstance();
        OAuth10aService service = oAuthClient.getService();
        OAuth1AccessToken accessToken = oAuthClient.getAccessToken();

        OAuthRequest oAuthRequest = new OAuthRequest(Verb.POST, httpPost.getURI().toString());

        service.signRequest(accessToken, oAuthRequest);

        String authorizationHeader = oAuthRequest.getHeaders().get("Authorization");
        if (authorizationHeader != null) {
            httpPost.setHeader("Authorization", authorizationHeader);
            httpPost.addHeader("Content-Type", "application/octet-stream");
        }
        try {

            byte[] filebytes = filebytes(request);

            if (filebytes.length == 0) {
                throw new IllegalArgumentException("filebytes is empty");
            }

            ByteArrayEntity byteArrayEntity = new ByteArrayEntity(filebytes, ContentType.APPLICATION_OCTET_STREAM);
            httpPost.setEntity(byteArrayEntity);


            HttpResponse response = getResponseGeneralModel(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 302) {
                System.out.println("K31 - deu 302");

                Thread.sleep(3000);

                return postHttpRequestRecursive(request, requestCount + 1);
            }

            HttpEntity entityResponse = response.getEntity();
            String result = EntityUtils.toString(entityResponse);
            responseMessage = new ResponseGeneralModel(result, false, response.getStatusLine().getStatusCode());
        } catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException | IOException e) {
            e.printStackTrace();
            System.out.println("ERROR - K31 " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return responseMessage;
    }
}
