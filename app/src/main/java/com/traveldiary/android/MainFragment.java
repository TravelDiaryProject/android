package com.traveldiary.android;

import android.app.Activity;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.traveldiary.android.network.CallBackInterface;
import com.traveldiary.android.model.City;
import com.traveldiary.android.model.Place;
import com.traveldiary.android.model.RegistrationResponse;
import com.traveldiary.android.model.Trip;
import com.traveldiary.android.network.Network;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

import static com.traveldiary.android.Constans.PLACES_BY_CITY;


public class MainFragment extends Fragment implements CallBackInterface{

    private ChangeFragmentInterface mChangeFragmentInterface;

    private List<City> mCityList;
    private List<String> mCities;
    private SearchView searchView;

    private Network network;

    private SimpleCursorAdapter simpleCursorAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main,
                container, false);

        network = new Network(this);

        mCityList = new ArrayList<>();
        mCities = new ArrayList<>();

        network.getAllCities();

        final String[] from = new String[] {"cityName"};
        final int[] to = new int[] {android.R.id.text1};
        simpleCursorAdapter = new SimpleCursorAdapter(getActivity(),android.R.layout.simple_list_item_1,null,from,to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        searchView = (SearchView) rootView.findViewById(R.id.search);

        searchView.setSuggestionsAdapter(simpleCursorAdapter);
        searchView.setIconifiedByDefault(false);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionClick(int position) {
                CursorAdapter ca = searchView.getSuggestionsAdapter();
                Cursor cursor = ca.getCursor();
                cursor.moveToPosition(position);
                searchView.setQuery(cursor.getString(cursor.getColumnIndex("cityName")),false);
                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position) {

                // Your code here
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                selectedCity(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                populateAdapter(s);
                return false;
            }
        });

        return rootView;
    }

    private void populateAdapter(String query) {
        final MatrixCursor c = new MatrixCursor(new String[]{ BaseColumns._ID, "cityName" });
        for (int i=0; i<mCities.size(); i++) {
            if (mCities.get(i).toLowerCase().startsWith(query.toLowerCase()))
                c.addRow(new Object[] {i, mCities.get(i)});
        }
        simpleCursorAdapter.changeCursor(c);
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

    public void selectedCity(String city){
        System.out.println("selected");
        if (city != null){
            System.out.println("selected1");
            for (int i = 0; i < mCityList.size(); i++){
                System.out.println("selected2");
                if (mCityList.get(i).getName().equals(city)) {
                    System.out.println("selected3 id = " + mCityList.get(i).getName() + " " + mCityList.get(i).getId());
                    PlacesFragment placesFragment = new PlacesFragment();
                    Bundle args = new Bundle();
                    args.putInt(PLACES_BY_CITY, mCityList.get(i).getId());
                    placesFragment.setArguments(args);
                    mChangeFragmentInterface.trans(placesFragment);
                    break;
                }
            }
        }
    }

    @Override
    public void getAllCities(List<City> allCities) {
        mCityList.addAll(allCities);

        for (int i = 0; i < mCityList.size(); i++){
            mCities.add(mCityList.get(i).getName());
        }
    }

    @Override
    public void getAllPlaces(List<Place> allPlaces) {

    }

    @Override
    public void getMyPlaces(List<Place> myPlaces) {

    }

    @Override
    public void getPlacesByTrip(List<Place> placesByTrip) {

    }

    @Override
    public void getPlacesByCity(List<Place> placesByCity) {

    }

    @Override
    public void getAllTrips(List<Trip> allTrips) {

    }

    @Override
    public void getMyTrips(List<Trip> myTrips) {

    }

    @Override
    public void getTripsByCity(List<Trip> tripsByCity) {

    }

    @Override
    public void createTrip(String info) {

    }

    @Override
    public void signIn(Response<RegistrationResponse> response) {

    }

    @Override
    public void registration(Response<RegistrationResponse> response) {

    }

    @Override
    public void uploadPlace(Response<ResponseBody> response) {

    }
}
