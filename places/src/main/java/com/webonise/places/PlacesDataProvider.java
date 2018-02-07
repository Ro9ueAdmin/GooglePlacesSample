package com.webonise.places;

import io.reactivex.Single;

public interface PlacesDataProvider {

    Single<Response<SearchResult>> search(SearchRequest searchRequest);

    Single<Response<Place>> getPlaceDetails(String placeId);

    void savePlace(Place place);

}
