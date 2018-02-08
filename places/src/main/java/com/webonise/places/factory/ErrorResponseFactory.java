package com.webonise.places.factory;

import android.content.Context;

import com.webonise.places.PlaceException;
import com.webonise.places.R;
import com.webonise.places.Response;
import com.webonise.places.constants.Constants;

import static com.webonise.rest.ResponseCode.Places.INVALID_REQUEST;
import static com.webonise.rest.ResponseCode.Places.OVER_QUERY_LIMIT;
import static com.webonise.rest.ResponseCode.Places.REQUEST_DENIED;
import static com.webonise.rest.ResponseCode.Places.UNKNOWN_ERROR;
import static com.webonise.rest.ResponseCode.Places.ZERO_RESULTS;

public class ErrorResponseFactory {

    public static <T> Response<T> createErrorResponse(Context context, String errorType) {
        String errorMsg;
        switch (errorType) {
            case Constants.ErrorType.EMPTY_LOCAL_PLACE_LIST:
                errorMsg = context.getString(R.string.empty_local_list);
                break;
            case Constants.ErrorType.NETWORK_ERROR:
                errorMsg = context.getString(R.string.network_error);
                break;
            case ZERO_RESULTS:
            case OVER_QUERY_LIMIT:
            case REQUEST_DENIED:
            case INVALID_REQUEST:
            case UNKNOWN_ERROR:
                errorMsg = errorType;
                break;
            default:
                errorMsg = context.getString(R.string.unknown_error);
                break;
        }
        return new Response<>(errorType, errorMsg);
    }

    public static <T> Response<T> createErrorResponse(Context context, Throwable throwable) {
        if (throwable instanceof PlaceException) {
            return createErrorResponse(context, ((PlaceException) throwable).getErrorType());
        } else {
            return createErrorResponse(context, Constants.ErrorType.NETWORK_ERROR);
        }
    }
}
