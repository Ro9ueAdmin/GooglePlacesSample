package com.webonise.places.location;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;

import com.webonise.places.storage.PreferenceHelper;
import com.yayandroid.locationmanager.LocationManager;
import com.yayandroid.locationmanager.configuration.DefaultProviderConfiguration;
import com.yayandroid.locationmanager.configuration.GooglePlayServicesConfiguration;
import com.yayandroid.locationmanager.configuration.LocationConfiguration;
import com.yayandroid.locationmanager.configuration.PermissionConfiguration;
import com.yayandroid.locationmanager.constants.ProviderType;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

public class LocationChecker {

    private LocationManager locationManager;
    private PreferenceHelper preferenceHelper;

    public LocationChecker() {
    }

    public Flowable<Location> getLocation(final Activity activity) {
        final LocationConfiguration locationConfigurations = new LocationConfiguration.Builder()
                .keepTracking(false)
                .askForPermission(new PermissionConfiguration.Builder()
                        .build())
                .useGooglePlayServices(new GooglePlayServicesConfiguration.Builder()
                        .askForGooglePlayServices(false)
                        .askForSettingsApi(true)
                        .failOnConnectionSuspended(true)
                        .failOnSettingsApiSuspended(false)
                        .ignoreLastKnowLocation(false)
                        .setWaitPeriod(20 * 1000)
                        .build())
                .useDefaultProviders(new DefaultProviderConfiguration.Builder()
                        .requiredTimeInterval(5 * 60 * 1000)
                        .requiredDistanceInterval(0)
                        .acceptableAccuracy(5.0f)
                        .acceptableTimePeriod(5 * 60 * 1000)
                        .gpsMessage("Turn on GPS?")
                        .setWaitPeriod(ProviderType.GPS, 20 * 1000)
                        .setWaitPeriod(ProviderType.NETWORK, 20 * 1000)
                        .build())
                .build();


        return Flowable.create(new FlowableOnSubscribe<Location>() {
            @Override
            public void subscribe(final FlowableEmitter<Location> e) throws Exception {
                LocationManager.enableLog(true);
                LocationListenerAdapter locationListenerAdapter = new LocationListenerAdapter() {
                    @Override
                    public void onLocationChanged(Location location) {
                        preferenceHelper.saveLocation(location);
                        e.onNext(location);
                        e.onComplete();
                    }

                    @Override
                    public void onLocationFailed(int type) {
                        e.onError(new Throwable("Error finding location. Type: " + type));
                    }
                };
                locationManager = new LocationManager.Builder(activity.getApplicationContext())
                        .activity(activity)
                        .configuration(locationConfigurations)
                        .notify(locationListenerAdapter)
                        .build();
                locationManager.get();
            }
        }, BackpressureStrategy.BUFFER);
    }

    public void cancel() {
        if (locationManager != null) {
            locationManager.cancel();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (locationManager != null) {
            locationManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (locationManager != null) {
            locationManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
