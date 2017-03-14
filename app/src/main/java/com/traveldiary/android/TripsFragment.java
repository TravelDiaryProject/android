package com.traveldiary.android;

import android.app.Activity;
import android.content.Intent;
import android.icu.util.ValueIterator;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.traveldiary.android.Interfaces.ChangeFragmentInterface;
import com.traveldiary.android.Interfaces.TravelDiaryService;
import com.traveldiary.android.adapter.RecyclerAdapter;
import com.traveldiary.android.essence.Trip;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.traveldiary.android.Constans.ID_STRING;
import static com.traveldiary.android.Constans.ROOT_URL;
import static com.traveldiary.android.Constans.TRIPS_FOR;

public class TripsFragment extends Fragment implements View.OnClickListener {

    private ChangeFragmentInterface mChangeFragmentInterface;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private Trip trip;
    private List<Trip> mTripList;
    private LinearLayoutManager mLayoutManager;
    private FloatingActionButton addTripButton;

    private ProgressBar mProgressBar;

    private Retrofit retrofit;
    private static TravelDiaryService travelDiaryService;

    private String tripsFor = "All";

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

        if (tripsFor.equals("My")) {
            downloadMyTrips();
            System.out.println("token = " + LoginActivity.TOKEN_TO_SEND);
        }else {
            downloadAllTrips();
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

    private void downloadMyTrips() {

        travelDiaryService = Api.getTravelDiaryService();

        travelDiaryService.listMyTrips(LoginActivity.TOKEN_TO_SEND.toString()).enqueue(new Callback<List<Trip>>() {
            @Override
            public void onResponse(Call<List<Trip>> call, retrofit2.Response<List<Trip>> response) {

                System.out.println(response.body().toString());

                mTripList.addAll(response.body());

                mProgressBar.setVisibility(View.GONE);

                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Trip>> call, Throwable t) {

            }
        });
    }

    private void downloadAllTrips() {

        travelDiaryService = Api.getTravelDiaryService();

        travelDiaryService.listAllTrips().enqueue(new Callback<List<Trip>>() {
            @Override
            public void onResponse(Call<List<Trip>> call, retrofit2.Response<List<Trip>> response) {

                System.out.printf("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAonResponse = " + response.body().toString());


                mTripList.addAll(response.body());


                mProgressBar.setVisibility(View.GONE);

                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Trip>> call, Throwable t) {
                System.out.printf("AAAAAAAAAAAAAAAAAAAAAAAAAAAonFailure = " + t.toString());
            }
        });
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
}