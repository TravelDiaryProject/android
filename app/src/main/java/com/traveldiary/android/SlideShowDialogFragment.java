package com.traveldiary.android;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.traveldiary.android.animation.ZoomOutPageTransformer;
import com.traveldiary.android.model.Place;

import java.util.List;

import static com.traveldiary.android.Constans.ROOT_URL;

/**
 * Created by Cyborg on 4/14/2017.
 */

public class SlideShowDialogFragment extends DialogFragment {

    private ViewPager viewPager;
    private TextView listCount;
    private MyPagerAdapter myPagerAdapter;
    private List<Place> placeList;
    public int selectedPosition = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_slide_show, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        listCount = (TextView) v.findViewById(R.id.list_count);
        //viewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        if (getArguments() !=null) {
            placeList = (List<Place>) getArguments().getSerializable("placeList");
            selectedPosition = getArguments().getInt("selectedPosition");
        }

        myPagerAdapter = new MyPagerAdapter();
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.setAdapter(myPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(selectedPosition);

        return v;
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition);
    }

    private void displayMetaInfo(int position) {
        listCount.setText((position + 1) + " of " + placeList.size());

        /*Image image = images.get(position);
        lblTitle.setText(image.getName());
        lblDate.setText(image.getTimestamp());*/
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            displayMetaInfo(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(android.support.v4.app.DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }


    public class MyPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.fullscreen_image_layout, container, false);

            PhotoView imageView = (PhotoView) view.findViewById(R.id.photo_view);



            System.out.println(ROOT_URL + placeList.get(position).getPhoto());
            Glide.with(getActivity())
                    .load(ROOT_URL + placeList.get(position).getPhoto())
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
                    .thumbnail(0.1f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);

            /*PhotoView imageView = (PhotoView) view.findViewById(R.id.photo_view);
            imageView.setImageResource(list.get(position));*/

            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {

            return placeList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }


}
