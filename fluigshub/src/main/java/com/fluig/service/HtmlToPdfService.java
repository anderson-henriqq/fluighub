package com.fluig.service;

import com.fluig.exception.NoEndpointFoundException;
import com.fluig.model.CreateFolderModel;
import com.fluig.model.HtmlToPdfModel;
import com.fluig.model.ResponseGeneralModel;
import com.fluig.utils.BuilderURI;
import com.fluig.utils.MultpartRequestUtil;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;


import org.apache.http.entity.mime.MultipartEntityBuilder;

import org.xhtmlrenderer.pdf.ITextRenderer;

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
import java.util.Base64;

public class HtmlToPdfService {

    private final ByteArrayOutputStream baos;
    private String newfolderId;

    public HtmlToPdfService() {
        baos = new ByteArrayOutputStream();
        newfolderId = "";
    }

    private static String decodeFromBase64(String encodedString) {
        byte[] decodedBytes = Base64.getMimeDecoder().decode(encodedString);
        return new String(decodedBytes);

    }

    static ResponseGeneralModel getResponseUploadPDF(URI uri, MultipartEntityBuilder builder, InputStream inputStream) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException, IOException {
        return MultpartRequestUtil.uploadFile(uri, builder, inputStream, "file.zip");
    }

    public static String replaceSpecialChars(String htmlContent) {
        htmlContent = htmlContent.replaceAll("&", "&amp;");
        htmlContent = htmlContent.replaceAll("\\s<\\s", " &lt; ");
        htmlContent = htmlContent.replaceAll(" > ", "&gt;");
        htmlContent = htmlContent.replaceAll("\"", "'");
        return htmlContent;

    }

    public ResponseGeneralModel executeService(HtmlToPdfModel request) throws IOException {

        try {
            CreateFolderModel createFolderModel = new CreateFolderModel(request.getPathId(), request.getFolderName());
            newfolderId = CreateFolderService.createFolder(createFolderModel);
            System.out.println("DECODANDO HTML...");
            String decodedParams = decodeFromBase64(request.getParams());
            request.setParams(decodedParams);
            System.out.println("HTML DECODADO, GERANDO PDF...");
            request.setPathId(newfolderId);
            return postHttpRequest(request);

        } catch (NoEndpointFoundException | URISyntaxException | NoSuchPaddingException | IllegalBlockSizeException |
                 NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | OAuthMessageSignerException |
                 OAuthExpectationFailedException | OAuthCommunicationException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] executeServiceToReturnBytes(HtmlToPdfModel request) {
        try {
            System.out.println("DECODANDO HTML...");
            String decodedParams = decodeFromBase64(request.getParams());
            request.setParams(decodedParams);
            System.out.println("HTML DECODADO, GERANDO PDF...");
            return convertHtmlToPdf(request.getParams());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] convertHtmlToPdf(String htmlContent) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            htmlContent = sanitizeHtmlContent(htmlContent);
            ITextRenderer renderer = new ITextRenderer();

            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(baos);
            System.out.println("PDF GERADO COM SUCESSO!");
            return baos.toByteArray();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    private String sanitizeHtmlContent(String htmlContent) {
        return replaceSpecialChars(htmlContent);
    }

    public ResponseGeneralModel postHttpRequest(HtmlToPdfModel request) throws NoEndpointFoundException, URISyntaxException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        ResponseGeneralModel responseMessage;
        URI uri = new BuilderURI().URIuploadPdfFile(request);
        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            InputStream inputStream = new ByteArrayInputStream(convertHtmlToPdf(request.getParams()));

            responseMessage = getResponseUploadPDF(uri, builder, inputStream);

        } catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException | IOException e) {
            throw new RuntimeException(e);
        }
        return responseMessage;
    }





}



