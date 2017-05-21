package com.traveldiary.android.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.traveldiary.android.R;
import com.traveldiary.android.ToolbarActionMode;
import com.traveldiary.android.activity.DetailActivity;
import com.traveldiary.android.activity.LoginActivity;
import com.traveldiary.android.callback.SimpleCallBack;
import com.traveldiary.android.adapter.RecyclerAdapter;
import com.traveldiary.android.model.Trip;
import com.traveldiary.android.callback.CallbackTrips;

import java.util.ArrayList;
import java.util.List;

import static com.traveldiary.android.App.dataService;
import static com.traveldiary.android.Constans.FUTURE;
import static com.traveldiary.android.Constans.ID_STRING;
import static com.traveldiary.android.Constans.MY;
import static com.traveldiary.android.Constans.TOKEN_CONST;
import static com.traveldiary.android.Constans.TRIPS_FOR;

public class TripsFragment extends Fragment implements View.OnClickListener, RecyclerAdapter.RecyclerItemListener, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecyclerView;
    private RecyclerAdapter mRecyclerAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<Trip> mTripList;
    private LinearLayoutManager mLayoutManager;
    private ProgressBar mProgressBar;
    private String mTripsFor;

    private TextView mNoTripsTextView;
    private Button mNoTripsButton;

    private boolean isRefresh = false;

    private ActionMode mActionMode;


    public interface OnPlaneButtonListener{
        void onPlaneButtonClick();
    }

    private OnPlaneButtonListener onPlaneButtonListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onPlaneButtonListener = (OnPlaneButtonListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement OnPlaneButtonListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            mTripsFor = getArguments().getString(TRIPS_FOR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trips,
                container, false);

        mNoTripsTextView = (TextView) rootView.findViewById(R.id.no_trips_textView);
        mNoTripsButton = (Button) rootView.findViewById(R.id.no_trips_planeButton);
        mNoTripsButton.setOnClickListener(this);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.trips_progress);
        mProgressBar.setVisibility(View.VISIBLE);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.trips_swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.trips_recycler_view);
        mTripList = new ArrayList<>();
        mRecyclerAdapter = new RecyclerAdapter(getActivity(), mTripList, this, null);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mRecyclerAdapter);

        listTripsByForType();

        return rootView;
    }

    private void listTripsByForType(){

        if (TOKEN_CONST == null || TOKEN_CONST.equals("")) {
            setNoViewsInfos(getResources().getString(R.string.need_authorization_function), getResources().getString(R.string.login));
            mProgressBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setRefreshing(false);
        } else {
            switch (mTripsFor) {
                case MY:
                    dataService.getMyTrips(getCallbackTrips());
                    break;

                case FUTURE:
                    dataService.getFutureTrips(getCallbackTrips());
                    break;
            }
        }
    }

    @NonNull
    private CallbackTrips getCallbackTrips() {
        return new CallbackTrips() {
            @Override
            public void response(List<Trip> tripList) {
                manipulateWithResponse(tripList);
            }

            @Override
            public void fail(Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                setNoViewsInfos(getResources().getString(R.string.check_network_connection), getResources().getString(R.string.try_again));
            }
        };
    }

    public void setNoViewsInfos(String textView, String buttonText){

        if (mTripList.size()==0) {
            mNoTripsTextView.setText(textView);
            mNoTripsTextView.setVisibility(View.VISIBLE);
            if (buttonText != null) {
                mNoTripsButton.setText(buttonText);
                mNoTripsButton.setVisibility(View.VISIBLE);
            }
        }
    }

    public void manipulateWithResponse(List<Trip> tripsList){

        Activity activity = getActivity();
        if(activity != null && isAdded()){
            mNoTripsTextView.setVisibility(View.GONE);
            mNoTripsButton.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);

            if (tripsList.size()==0){
                mTripList.clear();

                if (mTripsFor.equals(MY))
                    setNoViewsInfos(getString(R.string.no_my_trips), null);
                else
                    setNoViewsInfos(getString(R.string.no_future_trips), getString(R.string.plan));
            }

            if (!isRefresh) {
                mTripList.clear();
                mTripList.addAll(tripsList);
                mRecyclerAdapter.updateAdapterTrip(tripsList);
            }else {
                isRefresh = false;
                mTripList.clear();
                mTripList.addAll(tripsList);
                mRecyclerAdapter.updateAdapterTrip(tripsList);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    @Override
    public void onItemClick(View view, int position) {

        if ( mActionMode != null){
            onListItemSelect(position);
        }else {
            final Trip trip = mTripList.get(position);

            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra(ID_STRING, trip.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onItemLongClick(View view, final int position) {
        onListItemSelect(position);
    }

    private void onListItemSelect(int position) {
        mRecyclerAdapter.toggleSelection(position);

        boolean hasCheckedItems = mRecyclerAdapter.getSelectedCount() > 0;

        if (hasCheckedItems && mActionMode == null)
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(new ToolbarActionMode(mRecyclerAdapter, TripsFragment.this, null));
        else if (!hasCheckedItems && mActionMode != null)
            mActionMode.finish();

        if (mActionMode != null)
            mActionMode.setTitle(String.valueOf(mRecyclerAdapter.getSelectedCount()));
    }

    public void setNullToActionMode() {
        if (mActionMode != null) {
            mActionMode = null;
        }
    }

    public void deleteRows() {
        final SparseBooleanArray selected = mRecyclerAdapter.getSelectedIds();//Get selected ids

        for (int i = 0; i < selected.size(); i++) {
            if (selected.valueAt(i)) {
                final Trip trip = mTripList.get(selected.keyAt(i));

                dataService.removeTrip(trip.getId(), new SimpleCallBack() {
                    @Override
                    public void response(Object o) {
                    }

                    @Override
                    public void fail(Throwable t) {
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        Toast.makeText(getActivity(), selected.size() + getResources().getString(R.string.item_deleted), Toast.LENGTH_SHORT).show();//Show Toast
        mActionMode.finish();
    }

    @Override
    public void onClick(View v){

        switch (v.getId()){
            case R.id.no_trips_planeButton:
                if (mNoTripsButton.getText().toString().equals(getResources().getString(R.string.try_again))){
                    onRefresh();
                }else if (mNoTripsButton.getText().toString().equals(getResources().getString(R.string.login))){
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                else {
                    onPlaneButtonListener.onPlaneButtonClick();
                }
                break;
        }
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        isRefresh = true;
        listTripsByForType();
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }
}