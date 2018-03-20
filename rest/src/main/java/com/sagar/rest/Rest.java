package com.sagar.rest;

import android.content.Context;

import com.facebook.stetho.Stetho;

public class Rest {

    public static void init(Context appContext) {
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(appContext);
        }
    }

}
