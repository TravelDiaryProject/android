package com.traveldiary.android;

import android.app.Activity;
import android.content.Intent;
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

import com.traveldiary.android.network.CallBack;
import com.traveldiary.android.adapter.RecyclerAdapter;
import com.traveldiary.android.model.Trip;

import java.util.ArrayList;
import java.util.List;

import static com.traveldiary.android.App.network;
import static com.traveldiary.android.Constans.ALL;
import static com.traveldiary.android.Constans.ID_STRING;
import static com.traveldiary.android.Constans.MY;
import static com.traveldiary.android.Constans.TRIPS_FOR;

public class TripsFragment extends Fragment implements View.OnClickListener, RecyclerAdapter.ItemClickListener {

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

        if (tripsFor == null){
            System.out.println("TRIPSFOR == NULL!!!!!!!!!!!!!!!!!!!!!!!!!");
        }else if (tripsFor.equals(MY)) {
            network.getMyTrips(LoginActivity.TOKEN_TO_SEND.toString(), new CallBack() {
                @Override
                public void responseNetwork(Object o) {
                    manipulateWithResponse(o);
                }

                @Override
                public void failNetwork(Throwable t) {

                }
            });
        }else if (tripsFor.equals(ALL)){
            network.getAllTrips(new CallBack() {
                @Override
                public void responseNetwork(Object o) {
                    manipulateWithResponse(o);
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


    @Override
    public void onItemClick(View view, int possition) {
        int itemPossition = recyclerView.getChildLayoutPosition(view);
        Trip trip = mTripList.get(itemPossition);

        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(ID_STRING, trip.getId());
        startActivity(intent);
    }


    @Override
    public void onClick(View v){
        if (v.getId()==R.id.add_trip_button){

            Fragment fragment = new CreatTripFragment();
            mChangeFragmentInterface.trans(fragment);
        }
    }

    public void manipulateWithResponse(Object o){
        List<Trip> tripsList = (List<Trip>) o;

        mTripList.addAll(tripsList);
        mProgressBar.setVisibility(View.GONE);
        recyclerAdapter.notifyDataSetChanged();
    }
}