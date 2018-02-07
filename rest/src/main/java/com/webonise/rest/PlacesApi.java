package com.webonise.rest;

import com.webonise.rest.response.PlaceSearchResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesApi {

    @GET
    Single<PlaceSearchResponse> searchPlaces(
            @Query("key") String apiKey,
            @Query("location") String location,
            @Query("keyword") String keyword,
            @Query("radius") String radius,
            @Query("pagetoken") String pageToken
    );

    @GET
    Single<PlaceSearchResponse> getPlaceDetails(
            @Query("key") String apiKey,
            @Query("placeid") String placeId
    );

}
