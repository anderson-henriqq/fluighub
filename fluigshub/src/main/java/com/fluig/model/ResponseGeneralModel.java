package com.fluig.model;

public class ResponseGeneralModel extends GeneralModel {
    private String message;
    private boolean error;
    private int code;

    public ResponseGeneralModel(String message, boolean error, int code) {
        this.message = message;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
