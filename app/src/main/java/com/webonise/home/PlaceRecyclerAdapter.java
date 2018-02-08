package com.webonise.home;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.webonise.R;
import com.webonise.constants.Constants;
import com.webonise.place_details.PlaceDetailsActivity;
import com.webonise.places.Place;
import com.webonise.util.ImageUtil;
import com.webonise.util.ValidationUtil;

import java.util.List;

public class PlaceRecyclerAdapter extends RecyclerView.Adapter<PlaceRecyclerAdapter.PlaceViewHolder> {

    private Context context;
    private final LayoutInflater layoutInflater;
    private final List<Place> places;

    PlaceRecyclerAdapter(Context context, List<Place> places) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.places = places;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_place, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {
        final Place place = places.get(position);
        if (ValidationUtil.isStringNotEmpty(place.getName())) {
            holder.placeNameView.setText(place.getName());
        }
        if (ValidationUtil.isStringNotEmpty(place.getIcon())) {
            ImageUtil.load(Constants.ImageType.PLACE_ICON, holder.placeIconView, place.getIcon());
        }
        if (ValidationUtil.isStringNotEmpty(place.getVicinity())) {
            holder.placeVicinityView.setText(place.getVicinity());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PlaceDetailsActivity.class);
                intent.putExtra(Constants.IntentKey.KEY_PLACE_ID, place.getPlaceId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    static class PlaceViewHolder extends RecyclerView.ViewHolder {

        private ImageView placeIconView;
        private TextView placeNameView, placeVicinityView;

        PlaceViewHolder(View itemView) {
            super(itemView);
            placeIconView = itemView.findViewById(R.id.place_icon);
            placeNameView = itemView.findViewById(R.id.place_name);
            placeVicinityView = itemView.findViewById(R.id.place_vicinity);
        }
    }

}
