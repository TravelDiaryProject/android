package com.traveldiary.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.traveldiary.android.R;
import com.traveldiary.android.essence.Header;
import com.traveldiary.android.essence.Trip;

import java.util.List;


/**
 * Created by Cyborg on 2/26/2017.
 */

public class AdapterWithHeader extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADRE = 0;
    private static final int TYPE_ITEM = 1;

    private final View.OnClickListener mOnClickListener;

    private String ROOT_URL = "http://188.166.77.89/";

    Context mContext;

    Header header;
    List<Trip> listTrips;

    Button button;

    public AdapterWithHeader(Header header, List<Trip> listTrips, View.OnClickListener onClickListener) {
        this.header = header;
        this.listTrips = listTrips;
        this.mOnClickListener = onClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mContext = parent.getContext();

        switch (viewType){
            case TYPE_HEADRE:
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_item, parent, false);
                button = (Button) v.findViewById(R.id.button);
                button.setOnClickListener(mOnClickListener);
                return new VHHeader(v);
            case TYPE_ITEM:
                View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_card, parent, false);
                v1.setOnClickListener(mOnClickListener);
                return new VHItem(v1);
        }

        return null;
    }

    private Trip getItem(int position){
        return listTrips.get(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof VHHeader){
            VHHeader headerHolder = (VHHeader) holder;
            headerHolder.headreText.setText(header.getName());
            //header.setId(String.valueOf(headerHolder.editText1.getText()));

            headerHolder.editText1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    header.setId(charSequence.toString());

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });


            //headerHolder.button.setOnClickListener(mOnClickListener);         NullPointerException

        }else if (holder instanceof VHItem){
            Trip cuurentItem = getItem(position -1);
            VHItem itemHolder = (VHItem) holder;

            Glide.with(mContext).load(ROOT_URL + cuurentItem.getPhoto())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            //mProgressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .thumbnail(0.1f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(itemHolder.imageView);
            itemHolder.name.setText(cuurentItem.getTitle());

        }
    }

    @Override
    public int getItemViewType(int position) {

        if (isPositionHeader(position))
            return TYPE_HEADRE;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position){
        return position == 0;
    }

    @Override
    public int getItemCount() {
        return listTrips.size() + 1;
    }


    class VHHeader extends RecyclerView.ViewHolder{

        TextView headreText;
        EditText editText1;
        Button button;

        public VHHeader(View itemView) {
            super(itemView);
            this.headreText = (TextView) itemView.findViewById(R.id.txtHeader);
            this.editText1 = (EditText) itemView.findViewById(R.id.editText);
            this.button = (Button) itemView.findViewById(R.id.createTripButton);
//            this.button.setOnClickListener(mOnClickListener);
        }
    }

    class VHItem extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView name;

        public VHItem(View itemView) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.tripTitleView);
            this.imageView = (ImageView) itemView.findViewById(R.id.tripImageView);
        }
    }


}
