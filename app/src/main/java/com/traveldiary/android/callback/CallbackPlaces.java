package com.traveldiary.android.callback;

import com.traveldiary.android.model.Place;

import java.util.List;


public interface CallbackPlaces {
    void response(List<Place> placeList);
    void fail(Throwable t);
}
