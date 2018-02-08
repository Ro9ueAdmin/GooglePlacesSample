package com.webonise.place_details;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.webonise.AppManager;
import com.webonise.BaseActivity;
import com.webonise.R;
import com.webonise.constants.Constants;
import com.webonise.databinding.ActivityPlaceDetailsBinding;
import com.webonise.places.Place;
import com.webonise.rest.response.PhotoResponseData;
import com.webonise.util.UIUtil;
import com.webonise.util.ValidationUtil;

import java.util.ArrayList;
import java.util.List;

public class PlaceDetailsActivity extends BaseActivity
        implements PlaceDetailsOps.View {

    private ActivityPlaceDetailsBinding binding;
    private GoogleMap googleMap;
    private Place place;
    private PhotosRecyclerAdapter photosRecyclerAdapter;
    private List<PhotoResponseData> photoResponseDataList;
    private PlaceDetailsOps placeDetailsOps;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_place_details);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(onMapReadyCallback);

        photoResponseDataList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        photosRecyclerAdapter = new PhotosRecyclerAdapter(this, photoResponseDataList);
        binding.recyclerView.setAdapter(photosRecyclerAdapter);

        String placeId = getIntent().getStringExtra(Constants.IntentKey.KEY_PLACE_ID);
        placeDetailsOps = new PlaceDetailsOps(
                this,
                AppManager.getAppModels().getPlacesDataProvider(),
                this,
                placeId
        );
        placeDetailsOps.loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        placeDetailsOps.cancel();
    }

    @Override
    public void showProgressBar() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        binding.progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showError(String message) {
        UIUtil.showMessage(this, message);
    }

    @Override
    public void showPlaceDetails(Place place) {
        this.place = place;
        showDataOnMap();
        if (ValidationUtil.isStringNotEmpty(place.getName())) {
            binding.title.setText(place.getName());
        }
        if (ValidationUtil.isListNotEmpty(place.getPhotos())) {
            binding.emptyView.setVisibility(View.INVISIBLE);
            photoResponseDataList.clear();
            photoResponseDataList.addAll(place.getPhotos());
            photosRecyclerAdapter.notifyDataSetChanged();
        } else {
            binding.emptyView.setVisibility(View.VISIBLE);
        }
    }

    private OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            PlaceDetailsActivity.this.googleMap = googleMap;
            showDataOnMap();
        }
    };

    private void showDataOnMap() {
        if (googleMap != null && place != null && place.getLatitude() != -1 && place.getLongitude() != -1) {
            LatLng selectedLocation = new LatLng(place.getLatitude(), place.getLongitude());
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(selectedLocation)
                    .title(place.getName())
            );
            marker.showInfoWindow();
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 16f));
        }
    }
}
