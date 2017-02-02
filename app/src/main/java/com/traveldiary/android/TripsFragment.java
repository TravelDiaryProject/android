package com.traveldiary.android;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TripsFragment extends Fragment {

    private ChangeFragmentInterface mChangeFragmentInterface;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private Trip trip;
    private List<Trip> mTripList;
    private LinearLayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trips,
                container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.trips_recycler_view);
        //mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mTripList = new ArrayList<>();

        recyclerAdapter = new RecyclerAdapter(getActivity(), mTripList, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Trip trip = (Trip) view.getTag(); //получаем нажатый трип
                Fragment fragment = new PlacesFragment();

                Bundle args = new Bundle();
                args.putInt("id", trip.getId());
                fragment.setArguments(args);//передаем в новый фрагмент ид трипа чтобы подтянуть имг этого трипа

                mChangeFragmentInterface.trans(fragment);
            }
        }, null);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);

        downloadImage();

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

    private void downloadImage() {

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(AllTripsActivity.URL_TRIPS,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject tripJson = response.getJSONObject(i);

                                trip = new Trip(tripJson.getInt("id"), tripJson.getString("title"), tripJson.getString("photo"));
                                mTripList.add(trip);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        recyclerAdapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("ERROR = " + error.toString());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);

    }
}