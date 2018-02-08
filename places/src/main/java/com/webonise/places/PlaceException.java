package com.webonise.places;

public class PlaceException extends Exception {

    private String errorType;

    public PlaceException(String errorType) {
        super(errorType);
        this.errorType = errorType;
    }

    public String getErrorType() {
        return errorType;
    }
}
