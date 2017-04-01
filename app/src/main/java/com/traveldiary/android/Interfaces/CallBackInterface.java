package com.traveldiary.android.Interfaces;

import com.traveldiary.android.essence.City;
import com.traveldiary.android.essence.Place;
import com.traveldiary.android.essence.RegistrationResponse;
import com.traveldiary.android.essence.Trip;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by Cyborg on 3/30/2017.
 */

public interface CallBackInterface {
    void getAllPlaces(List<Place> allPlaces);
    void getMyPlaces(List<Place> myPlaces);
    void getPlacesByTrip(List<Place> placesByTrip);
    void getPlacesByCity(List<Place> placesByCity);

    void getAllTrips(List<Trip> allTrips);
    void getMyTrips(List<Trip> myTrips);
    void getTripsByCity(List<Trip> tripsByCity);

    void createTrip(String info);
    void signIn(Response<RegistrationResponse> response);
    void registration(Response<RegistrationResponse> response);

    void uploadPlace(Response<ResponseBody> response);

    void getAllCities(List<City> allCities);
}
