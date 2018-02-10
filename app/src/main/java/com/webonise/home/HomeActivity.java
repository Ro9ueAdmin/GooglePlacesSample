package com.webonise.home;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.webonise.AppManager;
import com.webonise.AppModels;
import com.webonise.BaseActivity;
import com.webonise.R;
import com.webonise.databinding.ActivityHomeBinding;
import com.webonise.places.Place;
import com.webonise.places.Response;
import com.webonise.places.SearchResult;
import com.webonise.places.location.LocationChecker;
import com.webonise.util.UIUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class HomeActivity extends BaseActivity
        implements PlacesDataAdapter.View {

    private ActivityHomeBinding binding;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private PlacesDataAdapter placesDataAdapter;
    private List<Place> places;
    private PlaceRecyclerAdapter placeRecyclerAdapter;
    private LocationChecker locationChecker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        setUpToolbar("", true);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Initialize Search View
        binding.searchView.setIconifiedByDefault(false);
        View searchPlate = binding.searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        searchPlate.setBackgroundResource(R.drawable.search_view_bg);

        // Initialize Recycler View
        places = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        placeRecyclerAdapter = new PlaceRecyclerAdapter(this, places);
        binding.recyclerView.setAdapter(placeRecyclerAdapter);

        // Initialize data to display
        AppModels appModels = AppManager.getAppModels();
        placesDataAdapter = new PlacesDataAdapter(
                appModels.getPlacesDataProvider(),
                this,
                appModels.getPreferenceHelper(),
                this
        );
        subscribeForPlacesUpdates();

        // Initialize location client
        locationChecker = new LocationChecker(appModels.getPreferenceHelper());
        fetchLocation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (locationChecker != null) {
            locationChecker.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (locationChecker != null) {
            locationChecker.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        placesDataAdapter.cancel();
        compositeDisposable.dispose();
    }

    @Override
    public void clearAllData() {
        places.clear();
        placeRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void showProgressBar() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        binding.progressBar.setVisibility(View.INVISIBLE);
    }

    private void subscribeForPlacesUpdates() {
        compositeDisposable.add(RxSearchObservable.fromView(binding.searchView)
                .debounce(300, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String newKeyword) throws Exception {
                        placesDataAdapter.setSearchKeyword(newKeyword);
                    }
                }));

        compositeDisposable.add(placesDataAdapter.getDataFlowable()
                .subscribe(new Consumer<Response<SearchResult>>() {
                    @Override
                    public void accept(Response<SearchResult> response) throws Exception {
                        if (response.isError()) {
                            UIUtil.showMessage(HomeActivity.this, response.getErrorMessage());
                        } else {
                            SearchResult searchResult = response.getData();
                            List<Place> placeList = searchResult.getPlaces();
                            places.addAll(placeList);
                            placeRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        UIUtil.showMessage(HomeActivity.this, "Error: " + throwable.getMessage());
                    }
                }));
        placesDataAdapter.setSearchKeyword("");
    }

    private void fetchLocation() {
        compositeDisposable.add(locationChecker.getLocation(this)
                .subscribe(new Consumer<Location>() {
                    @Override
                    public void accept(Location location) throws Exception {
                        UIUtil.showMessage(HomeActivity.this, "Location found");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        UIUtil.showMessage(HomeActivity.this, "Location Error: " + throwable.getMessage());
                    }
                }));
    }

}
