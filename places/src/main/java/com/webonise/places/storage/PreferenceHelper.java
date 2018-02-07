package com.webonise.places.storage;

import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.webonise.places.constants.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class PreferenceHelper {

    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";

    private SharedPreferences sharedPreferences;

    public void saveLocation(Location location) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(LATITUDE, location.getLatitude());
            jsonObject.put(LONGITUDE, location.getLongitude());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constants.PreferenceKey.LOCATION, jsonObject.toString());
            editor.apply();
        } catch (JSONException e) {
            Log.d("PreferenceHelper", "Json exception while encoding location");
        }
    }

    public Location getLocation() {
        String locationString = sharedPreferences.getString(Constants.PreferenceKey.LOCATION, "");
        try {
            JSONObject jsonObject = new JSONObject(locationString);
            double latitude = jsonObject.getDouble(LATITUDE);
            double longitude = jsonObject.getDouble(LONGITUDE);
            Location location = new Location("");
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            return location;
        } catch (JSONException e) {
            Log.d("PreferenceHelper", "Json exception while decoding location");
        }
        return null;
    }

}
