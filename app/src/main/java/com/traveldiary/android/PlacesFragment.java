package com.traveldiary.android;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.traveldiary.android.network.CallBack;
import com.traveldiary.android.adapter.RecyclerAdapter;
import com.traveldiary.android.model.Place;
import com.traveldiary.android.network.InternetStatus;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

import static com.traveldiary.android.App.network;
import static com.traveldiary.android.Constans.ID_STRING;
import static com.traveldiary.android.Constans.MY;
import static com.traveldiary.android.Constans.PLACES_BY_CITY;
import static com.traveldiary.android.Constans.PLACES_FOR;
import static com.traveldiary.android.Constans.PLACE_ID;
import static com.traveldiary.android.Constans.TOP;
import static com.traveldiary.android.Constans.TRIP_ID;


public class PlacesFragment extends Fragment implements RecyclerAdapter.ItemClickListener {
    private ChangeFragmentInterface mChangeFragmentInterface;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private List<Place> mPlacesList;
    private LinearLayoutManager mLayoutManager;
    private ProgressBar mProgressBar;

    private FloatingActionButton addPlaceButton;

    private int tripId;
    private int cityId;

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
            cityId = getArguments().getInt(PLACES_BY_CITY);
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
                uploadActivity.putExtra(TRIP_ID, tripId);
                startActivity(uploadActivity);*/
            }
        });

        recyclerView = (RecyclerView) rootView.findViewById(R.id.places_recycler_view);

        mPlacesList = new ArrayList<>();

        recyclerAdapter = new RecyclerAdapter(getActivity(), null, this /*{
            @Override
            public void onClick(View view) {
                System.out.println("Click on place ");

                int itemPossition = recyclerView.getChildLayoutPosition(view);
                Place place = mPlacesList.get(itemPossition);

                if (!place.getLatitude().isEmpty() && !place.getLongitude().isEmpty()) {


                   *//* String uri = String.format(Locale.ENGLISH, "geo:%f,%f", Double.parseDouble(place.getLatitude()), Double.parseDouble(place.getLongitude()));
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);*//*

                   *//* Intent intent = new Intent(getActivity(), MapsActivity.class);
                    intent.putExtra("Latitude", place.getLatitude());
                    intent.putExtra("Longitude", place.getLongitude());
                    startActivity(intent);*//*

                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra("Id", place.getId());
                    intent.putExtra("Photo", place.getPhoto());
                    startActivity(intent);
                }

            }
        }*/, mPlacesList);
        mLayoutManager = new LinearLayoutManager(getActivity());
        //mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);

        // временный ужас!!!!!!
        if (placesFor == null){
            System.out.println("null places for");

            if (cityId != 0){
                network.getPlacesByCity(cityId, new CallBack() {
                    @Override
                    public void responseNetwork(Object o) {
                        manipulationWithResponse(o);
                    }

                    @Override
                    public void failNetwork(Throwable t) {

                    }
                });
            }else{
                network.getPlacesByTrip(tripId, new CallBack() {
                    @Override
                    public void responseNetwork(Object o) {
                        manipulationWithResponse(o);
                    }

                    @Override
                    public void failNetwork(Throwable t) {

                    }
                });
            }

        }else if (placesFor.equals(TOP)){
            network.getTopPlaces(new CallBack() {
                @Override
                public void responseNetwork(Object o) {
                    manipulationWithResponse(o);
                }

                @Override
                public void failNetwork(Throwable t) {

                }
            });
        }else if (placesFor.equals(MY)){
            network.getMyPlaces(LoginActivity.TOKEN_TO_SEND.toString(), new CallBack() {
                @Override
                public void responseNetwork(Object o) {
                    manipulationWithResponse(o);
                }

                @Override
                public void failNetwork(Throwable t) {

                }
            });
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

    public void manipulationWithResponse(Object o){
        List<Place> placesList = (List<Place>) o;

        mPlacesList.addAll(placesList);
        recyclerAdapter.notifyDataSetChanged();
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(final View view, final int possition) {

        if (view.getId()!=R.id.placeShowInMapButton){
            // проверка подключения к инету
            InternetStatus inetStatus = new InternetStatus();
            inetStatus.check(getActivity(), new CallBack() {
                @Override
                public void responseNetwork(Object o) {
                    // проверка авторизации
                    if (LoginActivity.TOKEN_TO_SEND != null){
                        addToFuture(view, possition);
                    }else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "Need Authorization", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void failNetwork(Throwable t) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "No Internet", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    // TODO: 4/11/2017 rename this method
    public void addToFuture(final View view, final int possition){

        final Place place = mPlacesList.get(possition);

        switch (view.getId()){
            case R.id.placeImageView:
                if (placesFor!=null && placesFor.equals(TOP)) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra(ID_STRING, place.getTripId());
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(getActivity(), ShowPLaceWithMap.class);
                    intent.putExtra(TRIP_ID, place.getTripId());
                    intent.putExtra(PLACE_ID, place.getId());
                    startActivity(intent);
                }

                // TODO: 4/10/2017 логика нажатия на плейс не из списка топов

                break;

            case R.id.placeAddToFutureButton:
                network.addToFutureTrips(LoginActivity.TOKEN_TO_SEND.toString(), place.getId(), new CallBack() {
                    @Override
                    public void responseNetwork(Object o) {
                        Response<ResponseBody> response = (Response<ResponseBody>) o;
                        if (response.code()==201) {
                            Toast.makeText(getActivity(), "places added to your future trips", Toast.LENGTH_LONG).show();

                            place.isFuture = true;
                            recyclerAdapter.notifyItemChanged(possition);
                        }else {
                            Toast.makeText(getActivity(), "Проблема с сервером ", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void failNetwork(Throwable t) {

                    }
                });
                break;

            case R.id.placeLikeButton:
                network.likePlace(LoginActivity.TOKEN_TO_SEND.toString(), place.getId(), new CallBack() {
                    @Override
                    public void responseNetwork(Object o) {
                        Response<ResponseBody> response = (Response<ResponseBody>) o;
                        if (response.code()==201) {
                            Toast.makeText(getActivity(), "places Liked ", Toast.LENGTH_LONG).show();

                            place.isLike = true;
                            recyclerAdapter.notifyItemChanged(possition);
                        }else {
                            Toast.makeText(getActivity(), "Проблема с сервером ", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void failNetwork(Throwable t) {

                    }
                });
                break;
        }
    }
}
