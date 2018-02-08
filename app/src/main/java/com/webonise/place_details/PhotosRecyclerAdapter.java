package com.webonise.place_details;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.webonise.R;
import com.webonise.constants.Constants;
import com.webonise.download.DownloadHelper;
import com.webonise.rest.response.PhotoResponseData;
import com.webonise.util.ImageUtil;
import com.webonise.util.ValidationUtil;

import java.util.List;

public class PhotosRecyclerAdapter extends RecyclerView.Adapter<PhotosRecyclerAdapter.PhotoViewHolder> {

    private LayoutInflater layoutInflater;
    private List<PhotoResponseData> photoList;
    private DownloadHelper downloadHelper;

    PhotosRecyclerAdapter(Context context, List<PhotoResponseData> photoList) {
        layoutInflater = LayoutInflater.from(context);
        this.photoList = photoList;
        RxPermissions rxPermissions = new RxPermissions((Activity) context);
        downloadHelper = new DownloadHelper(context, rxPermissions);
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        final PhotoResponseData photoResponseData = photoList.get(position);
        if (ValidationUtil.isStringNotEmpty(photoResponseData.photo_reference)) {
            ImageUtil.load(
                    Constants.ImageType.PLACE_PHOTO,
                    holder.imageView,
                    photoResponseData.photo_reference
            );
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    downloadHelper.startDownloading(
                            ImageUtil.createPhotoDownloadUrl(
                                    photoResponseData.photo_reference
                            ),
                            String.valueOf(System.currentTimeMillis())
                    );
                    return true;
                }
            });
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
