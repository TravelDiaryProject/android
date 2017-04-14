package com.traveldiary.android;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.traveldiary.android.model.Place;
import com.traveldiary.android.network.CallBack;

import java.util.ArrayList;
import java.util.List;

import static com.traveldiary.android.App.network;
import static com.traveldiary.android.Constans.ID_STRING;
import static com.traveldiary.android.Constans.PLACE_ID;


public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private int tripId;
    private int focusPlaceId;
    private List<Place> mPlacesList = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments()!=null){
            tripId = getArguments().getInt(ID_STRING);
            focusPlaceId = getArguments().getInt(PLACE_ID);

            Log.d("MYLOG", "tripId = " + tripId + " focusPLaceId = " + focusPlaceId);

        }

        /*network.getPlacesByTrip(tripId, new CallBack() {
            @Override
            public void responseNetwork(Object o) {
                List<Place> placesList = (List<Place>) o;
                mPlacesList.addAll(placesList);
            }

            @Override
            public void failNetwork(Throwable t) {

            }
        });*/


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MapFragment fragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        network.getPlacesByTrip(tripId, new CallBack() {
            @Override
            public void responseNetwork(Object o) {
                List<Place> placesList = (List<Place>) o;
                mPlacesList.addAll(placesList);
                smth(googleMap);
            }

            @Override
            public void failNetwork(Throwable t) {

            }
        });






    }

    private MarkerOptions markerInit(Place place){

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(Double.parseDouble(place.getLatitude()), Double.parseDouble(place.getLongitude())));
        markerOptions.title("Title");

        return markerOptions;
    }

    private void smth(GoogleMap googleMap){

        Log.d("MYLOG", "WTF list size = " + mPlacesList.size());

        for (int i = 0; i < mPlacesList.size(); i++){
            if (mPlacesList.get(i).getLatitude()!=null && mPlacesList.get(i).getLongitude()!=null
                    && !mPlacesList.get(i).getLatitude().equals("")) {
                googleMap.addMarker(markerInit(mPlacesList.get(i)));

                if (mPlacesList.get(i).getId() == focusPlaceId){

                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(Double.parseDouble(mPlacesList.get(i).getLatitude()),
                                    Double.parseDouble(mPlacesList.get(i).getLongitude())), 10));
                }

                System.out.println("place lat = " + mPlacesList.get(i).getLatitude() + " long = " + mPlacesList.get(i).getLongitude());
            }

        }
    }

}
