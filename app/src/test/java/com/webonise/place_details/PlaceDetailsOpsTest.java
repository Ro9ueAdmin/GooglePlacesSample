package com.webonise.place_details;

import android.content.Context;

import com.webonise.places.Place;
import com.webonise.places.PlacesDataProvider;
import com.webonise.places.Response;
import com.webonise.places.constants.Constants;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.concurrent.Callable;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class PlaceDetailsOpsTest {

    @Mock
    private PlaceDetailsOps.View view;

    @Mock
    private PlacesDataProvider placesDataProvider;

    @Mock
    private Context context;

    private PlaceDetailsOps detailsOps;

    @BeforeClass
    public static void setUpBeforeClass() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(new Function<Callable<Scheduler>, Scheduler>() {
            @Override
            public Scheduler apply(Callable<Scheduler> schedulerCallable) throws Exception {
                return Schedulers.trampoline();
            }
        });
    }

    @Before
    public void setUp() {
        detailsOps = new PlaceDetailsOps(view, placesDataProvider, context, "id");
    }

    @Test
    public void test_LoadData_With_Success_Response() {
        Place mockPlace = mock(Place.class);
        Response<Place> mockPlaceResponse = new Response<>(Constants.ResponseSource.NETWORK, mockPlace);
        when(placesDataProvider.getPlaceDetails("id")).thenReturn(Single.just(mockPlaceResponse));

        detailsOps.loadData();

        verify(view, times(1)).showProgressBar();
        verify(view, times(1)).hideProgressBar();

        ArgumentCaptor<Place> placeArgumentCaptor = ArgumentCaptor.forClass(Place.class);
        verify(view, times(1)).showPlaceDetails(placeArgumentCaptor.capture());
        Place actualPlace = placeArgumentCaptor.getValue();
        assertNotNull(actualPlace);
        assertEquals(mockPlace, actualPlace);
    }

    @Test
    public void test_LoadData_With_Error_Response() {
        Response<Place> mockPlaceResponse = (Response<Place>) mock(Response.class);
        when(mockPlaceResponse.isError()).thenReturn(true);
        when(placesDataProvider.getPlaceDetails("id")).thenReturn(Single.just(mockPlaceResponse));

        detailsOps.loadData();

        verify(view, times(1)).showProgressBar();
        verify(view, times(1)).hideProgressBar();

        ArgumentCaptor<String> errorResponseArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(view, times(1)).showError(errorResponseArgumentCaptor.capture());
    }
}
