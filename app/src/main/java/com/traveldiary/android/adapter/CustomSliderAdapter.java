package com.traveldiary.android.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.traveldiary.android.R;
import com.traveldiary.android.model.Place;

import java.util.List;

import static com.traveldiary.android.Constans.ROOT_URL;

/**
 * Created by Cyborg on 11/21/2017.
 */

public class CustomSliderAdapter extends PagerAdapter {
    private Context mContext;
    private List<Place> mPlaces;

    public CustomSliderAdapter(Context mContext, List<Place> places) {
        this.mContext = mContext;
        this.mPlaces = places;
    }

    @Override
    public int getCount() {
        return mPlaces.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.pager_item_custom_slider, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.img_pager_item);

        Glide.with(mContext).load(ROOT_URL + mPlaces.get(position).getThumbnail())
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
                .crossFade()
                //.placeholder(R.drawable.ic_image_black_24dp)

                .placeholder( ContextCompat.getDrawable(mContext, R.drawable.ic_image_black_24dp) )
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}