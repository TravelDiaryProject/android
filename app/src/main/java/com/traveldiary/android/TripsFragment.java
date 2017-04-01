package com.traveldiary.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.traveldiary.android.network.CallBackInterface;
import com.traveldiary.android.adapter.RecyclerAdapter;
import com.traveldiary.android.model.City;
import com.traveldiary.android.model.Place;
import com.traveldiary.android.model.RegistrationResponse;
import com.traveldiary.android.model.Trip;
import com.traveldiary.android.network.Network;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

import static com.traveldiary.android.Constans.ALL;
import static com.traveldiary.android.Constans.ID_STRING;
import static com.traveldiary.android.Constans.MY;
import static com.traveldiary.android.Constans.TRIPS_FOR;

public class TripsFragment extends Fragment implements View.OnClickListener, CallBackInterface {

    private ChangeFragmentInterface mChangeFragmentInterface;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private List<Trip> mTripList;
    private LinearLayoutManager mLayoutManager;
    private FloatingActionButton addTripButton;

    private ProgressBar mProgressBar;

    private String tripsFor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null){
            tripsFor = getArguments().getString(TRIPS_FOR);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trips,
                container, false);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.trips_progress);
        mProgressBar.setVisibility(View.VISIBLE);

        addTripButton = (FloatingActionButton) rootView.findViewById(R.id.add_trip_button);
        addTripButton.setOnClickListener(this);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.trips_recycler_view);
        mTripList = new ArrayList<>();
        recyclerAdapter = new RecyclerAdapter(getActivity(), mTripList, this, null);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);


        Network network = new Network(this);

        if (tripsFor == null){
            System.out.println("TRIPSFOR == NULL!!!!!!!!!!!!!!!!!!!!!!!!!");
        }else if (tripsFor.equals(MY)) {
            network.getMyTrips(LoginActivity.TOKEN_TO_SEND.toString());
        }else if (tripsFor.equals(ALL)){
            network.getAllTrips();
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
    public void onClick(View v){

        if (v.getId()==R.id.add_trip_button){
            System.out.println("float");

            Fragment fragment = new CreatTripFragment();
            mChangeFragmentInterface.trans(fragment);
        }

        else if (v.getParent()instanceof RecyclerView){
            System.out.println("id click trip = " + v.getId());

            int itemPossition = recyclerView.getChildLayoutPosition(v);
            Trip trip = mTripList.get(itemPossition);

            Fragment fragment = new PlacesFragment();

            Bundle args = new Bundle();
            args.putInt(ID_STRING, trip.getId());
            fragment.setArguments(args);//передаем в новый фрагмент ид трипа чтобы подтянуть имг этого трипа

            mChangeFragmentInterface.trans(fragment);
        }
    }

    @Override
    public void getAllTrips(List<Trip> allTrips) {

        mTripList.addAll(allTrips);

        mProgressBar.setVisibility(View.GONE);

        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void getMyTrips(List<Trip> myTrips) {

        mTripList.addAll(myTrips);

        mProgressBar.setVisibility(View.GONE);

        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void getTripsByCity(List<Trip> tripsByCity) {

        mTripList.addAll(tripsByCity);

        mProgressBar.setVisibility(View.GONE);

        recyclerAdapter.notifyDataSetChanged();
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

    @Override
    public void getAllPlaces(List<Place> allPlaces) {

    }

    @Override
    public void getMyPlaces(List<Place> myPlaces) {

    }

    @Override
    public void getPlacesByTrip(List<Place> placesByTrip) {

    }

    @Override
    public void getPlacesByCity(List<Place> placesByCity) {

    }

}