package com.sagar.home;

import android.content.Context;
import android.location.Location;

import com.sagar.places.Place;
import com.sagar.places.PlacesDataProvider;
import com.sagar.places.Response;
import com.sagar.places.SearchRequest;
import com.sagar.places.SearchResult;
import com.sagar.places.constants.Constants;
import com.sagar.places.storage.PreferenceHelper;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class PlacesDataAdapterTest {

    @Mock
    private PlacesDataProvider placesDataProvider;

    @Mock
    private PreferenceHelper preferenceHelper;

    @Mock
    private Context context;

    @Mock
    private PlacesDataAdapter.View view;

    private PlacesDataAdapter placesDataAdapter;

    @BeforeClass
    public static void setUpClass() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(new Function<Callable<Scheduler>, Scheduler>() {
            @Override
            public Scheduler apply(Callable<Scheduler> schedulerCallable) throws Exception {
                return Schedulers.trampoline();
            }
        });
    }

    @Before
    public void setUp() {
        Location location = mock(Location.class);
        when(location.getLatitude()).thenReturn(73.333);
        when(location.getLongitude()).thenReturn(-122.3232);
        when(preferenceHelper.getLocation()).thenReturn(location);
        placesDataAdapter = new PlacesDataAdapter(placesDataProvider, context, preferenceHelper, view);
    }

    @Test
    public void test_SetKeyword() {
        List<Place> placeList = new ArrayList<>();
        Place place1 = mock(Place.class);
        when(place1.getName()).thenReturn("tester");
        placeList.add(place1);
        SearchResult searchResult = new SearchResult(placeList, "", "");
        Response<SearchResult> searchResultResponseDummy = new Response<>(Constants.ResponseSource.NETWORK, searchResult);
        Single<Response<SearchResult>> searchResultResponseSingle = Single.just(searchResultResponseDummy);
        when(placesDataProvider.search(Matchers.any(SearchRequest.class))).thenReturn(searchResultResponseSingle);

        ArgumentCaptor<SearchRequest> searchRequestArgumentCaptor = ArgumentCaptor.forClass(SearchRequest.class);

        List<Response<SearchResult>> searchResultResponseList = placesDataAdapter.getDataFlowable()
                .test()
                .values();

        placesDataAdapter.setSearchKeyword("gg");

        verify(view, times(1)).clearAllData();
        verify(view, times(1)).showProgressBar();
        verify(placesDataProvider, times(1)).search(searchRequestArgumentCaptor.capture());
        SearchRequest searchRequest = searchRequestArgumentCaptor.getValue();
        assertNotNull(searchRequest);
        assertEquals("gg", searchRequest.getKeyword());

        assertNotNull(searchResultResponseList);
        assertEquals(1, searchResultResponseList.size());
        Response<SearchResult> searchResultResponse = searchResultResponseList.get(0);
        assertNotNull(searchResultResponse);
        SearchResult actualSearchResult = searchResultResponse.getData();
        assertNotNull(actualSearchResult);
        Place actualPlace = actualSearchResult.getPlaces().get(0);
        assertNotNull(actualPlace);
        assertEquals("tester", actualPlace.getName());
    }

    @Test
    public void test_SetKeyword_WhenNowPlaceFoundMatchingKeyword() {
        Single<Response<SearchResult>> searchResultResponseSingle = Single.error(new Throwable(""));
        when(placesDataProvider.search(Matchers.any(SearchRequest.class))).thenReturn(searchResultResponseSingle);

        ArgumentCaptor<SearchRequest> searchRequestArgumentCaptor = ArgumentCaptor.forClass(SearchRequest.class);

        List<Response<SearchResult>> searchResultResponseList = placesDataAdapter.getDataFlowable()
                .test()
                .values();

        placesDataAdapter.setSearchKeyword("gg");

        verify(view, times(1)).clearAllData();
        verify(view, times(1)).showProgressBar();
        verify(view, times(1)).hideProgressBar();
        verify(placesDataProvider, times(1)).search(searchRequestArgumentCaptor.capture());

        assertNotNull(searchResultResponseList);
        assertEquals(1, searchResultResponseList.size());
        Response<SearchResult> searchResultResponse = searchResultResponseList.get(0);
        assertNotNull(searchResultResponse);
        assertTrue(searchResultResponse.isError());
    }

    @Test
    public void test_HasMoreData() {
        List<Place> placeList = new ArrayList<>();
        Place place1 = mock(Place.class);
        when(place1.getName()).thenReturn("tester");
        placeList.add(place1);
        SearchResult searchResult = new SearchResult(placeList, "nextpagetoken", "");
        Response<SearchResult> searchResultResponseDummy = new Response<>(Constants.ResponseSource.NETWORK, searchResult);
        Single<Response<SearchResult>> searchResultResponseSingle = Single.just(searchResultResponseDummy);
        when(placesDataProvider.search(Matchers.any(SearchRequest.class))).thenReturn(searchResultResponseSingle);

        assertFalse(placesDataAdapter.hasMoreData());

        placesDataAdapter.setSearchKeyword("search_keyword");

        assertTrue(placesDataAdapter.hasMoreData());
    }

    @Test
    public void test_LoadData() {
        List<Place> placeList = new ArrayList<>();
        Place place1 = mock(Place.class);
        when(place1.getName()).thenReturn("tester");
        placeList.add(place1);
        SearchResult searchResult = new SearchResult(placeList, "", "");
        Response<SearchResult> searchResultResponseDummy = new Response<>(Constants.ResponseSource.NETWORK, searchResult);
        Single<Response<SearchResult>> searchResultResponseSingle = Single.just(searchResultResponseDummy);
        when(placesDataProvider.search(Matchers.any(SearchRequest.class))).thenReturn(searchResultResponseSingle);

        ArgumentCaptor<SearchRequest> searchRequestArgumentCaptor = ArgumentCaptor.forClass(SearchRequest.class);

        List<Response<SearchResult>> searchResultResponseList = placesDataAdapter.getDataFlowable()
                .test()
                .values();

        placesDataAdapter.setSearchKeyword("gg");

        verify(view, times(1)).clearAllData();
        verify(view, times(1)).showProgressBar();
        verify(placesDataProvider, times(1)).search(searchRequestArgumentCaptor.capture());
        SearchRequest searchRequest = searchRequestArgumentCaptor.getValue();
        assertNotNull(searchRequest);
        assertEquals("gg", searchRequest.getKeyword());

        assertNotNull(searchResultResponseList);
        assertEquals(1, searchResultResponseList.size());
        Response<SearchResult> searchResultResponse = searchResultResponseList.get(0);
        assertNotNull(searchResultResponse);
        SearchResult actualSearchResult = searchResultResponse.getData();
        assertNotNull(actualSearchResult);
        Place actualPlace = actualSearchResult.getPlaces().get(0);
        assertNotNull(actualPlace);
        assertEquals("tester", actualPlace.getName());
    }
}
