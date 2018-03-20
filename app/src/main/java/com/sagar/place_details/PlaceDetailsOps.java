package com.sagar.place_details;

import android.content.Context;

import com.sagar.places.Place;
import com.sagar.places.PlacesDataProvider;
import com.sagar.places.Response;
import com.sagar.places.factory.ErrorResponseFactory;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

class PlaceDetailsOps {

    interface View {
        void showProgressBar();
        void hideProgressBar();
        void showError(String message);
        void showPlaceDetails(Place place);
    }

    private View view;
    private PlacesDataProvider placesDataProvider;
    private Context context;
    private String placeId;
    private Disposable disposable;

    PlaceDetailsOps(View view, PlacesDataProvider placesDataProvider, Context context, String placeId) {
        this.view = view;
        this.placesDataProvider = placesDataProvider;
        this.context = context;
        this.placeId = placeId;
    }

    void loadData() {
        view.showProgressBar();
        disposable = placesDataProvider.getPlaceDetails(placeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response<Place>>() {
                    @Override
                    public void accept(Response<Place> response) throws Exception {
                        view.hideProgressBar();
                        if (response.isError()) {
                            view.showError(response.getErrorMessage());
                        } else {
                            view.showPlaceDetails(response.getData());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        view.hideProgressBar();
                        Response<Void> errorResponse = ErrorResponseFactory.createErrorResponse(context, throwable);
                        view.showError(errorResponse.getErrorMessage());
                    }
                });
    }

    void cancel() {
        if (disposable != null) {
            disposable.dispose();
        }
    }
}
