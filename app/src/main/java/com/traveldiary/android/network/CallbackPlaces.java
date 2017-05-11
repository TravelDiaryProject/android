package com.traveldiary.android.network;

import com.traveldiary.android.model.Place;

import java.util.List;


public interface CallbackPlaces {
    void responseNetwork(List<Place> placeList);
    void failNetwork(Throwable t);
}
