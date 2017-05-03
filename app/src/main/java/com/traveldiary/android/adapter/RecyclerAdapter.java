package com.traveldiary.android.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
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
import com.traveldiary.android.network.CallBack;

import java.util.ArrayList;
import java.util.List;

import static com.traveldiary.android.App.dataService;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_TRIP = 0;
    private static final int TYPE_PLACE = 1;
    private static final int TYPE_PROGRESS = 2;

    private boolean isLoadMore = false;

    private int mSelectedItemPosition = -1;

    private String ROOT_URL = "http://188.166.77.89/";

    private Context mContext;
    private List<Trip> mTripsList;
    private List<Place> mPlaceList;

    private ItemClickListener itemClickListener = null;
    private ItemLongClickListener itemLongClickListener = null;

    private ProgressBar mProgressBar;

    public RecyclerAdapter(Context mContext, List<Trip> tripList, ItemClickListener itemClickListener, ItemLongClickListener itemLongClickListener, List<Place> placeList ) {
        this.mContext = mContext;
        this.mTripsList = tripList;
        this.itemClickListener = itemClickListener;
        this.itemLongClickListener = itemLongClickListener;
        this.mPlaceList = placeList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType){
            case TYPE_PLACE:
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.place_card, parent, false);
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
            placeViewHolder.bindData(mPlaceList.get(position));

        }else if (holder instanceof TripViewHolder){
            TripViewHolder tripViewHolder = (TripViewHolder) holder;
            tripViewHolder.bindData(mTripsList.get(position));

        }else {
            ProgressHolder progressHolder = (ProgressHolder) holder;
            progressHolder.bindData();
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
        if (mTripsList==null && mPlaceList.size()>0){
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

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface ItemLongClickListener{
        void onItemLongClick(View view, int position);
    }

    public class TripViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private HorizontalRecyclerAdapter horizontalRecyclerAdapter;
        private RecyclerView horizontalRecycler;
        private TextView title;

        private List<Place> placesForHorizontal = new ArrayList<>();

        public TripViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            title = (TextView) view.findViewById(R.id.tvHorizontalHeader);
            horizontalRecycler = (RecyclerView) view.findViewById(R.id.rvHorizontal);
            horizontalRecycler.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            horizontalRecyclerAdapter = new HorizontalRecyclerAdapter(mContext);
            horizontalRecycler.setAdapter(horizontalRecyclerAdapter);
        }

        public void bindData(final Trip trip){

            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! bindData TripHolder");

            title.setText(trip.getTitle());

            dataService.getPLacesByTrip(trip.getId(), new CallBack() {
                @Override
                public void responseNetwork(Object o) {
                    List<Place> list = (List<Place>) o;
                    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! bindData TripHolder list.size = " + list.size());
                    placesForHorizontal.clear();
                    placesForHorizontal.addAll(list);
                    if (placesForHorizontal.size()==0){
                        placesForHorizontal.add(new Place(trip.getThumbnail()));
                    }
                    horizontalRecyclerAdapter.setData(placesForHorizontal);
                    horizontalRecyclerAdapter.notifyDataSetChanged();
                }

                @Override
                public void failNetwork(Throwable t) {
                    Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(v, this.getLayoutPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            itemLongClickListener.onItemLongClick(v, this.getLayoutPosition());
            return true;
        }
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private TextView placeTitleText;
        private ImageView titleImageView;
        private ImageView placeLikeButton;
        private ImageView placeAddToFutureButton;
        private ImageView placeShowInMapButton;

        private Place place;

        public PlaceViewHolder(View view) {
            super(view);

            view.setOnLongClickListener(this);

            placeTitleText = (TextView) view.findViewById(R.id.placeTitleText);
            titleImageView = (ImageView) view.findViewById(R.id.placeImageView);
            placeLikeButton = (ImageView) view.findViewById(R.id.placeLikeButton);
            placeAddToFutureButton = (ImageView) view.findViewById(R.id.placeAddToFutureButton);
            placeShowInMapButton = (ImageView) view.findViewById(R.id.placeShowInMapButton);

            mProgressBar = (ProgressBar) view.findViewById(R.id.card_progress);

            titleImageView.setOnClickListener(this);
            titleImageView.setOnLongClickListener(this);
            placeLikeButton.setOnClickListener(this);
            placeAddToFutureButton.setOnClickListener(this);
            placeShowInMapButton.setOnClickListener(this);
        }

        public void bindData(Place place){
            this.place = place;

            mProgressBar.setVisibility(View.VISIBLE);
            placeTitleText.setText(place.getTitle());

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
                    .placeholder(R.drawable.ic_image_black_24dp)
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
                placeLikeButton.setVisibility(View.GONE);
                placeAddToFutureButton.setVisibility(View.GONE);
            }else if (place.getIsMine() ==0){
                placeLikeButton.setVisibility(View.VISIBLE);
                placeAddToFutureButton.setVisibility(View.VISIBLE);
            }
            placeShowInMapButton.setImageResource(R.drawable.ic_location);

        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.placeLikeButton:
                    if (place.getIsLiked()==1){
                        // is Liked
                    }else if (place.getIsLiked()==0){
                        itemClickListener.onItemClick(v, this.getLayoutPosition());
                    }
                    break;

                case R.id.placeAddToFutureButton:
                    if (place.getIsInFutureTrips()==1){
                        // in Fututre
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

        @Override
        public boolean onLongClick(View v) {
            if (place.getIsMine() ==1){
                itemLongClickListener.onItemLongClick(v, this.getLayoutPosition());
            }
            return false;
        }
    }

    public class ProgressHolder extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;

        public ProgressHolder(View view) {
            super(view);

            progressBar = (ProgressBar) view.findViewById(R.id.load_new_items_recycler);
        }

        public void bindData() {
            if (isLoadMore)
                progressBar.setVisibility(View.VISIBLE);
            else
                progressBar.setVisibility(View.GONE);
        }
    }

}