package com.webonise.place_details;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.webonise.R;
import com.webonise.constants.Constants;
import com.webonise.rest.response.PhotoResponseData;
import com.webonise.util.ImageUtil;
import com.webonise.util.ValidationUtil;

import java.util.List;

public class PhotosRecyclerAdapter extends RecyclerView.Adapter<PhotosRecyclerAdapter.PhotoViewHolder> {

    private LayoutInflater layoutInflater;
    private List<PhotoResponseData> photoList;

    PhotosRecyclerAdapter(Context context, List<PhotoResponseData> photoList) {
        layoutInflater = LayoutInflater.from(context);
        this.photoList = photoList;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        PhotoResponseData photoResponseData = photoList.get(position);
        if (ValidationUtil.isStringNotEmpty(photoResponseData.photo_reference)) {
            ImageUtil.load(
                    Constants.ImageType.PLACE_PHOTO,
                    holder.imageView,
                    photoResponseData.photo_reference
            );
        }
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        PhotoViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }

}
