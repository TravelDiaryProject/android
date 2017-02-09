package com.traveldiary.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.traveldiary.android.R;
import com.traveldiary.android.essence.Place;
import com.traveldiary.android.essence.Trip;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private final View.OnClickListener mOnClickListener;

    private String ROOT_URL = "http://188.166.77.89/";

    private Context mContext;
    private List<Trip> mTripsList;
    private List<Place> mPlaceList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.tripTitleView);
            imageView = (ImageView) view.findViewById(R.id.tripImageView);
        }
    }

    public RecyclerAdapter(Context mContext, List<Trip> tripList, View.OnClickListener onClickListener, List<Place> placeList ) {
        this.mContext = mContext;
        this.mTripsList = tripList;
        mOnClickListener = onClickListener;
        this.mPlaceList = placeList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trip_card, parent, false);

        itemView.setOnClickListener(mOnClickListener);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (mTripsList != null) {
            Trip trip = mTripsList.get(position);

            holder.title.setText(trip.getTitle());
            Glide.with(mContext).load(ROOT_URL + trip.getPhoto())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imageView);

        }else if (mPlaceList != null){
            Place place = mPlaceList.get(position);

            //holder.title.setText("Latitude = " + place.getLatitude() + " Longitude = " + place.getLongitude());
            Glide.with(mContext).load(ROOT_URL + place.getPhoto())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        int size = 0;
        if (mTripsList==null){
            size = mPlaceList.size();
        }else if (mPlaceList==null){
            size = mTripsList.size();
        }
        return size;
    }
}