package com.webonise.places;

import android.location.Location;

public class SearchRequest {

    private Location location;
    private String keyword;
    private String pageToken;

    public SearchRequest(Location location, String keyword, String pageToken) {
        this.location = location;
        this.keyword = keyword;
        this.pageToken = pageToken;
    }

    public Location getLocation() {
        return location;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getPageToken() {
        return pageToken;
    }
}
