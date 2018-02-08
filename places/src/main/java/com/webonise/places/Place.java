package com.webonise.places;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.webonise.rest.response.GeometryResponseData;
import com.webonise.rest.response.LocationResponseData;
import com.webonise.rest.response.PhotoResponseData;
import com.webonise.rest.response.PlaceDetailsResponseData;

import java.util.List;

@Entity(tableName = "place")
public class Place {

    @ColumnInfo(name = "icon")
    private String icon;
    @ColumnInfo(name = "name")
    private String name;
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "place_id")
    private String placeId;
    @ColumnInfo(name = "rating")
    private double rating;
    @ColumnInfo(name = "vicinity")
    private String vicinity;
    @Ignore
    private List<PhotoResponseData> photos;
    @ColumnInfo(name = "formatted_address")
    private String formattedAddress;
    @ColumnInfo(name = "formatted_phone_number")
    private String formattedPhoneNumber;
    @ColumnInfo(name = "latitude")
    private double latitude;
    @ColumnInfo(name = "longitude")
    private double longitude;

    public Place() {

    }

    public Place(PlaceDetailsResponseData responseData) {
        this.icon = responseData.icon;
        this.name = responseData.name;
        this.placeId = responseData.place_id;
        this.rating = responseData.rating;
        this.vicinity = responseData.vicinity;
        this.photos = responseData.photos;
        this.formattedAddress = responseData.formatted_address;
        this.formattedPhoneNumber = responseData.formatted_phone_number;
        GeometryResponseData geometryResponseData = responseData.geometry;
        if (geometryResponseData != null) {
            LocationResponseData locationResponseData = geometryResponseData.location;
            if (locationResponseData != null) {
                this.latitude = locationResponseData.lat;
                this.longitude = locationResponseData.lng;
            }
        }
    }

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getPlaceId() {
        return placeId;
    }

    public double getRating() {
        return rating;
    }

    public String getVicinity() {
        return vicinity;
    }

    public List<PhotoResponseData> getPhotos() {
        return photos;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public String getFormattedPhoneNumber() {
        return formattedPhoneNumber;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public void setFormattedPhoneNumber(String formattedPhoneNumber) {
        this.formattedPhoneNumber = formattedPhoneNumber;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
