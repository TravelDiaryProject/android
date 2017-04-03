package com.traveldiary.android;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.traveldiary.android.adapter.RecyclerAdapter;
import com.traveldiary.android.model.City;
import com.traveldiary.android.model.Place;
import com.traveldiary.android.model.RegistrationResponse;
import com.traveldiary.android.model.Trip;
import com.traveldiary.android.network.CallBackInterface;
import com.traveldiary.android.network.Network;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

import static com.traveldiary.android.App.network;
import static com.traveldiary.android.Constans.ALL;
import static com.traveldiary.android.Constans.ID_STRING;
import static com.traveldiary.android.Constans.MY;
import static com.traveldiary.android.Constans.PLACES_BY_CITY;
import static com.traveldiary.android.Constans.PLACES_FOR;
import static com.traveldiary.android.Constans.ROOT_URL;

public class DetailActivity extends AppCompatActivity implements CallBackInterface, RecyclerAdapter.ItemClickListener{

    private String photo;

    private RecyclerView recyclerView;

    private Dialog mDialog;
    private Button uploadFromGalleryBut;
    private Button uploadFromCameraBut;
    private RecyclerAdapter recyclerAdapter;
    private List<Place> mPlacesList;
    private LinearLayoutManager mLayoutManager;

    private ImageView collapseImage;

    private int tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        collapseImage = (ImageView) findViewById(R.id.detailImageView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Open MAPS", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

        //photo = getIntent().getStringExtra("Photo");

        tripId = getIntent().getIntExtra(ID_STRING, -1); // what to do if id not send
        //Network network = new Network(this);
        network.setCallBackInterface(this);
        network.getTripById(tripId);
        network.getPlacesByTrip(tripId);









        mDialog = new Dialog(this);
        mDialog.setContentView(R.layout.dialog_view);
        uploadFromGalleryBut = (Button) mDialog.findViewById(R.id.uploadFromGalleryBut);
        uploadFromCameraBut = (Button) mDialog.findViewById(R.id.uploadFromCameraBut);


        recyclerView = (RecyclerView) findViewById(R.id.detail_activity_places_recycler_view);

        mPlacesList = new ArrayList<>();

        recyclerAdapter = new RecyclerAdapter(this, null, this, mPlacesList);
        mLayoutManager = new LinearLayoutManager(this);
        //mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);

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
    public void onItemClick(View view, int possition) {
        System.out.println("Click = " + view.getId() + " pos = " + possition);
        System.out.println("click place id = " + mPlacesList.get(possition).getId());
                switch (view.getId()){
                    case R.id.placeLikeButton:
                        System.out.println("Add to my trips by id = " + mPlacesList.get(possition).getId());
                        /*ImageView imageView = (ImageView) view;
                        imageView.setVisibility(View.GONE);*/
                        //view.setVisibility(View.GONE);
                        //recyclerAdapter.notifyItemChanged(possition);
                        break;
                }
    }

    @Override
    public void getPlacesByTrip(List<Place> placesByTrip) {

        mPlacesList.addAll(placesByTrip);

        recyclerAdapter.notifyDataSetChanged();

    }

    @Override
    public void getTripById(Trip trip) {
        setCollapseInfo(trip.getPhoto());
    }


    @Override
    public void getAllPlaces(List<Place> allPlaces) {


    }

    @Override
    public void getMyPlaces(List<Place> myPlaces) {

    }

    @Override
    public void getPlacesByCity(List<Place> placesByCity) {

    }

    @Override
    public void getAllTrips(List<Trip> allTrips) {

    }

    @Override
    public void getMyTrips(List<Trip> myTrips) {

    }

    @Override
    public void getTripsByCity(List<Trip> tripsByCity) {

    }

    @Override
    public void createTrip(String info) {

    }

    @Override
    public void signIn(Response<RegistrationResponse> response) {

    }

    @Override
    public void registration(Response<RegistrationResponse> response) {

    }

    @Override
    public void uploadPlace(Response<ResponseBody> response) {

    }

    @Override
    public void getAllCities(List<City> allCities) {

    }
}