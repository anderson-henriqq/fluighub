package com.fluig.model;

import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

public class UploadAttachModel {
    @FormParam("fileName")
    @PartType(MediaType.TEXT_PLAIN)
    private String filename;

    @FormParam("file")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    private InputPart file;


    public InputPart getFile() {
        return file;
    }

    public void setFile(InputPart file) {
        this.file = file;
    }


    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

}
