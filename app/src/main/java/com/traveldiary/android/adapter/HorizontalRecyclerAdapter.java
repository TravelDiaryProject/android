package com.traveldiary.android.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.traveldiary.android.R;
import com.traveldiary.android.model.Place;

import java.util.List;

import static com.traveldiary.android.Constans.ROOT_URL;

class HorizontalRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Place> mPlaceList;
    private RecyclerAdapter.RecyclerItemListener recyclerItemListener = null;
    private int parentPosition;

    HorizontalRecyclerAdapter(Context context, RecyclerAdapter.RecyclerItemListener recyclerItemListener) {
        this.mContext = context;
        this.recyclerItemListener = recyclerItemListener;
    }

    void setData(List<Place> list, int parentPosition){
        this.parentPosition = parentPosition;
        if (mPlaceList!=list){
            mPlaceList = list;
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trip_horizontal_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.bindData(mPlaceList.get(position));

    }

    @Override
    public int getItemCount() {
        int size = 0;
        if (mPlaceList!=null)
            size = mPlaceList.size();
        return size;
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private ImageView imageView;

        private ItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            imageView = (ImageView) itemView.findViewById(R.id.horizontal_trip_image);
        }

        private void bindData(Place place){
            Glide.with(mContext).load(ROOT_URL + place.getThumbnail())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .thumbnail(0.5f)
                    //.placeholder(R.drawable.ic_image_black_24dp)
                    .placeholder( ContextCompat.getDrawable(mContext, R.drawable.ic_image_black_24dp) )
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }

        @Override
        public void onClick(View v) {
            recyclerItemListener.onItemClick(v, parentPosition);
        }

        @Override
        public boolean onLongClick(View v) {
            recyclerItemListener.onItemLongClick(v, parentPosition);
            return true;
        }
    }
}
