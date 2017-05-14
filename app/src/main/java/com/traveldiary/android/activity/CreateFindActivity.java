package com.traveldiary.android.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.traveldiary.android.R;
import com.traveldiary.android.fragment.CreateTripFragment;
import com.traveldiary.android.fragment.PlacesFragment;
import com.traveldiary.android.fragment.TripsFragment;

import static com.traveldiary.android.Constans.CREATE_TRIP;
import static com.traveldiary.android.Constans.PLACES_BY_CITY;
import static com.traveldiary.android.Constans.PLACES_BY_CITY_NAME;
import static com.traveldiary.android.Constans.PLACES_BY_COUNTRY;
import static com.traveldiary.android.Constans.PLACES_BY_COUNTRY_NAME;
import static com.traveldiary.android.Constans.PLACES_FOR;
import static com.traveldiary.android.Constans.PLACES_FOR_CITY;
import static com.traveldiary.android.Constans.PLACES_FOR_COUNTRY;
import static com.traveldiary.android.Constans.PLACES_SEARCH_NAME;
import static com.traveldiary.android.Constans.PLACE_ID;

public class CreateFindActivity extends AppCompatActivity implements TripsFragment.OnPlaneButtonListener {

    private String createTrip;
    private String placesFor;
    private int idSerch;
    private String nameSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_find);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCreate);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        createTrip = getIntent().getStringExtra(CREATE_TRIP);
        placesFor = getIntent().getStringExtra(PLACES_FOR);
        idSerch = getIntent().getIntExtra(PLACE_ID, -1);
        nameSearch = getIntent().getStringExtra(PLACES_SEARCH_NAME);


        if (createTrip!=null){

            CreateTripFragment createTripFragment = new CreateTripFragment();
            trans(createTripFragment);
        } else if (placesFor != null && placesFor.equals(PLACES_FOR_CITY)) {
            PlacesFragment placesFragment = new PlacesFragment();
            Bundle args = new Bundle();
            args.putString(PLACES_FOR, PLACES_FOR_CITY);
            args.putInt(PLACES_BY_CITY, idSerch);
            args.putString(PLACES_BY_CITY_NAME, nameSearch);
            placesFragment.setArguments(args);

            trans(placesFragment);

        } else if (placesFor != null && placesFor.equals(PLACES_FOR_COUNTRY)) {
            PlacesFragment placesFragment = new PlacesFragment();
            Bundle args = new Bundle();
            args.putString(PLACES_FOR, PLACES_FOR_COUNTRY);
            args.putInt(PLACES_BY_COUNTRY, idSerch);
            args.putString(PLACES_BY_COUNTRY_NAME, nameSearch);
            placesFragment.setArguments(args);

            trans(placesFragment);
        }
    }

    public void trans(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_create_find, fragment);
        //ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onPlaneButtonClick() {
//        mNavigationView.setCheckedItem(R.id.menu_find_place);
//        setTitle(mNavigationView.getMenu().getItem(2).getTitle());
//
//        FindPlaceFragment findPlaceFragment = new FindPlaceFragment();
//        trans(findPlaceFragment);
    }
}
