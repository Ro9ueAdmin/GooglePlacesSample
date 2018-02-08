package com.webonise;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.webonise.places.PlacesDataProvider;
import com.webonise.places.PlacesDataProviderImpl;
import com.webonise.places.storage.AppDatabase;
import com.webonise.places.storage.PlacesLocalRepo;
import com.webonise.places.storage.PreferenceHelper;
import com.webonise.rest.PlacesApi;
import com.webonise.rest.Rest;
import com.webonise.rest.ServiceGenerator;
import com.webonise.rest.ServiceSettings;

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
