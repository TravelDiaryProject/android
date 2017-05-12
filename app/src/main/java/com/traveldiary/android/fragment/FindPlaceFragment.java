package com.traveldiary.android.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.traveldiary.android.R;
import com.traveldiary.android.Validator;
import com.traveldiary.android.activity.CreateFindActivity;
import com.traveldiary.android.model.Country;
import com.traveldiary.android.model.City;
import com.traveldiary.android.callback.CallbackCities;
import com.traveldiary.android.callback.CallbackCountries;

import java.util.ArrayList;
import java.util.List;

import static com.traveldiary.android.App.dataService;
import static com.traveldiary.android.Constans.PLACES_FOR;
import static com.traveldiary.android.Constans.PLACES_FOR_CITY;
import static com.traveldiary.android.Constans.PLACES_FOR_COUNTRY;
import static com.traveldiary.android.Constans.PLACES_SEARCH_NAME;
import static com.traveldiary.android.Constans.PLACE_ID;


public class FindPlaceFragment extends Fragment {

    private List<City> mCityList;
    private List<Country> mCountryList;
    private List<String> mStringList;

    private ArrayAdapter<String> adapter;

    private AutoCompleteTextView mAutoCompleteTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_find_place,
                container, false);

        mAutoCompleteTextView = (AutoCompleteTextView) rootView.findViewById(R.id.find_autocompletetext);
        ImageView mSearchButton = (ImageView) rootView.findViewById(R.id.find_search_button);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Validator.isNetworkAvailable(getActivity())) {
                    String selectedCity = mAutoCompleteTextView.getText().toString();

                    if (!isWeHaveThisCityOrCountry(selectedCity)) {
                        Toast.makeText(getActivity(), getString(R.string.no_places_by_request), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mCityList = new ArrayList<>();
        mCountryList = new ArrayList<>();
        mStringList = new ArrayList<>();

        adapter = new ArrayAdapter<>
                (getActivity(), android.R.layout.select_dialog_item, mStringList);
        mAutoCompleteTextView.setThreshold(1);
        mAutoCompleteTextView.setAdapter(adapter);

        dataService.getAllCities(new CallbackCities() {
            @Override
            public void response(List<City> cityList) {
                mCityList.clear();
                mCityList.addAll(cityList);

                for (int i = 0; i < mCityList.size(); i++){
                    mStringList.add(mCityList.get(i).getName());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void fail(Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        dataService.getAllCountries(new CallbackCountries() {
            @Override
            public void response(List<Country> countryList) {
                mCountryList.clear();
                mCountryList.addAll(countryList);

                for (int i = 0; i < mCountryList.size(); i++){
                    mStringList.add(mCountryList.get(i).getName());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void fail(Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return rootView;
    }

    private boolean isWeHaveThisCityOrCountry(String selectedCityOrCountry){

        for (int i = 0; i < mCityList.size(); i++){
            if (mCityList.get(i).getName().toLowerCase().equals(selectedCityOrCountry.toLowerCase())) {

                Intent intent = new Intent(getActivity(), CreateFindActivity.class);
                intent.putExtra(PLACES_FOR, PLACES_FOR_CITY);
                intent.putExtra(PLACE_ID, mCityList.get(i).getId());
                intent.putExtra(PLACES_SEARCH_NAME, mCityList.get(i).getName());
                startActivity(intent);
                return true;
            }
        }

        for (int i = 0; i < mCountryList.size(); i++){
            if (mCountryList.get(i).getName().toLowerCase().equals(selectedCityOrCountry.toLowerCase())) {

                Intent intent = new Intent(getActivity(), CreateFindActivity.class);
                intent.putExtra(PLACES_FOR, PLACES_FOR_COUNTRY);
                intent.putExtra(PLACE_ID, mCountryList.get(i).getId());
                intent.putExtra(PLACES_SEARCH_NAME, mCountryList.get(i).getName());
                startActivity(intent);
                return true;
            }
        }
        return false;
    }
}
