package com.fluig.model;

import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;


public class UploadFileModel {

    @FormParam("file")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    private InputPart file;

    @FormParam("pathId")
    @PartType(MediaType.TEXT_PLAIN)
    private String pathId;

    @FormParam("nameFile")
    @PartType(MediaType.TEXT_PLAIN)
    private String nameFile;

    public InputPart getFile() {
        return file;
    }

    public void setFile(InputPart file) {
        this.file = file;
    }

    public String getPathId() {
        return pathId;
    }

    public void setPathId(String pathId) {
        this.pathId = pathId;
    }

    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }
}
