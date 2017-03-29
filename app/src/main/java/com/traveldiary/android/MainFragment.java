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

import com.traveldiary.android.Interfaces.ChangeFragmentInterface;
import com.traveldiary.android.Interfaces.TravelDiaryService;
import com.traveldiary.android.essence.City;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static com.traveldiary.android.Constans.PLACES_BY_CITY;
import static com.traveldiary.android.Constans.PLACES_FOR;


public class MainFragment extends Fragment {

    private ChangeFragmentInterface mChangeFragmentInterface;

    private List<City> mCityList;
    private List<String> mCities;
    private static TravelDiaryService travelDiaryService;
    private SearchView searchView;

    private SimpleCursorAdapter simpleCursorAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main,
                container, false);

        mCityList = new ArrayList<>();
        mCities = new ArrayList<>();

        downloadCity();

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

    private void downloadCity(){

        travelDiaryService = Api.getTravelDiaryService();

        travelDiaryService.listAllCities().enqueue(new Callback<List<City>>() {
            @Override
            public void onResponse(Call<List<City>> call, retrofit2.Response<List<City>> response) {

                mCityList.addAll(response.body());

                System.out.println("gorod = " + mCityList.get(0).getName() + " id " + mCityList.get(0).getId());
                System.out.println("gorod = " + mCityList.get(1).getName() + " id " + mCityList.get(1).getId());
                System.out.println("gorod = " + mCityList.get(2).getName() + " id " + mCityList.get(2).getId());

                for (int i = 0; i < mCityList.size(); i++){
                    mCities.add(mCityList.get(i).getName());
                }
            }

            @Override
            public void onFailure(Call<List<City>> call, Throwable t) {

            }
        });
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
}
