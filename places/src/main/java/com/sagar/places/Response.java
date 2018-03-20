package com.sagar.places;

import com.sagar.util.ValidationUtil;

public class Response<T> {

    private String errorMessage;
    private String errorType;
    private String responseSource;
    private T data;

    public Response(String errorType, String errorMessage) {
        this.errorMessage = errorMessage;
        this.errorType = errorType;
    }

    public Response(String responseSource, T data) {
        this.responseSource = responseSource;
        this.data = data;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getErrorType() {
        return errorType;
    }

    public String getResponseSource() {
        return responseSource;
    }

    public boolean isError() {
        return ValidationUtil.isStringNotEmpty(errorType);
    }

    public T getData() {
        return data;
    }
}
