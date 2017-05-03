package com.traveldiary.android;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.traveldiary.android.model.Country;
import com.traveldiary.android.network.CallBack;
import com.traveldiary.android.model.City;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.traveldiary.android.App.dataService;
import static com.traveldiary.android.Constans.PLACES_BY_CITY;
import static com.traveldiary.android.Constans.PLACES_BY_COUNTRY;
import static com.traveldiary.android.Constans.PLACES_FOR;
import static com.traveldiary.android.Constans.PLACES_FOR_CITY;
import static com.traveldiary.android.Constans.PLACES_FOR_COUNTRY;


public class FindPlaceFragment extends Fragment {

    private List<City> mCityList;
    private List<Country> mCountryList;
    private List<String> mStringList;

    private Set<String> mStringSet;

    private ArrayAdapter<String> adapter;

    private AutoCompleteTextView mAutoCompleteTextView;
    private ImageView mSearchButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_find_place,
                container, false);

        mAutoCompleteTextView = (AutoCompleteTextView) rootView.findViewById(R.id.find_autocompletetext);
        mSearchButton = (ImageView) rootView.findViewById(R.id.find_search_button);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String selectedCity = mAutoCompleteTextView.getText().toString();

                if (!isWeHaveThisCityOrCountry(selectedCity)){
                    Toast.makeText(getActivity(), "NO this city", Toast.LENGTH_SHORT).show();
                }


            }
        });

        mCityList = new ArrayList<>();
        mCountryList = new ArrayList<>();
        mStringList = new ArrayList<>();
        mStringSet = new HashSet<>();

        adapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.select_dialog_item, mStringList);
        mAutoCompleteTextView.setThreshold(1);//will start working from first character
        mAutoCompleteTextView.setAdapter(adapter);


        dataService.getAllCities(new CallBack() {
            @Override
            public void responseNetwork(Object o) {
                mCityList.clear();
                mCityList.addAll((List<City>) o);

                for (int i = 0; i < mCityList.size(); i++){
                    mStringList.add(mCityList.get(i).getName());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void failNetwork(Throwable t) {

            }
        });

        dataService.getAllCountries(new CallBack() {
            @Override
            public void responseNetwork(Object o) {
                mCountryList.clear();
                mCountryList.addAll((List<Country>) o);

                for (int i = 0; i < mCountryList.size(); i++){
                    mStringList.add(mCountryList.get(i).getName());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void failNetwork(Throwable t) {

            }
        });
        return rootView;
    }

    private boolean isWeHaveThisCityOrCountry(String selectedCityOrCountry){

        for (int i = 0; i < mCityList.size(); i++){
            if (mCityList.get(i).getName().toLowerCase().equals(selectedCityOrCountry.toLowerCase())) {

                PlacesFragment placesFragment = new PlacesFragment();
                Bundle args = new Bundle();
                args.putString(PLACES_FOR, PLACES_FOR_CITY);
                args.putInt(PLACES_BY_CITY, mCityList.get(i).getId());
                placesFragment.setArguments(args);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_main, placesFragment);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();

                return true;
            }
        }

        for (int i = 0; i < mCountryList.size(); i++){
            if (mCountryList.get(i).getName().toLowerCase().equals(selectedCityOrCountry.toLowerCase())) {

                PlacesFragment placesFragment = new PlacesFragment();
                Bundle args = new Bundle();
                args.putString(PLACES_FOR, PLACES_FOR_COUNTRY);
                args.putInt(PLACES_BY_COUNTRY, mCountryList.get(i).getId());
                placesFragment.setArguments(args);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_main, placesFragment);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();

                return true;
            }
        }
        return false;
    }
}
