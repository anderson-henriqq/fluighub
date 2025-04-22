package com.fluig.exception;

public class NoEndpointFoundException extends Exception {
    private static final long serialVersionUID = -1286080366345707624L;

    public NoEndpointFoundException() {
        super();
    }

    public NoEndpointFoundException(String message) {
        super(message);
    }
}