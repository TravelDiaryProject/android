package com.traveldiary.android.network;

import com.traveldiary.android.model.Country;

import java.util.List;


public interface CallbackCountries {
    void responseNetwork(List<Country> countryList);
    void failNetwork(Throwable t);
}
