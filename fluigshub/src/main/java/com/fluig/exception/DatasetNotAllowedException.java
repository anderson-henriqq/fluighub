package com.fluig.exception;

public class DatasetNotAllowedException extends Exception {
    private static final long serialVersionUID = -1286080366345707624L;

    public DatasetNotAllowedException() {
        super();
    }

    public DatasetNotAllowedException(String message) {
        super(message);
    }
}