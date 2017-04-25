package com.traveldiary.android.adapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
                        .inflate(R.layout.second_test_trip, parent, false);
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
            myViewHolder.bindData(mTripsList.get(position), position);

        }
    }

    public void removePlace(Place placeRemove){
        if (mPlaceList!=null && placeRemove!=null){
            mPlaceList.remove(placeRemove);
        }
        notifyDataSetChanged();
    }

    public void removeTrip(Trip tripRemove){
        if (mTripsList!=null && tripRemove!=null){
            mTripsList.remove(tripRemove);
        }
        notifyDataSetChanged();
    }

    public void updateAdapter(List<Place> updateList){
        if (updateList!=null){
            mPlaceList.clear();
            mPlaceList.addAll(updateList);
        }
        notifyDataSetChanged();
    }

    public void updateAdapterTrip(List<Trip> updateList){
        if (updateList!=null){
            mTripsList.clear();
            mTripsList.addAll(updateList);
        }
        notifyDataSetChanged();
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

        private Trip trip;

        private TextView title;
        private ImageView imageView2;
        private ImageView imageView3;
        private ImageView imageView4;

        private ImageView tripRemoveButton;

        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            title = (TextView) view.findViewById(R.id.tripTitleView);
            mProgressBar = (ProgressBar) view.findViewById(R.id.card_progress);

            imageView2 = (ImageView) view.findViewById(R.id.tripImageView2);
            imageView3 = (ImageView) view.findViewById(R.id.tripImageView3);
            imageView4 = (ImageView) view.findViewById(R.id.tripImageView4);

            tripRemoveButton = (ImageView) view.findViewById(R.id.tripRemoveButton);

            tripRemoveButton.setOnClickListener(this);

            //title.setOnClickListener(this);
            //titleImageView.setOnClickListener(this);
        }

        public void bindData(Trip trip, int possition){

            this.trip = trip;

            mProgressBar.setVisibility(View.VISIBLE);
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
                    .into(imageView4);

            tripRemoveButton.setImageResource(R.drawable.ic_delete_24dp);
            title.setText(trip.getTitle());

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

        private TextView placeTitleText;
        private ImageView placeRemoveButton;
        private ImageView titleImageView;
        private ImageView placeLikeButton;
        private ImageView placeAddToFutureButton;
        private ImageView placeShowInMapButton;

        private Place place;
        private int possition;

        public PlaceHolder(View view) {
            super(view);

            placeTitleText = (TextView) view.findViewById(R.id.placeTitleText);
            placeRemoveButton = (ImageView) view.findViewById(R.id.placeRemoveButton);
            titleImageView = (ImageView) view.findViewById(R.id.placeImageView);
            placeLikeButton = (ImageView) view.findViewById(R.id.placeLikeButton);
            placeAddToFutureButton = (ImageView) view.findViewById(R.id.placeAddToFutureButton);
            placeShowInMapButton = (ImageView) view.findViewById(R.id.placeShowInMapButton);

            mProgressBar = (ProgressBar) view.findViewById(R.id.card_progress);

            placeRemoveButton.setOnClickListener(this);
            titleImageView.setOnClickListener(this);
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

            placeTitleText.setText(place.getTitle());

            Glide.with(mContext).load(ROOT_URL + place.getThumbnail())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(mContext, "exception = ", Toast.LENGTH_SHORT).show();
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
                    .into(titleImageView);

            if (place.getIsLiked()==1){
                placeLikeButton.setImageResource(R.drawable.ic_liked);
            }else if (place.getIsLiked()==0){
                placeLikeButton.setImageResource(R.drawable.ic_like);
            }

            if (place.getIsInFutureTrips()==1){
                placeAddToFutureButton.setImageResource(R.drawable.ic_added);
            }else if (place.getIsInFutureTrips()==0){
                placeAddToFutureButton.setImageResource(R.drawable.ic_add);
            }

            if (place.getIsMine() ==1){
                placeRemoveButton.setImageResource(R.drawable.ic_delete_24dp);
                placeRemoveButton.setVisibility(View.VISIBLE);
                placeLikeButton.setVisibility(View.GONE);
                placeAddToFutureButton.setVisibility(View.GONE);
            }else if (place.getIsMine() ==0){
                placeRemoveButton.setVisibility(View.GONE);
                placeLikeButton.setVisibility(View.VISIBLE);
                placeAddToFutureButton.setVisibility(View.VISIBLE);
            }

            placeShowInMapButton.setImageResource(R.drawable.ic_location);

        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.placeRemoveButton:
                    itemClickListener.onItemClick(v, this.getLayoutPosition());
                    break;

                case R.id.placeLikeButton:
                    if (place.getIsLiked()==1){
                        // delete like
                    }else if (place.getIsLiked()==0){
                        itemClickListener.onItemClick(v, this.getLayoutPosition());
                    }
                    break;

                case R.id.placeAddToFutureButton:
                    if (place.getIsInFutureTrips()==1){
                        // now we can only add to future, we can not delete place from future trip
                        // delete from future
                    }else if (place.getIsInFutureTrips()==0){
                        itemClickListener.onItemClick(v, this.getLayoutPosition());
                    }
                    break;

                case R.id.placeImageView:
                    itemClickListener.onItemClick(v, this.getLayoutPosition());
                    break;

                case R.id.placeShowInMapButton:
                    itemClickListener.onItemClick(v, this.getLayoutPosition());
                    break;

            }
        }
    }
}