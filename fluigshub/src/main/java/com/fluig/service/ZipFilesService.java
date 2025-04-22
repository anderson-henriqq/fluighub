package com.fluig.service;

import com.fluig.exception.NoEndpointFoundException;
import com.fluig.model.AttachmentModel;
import com.fluig.model.ResponseGeneralModel;
import com.fluig.model.ZipFilesModel;
import com.fluig.oauthhub.OAuthClientInstance;
import com.fluig.utils.BuilderURI;
import com.fluig.utils.Downloader;
import com.fluig.utils.Zipper;
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
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipOutputStream;

/**
 * Esta classe é responsável por gerenciar a criação de arquivos ZIP em memória.
 */
public class ZipFilesService {
    private static HttpClient HTTP_CLIENT = HttpClientBuilder.create().build();
    private final Zipper zipper;

    public ZipFilesService() {
        zipper = new Zipper();
    }

    static ResponseGeneralModel getResponseGeneralModel(HttpPost httpPost, MultipartEntityBuilder builder, InputStream inputStream) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException, IOException {
        builder.addBinaryBody("file", inputStream, ContentType.APPLICATION_OCTET_STREAM, "file.zip");


        HttpEntity multipartEntity = builder.build();
        httpPost.setEntity(multipartEntity);
        OAuthClientInstance oAuthClient = OAuthClientInstance.getInstance();
        OAuth10aService service = oAuthClient.getService();
        OAuth1AccessToken accessToken = oAuthClient.getAccessToken();

        OAuthRequest oAuthRequest = new OAuthRequest(Verb.POST, httpPost.getURI().toString());

        service.signRequest(accessToken, oAuthRequest);

        String authorizationHeader = oAuthRequest.getHeaders().get("Authorization");
        if (authorizationHeader != null) {
            httpPost.setHeader("Authorization", authorizationHeader);
        }

        HttpResponse response = HTTP_CLIENT.execute(httpPost);
        HttpEntity entity = response.getEntity();
        String result = EntityUtils.toString(entity);
        ResponseGeneralModel responseMessage = new ResponseGeneralModel(result, false, response.getStatusLine().getStatusCode());
        return responseMessage;
    }

    public ResponseGeneralModel executeService(ZipFilesModel request) throws NoEndpointFoundException, URISyntaxException {
        List<AttachmentModel> attachments = request.getBody().getAttachments();
        System.out.println("INICIANDO ZIPAGEM DE ARQUIVOS...");

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ZipOutputStream zos = new ZipOutputStream(baos)) {

            ExecuteMultDownloads(attachments, zos);
            zos.finish();
            if (Objects.equals(request.getMethod(), "post")) {
                return postHttpRequest(request, baos);
            } else {
                return new ResponseGeneralModel("Método Http não suportado", true, 405);
            }
        } catch (Exception e) {
            System.out.println("ERRO - K31: " + e);
            return new ResponseGeneralModel("Erro ao baixar ou zipar arquivos: " + e.getMessage(), true, 500);
        }

    }

    public synchronized void downloadAndZip(String urlFile, String destinationFolder, ZipOutputStream zos) {
        if (urlFile == null || urlFile.isEmpty()) {
            throw new IllegalArgumentException("A URL do arquivo não pode ser nula ou vazia.");
        }
        try {
            byte[] fileByte = Downloader.downloadFileToMemory(urlFile);
            zipper.zipDirectory(fileByte, destinationFolder, zos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void ExecuteMultDownloads(List<AttachmentModel> attachments, ZipOutputStream zos) {
        System.out.println("PREPARANDO THREADS DO SERVIDOR...");
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            for (AttachmentModel file : attachments) {
                executor.execute(() -> downloadAndZip(file.getKey(), file.getPath(), zos));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
            try {
                boolean finished = executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                if (!finished) {
                    System.err.println("Timeout: Nem todas as tarefas foram concluídas dentro do prazo.");
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
                System.err.println("Erro ao aguardar a conclusão dos downloads: " + e.getMessage());
            }
        }
    }

    public ResponseGeneralModel postHttpRequest(ZipFilesModel request, ByteArrayOutputStream baos) throws NoEndpointFoundException, URISyntaxException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, IOException {
        ResponseGeneralModel responseMessage = null;
        URI uri = new BuilderURI().URIuploadFileZip(request);
        HttpPost httpPost = new HttpPost(uri);

        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            if (baos.size() == 0) {
                throw new Exception("Arquivo ZIP vazio");
            }
            InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
            responseMessage = getResponseGeneralModel(httpPost, builder, inputStream);
        } catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException | IOException e) {
            System.out.println("ERROR - K31 " + e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return responseMessage;
    }


}
