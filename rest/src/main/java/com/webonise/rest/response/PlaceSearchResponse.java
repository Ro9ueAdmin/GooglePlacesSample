package com.webonise.rest.response;

import java.util.Collections;
import java.util.List;

public class PlaceSearchResponse extends BasePlaceResponse {

    public List<PlaceDetailsResponseData> results;

    PlaceSearchResponse() {
        results = Collections.emptyList();
    }

}
