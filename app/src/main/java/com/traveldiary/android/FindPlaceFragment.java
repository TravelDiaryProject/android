package com.traveldiary.android;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.traveldiary.android.model.Country;
import com.traveldiary.android.network.CallBack;
import com.traveldiary.android.model.City;

import java.util.ArrayList;
import java.util.List;

import static com.traveldiary.android.App.network;
import static com.traveldiary.android.Constans.PLACES_BY_CITY;
import static com.traveldiary.android.Constans.PLACES_BY_COUNTRY;


public class FindPlaceFragment extends Fragment {

    private List<City> mCityList;
    private List<Country> mCountryList;
    private List<String> mStringList;

    private String[] mCitiesName;

    private int mSelectedCity = 0;

    private AutoCompleteTextView autoCompleteTextView;
    private Button searchButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_find_place,
                container, false);

        autoCompleteTextView = (AutoCompleteTextView) rootView.findViewById(R.id.find_autocompletetext);
        searchButton = (Button) rootView.findViewById(R.id.find_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String selectedCity = autoCompleteTextView.getText().toString();

                if (!isWeHaveThisCityOrCountry(selectedCity)){
                    Toast.makeText(getActivity(), "NO this city", Toast.LENGTH_SHORT).show();
                }


            }
        });

        mCityList = new ArrayList<>();
        mCountryList = new ArrayList<>();
        mStringList = new ArrayList<>();


        network.getAllCities(new CallBack() {
            @Override
            public void responseNetwork(Object o) {
                List<City> allCities = (List<City>) o;

                mCityList.addAll(allCities);

                for (int i = 0; i < mCityList.size(); i++){
                    mStringList.add(mCityList.get(i).getName());
                }

                network.getAllCountries(new CallBack() {
                    @Override
                    public void responseNetwork(Object o) {
                        List<Country> allCounties = (List<Country>) o;

                        mCountryList.addAll(allCounties);

                        for (int i = 0; i < mCountryList.size(); i++){
                            mStringList.add(mCountryList.get(i).getName());
                        }

                        mCitiesName = mStringList.toArray(new String[mStringList.size()]);

                        autocpleteAdapter(mCitiesName);

                    }

                    @Override
                    public void failNetwork(Throwable t) {

                    }
                });
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

    private void autocpleteAdapter(String[] cities){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.select_dialog_item, cities);
        autoCompleteTextView.setThreshold(1);//will start working from first character
        autoCompleteTextView.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
    }

}
