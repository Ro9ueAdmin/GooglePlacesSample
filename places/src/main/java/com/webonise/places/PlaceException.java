package com.webonise.places;

public class PlaceException extends Exception {

    private Response<Void> errorDetails;

    public PlaceException(Response<Void> errorDetails) {
        super(errorDetails.getErrorMessage());
        this.errorDetails = errorDetails;
    }

    public Response<Void> getErrorDetails() {
        return errorDetails;
    }
}
