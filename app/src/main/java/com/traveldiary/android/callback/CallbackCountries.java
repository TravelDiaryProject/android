package com.traveldiary.android.callback;

import com.traveldiary.android.model.Country;

import java.util.List;


public interface CallbackCountries {
    void response(List<Country> countryList);
    void fail(Throwable t);
}
