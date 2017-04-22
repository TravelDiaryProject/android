package com.traveldiary.android;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.traveldiary.android.network.CallBack;
import com.traveldiary.android.adapter.RecyclerAdapter;
import com.traveldiary.android.model.Trip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.traveldiary.android.App.network;
import static com.traveldiary.android.Constans.ALL;
import static com.traveldiary.android.Constans.FUTURE;
import static com.traveldiary.android.Constans.ID_STRING;
import static com.traveldiary.android.Constans.MY;
import static com.traveldiary.android.Constans.TOKEN_CONST;
import static com.traveldiary.android.Constans.TRIPS_FOR;

public class TripsFragment extends Fragment implements View.OnClickListener, RecyclerAdapter.ItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

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

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.trips_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.trips_recycler_view);
        mTripList = new ArrayList<>();
        recyclerAdapter = new RecyclerAdapter(getActivity(), mTripList, this, null);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);

        listTripsByForType(false);

        return rootView;
    }

    private void listTripsByForType(final boolean isThisRefresh){
        switch (tripsFor){
            case MY:
                network.getMyTrips(TOKEN_CONST, new CallBack() {
                    @Override
                    public void responseNetwork(Object o) {
                        manipulateWithResponse(o, isThisRefresh);
                    }

                    @Override
                    public void failNetwork(Throwable t) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
                break;

            case FUTURE:
                network.getFutureTrips(TOKEN_CONST ,new CallBack() {
                    @Override
                    public void responseNetwork(Object o) {

                        manipulateWithResponse(o, isThisRefresh);
                    }

                    @Override
                    public void failNetwork(Throwable t) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
                break;
        }
    }

    public void manipulateWithResponse(Object o, boolean isThisRefresh){
        List<Trip> tripsList = (List<Trip>) o;

        if (tripsList.size()==0){
            Toast.makeText(getActivity(), "No Trips", Toast.LENGTH_LONG).show();
        }

        if (!isThisRefresh) {
            //Collections.reverse(tripsList);
            //mTripList.addAll(tripsList);
            //recyclerAdapter.notifyDataSetChanged();
            recyclerAdapter.updateAdapterTrip(tripsList);
            //swipeRefreshLayout.setRefreshing(false);
            mProgressBar.setVisibility(View.GONE);
        }else {
            recyclerAdapter.updateAdapterTrip(tripsList);
            swipeRefreshLayout.setRefreshing(false);
        }

    }



    @Override
    public void onItemClick(View view, int possition) {

        Trip trip = mTripList.get(possition);
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(ID_STRING, trip.getId());
        startActivity(intent);
    }


    @Override
    public void onClick(View v){
        if (v.getId()==R.id.add_trip_button){

            CreatTripFragment creatTripFragment = new CreatTripFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_main, creatTripFragment);
            ft.addToBackStack(null);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);

        listTripsByForType(true);
    }
}