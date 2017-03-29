package com.traveldiary.android;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Cyborg on 2/22/2017.
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private double lat;
    private double lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        lat = Double.parseDouble(getIntent().getStringExtra("Latitude"));
        lon = Double.parseDouble(getIntent().getStringExtra("Longitude"));


    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(lat, lon);
        map.addMarker(new MarkerOptions().position(sydney).title("Тут будет название места!"));
        //map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
