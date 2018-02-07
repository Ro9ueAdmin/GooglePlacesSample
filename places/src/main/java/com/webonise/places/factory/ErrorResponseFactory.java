package com.webonise.places.factory;

import android.content.Context;

import com.webonise.places.R;
import com.webonise.places.Response;
import com.webonise.places.constants.Constants;

public class ErrorResponseFactory {

    public static Response<Void> createErrorResponse(Context context, String errorType) {
        int errorMsgResId;
        switch (errorType) {
            case Constants.ErrorType.EMPTY_LOCAL_PLACE_LIST:
                errorMsgResId = R.string.empty_local_list;
                break;
            case Constants.ErrorType.NETWORK_ERROR:
                errorMsgResId = R.string.network_error;
                break;
            default:
                errorMsgResId = R.string.unknown_error;
                break;
        }
        return new Response<>(errorType, context.getString(errorMsgResId));
    }

}
