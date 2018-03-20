package com.sagar.util;

import android.net.Uri;
import android.widget.ImageView;

import com.sagar.constants.Constants;

public class ImageUtil {

    public static void load(String imageType, ImageView imageView, String imageUrl) {
        switch (imageType) {
            case Constants.ImageType.PLACE_ICON:
                imageView.setImageURI(Uri.parse(imageUrl));
                break;
            case Constants.ImageType.PLACE_PHOTO:
                String photoUri = createPhotoDownloadUrl(imageUrl);
                imageView.setImageURI(Uri.parse(photoUri));
                break;
            default:
                break;
        }
    }

    public static String createPhotoDownloadUrl(String reference) {
        return "https://maps.googleapis.com/maps/api/place/photo?"
                + "&maxwidth=100"
                + "&maxheight=100"
                + "&photoreference=" + reference
                + "&key=" + com.sagar.places.constants.Constants.API_KEY;
    }
}
