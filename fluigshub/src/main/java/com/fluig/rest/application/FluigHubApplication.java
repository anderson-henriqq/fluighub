package com.fluig.rest.application;

import com.fluig.configuration.ConfigurationHub;
import com.fluig.controller.*;
import com.fluig.exception.NotFoundExceptionMapper;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class FluigHubApplication extends Application {
    private final Set<Object> singletons = new HashSet<>();

    public FluigHubApplication() {
        ConfigurationHub configurationHub = ConfigurationHub.getInstance();
        singletons.add(new Base64Controller());
        if (Boolean.parseBoolean(configurationHub.getProperty("endpoint.dataset"))) {
            singletons.add(new DatasetSearchController());
        }
        singletons.add(new QRcodeController());
        if (Boolean.parseBoolean(configurationHub.getProperty("endpoint.zipfiles"))) {
            singletons.add(new ZipFilesController());
        }
        if (Boolean.parseBoolean(configurationHub.getProperty("endpoint.htmltopdf"))) {
            singletons.add(new HtmlToPdfController());
        }
        if (Boolean.parseBoolean(configurationHub.getProperty("endpoint.mergepdf"))) {
            singletons.add(new MergePdfController());
            singletons.add(new UploadFileController());
        }
        if (Boolean.parseBoolean(configurationHub.getProperty("endpoint.crypto"))) {
            singletons.add(new CryptoController());
        }
        if (Boolean.parseBoolean(configurationHub.getProperty("endpoint.token"))) {
            singletons.add(new UserLoginController());
            singletons.add(new CheckTokenController());
        }
        if (Boolean.parseBoolean(configurationHub.getProperty("endpoint.folder"))) {
            singletons.add(new CreateFolderController());
        }
        if (Boolean.parseBoolean(configurationHub.getProperty("endpoint.process"))) {
            singletons.add(new MoveStartProcessController());
            singletons.add(new AttachFileController());
            singletons.add(new DeleteAttachController());
            singletons.add(new UploadAttachController());
        }
        if (Boolean.parseBoolean(configurationHub.getProperty("endpoint.coordenadas"))) {
            singletons.add(new CoordinatesController());
            singletons.add(new DistanceController());
            singletons.add(new SunlineController());
        }
        singletons.add(new PageCountPDFController());
        singletons.add(new VersionController());
        singletons.add(new NotFoundExceptionMapper());
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}