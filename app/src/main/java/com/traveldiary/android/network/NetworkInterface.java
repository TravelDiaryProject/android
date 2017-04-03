package com.traveldiary.android.network;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Cyborg on 3/30/2017.
 */

public interface NetworkInterface {
    void getAllPlaces();
    void getMyPlaces(String token);//передавать токен
    void getPlacesByTrip(int tripId);//передавать ИД трипа
    void getPlacesByCity(int cityId);//передавать ИД города

    void getAllTrips();
    void getMyTrips(String token);
    void getTripsByCity(int cityId);
    void getTripById(int tripId);

    void createTrip(String token, String tripTitle);
    void signIn(String email, String password);
    void registration(String email, String password);

    void uploadPlace(String token, MultipartBody.Part body, RequestBody tripIdRequest);

    void getAllCities();

}
