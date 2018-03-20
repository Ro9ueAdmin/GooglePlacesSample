package com.sagar.rest;

import com.sagar.rest.response.PlaceDetailsResponse;
import com.sagar.rest.response.PlaceSearchResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesApi {

    @GET("maps/api/place/nearbysearch/json")
    Single<PlaceSearchResponse> searchPlaces(
            @Query("key") String apiKey,
            @Query("location") String location,
            @Query("keyword") String keyword,
            @Query("radius") int radius,
            @Query("pagetoken") String pageToken
    );

    @GET("maps/api/place/details/json")
    Single<PlaceDetailsResponse> getPlaceDetails(
            @Query("key") String apiKey,
            @Query("placeid") String placeId
    );

}
