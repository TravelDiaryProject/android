package com.cyborg.td;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Cyborg on 1/29/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private Context mContext;
    private List<Place> mPlaceList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public WebView webView;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            webView = (WebView) view.findViewById(R.id.webView);
        }
    }


    public RecyclerAdapter(Context mContext, List<Place> placeList) {
        this.mContext = mContext;
        this.mPlaceList = placeList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Place place = mPlaceList.get(position);
        holder.title.setText(place.getTitle());
        //holder.webView.loadUrl(WatchingActivity.ROOT_URL + place.getPhoto());

    }

    @Override
    public int getItemCount() {
        return mPlaceList.size();
    }
}