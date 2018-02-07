package com.webonise.places.constants;

public class Constants {

    public static final String API_KEY = "";
    public static final int RADIUS = 1000;

    public static final class ErrorType {
        public static final String EMPTY_LOCAL_PLACE_LIST = "EMPTY_LOCAL_PLACE_LIST";
        public static final String EMPTY_SERVER_PLACE_LIST = "EMPTY_SERVER_PLACE_LIST";
        public static final String FETCH_PLACE_DETAILS_ERROR = "FETCH_PLACE_DETAILS_ERROR";
        public static final String NETWORK_ERROR = "NETWORK_ERROR";
    }

    public static final class ResponseSource {
        public static final String LOCAL = "LOCAL";
        public static final String NETWORK = "NETWORK";
    }

    public static final class PreferenceKey {
        public static final String LOCATION = "LOCATION";
    }
}
