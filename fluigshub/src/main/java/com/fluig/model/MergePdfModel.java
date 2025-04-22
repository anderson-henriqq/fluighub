package com.fluig.model;

import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

public class MergePdfModel {

    @FormParam("pdfFiles")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    private List<InputPart> pdfFiles;

    public List<InputPart> getPdfFiles() {
        return pdfFiles;
    }

    public void setPdfFiles(List<InputPart> pdfFiles) {
        this.pdfFiles = pdfFiles;
    }
}
