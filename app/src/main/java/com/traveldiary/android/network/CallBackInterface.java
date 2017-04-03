package com.traveldiary.android.network;

import com.traveldiary.android.model.City;
import com.traveldiary.android.model.Place;
import com.traveldiary.android.model.RegistrationResponse;
import com.traveldiary.android.model.Trip;

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
    void getTripById(Trip trip);

    void createTrip(String info);
    void signIn(Response<RegistrationResponse> response);
    void registration(Response<RegistrationResponse> response);

    void uploadPlace(Response<ResponseBody> response);

    void getAllCities(List<City> allCities);
}
