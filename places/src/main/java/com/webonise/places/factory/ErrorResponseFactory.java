package com.webonise.places.factory;

import android.content.Context;

import com.webonise.places.PlaceException;
import com.webonise.places.R;
import com.webonise.places.Response;
import com.webonise.places.constants.Constants;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.HttpException;

import static com.webonise.rest.ResponseCode.Places.INVALID_REQUEST;
import static com.webonise.rest.ResponseCode.Places.OVER_QUERY_LIMIT;
import static com.webonise.rest.ResponseCode.Places.REQUEST_DENIED;
import static com.webonise.rest.ResponseCode.Places.ZERO_RESULTS;

public class ErrorResponseFactory {

    public static <T> Response<T> createErrorResponse(Context context, String errorType) {
        String errorMsg;
        switch (errorType) {
            case ZERO_RESULTS:
            case OVER_QUERY_LIMIT:
            case REQUEST_DENIED:
            case INVALID_REQUEST:
                errorMsg = errorType;
                break;
            case Constants.ErrorType.EMPTY_LOCAL_PLACE_LIST:
                errorMsg = context.getString(R.string.empty_local_list);
                break;
            case Constants.ErrorType.NETWORK_ERROR:
                errorMsg = context.getString(R.string.network_error_message);
                break;
            case Constants.ErrorType.HTTP_ERROR:
                errorMsg = context.getString(R.string.http_error_message);
                break;
            case Constants.ErrorType.TIMEOUT_ERROR:
                errorMsg = context.getString(R.string.timeout_error_message);
                break;
            case Constants.ErrorType.UNKNOWN_HOST_ERROR:
                errorMsg = context.getString(R.string.unknown_host_error_message);
                break;
            case Constants.ErrorType.UNKNOWN_ERROR:
            default:
                errorMsg = context.getString(R.string.unknown_error_message);
                break;
        }
        return new Response<>(errorType, errorMsg);
    }

    public static <T> Response<T> createErrorResponse(Context context, Throwable throwable) {
        String errorType;
        if (throwable instanceof PlaceException) {
            errorType = ((PlaceException) throwable).getErrorType();
        } else if (throwable instanceof ConnectException) {
            errorType = Constants.ErrorType.NETWORK_ERROR;
        } else if (throwable instanceof HttpException) {
            errorType = Constants.ErrorType.HTTP_ERROR;
        } else if (throwable instanceof SocketTimeoutException) {
            errorType = Constants.ErrorType.TIMEOUT_ERROR;
        } else if (throwable instanceof UnknownHostException) {
            errorType = Constants.ErrorType.UNKNOWN_HOST_ERROR;
        } else if (throwable instanceof IOException) {
            errorType = Constants.ErrorType.UNKNOWN_ERROR;
        } else {
            errorType = Constants.ErrorType.UNKNOWN_ERROR;
        }
        return createErrorResponse(context, errorType);
    }
}
