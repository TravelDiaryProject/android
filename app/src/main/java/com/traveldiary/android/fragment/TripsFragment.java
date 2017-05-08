package com.traveldiary.android.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.traveldiary.android.activity.CreateFindActivity;
import com.traveldiary.android.activity.DetailActivity;
import com.traveldiary.android.network.CallBack;
import com.traveldiary.android.adapter.RecyclerAdapter;
import com.traveldiary.android.model.Trip;

import java.util.ArrayList;
import java.util.List;

import static com.traveldiary.android.App.dataService;
import static com.traveldiary.android.Constans.CREATE_TRIP;
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
    private ProgressBar mProgressBar;
    private String mTripsFor;

    private TextView mNoTripsTextView;
    private Button mNoTripsButton;

    private ActionMode mActionMode;


    public interface OnPlaneButtonListener{
        public void onPlaneButtonClick();
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
            Log.d(TAG, "onCreate, mTripsFor = " + mTripsFor);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trips,
                container, false);


        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }


        mNoTripsTextView = (TextView) rootView.findViewById(R.id.no_trips_textView);
        mNoTripsButton = (Button) rootView.findViewById(R.id.no_trips_planeButton);
        mNoTripsButton.setOnClickListener(this);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.trips_progress);
        mProgressBar.setVisibility(View.VISIBLE);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.trips_swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.trips_recycler_view);
        mTripList = new ArrayList<>();
        mRecyclerAdapter = new RecyclerAdapter(getActivity(), mTripList, this, this, null);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mRecyclerAdapter);

        listTripsByForType(false);

        return rootView;
    }

    private void listTripsByForType(final boolean isThisRefresh){

        switch (mTripsFor){
            case MY:

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
                        noNetworkOrEmptyListInfo(getResources().getString(R.string.check_network_connection), getResources().getString(R.string.try_again));
                    }
                });
                break;

            case FUTURE:

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
                        noNetworkOrEmptyListInfo(getResources().getString(R.string.check_network_connection), getResources().getString(R.string.try_again));
                    }
                });
                break;
        }
    }

    public void noNetworkOrEmptyListInfo(String textView, String buttonText){

        if (mTripList.size()==0) {
            mNoTripsTextView.setText(textView);
            mNoTripsTextView.setVisibility(View.VISIBLE);
            if (buttonText != null) {
                mNoTripsButton.setText(buttonText);
                mNoTripsButton.setVisibility(View.VISIBLE);
            }
        }
    }

    public void manipulateWithResponse(Object o, boolean isThisRefresh){
        List<Trip> tripsList = (List<Trip>) o;

        mNoTripsTextView.setVisibility(View.GONE);
        mNoTripsButton.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);

        if (tripsList.size()==0){
            mTripList.clear();

            if (mTripsFor.equals(MY))
                noNetworkOrEmptyListInfo(getResources().getString(R.string.no_my_trips), null);
            else
                noNetworkOrEmptyListInfo(getResources().getString(R.string.no_future_trips), getResources().getString(R.string.plane));
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

        for (int i = (selected.size() - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {
                final int finalI = i;
                final Trip trip = mTripList.get(selected.keyAt(finalI));

                dataService.removeTrip(mTripList.get(selected.keyAt(i)), new CallBack() {
                            @Override
                            public void responseNetwork(Object o) {
                                mTripList.remove(trip);

                                if (selected.size()>2)
                                    mRecyclerAdapter.notifyDataSetChanged();
                                else
                                    mRecyclerAdapter.notifyItemRemoved(selected.keyAt(finalI));
                            }

                            @Override
                            public void failNetwork(Throwable t) {
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
                }else {
                    onPlaneButtonListener.onPlaneButtonClick();
                }
                break;
        }
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        listTripsByForType(true);
    }
}