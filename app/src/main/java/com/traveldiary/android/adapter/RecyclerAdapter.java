package com.traveldiary.android.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageButton;
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
import com.traveldiary.android.callback.CallbackPlaces;

import java.util.ArrayList;
import java.util.List;

import static com.traveldiary.android.App.dataService;
import static com.traveldiary.android.Constans.ROOT_URL;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_TRIP = 0;
    private static final int TYPE_PLACE = 1;
    private static final int TYPE_PROGRESS = 2;

    private boolean isLoadMore = false;

    private Context mContext;
    private List<Trip> mTripsList;
    private List<Place> mPlaceList;

    private RecyclerItemListener recyclerItemListener = null;

    private ProgressBar mProgressBar;
    private SparseBooleanArray mSelectedItemsIds;

    public RecyclerAdapter(Context mContext, List<Trip> tripList, RecyclerItemListener recyclerItemListener, List<Place> placeList) {
        this.mContext = mContext;
        this.mTripsList = tripList;
        this.recyclerItemListener = recyclerItemListener;
        this.mPlaceList = placeList;
        mSelectedItemsIds = new SparseBooleanArray();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType){
            case TYPE_PLACE:
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.test_place_card, parent, false);
                return new PlaceViewHolder(itemView);
            case TYPE_TRIP:
                View itemView2 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.trip_horizontal_item_layout, parent, false);

                return new TripViewHolder(itemView2);
            case TYPE_PROGRESS:
                View itemView3 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.progress_item, parent, false);
                return new ProgressHolder(itemView3);
        }
        return null;
    }

    public void addLoadingFooter(){
        mPlaceList.add(new Place());
        notifyItemInserted((mPlaceList.size()-1));
        isLoadMore = true;
    }

    public void removeLoadingFooter(){
        int pos = mPlaceList.size()-1;
        Place place = mPlaceList.get(pos);
        if (place!=null){
            mPlaceList.remove(pos);
            isLoadMore = false;
            notifyItemRemoved(pos);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof PlaceViewHolder){
            PlaceViewHolder placeViewHolder = (PlaceViewHolder) holder;
            placeViewHolder.bindData(mPlaceList.get(position), position);

        }else if (holder instanceof TripViewHolder){
            TripViewHolder tripViewHolder = (TripViewHolder) holder;
            tripViewHolder.bindData(mTripsList.get(position), position);

        }else {
            ProgressHolder progressHolder = (ProgressHolder) holder;
            progressHolder.bindData();
        }
    }

    public void updateAdapterPlace(List<Place> updateList){
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
        else if (mPlaceList!=null && position == mPlaceList.size() -1 && isLoadMore)
            return TYPE_PROGRESS;
        else
            return TYPE_PLACE;
    }

    public interface RecyclerItemListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        int[] keys = new int[mSelectedItemsIds.size()];
        for (int i = 0; i < mSelectedItemsIds.size(); i++) {
            keys[i] = mSelectedItemsIds.keyAt(i);
        }

        for (int key : keys) {
            selectView(key, false);
        }
        mSelectedItemsIds = new SparseBooleanArray();
    }

    private void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, true);
        else
            mSelectedItemsIds.delete(position);

        notifyItemChanged(position);
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    private class TripViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private HorizontalRecyclerAdapter horizontalRecyclerAdapter;
        private RecyclerView horizontalRecycler;
        private TextView title;

        private List<Place> placesForHorizontal = new ArrayList<>();

        private TripViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            title = (TextView) view.findViewById(R.id.tvHorizontalHeader);
            horizontalRecycler = (RecyclerView) view.findViewById(R.id.rvHorizontal);
            horizontalRecycler.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            horizontalRecyclerAdapter = new HorizontalRecyclerAdapter(mContext, recyclerItemListener);
            horizontalRecycler.setAdapter(horizontalRecyclerAdapter);
        }

        private void bindData(final Trip trip, final int position) {

            if (mSelectedItemsIds.get(position))
                itemView.setBackgroundResource(R.color.grey);
            else
                itemView.setBackgroundResource(R.color.white);

            title.setText(trip.getTitle());

            dataService.getMyPlacesByTrip(trip.getId(), new CallbackPlaces() {
                @Override
                public void response(List<Place> placeList) {
                    placesForHorizontal.clear();
                    placesForHorizontal.addAll(placeList);
                    if (placesForHorizontal.size()==0){
                        placesForHorizontal.add(new Place());
                    }
                    horizontalRecyclerAdapter.setData(placesForHorizontal, position);
                    horizontalRecyclerAdapter.notifyDataSetChanged();
                }

                @Override
                public void fail(Throwable t) {
                    //Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onClick(View v) {
            recyclerItemListener.onItemClick(v, this.getLayoutPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            recyclerItemListener.onItemLongClick(v, this.getLayoutPosition());
            return true;
        }
    }

    private class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private TextView placeTitleText;
        private ImageView titleImageView;
        private ImageButton placeLikeButton;
        private ImageButton placeAddToFutureButton;
        private ImageButton placeShowInMapButton;


        private Place place;

        private PlaceViewHolder(View view) {
            super(view);

            //view.setOnLongClickListener(this);

            placeTitleText = (TextView) view.findViewById(R.id.placeTitleText);
            titleImageView = (ImageView) view.findViewById(R.id.placeImageView);
            placeLikeButton = (ImageButton) view.findViewById(R.id.placeLikeButton);
            placeAddToFutureButton = (ImageButton) view.findViewById(R.id.placeAddToFutureButton);
            placeShowInMapButton = (ImageButton) view.findViewById(R.id.placeShowInMapButton);

            mProgressBar = (ProgressBar) view.findViewById(R.id.card_progress);

            titleImageView.setOnClickListener(this);
            titleImageView.setOnLongClickListener(this);
            placeLikeButton.setOnClickListener(this);
            placeAddToFutureButton.setOnClickListener(this);
            placeShowInMapButton.setOnClickListener(this);
        }

        private void bindData(Place place, int position){
            this.place = place;

            mProgressBar.setVisibility(View.VISIBLE);
            placeTitleText.setText(place.getCountryName() + ", " + place.getCityName() + ", " + place.getTitle());

            Glide.with(mContext).load(ROOT_URL + place.getThumbnail())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            mProgressBar.setVisibility(View.GONE);
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
                    //.placeholder(R.drawable.ic_image_black_24dp)

                    .placeholder( ContextCompat.getDrawable(mContext, R.drawable.ic_image_black_24dp) )
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(titleImageView);

            if (mSelectedItemsIds.get(position)) {
                titleImageView.setAlpha(0.4f);
            }else {
                titleImageView.setAlpha(1f);
            }

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
                placeLikeButton.setVisibility(View.GONE);
                placeAddToFutureButton.setVisibility(View.GONE);
            }else if (place.getIsMine() ==0){
                placeLikeButton.setVisibility(View.VISIBLE);
                placeAddToFutureButton.setVisibility(View.VISIBLE);
            }
            placeShowInMapButton.setImageResource(R.drawable.ic_location);

            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.placeLikeButton:
                    recyclerItemListener.onItemClick(v, this.getLayoutPosition());
                    break;

                case R.id.placeAddToFutureButton:
                    if (place.getIsInFutureTrips()==0){
                        recyclerItemListener.onItemClick(v, this.getLayoutPosition());
                    }
                    break;

                case R.id.placeImageView:
                    recyclerItemListener.onItemClick(v, this.getLayoutPosition());
                    break;

                case R.id.placeShowInMapButton:
                    recyclerItemListener.onItemClick(v, this.getLayoutPosition());
                    break;
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (place.getIsMine() ==1){
                recyclerItemListener.onItemLongClick(v, this.getLayoutPosition());
            }
            return false;
        }
    }

    private class ProgressHolder extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;

        private ProgressHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.load_new_items_recycler);
        }

        private void bindData() {
            if (isLoadMore) {
                progressBar.setVisibility(View.VISIBLE);
            }else {
                progressBar.setVisibility(View.GONE);
            }
        }
    }
}