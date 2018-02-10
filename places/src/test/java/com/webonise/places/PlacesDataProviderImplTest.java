package com.webonise.places;

import com.google.gson.Gson;
import com.webonise.places.storage.PlacesLocalRepo;
import com.webonise.rest.PlacesApi;
import com.webonise.rest.response.PlaceDetailsResponse;
import com.webonise.rest.response.PlaceSearchResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class PlacesDataProviderImplTest {

    @Mock
    private PlacesLocalRepo placesLocalRepo;

    @Mock
    private PlacesApi placesApi;

    private PlacesDataProvider placesDataProvider;
    private Gson gson;

    @Before
    public void setUp() {
        placesDataProvider = new PlacesDataProviderImpl(placesApi, placesLocalRepo);
        gson = new Gson();
    }

    @Test
    public void test_SearchPlaces_WithNullResponse_By_PlacesAPI() {
        when(placesApi.searchPlaces(
                Matchers.anyString(),
                Matchers.anyString(),
                Matchers.anyString(),
                Matchers.anyInt(),
                Matchers.anyString()
        )).thenReturn(null);
        SearchRequest searchRequest = mock(SearchRequest.class);
        when(searchRequest.getKeyword()).thenReturn("gg");
        placesDataProvider.search(searchRequest)
                .test()
                .assertError(NullPointerException.class);
    }

    @Test
    public void test_SearchPlaces_With_Error_Status_Code_BY_API() {
        String dummyErrorResponse = createDummyResponse("error_response.json");
        PlaceSearchResponse placeSearchResponse = gson.fromJson(dummyErrorResponse, PlaceSearchResponse.class);
        when(placesApi.searchPlaces(
                Matchers.anyString(),
                Matchers.anyString(),
                Matchers.anyString(),
                Matchers.anyInt(),
                Matchers.anyString()
        )).thenReturn(Single.just(placeSearchResponse));
        SearchRequest searchRequest = mock(SearchRequest.class);
        when(searchRequest.getKeyword()).thenReturn("gg");
        placesDataProvider.search(searchRequest)
                .test()
                .assertError(PlaceException.class);
    }

    @Test
    public void test_SearchPlaces_With_Valid_PlacesApi_Response() {
        String dummyValidResponseString = createDummyResponse("place_search_response.json");
        PlaceSearchResponse placeSearchResponse = gson.fromJson(dummyValidResponseString, PlaceSearchResponse.class);
        Single<PlaceSearchResponse> dummySearchPlaceResponseSingle = Single.just(placeSearchResponse);
        when(placesApi.searchPlaces(
                Matchers.anyString(),
                Matchers.anyString(),
                Matchers.anyString(),
                Matchers.anyInt(),
                Matchers.anyString()
        )).thenReturn(dummySearchPlaceResponseSingle);
        SearchRequest searchRequest = mock(SearchRequest.class);
        when(searchRequest.getKeyword()).thenReturn("gg");
        List<Response<SearchResult>> searchResponseList = placesDataProvider.search(searchRequest)
                .test()
                .assertNoErrors()
                .values();
        Response<SearchResult> searchResponse = searchResponseList.get(0);
        assertNotNull(searchResponse);
        assertFalse(searchResponse.isError());
        SearchResult searchResult = searchResponse.getData();
        assertNotNull(searchResult);
        List<Place> places = searchResult.getPlaces();
        assertNotNull(places);
        assertEquals(1, places.size());
        Place place = places.get(0);
        assertNotNull(place);
        assertEquals("https://maps.gstatic.com/mapfiles/place_api/icons/generic_business-71.png", place.getIcon());
        assertEquals("Vedansh computer repairs", place.getName());
    }

    @Test
    public void test_SearchPlaces_When_Keyword_Emptyu_Local_Places_Exists() {
        Place place1 = mock(Place.class);
        when(place1.getName()).thenReturn("Apna Ghar");
        Place place2 = mock(Place.class);
        List<Place> placeList = new ArrayList<>();
        placeList.add(place1);
        placeList.add(place2);
        Single<List<Place>> placeListSingle = Single.just(placeList);
        when(placesLocalRepo.getCachePlaceList()).thenReturn(placeListSingle);

        SearchRequest searchRequest = mock(SearchRequest.class);
        when(searchRequest.getKeyword()).thenReturn("");

        List<Response<SearchResult>> searchResponseList = placesDataProvider.search(searchRequest)
                .test()
                .assertNoErrors()
                .values();

        Response<SearchResult> searchResponse = searchResponseList.get(0);
        assertNotNull(searchResponse);
        assertFalse(searchResponse.isError());
        SearchResult searchResult = searchResponse.getData();
        assertNotNull(searchResult);
        List<Place> places = searchResult.getPlaces();
        assertNotNull(places);
        assertEquals(2, places.size());
        Place place = places.get(0);
        assertNotNull(place);
        assertEquals("Apna Ghar", place.getName());
    }

    @Test
    public void test_SearchPlaces_When_Keyword_Empty_And_Local_List_Empty() {
        List<Place> placeList = new ArrayList<>();
        Single<List<Place>> placeListSingle = Single.just(placeList);
        when(placesLocalRepo.getCachePlaceList()).thenReturn(placeListSingle);

        SearchRequest searchRequest = mock(SearchRequest.class);
        when(searchRequest.getKeyword()).thenReturn("");

        placesDataProvider.search(searchRequest)
                .test()
                .assertError(PlaceException.class);
    }

    @Test
    public void test_GetPlacesDetails_When_Dummy_Response() {
        String dummyValidResponseString = createDummyResponse("place_details_response.json");
        PlaceDetailsResponse placeDetailsResponse = gson.fromJson(dummyValidResponseString, PlaceDetailsResponse.class);
        when(placesApi.getPlaceDetails(
                Matchers.anyString(),
                Matchers.anyString()
        )).thenReturn(Single.just(placeDetailsResponse));

        List<Response<Place>> placeResponseList = placesDataProvider.getPlaceDetails("asdfgr")
                .test()
                .values();
        Response<Place> placeResponse = placeResponseList.get(0);

        assertNotNull(placeResponse);
        assertFalse(placeResponse.isError());
        Place place = placeResponse.getData();
        assertNotNull(place);
        assertEquals("ChIJb0DYPTMSrjsRsnSY4EZpL1A", place.getPlaceId());
        assertEquals("GG Luggage", place.getName());
        assertEquals("Varthur Rd, Sanjay Nagar, Marathahalli, Bengaluru, Karnataka 560037, India", place.getFormattedAddress());
    }

    @Test
    public void test_GetPlacesDetails_With_Error_Status_Code_BY_API() {
        String dummyValidResponseString = createDummyResponse("error_response.json");
        PlaceDetailsResponse placeDetailsResponse = gson.fromJson(dummyValidResponseString, PlaceDetailsResponse.class);
        when(placesApi.getPlaceDetails(
                Matchers.anyString(),
                Matchers.anyString()
        )).thenReturn(Single.just(placeDetailsResponse));

        placesDataProvider.getPlaceDetails("asdfgr")
                .test()
                .assertError(PlaceException.class);
    }

    private String createDummyResponse(String fileName) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName);
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}
