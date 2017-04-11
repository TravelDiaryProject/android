package com.traveldiary.android;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import static com.traveldiary.android.Constans.ID_STRING;
import static com.traveldiary.android.Constans.PLACE_ID;
import static com.traveldiary.android.Constans.TRIP_ID;

public class ShowPLaceWithMap extends AppCompatActivity {

    private int tripId;
    private int focusPlaceId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_place_with_map);

        tripId = getIntent().getIntExtra(TRIP_ID, -1);
        focusPlaceId = getIntent().getIntExtra(PLACE_ID, 0);

        MapsFragment mapsFragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putInt(ID_STRING, tripId);
        args.putInt(PLACE_ID, focusPlaceId);
        mapsFragment.setArguments(args);
        trans(mapsFragment);




    }

    public void trans(Fragment fragment) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_show_act_map, fragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
}
