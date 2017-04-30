package com.traveldiary.android;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.traveldiary.android.data.Data;
import com.traveldiary.android.data.DataService;
import com.traveldiary.android.network.CallBack;
import com.traveldiary.android.adapter.RecyclerAdapter;
import com.traveldiary.android.model.Trip;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static com.traveldiary.android.App.network;
import static com.traveldiary.android.Constans.FUTURE;
import static com.traveldiary.android.Constans.ID_STRING;
import static com.traveldiary.android.Constans.MY;
import static com.traveldiary.android.Constans.TOKEN_CONST;
import static com.traveldiary.android.Constans.TRIPS_FOR;

public class TripsFragment extends Fragment implements View.OnClickListener, RecyclerAdapter.ItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static String TAG = "TripsFragment";

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<Trip> mTripList;
    private LinearLayoutManager mLayoutManager;
    private FloatingActionButton mAddTripFloatButton;

    private ProgressBar mProgressBar;
    private String tripsFor;

    private TextView noTripsTextView;
    private Button noTripsButton;
    private DataService dataService;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null){
            tripsFor = getArguments().getString(TRIPS_FOR);
            Log.d(TAG, "onCreate, tripsFor = " + tripsFor);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trips,
                container, false);

        Log.d(TAG, "onCreateView");

        dataService = new DataService();

        noTripsTextView = (TextView) rootView.findViewById(R.id.no_trips_textView);
        noTripsButton = (Button) rootView.findViewById(R.id.no_trips_planeButton);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.trips_progress);
        mProgressBar.setVisibility(View.VISIBLE);

        mAddTripFloatButton = (FloatingActionButton) rootView.findViewById(R.id.add_trip_button);
        mAddTripFloatButton.setOnClickListener(this);

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
                mAddTripFloatButton.setImageResource(R.drawable.ic_create_new_folder_24dp);

                dataService.getMyTrips(new CallBack() {
                    @Override
                    public void responseNetwork(Object o) {
                        manipulateWithResponse(o, isThisRefresh);
                    }

                    @Override
                    public void failNetwork(Throwable t) {
                        mProgressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;

            case FUTURE:
                mAddTripFloatButton.hide();

                dataService.getFutureTrips(new CallBack() {
                    @Override
                    public void responseNetwork(Object o) {
                        manipulateWithResponse(o, isThisRefresh);
                    }

                    @Override
                    public void failNetwork(Throwable t) {
                        mProgressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }

    public void manipulateWithResponse(Object o, boolean isThisRefresh){
        List<Trip> tripsList = (List<Trip>) o;

        mProgressBar.setVisibility(View.GONE);

        if (tripsList.size()==0){
            mTripList.clear();
            noTripsTextView.setVisibility(View.VISIBLE);
            noTripsButton.setVisibility(View.VISIBLE);
            //Toast.makeText(getActivity(), "No Trips", Toast.LENGTH_LONG).show();
        }

        if (!isThisRefresh) {

            mTripList.clear();
            mTripList.addAll(tripsList);
            recyclerAdapter.updateAdapterTrip(tripsList);
        }else {
            mTripList.clear();
            mTripList.addAll(tripsList);
            recyclerAdapter.updateAdapterTrip(tripsList);
            swipeRefreshLayout.setRefreshing(false);
        }

    }



    @Override
    public void onItemClick(View view, int possition) {

        final Trip trip = mTripList.get(possition);

        if (view.getId()==R.id.tripRemoveButton){
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Точно удалить?")
                    .setCancelable(true)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                            dataService.removeTrip(trip, new CallBack() {
                                @Override
                                public void responseNetwork(Object o) {
                                    Toast.makeText(getActivity(), "удалено", Toast.LENGTH_SHORT).show();
                                    //recyclerAdapter.removeTrip(trip);
                                    dialog.cancel();
                                }

                                @Override
                                public void failNetwork(Throwable t) {
                                    Toast.makeText(getActivity(), "ошибка", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }
                            });
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();

                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }else {
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra(ID_STRING, trip.getId());
            startActivity(intent);
        }
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