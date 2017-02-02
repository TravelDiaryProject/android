package com.traveldiary.android;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AllTripsActivity extends AppCompatActivity implements ChangeFragmentInterface {

    static final String ROOT_URL = "http://188.166.77.89/";
    static final String URL_TRIPS = "http://188.166.77.89/api/v1/trips";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_trips);

        Fragment allTripsFragment = new TripsFragment();
        trans(allTripsFragment);

    }

    public void trans(Fragment fragment){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_all_trips_activity, fragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
}

