package com.traveldiary.android;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.traveldiary.android.Interfaces.CallBackInterface;
import com.traveldiary.android.Interfaces.ChangeFragmentInterface;
import com.traveldiary.android.Interfaces.TravelDiaryService;
import com.traveldiary.android.adapter.RecyclerAdapter;
import com.traveldiary.android.essence.City;
import com.traveldiary.android.essence.Place;
import com.traveldiary.android.essence.RegistrationResponse;
import com.traveldiary.android.essence.Trip;
import com.traveldiary.android.network.Network;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.traveldiary.android.Constans.ALL;
import static com.traveldiary.android.Constans.ID_STRING;
import static com.traveldiary.android.Constans.MY;
import static com.traveldiary.android.Constans.PLACES_BY_CITY;
import static com.traveldiary.android.Constans.PLACES_FOR;
import static com.traveldiary.android.Constans.ROOT_URL;


public class PlacesFragment extends Fragment implements CallBackInterface {
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

                   /* Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra("Id", place.getId());
                    intent.putExtra("Photo", place.getPhoto());
                    startActivity(intent);*/
                }

            }
        }, mPlacesList);
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);


        Network network = new Network(this);

        // временный ужас!!!!!!
        if (placesFor == null){
            System.out.println("null places for");

            if (cityId != 0){
                network.getPlacesByCity(cityId);
            }else{
                network.getPlacesByTrip(tripId);
            }

        }else if (placesFor.equals(MY)){
            network.getMyPlaces(LoginActivity.TOKEN_TO_SEND.toString());
        }else if (placesFor.equals(ALL)) {
            network.getAllPlaces();
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


    @Override
    public void getAllPlaces(List<Place> allPlaces) {

        mPlacesList.addAll(allPlaces);

        recyclerAdapter.notifyDataSetChanged();

        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void getMyPlaces(List<Place> myPlaces) {

        mPlacesList.addAll(myPlaces);

        mProgressBar.setVisibility(View.GONE);

        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void getPlacesByTrip(List<Place> placesByTrip) {

        mPlacesList.addAll(placesByTrip);

        mProgressBar.setVisibility(View.GONE);

        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void getPlacesByCity(List<Place> placesByCity) {

        mPlacesList.addAll(placesByCity);

        mProgressBar.setVisibility(View.GONE);

        recyclerAdapter.notifyDataSetChanged();
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
