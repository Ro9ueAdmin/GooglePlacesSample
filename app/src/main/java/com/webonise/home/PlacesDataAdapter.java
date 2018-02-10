package com.webonise.home;

import android.content.Context;

import com.webonise.places.PlacesDataProvider;
import com.webonise.places.Response;
import com.webonise.places.SearchRequest;
import com.webonise.places.SearchResult;

import com.webonise.places.factory.ErrorResponseFactory;
import com.webonise.places.storage.PreferenceHelper;
import com.webonise.util.ValidationUtil;

// https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyAPEDYAIrLbUlPLSR4IRv-FTFbmc8EzNBo&location=location%3D12.947105,%2077.7033862&keyword=g&radius=1000
// https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&type=restaurant&keyword=cruise&key=YOUR_API_KEY

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

class PlacesDataAdapter {

    interface View {
        void clearAllData();
        void showProgressBar();
        void hideProgressBar();
    }

    private PlacesDataProvider placesDataProvider;
    private Context context;
    private PreferenceHelper preferenceHelper;
    private String keyword;
    private boolean hasMoreData;
    private boolean isLoading;
    private String nextPageToken;
    private Subject<Response<SearchResult>> searchResultDataSubject = PublishSubject.create();
    private View view;
    private Disposable fetchPlacesDisposable;

    PlacesDataAdapter(PlacesDataProvider placesDataProvider, Context context, PreferenceHelper preferenceHelper, View view) {
        this.placesDataProvider = placesDataProvider;
        this.context = context;
        this.preferenceHelper = preferenceHelper;
        this.view = view;
    }

    void setSearchKeyword(String keyword) {
        cancel();
        this.keyword = keyword;
        this.hasMoreData = true;
        this.isLoading = false;
        view.clearAllData();
        loadData();
    }

    boolean hasMoreData() {
        return hasMoreData;
    }

    Flowable<Response<SearchResult>> getDataFlowable() {
        return searchResultDataSubject.toFlowable(BackpressureStrategy.BUFFER)
                .doOnNext(new Consumer<Response<SearchResult>>() {
                    @Override
                    public void accept(Response<SearchResult> response) throws Exception {
                        SearchResult searchResult = response.getData();
                        if (searchResult != null) {
                            nextPageToken = searchResult.getNextPageToken();
                            hasMoreData = ValidationUtil.isStringNotEmpty(nextPageToken);
                        }
                    }
                });
    }

    void loadData() {
        if (hasMoreData && !isLoading) {
            SearchRequest searchRequest = new SearchRequest(
                    preferenceHelper.getLocation(),
                    keyword,
                    nextPageToken
            );
            isLoading = true;
            view.showProgressBar();
            fetchPlacesDisposable = placesDataProvider.search(searchRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Response<SearchResult>>() {
                        @Override
                        public void accept(Response<SearchResult> response) throws Exception {
                            view.hideProgressBar();
                            searchResultDataSubject.onNext(response);
                            isLoading = false;
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            Response<SearchResult> errorResult = ErrorResponseFactory.createErrorResponse(
                                    context,
                                    throwable
                            );
                            view.hideProgressBar();
                            searchResultDataSubject.onNext(errorResult);
                            isLoading = false;
                        }
                    });
        }
    }

    void cancel() {
        if (fetchPlacesDisposable != null) {
            fetchPlacesDisposable.dispose();
        }
        isLoading = false;
    }
}
