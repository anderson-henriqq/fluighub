package com.fluig.model;

public class ResponseGeneralWithDataModel extends GeneralModel {
    private Object data; // Campo flexível para qualquer tipo de retorno
    private boolean error;
    private int code;

    public ResponseGeneralWithDataModel(Object data, boolean error, int code) {
        this.data = data;
        this.error = error;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}