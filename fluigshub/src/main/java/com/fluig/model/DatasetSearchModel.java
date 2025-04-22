package com.fluig.model;

public class DatasetSearchModel extends GeneralModel {
    private String endpoint;
    private String params;

    public DatasetSearchModel(String endpoint, String params, String method) {
        this.endpoint = endpoint;
        this.params = params;
        this.setMethod(method);
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
