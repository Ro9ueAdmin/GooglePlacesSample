package com.sagar.places.storage;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import com.sagar.places.Place;

@Database(entities = {Place.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PlacesLocalRepo getPlacesLocalRepo();
}
