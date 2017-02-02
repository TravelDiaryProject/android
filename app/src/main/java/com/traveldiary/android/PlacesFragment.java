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


public class PlacesFragment extends Fragment {
    private ChangeFragmentInterface mFragmentInterface;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private Place place;
    private List<Place> mPlacesList;
    private LinearLayoutManager mLayoutManager;

    private String URL_PLACES = "http://188.166.77.89/api/v1/trip/";
    private String URL_PLACES_2 = "/places";

    private int tripId;

    public PlacesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tripId = getArguments().getInt("id");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_places,
                container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.places_recycler_view);

        mPlacesList = new ArrayList<>();

        recyclerAdapter = new RecyclerAdapter(getActivity(), null, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Click on place ");
            }
        }, mPlacesList);
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
            mFragmentInterface = (ChangeFragmentInterface) activity;
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    private void downloadImage() {

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL_PLACES + tripId + URL_PLACES_2,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject placeJson = response.getJSONObject(i);

                                place = new Place(placeJson.getInt("id"), placeJson.getString("photo"),
                                        placeJson.getString("latitude"), placeJson.getString("longitude"));
                                mPlacesList.add(place);

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
