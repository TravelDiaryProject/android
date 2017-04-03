package com.traveldiary.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.traveldiary.android.R;
import com.traveldiary.android.model.Place;
import com.traveldiary.android.model.Trip;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //private final View.OnClickListener mOnClickListener;

    private static final int TYPE_TRIP = 0;
    private static final int TYPE_PLACE = 1;

    private int mSelectedItemPosition = -1;

    private String ROOT_URL = "http://188.166.77.89/";

    private Context mContext;
    private List<Trip> mTripsList;
    private List<Place> mPlaceList;

    private ItemClickListener itemClickListener = null;

    private ProgressBar mProgressBar;

    public RecyclerAdapter(Context mContext, List<Trip> tripList, ItemClickListener itemClickListener /*View.OnClickListener onClickListener*/, List<Place> placeList ) {
        this.mContext = mContext;
        this.mTripsList = tripList;
        //mOnClickListener = onClickListener;
        this.itemClickListener = itemClickListener;
        this.mPlaceList = placeList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType){
            case TYPE_PLACE:
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.place_card, parent, false);
                return new PlaceHolder(itemView);
            case TYPE_TRIP:
                View itemView2 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.trip_card, parent, false);
                return new MyViewHolder(itemView2);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof PlaceHolder){
            PlaceHolder placeHolder = (PlaceHolder) holder;
            placeHolder.bindData(mPlaceList.get(position), position);

        }else if (holder instanceof MyViewHolder){
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            mProgressBar.setVisibility(View.VISIBLE);

            Trip trip = mTripsList.get(position);

            myViewHolder.title.setText(trip.getTitle());
            Glide.with(mContext).load(ROOT_URL + trip.getPhoto())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            mProgressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .thumbnail(0.1f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(myViewHolder.imageView);

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

    @Override
    public int getItemViewType(int position) {
        if (mTripsList!=null)
            return TYPE_TRIP;
        else
            return TYPE_PLACE;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView title;
        private ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            title = (TextView) view.findViewById(R.id.tripTitleView);
            imageView = (ImageView) view.findViewById(R.id.tripImageView);
            mProgressBar = (ProgressBar) view.findViewById(R.id.card_progress);

            //title.setOnClickListener(this);
            //imageView.setOnClickListener(this);
        }

        public void bindData(){

        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(v, this.getLayoutPosition());
        }
    }

    public interface ItemClickListener {
        void onItemClick(View view, int possition);
    }

    public class PlaceHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView imageView;
        private ImageView placeLikeButton;
        private ImageView placeAddToFutureButton;
        private ImageView placeShowInMapButton;

        private Place place;
        private int possition;

        public PlaceHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.placeImageView);
            placeLikeButton = (ImageView) view.findViewById(R.id.placeLikeButton);
            placeAddToFutureButton = (ImageView) view.findViewById(R.id.placeAddToFutureButton);
            placeShowInMapButton = (ImageView) view.findViewById(R.id.placeShowInMapButton);

            mProgressBar = (ProgressBar) view.findViewById(R.id.card_progress);

            imageView.setOnClickListener(this);
            placeLikeButton.setOnClickListener(this);
            placeAddToFutureButton.setOnClickListener(this);
            placeShowInMapButton.setOnClickListener(this);
        }

        public void bindData(Place place, int currentPosition){
            this.possition = currentPosition;
            this.place = place;
            possition = currentPosition;


            mProgressBar.setVisibility(View.VISIBLE);
            //Place place = mPlaceList.get(position);

            Glide.with(mContext).load(ROOT_URL + place.getPhoto())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            mProgressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);

            if (place.selectedLike){
                placeLikeButton.setImageResource(R.drawable.ic_plus_one_black_24dp);
            }else {
                placeLikeButton.setImageResource(R.drawable.ic_plus_one_red_24dp);
            }

            if (place.selectedAddTrip){
                placeAddToFutureButton.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);
            }else {
                placeAddToFutureButton.setImageResource(R.drawable.ic_add_circle_outline_red_24dp);
            }

            placeShowInMapButton.setImageResource(R.drawable.ic_location_on_red_24dp);

        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.placeLikeButton:
                    if (place.selectedLike){
                        place.selectedLike = false;
                        placeLikeButton.setImageResource(R.drawable.ic_plus_one_red_24dp);
                    }else {
                        place.selectedLike = true;
                        placeLikeButton.setImageResource(R.drawable.ic_plus_one_black_24dp);
                    }
                    //notifyItemChanged(possition);
                    break;

                case R.id.placeAddToFutureButton:
                    if (place.selectedAddTrip){
                        place.selectedAddTrip = false;
                        placeAddToFutureButton.setImageResource(R.drawable.ic_add_circle_outline_red_24dp);
                    }else {
                        place.selectedAddTrip = true;
                        placeAddToFutureButton.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);
                    }
                    //notifyItemChanged(possition);
                    break;

                case R.id.placeImageView:
                    // TODO: 4/4/2017 Open fullScreen Activity
                    break;

                case R.id.placeShowInMapButton:
                    // TODO: 4/4/2017 Open MapActivity
                    break;

            }
        }
    }

    public interface PlaceClickListener {
        void onItemClick(View view, int possition);
    }
}