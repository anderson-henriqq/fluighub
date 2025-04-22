package com.fluig.service;

import com.fluig.exception.NoEndpointFoundException;
import com.fluig.model.ResponseGeneralModel;
import com.fluig.model.UploadFileModel;
import com.fluig.oauthhub.OAuthClientInstance;
import com.fluig.utils.BuilderURI;
import com.fluig.utils.MultpartRequestUtil;
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
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


public class UploadFileService {

    static ResponseGeneralModel getResponseUploadFile(URI uri, MultipartEntityBuilder builder, InputStream inputStream) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException, IOException {
        return MultpartRequestUtil.uploadFile(uri, builder, inputStream, "file");
    }

    public ResponseGeneralModel executeService(UploadFileModel files) throws IOException, NoEndpointFoundException, URISyntaxException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        System.out.println("EXECUTANDO UPLOAD...");
        return postHttpRequestToUploadFile(files);
    }

    private byte[] uploadFile(UploadFileModel files) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); InputStream inputStream = files.getFile().getBody(InputStream.class, null)) {
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

    public ResponseGeneralModel postHttpRequestToUploadFile(UploadFileModel request) throws NoEndpointFoundException, URISyntaxException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        ResponseGeneralModel responseMessage = null;
        URI uri = new BuilderURI().URIuploadFile(request);
        try {
            byte[] filesbytes = uploadFile(request);
            if (filesbytes.length == 0) {
                throw new IOException("Falha ao enviar arquivo array nulo");
            }
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            try (InputStream inputStream = new ByteArrayInputStream(filesbytes)) {
                responseMessage = getResponseUploadFile(uri, builder, inputStream);
            }
        } catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException | IOException e) {
            e.printStackTrace();
        }
        System.out.println("KKKKKKKKKKKK MENSAGEM AQUI: " + responseMessage);
        return responseMessage;
    }
}
