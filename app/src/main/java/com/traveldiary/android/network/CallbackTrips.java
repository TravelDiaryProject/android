package com.traveldiary.android.network;

import com.traveldiary.android.model.Trip;

import java.util.List;


public interface CallbackTrips {
    void responseNetwork(List<Trip> tripList);
    void failNetwork(Throwable t);
}
