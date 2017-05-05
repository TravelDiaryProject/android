package com.traveldiary.android.fragment;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.traveldiary.android.R;
import com.traveldiary.android.activity.DetailActivity;
import com.traveldiary.android.network.CallBack;
import com.traveldiary.android.adapter.RecyclerAdapter;
import com.traveldiary.android.model.Trip;

import java.util.ArrayList;
import java.util.List;

import static com.traveldiary.android.App.dataService;
import static com.traveldiary.android.Constans.FUTURE;
import static com.traveldiary.android.Constans.ID_STRING;
import static com.traveldiary.android.Constans.MY;
import static com.traveldiary.android.Constans.TRIPS_FOR;

public class TripsFragment extends Fragment implements View.OnClickListener, RecyclerAdapter.ItemClickListener, RecyclerAdapter.ItemLongClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static String TAG = "TripsFragment";

    private RecyclerView mRecyclerView;
    private RecyclerAdapter mRecyclerAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<Trip> mTripList;
    private LinearLayoutManager mLayoutManager;
    private FloatingActionButton mAddTripFloatButton;
    private ProgressBar mProgressBar;
    private String mTripsFor;

    private TextView mNoTripsTextView;
    private Button mNoTripsButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null){
            mTripsFor = getArguments().getString(TRIPS_FOR);
            Log.d(TAG, "onCreate, mTripsFor = " + mTripsFor);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trips,
                container, false);

        Log.d(TAG, "onCreateView");

        mNoTripsTextView = (TextView) rootView.findViewById(R.id.no_trips_textView);
        mNoTripsButton = (Button) rootView.findViewById(R.id.no_trips_planeButton);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.trips_progress);
        mProgressBar.setVisibility(View.VISIBLE);

        mAddTripFloatButton = (FloatingActionButton) rootView.findViewById(R.id.add_trip_button);
        mAddTripFloatButton.setOnClickListener(this);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.trips_swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.trips_recycler_view);
        mTripList = new ArrayList<>();
        mRecyclerAdapter = new RecyclerAdapter(getActivity(), mTripList, this, this, null);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mRecyclerAdapter);

        listTripsByForType(false);

        return rootView;
    }

    private void listTripsByForType(final boolean isThisRefresh){

        switch (mTripsFor){
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
                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        noNetworkOrEmptyListInfo("При загрузке данных произошла ошибка. Проверте подключение к сети.", "Повторить попытку");
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
                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        noNetworkOrEmptyListInfo("При загрузке данных произошла ошибка. Проверте подключение к сети.", "Повторить попытку");
                    }
                });
                break;
        }
    }

    public void noNetworkOrEmptyListInfo(String textView, String button){
        if (mTripList.size()==0){
            mNoTripsTextView.setText(textView);
            mNoTripsTextView.setVisibility(View.VISIBLE);
            mNoTripsButton.setText(button);
            mNoTripsButton.setVisibility(View.VISIBLE);
        }
    }

    public void manipulateWithResponse(Object o, boolean isThisRefresh){
        List<Trip> tripsList = (List<Trip>) o;

        mProgressBar.setVisibility(View.GONE);

        if (tripsList.size()==0){
            mTripList.clear();
            noNetworkOrEmptyListInfo("No trips yet. Click on the button to plan a trip", "Plane");
            //Toast.makeText(getActivity(), "No Trips", Toast.LENGTH_LONG).show();
        }

        if (!isThisRefresh) {

            mTripList.clear();
            mTripList.addAll(tripsList);
            mRecyclerAdapter.updateAdapterTrip(tripsList);
        }else {
            mTripList.clear();
            mTripList.addAll(tripsList);
            mRecyclerAdapter.updateAdapterTrip(tripsList);
            mSwipeRefreshLayout.setRefreshing(false);
        }

    }

    @Override
    public void onItemClick(View view, int position) {

        final Trip trip = mTripList.get(position);

        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(ID_STRING, trip.getId());
        startActivity(intent);

    }

    @Override
    public void onItemLongClick(View view, final int position) {
        final Trip trip = mTripList.get(position);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Удалить?")
                .setCancelable(true)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                        dataService.removeTrip(trip, new CallBack() {
                            @Override
                            public void responseNetwork(Object o) {
                                Toast.makeText(getActivity(), "удалено", Toast.LENGTH_SHORT).show();
                                mRecyclerAdapter.notifyItemRemoved(position);
                                mTripList.remove(position);
                                dialog.cancel();
                            }

                            @Override
                            public void failNetwork(Throwable t) {
                                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
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
        mSwipeRefreshLayout.setRefreshing(true);

        listTripsByForType(true);
    }
}