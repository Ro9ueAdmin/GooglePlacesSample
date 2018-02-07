package com.webonise.places;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;

import com.webonise.rest.response.PhotoResponseData;
import com.webonise.rest.response.PlaceDetailsResponseData;

import java.util.List;

@Entity(tableName = "place")
public class Place {

    @ColumnInfo(name = "icon")
    private String icon;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "place_id")
    private String placeId;
    @ColumnInfo(name = "rating")
    private int rating;
    @ColumnInfo(name = "vicinity")
    private String vicinity;
    private List<PhotoResponseData> photos;
    @ColumnInfo(name = "formatted_address")
    private String formattedAddress;
    @ColumnInfo(name = "formatted_phone_number")
    private String formattedPhoneNumber;

    public Place(PlaceDetailsResponseData responseData) {
        this.icon = responseData.icon;
        this.name = responseData.name;
        this.placeId = responseData.place_id;
        this.rating = responseData.rating;
        this.vicinity = responseData.vicinity;
        this.photos = responseData.photos;
        this.formattedAddress = responseData.formatted_address;
        this.formattedPhoneNumber = responseData.formatted_phone_number;
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

    public int getRating() {
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
}
