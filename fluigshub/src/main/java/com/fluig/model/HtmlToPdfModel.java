package com.fluig.model;

public class HtmlToPdfModel {

    private String service;
    private String params;
    private String fileName;
    private String folderName;
    private String pathId;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getPathId() {
        return pathId;
    }

    public void setPathId(String pathId) {
        this.pathId = pathId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }


    public String getService() {

        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}
