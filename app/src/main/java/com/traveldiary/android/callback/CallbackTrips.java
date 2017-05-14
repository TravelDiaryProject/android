package com.traveldiary.android.callback;

import com.traveldiary.android.model.Trip;

import java.util.List;


public interface CallbackTrips {
    void response(List<Trip> tripList);
    void neutral(List<Trip> neutralList);
    void fail(Throwable t);
}
