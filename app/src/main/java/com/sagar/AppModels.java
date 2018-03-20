package com.sagar;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sagar.places.PlacesDataProvider;
import com.sagar.places.PlacesDataProviderImpl;
import com.sagar.places.storage.AppDatabase;
import com.sagar.places.storage.PlacesLocalRepo;
import com.sagar.places.storage.PreferenceHelper;
import com.sagar.rest.PlacesApi;
import com.sagar.rest.Rest;
import com.sagar.rest.ServiceGenerator;
import com.sagar.rest.ServiceSettings;

public class AppModels {

    private static final String DB_NAME = "webonise.db";

    private final PreferenceHelper preferenceHelper;
    private final PlacesDataProvider placesDataProvider;

    AppModels(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferenceHelper = new PreferenceHelper(sharedPreferences);

        Rest.init(context);

        ServiceSettings serviceSettings = new ServiceSettings("https", "maps.googleapis.com", null);
        ServiceGenerator serviceGenerator = new ServiceGenerator(serviceSettings);
        PlacesApi placesApi = serviceGenerator.getApi(PlacesApi.class);

        AppDatabase database = Room.databaseBuilder(context, AppDatabase.class, DB_NAME).build();
        PlacesLocalRepo placesLocalRepo = database.getPlacesLocalRepo();
        placesDataProvider = new PlacesDataProviderImpl(placesApi, placesLocalRepo);
    }

    public PreferenceHelper getPreferenceHelper() {
        return preferenceHelper;
    }

    public PlacesDataProvider getPlacesDataProvider() {
        return placesDataProvider;
    }
}
