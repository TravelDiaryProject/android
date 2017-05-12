package com.traveldiary.android.callback;

import com.traveldiary.android.model.City;

import java.util.List;


public interface CallbackCities {
    void response(List<City> cityList);
    void fail(Throwable t);
}
