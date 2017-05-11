package com.traveldiary.android.network;

import com.traveldiary.android.model.City;

import java.util.List;


public interface CallbackCities {
    void responseNetwork(List<City> cityList);
    void failNetwork(Throwable t);
}
