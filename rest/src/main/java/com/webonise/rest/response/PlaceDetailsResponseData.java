package com.webonise.rest.response;

import java.util.Collections;
import java.util.List;

public class PlaceDetailsResponseData {

    public String icon;
    public String name;
    public String place_id;
    public int rating;
    public String vicinity;
    public List<PhotoResponseData> photos;
    public String formatted_address;
    public String formatted_phone_number;

    PlaceDetailsResponseData() {
        icon = "";
        name = "";
        place_id = "";
        rating = 0;
        vicinity = "";
        photos = Collections.emptyList();
        formatted_address = "";
        formatted_phone_number = "";
    }

}
