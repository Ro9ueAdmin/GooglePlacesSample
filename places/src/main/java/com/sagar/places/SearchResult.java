package com.sagar.places;

import java.util.List;

public class SearchResult {

    private List<Place> places;
    private String nextPageToken;
    private String dataProviderDetails;

    public SearchResult(List<Place> places, String nextPageToken, String dataProviderDetails) {
        this.places = places;
        this.nextPageToken = nextPageToken;
        this.dataProviderDetails = dataProviderDetails;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public String getDataProviderDetails() {
        return dataProviderDetails;
    }
}
