package com.traveldiary.android.activity;


import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.traveldiary.android.fragment.PlacesFragment;
import com.traveldiary.android.R;
import com.traveldiary.android.adapter.RecyclerAdapter;
import com.traveldiary.android.fragment.UploadDialog;
import com.traveldiary.android.model.Place;
import com.traveldiary.android.model.Trip;
import com.traveldiary.android.network.CallBack;

import java.util.List;

import static com.traveldiary.android.App.dataService;
import static com.traveldiary.android.Constans.GALLERY;
import static com.traveldiary.android.Constans.ID_STRING;
import static com.traveldiary.android.Constans.PLACES_FOR;
import static com.traveldiary.android.Constans.PLACES_FOR_TRIP;
import static com.traveldiary.android.Constans.ROOT_URL;
import static com.traveldiary.android.Constans.UPLOAD_FROM;

public class DetailActivity extends AppCompatActivity implements RecyclerAdapter.ItemClickListener, View.OnClickListener{

    private String photo;

    private List<Place> mPlacesList;

    private ImageView collapseImage;

    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    private FloatingActionButton mFab;

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
        

        mFab = (FloatingActionButton) findViewById(R.id.add_place_button);
        mFab.setOnClickListener(this);
        mFab.hide();

        Log.d("MYLOG", " tripID from Intent = " + mTripId);

        dataService.getTripById(mTripId, new CallBack() {
            @Override
            public void responseNetwork(Object o) {
                Trip trip = (Trip) o;
                if (trip.getIsMine()==1 && trip.getIsFuture()==0){
                    mFab.show();
                }
                mCollapsingToolbarLayout.setTitle(trip.getTitle());
            }

            @Override
            public void failNetwork(Throwable t) {
                System.out.println(t.getMessage());
            }
        });



        //collapseImage = (ImageView) findViewById(R.id.detailImageView);
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
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
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

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.add_place_button){
            DialogFragment uploadDialog = new UploadDialog();
            Bundle args = new Bundle();
            args.putInt(ID_STRING, mTripId);
            args.putString(UPLOAD_FROM, GALLERY);
            uploadDialog.setArguments(args);
            uploadDialog.show(getSupportFragmentManager(), "dialo");
        }
    }
}