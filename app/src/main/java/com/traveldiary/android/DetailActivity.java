package com.traveldiary.android;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.traveldiary.android.adapter.RecyclerAdapter;
import com.traveldiary.android.data.DataService;
import com.traveldiary.android.model.Place;
import com.traveldiary.android.model.Trip;
import com.traveldiary.android.network.CallBack;

import java.util.List;

import static com.traveldiary.android.App.dataService;
import static com.traveldiary.android.Constans.ID_STRING;
import static com.traveldiary.android.Constans.PLACES_FOR;
import static com.traveldiary.android.Constans.PLACES_FOR_TRIP;
import static com.traveldiary.android.Constans.ROOT_URL;

public class DetailActivity extends AppCompatActivity implements RecyclerAdapter.ItemClickListener{

    private String photo;

    private List<Place> mPlacesList;

    private ImageView collapseImage;

    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    private int mTripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collaps_toolbar_layout);

        mTripId = getIntent().getIntExtra(ID_STRING, -1); // what to do if id not send
        Log.d("MYLOG", " tripID from Intent = " + mTripId);

        dataService.getTripById(mTripId, new CallBack() {
            @Override
            public void responseNetwork(Object o) {
                Trip trip = (Trip) o;
                mCollapsingToolbarLayout.setTitle(trip.getTitle());
            }

            @Override
            public void failNetwork(Throwable t) {
                System.out.println(t.getMessage());
            }
        });



        //collapseImage = (ImageView) findViewById(R.id.detailImageView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Open MAPS", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

        //photo = getIntent().getStringExtra("Photo");

        PlacesFragment placesFragment = new PlacesFragment();
        Bundle args = new Bundle();
        args.putString(PLACES_FOR, PLACES_FOR_TRIP);
        args.putInt(ID_STRING, mTripId);
        placesFragment.setArguments(args);
        trans(placesFragment);

    }

    public void setCollapseInfo(String tripPhoto){
        Glide.with(this).load(ROOT_URL + tripPhoto)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        System.out.println(e.toString());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(collapseImage);
    }


    @Override
    public void onItemClick(View view, int position) {
        System.out.println("Click = " + view.getId() + " pos = " + position);
        System.out.println("click place id = " + mPlacesList.get(position).getId());
               /* switch (view.getId()){
                    case R.id.placeLikeButton:
                        System.out.println("Add to my trips by id = " + mPlacesList.get(possition).getId());
                        *//*ImageView imageView = (ImageView) view;
                        imageView.setVisibility(View.GONE);*//*
                        //view.setVisibility(View.GONE);
                        //recyclerAdapter.notifyItemChanged(possition);
                        break;
                }*/
    }

    public void trans(Fragment fragment) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_detail, fragment);
        //ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
        //return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}