package com.webonise.places.storage;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.webonise.places.Place;

@Database(entities = {Place.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PlacesLocalRepo getPlacesLocalRepo();
}
