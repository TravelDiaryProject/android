package com.traveldiary.android;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AllTripsActivity extends AppCompatActivity implements ChangeFragmentInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_trips);

        if (getIntent().hasExtra("OPEN_PLACES_FRAGMENT_WITH_ID")){
            Fragment placesFragment = new PlacesFragment();
            Bundle args = new Bundle();
            args.putInt("id", getIntent().getIntExtra("OPEN_PLACES_FRAGMENT_WITH_ID", 0));
            placesFragment.setArguments(args);
            trans(placesFragment);
        }else {

            Fragment allTripsFragment = new TripsFragment();
            trans(allTripsFragment);
        }
    }

    public void trans(Fragment fragment){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_all_trips_activity, fragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
}

