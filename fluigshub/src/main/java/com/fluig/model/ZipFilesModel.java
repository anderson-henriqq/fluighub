package com.fluig.model;

public class ZipFilesModel extends GeneralModel {
    private String endpoint;
    private String namefile;
    private String pathId;
    private BodyModel body;

    public BodyModel getBody() {
        return body;
    }


    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getNamefile() {
        return namefile;
    }

    public String getPathId() {
        return pathId;
    }

}
