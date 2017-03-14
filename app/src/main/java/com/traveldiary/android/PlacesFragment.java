package com.traveldiary.android;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.traveldiary.android.Interfaces.ChangeFragmentInterface;
import com.traveldiary.android.Interfaces.TravelDiaryService;
import com.traveldiary.android.adapter.RecyclerAdapter;
import com.traveldiary.android.essence.Place;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.traveldiary.android.Constans.ALL;
import static com.traveldiary.android.Constans.ID_STRING;
import static com.traveldiary.android.Constans.MY;
import static com.traveldiary.android.Constans.PLACES_FOR;
import static com.traveldiary.android.Constans.ROOT_URL;


public class PlacesFragment extends Fragment {
    private ChangeFragmentInterface mChangeFragmentInterface;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private Place place;
    private List<Place> mPlacesList;
    private LinearLayoutManager mLayoutManager;
    private ProgressBar mProgressBar;

    private FloatingActionButton addPlaceButton;

    private static TravelDiaryService travelDiaryService;

    private int tripId;

    private Dialog mDialog;
    private Button uploadFromGalleryBut;
    private Button uploadFromCameraBut;

    private String placesFor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null){
            placesFor = getArguments().getString(PLACES_FOR);
            tripId = getArguments().getInt(ID_STRING);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_places,
                container, false);

        mDialog = new Dialog(getActivity());
        mDialog.setContentView(R.layout.dialog_view);
        uploadFromGalleryBut = (Button) mDialog.findViewById(R.id.uploadFromGalleryBut);
        uploadFromCameraBut = (Button) mDialog.findViewById(R.id.uploadFromCameraBut);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.places_progress);
        mProgressBar.setVisibility(View.VISIBLE);

        addPlaceButton = (FloatingActionButton) rootView.findViewById(R.id.add_place_button);
        addPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.show();
                //mDialogBackButton();
                uploadFromGalleryBut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDialog.dismiss();

                        Fragment fragment = new UploadFragment();

                        Bundle args = new Bundle();
                        args.putInt(ID_STRING, tripId);
                        fragment.setArguments(args);//передаем в новый фрагмент ид трипа чтобы подтянуть имг этого трипа

                        mChangeFragmentInterface.trans(fragment);
                        //startActivity(new Intent(TestActivity.this, MainActivity.class));
                    }
                });

                /*Intent uploadActivity = new Intent(getActivity(), UploadActivity.class);
                uploadActivity.putExtra(TRIP_ID_STRING, tripId);
                startActivity(uploadActivity);*/
            }
        });

        recyclerView = (RecyclerView) rootView.findViewById(R.id.places_recycler_view);

        mPlacesList = new ArrayList<>();

        recyclerAdapter = new RecyclerAdapter(getActivity(), null, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Click on place ");

                int itemPossition = recyclerView.getChildLayoutPosition(view);
                Place place = mPlacesList.get(itemPossition);

                if (!place.getLatitude().isEmpty() && !place.getLongitude().isEmpty()) {


                   /* String uri = String.format(Locale.ENGLISH, "geo:%f,%f", Double.parseDouble(place.getLatitude()), Double.parseDouble(place.getLongitude()));
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);*/

                    Intent intent = new Intent(getActivity(), MapsActivity.class);
                    intent.putExtra("Latitude", place.getLatitude());
                    intent.putExtra("Longitude", place.getLongitude());
                    startActivity(intent);
                }

            }
        }, mPlacesList);
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);


        if (placesFor == null){
            downloadImageById();
        }else if (placesFor.equals(MY)){
            downloadMyPlaces();
        }else if (placesFor.equals(ALL)){
            downloadAllPlaces();
        }

        return rootView;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mChangeFragmentInterface = (ChangeFragmentInterface) activity;
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    private void downloadMyPlaces() {

        travelDiaryService = Api.getTravelDiaryService();

        travelDiaryService.listMyPlaces(LoginActivity.TOKEN_TO_SEND.toString()).enqueue(new Callback<List<Place>>() {
            @Override
            public void onResponse(Call<List<Place>> call, retrofit2.Response<List<Place>> response) {

                mPlacesList.addAll(response.body());

                mProgressBar.setVisibility(View.GONE);

                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Place>> call, Throwable t) {

            }
        });
    }

    private void downloadAllPlaces() {

        travelDiaryService = Api.getTravelDiaryService();

        travelDiaryService.listAllPlaces().enqueue(new Callback<List<Place>>() {
            @Override
            public void onResponse(Call<List<Place>> call, retrofit2.Response<List<Place>> response) {

                mPlacesList.addAll(response.body());

                mProgressBar.setVisibility(View.GONE);

                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Place>> call, Throwable t) {

            }
        });
    }

    private void downloadImageById() {

        travelDiaryService = Api.getTravelDiaryService();

        travelDiaryService.listPlacesByTrip(tripId).enqueue(new Callback<List<Place>>() {
            @Override
            public void onResponse(Call<List<Place>> call, retrofit2.Response<List<Place>> response) {

                mPlacesList.addAll(response.body());

                mProgressBar.setVisibility(View.GONE);

                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Place>> call, Throwable t) {

            }
        });
    }
}
