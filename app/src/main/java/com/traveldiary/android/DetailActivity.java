package com.traveldiary.android;

import android.app.Dialog;
import android.os.Bundle;
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
import com.traveldiary.android.network.CallBack;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

import static com.traveldiary.android.App.network;
import static com.traveldiary.android.Constans.ID_STRING;
import static com.traveldiary.android.Constans.ROOT_URL;

public class DetailActivity extends AppCompatActivity implements RecyclerAdapter.ItemClickListener{

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

        //network.getTripById(tripId);

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


        network.getPlacesByTrip(tripId, new CallBack() {
            @Override
            public void responseNetwork(Object o) {
                List<Place> response = (List<Place>) o;

                mPlacesList.addAll(response);
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void failNetwork(Throwable t) {

            }
        });
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
}