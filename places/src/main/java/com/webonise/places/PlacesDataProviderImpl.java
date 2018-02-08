package com.webonise.places;

import android.content.Context;
import android.location.Location;

import com.webonise.places.constants.Constants;
import com.webonise.places.storage.PlacesLocalRepo;
import com.webonise.rest.PlacesApi;
import com.webonise.rest.ResponseCode;
import com.webonise.rest.response.PlaceDetailsResponse;
import com.webonise.rest.response.PlaceDetailsResponseData;
import com.webonise.rest.response.PlaceSearchResponse;
import com.webonise.util.ValidationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class PlacesDataProviderImpl implements PlacesDataProvider {

    private final Context context;
    private final PlacesApi placesApi;
    private final PlacesLocalRepo placesLocalRepo;

    public PlacesDataProviderImpl(Context context, PlacesApi placesApi, PlacesLocalRepo placesLocalRepo) {
        this.context = context;
        this.placesApi = placesApi;
        this.placesLocalRepo = placesLocalRepo;
    }

    @Override
    public Single<Response<SearchResult>> search(SearchRequest searchRequest) {
        return Single.just(searchRequest)
                .flatMap(new Function<SearchRequest, SingleSource<Response<SearchResult>>>() {
                    @Override
                    public SingleSource<Response<SearchResult>> apply(SearchRequest searchRequest) throws Exception {
                        if (ValidationUtil.isStringEmpty(searchRequest.getKeyword())) {
                            return fetchSavedPlacesList();
                        } else {
                            return fetchPlacesFromServer(searchRequest);
                        }
                    }
                });
    }

    @Override
    public Single<Response<Place>> getPlaceDetails(String placeId) {
        return placesApi.getPlaceDetails(Constants.API_KEY, placeId)
                .map(new Function<PlaceDetailsResponse, Response<Place>>() {
                    @Override
                    public Response<Place> apply(PlaceDetailsResponse response) throws Exception {
                        if (response == null || !response.status.equals(ResponseCode.Places.OK)) {
                            throw new PlaceException(Constants.ErrorType.EMPTY_LOCAL_PLACE_LIST);
                        }
                        Place place = new Place(response.result);
                        return new Response<>(Constants.ResponseSource.NETWORK, place);
                    }
                });
    }

    @Override
    public void savePlace(final Place place) {
        Completable
                .fromCallable(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        placesLocalRepo.insertDetails(place);
                        return null;
                    }
                })
                .onErrorComplete()
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private Single<Response<SearchResult>> fetchSavedPlacesList() {
        return placesLocalRepo.getCachePlaceList()
                .flatMap(new Function<List<Place>, SingleSource<Response<SearchResult>>>() {
                    @Override
                    public SingleSource<Response<SearchResult>> apply(List<Place> places) throws Exception {
                        if (ValidationUtil.isListEmpty(places)) {
                            return Single.error(new PlaceException(Constants.ErrorType.EMPTY_LOCAL_PLACE_LIST));
                        }
                        SearchResult searchResult = new SearchResult(places, null, null);
                        return Single.just(new Response<>(Constants.ResponseSource.LOCAL, searchResult));
                    }
                });
    }

    private Single<Response<SearchResult>> fetchPlacesFromServer(SearchRequest searchRequest) {
        return Single.just(searchRequest)
                .flatMap(new Function<SearchRequest, SingleSource<PlaceSearchResponse>>() {
                    @Override
                    public SingleSource<PlaceSearchResponse> apply(SearchRequest searchRequest) throws Exception {
                        return placesApi.searchPlaces(
                                Constants.API_KEY,
                                getFormattedLocation(searchRequest.getLocation()),
                                searchRequest.getKeyword(),
                                Constants.RADIUS,
                                searchRequest.getPageToken()
                        );
                    }
                })
                .flatMap(new Function<PlaceSearchResponse, SingleSource<Response<SearchResult>>>() {
                    @Override
                    public SingleSource<Response<SearchResult>> apply(PlaceSearchResponse response) throws Exception {
                        if (response == null ||
                                !response.status.equals(ResponseCode.Places.OK) ||
                                ValidationUtil.isListEmpty(response.results)) {
                            return Single.error(new PlaceException(response != null ? response.status : Constants.ErrorType.GOOGLE_API_ERROR));
                        }

                        List<Place> placeList = new ArrayList<>();
                        for (PlaceDetailsResponseData placeDetailsResponseData : response.results) {
                            placeList.add(new Place(placeDetailsResponseData));
                        }
                        String htmlAttribution = ValidationUtil.isListNotEmpty(response.html_attributions) ? response.html_attributions.get(0) : null;
                        SearchResult searchResult = new SearchResult(placeList, "", htmlAttribution);
                        return Single.just(new Response<>(Constants.ResponseSource.NETWORK, searchResult));
                    }
                });
    }

    private String getFormattedLocation(Location location) {
        if (location == null) {
            return null;
        } else {
            return location.getLatitude() + ", " + location.getLongitude();
        }
    }

}
