package com.traveldiary.android.network;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public interface NetworkInterface {
    void getAllPlaces(CallBack callBack);
    void getMyPlaces(String token, CallBack callBack);//передавать токен
    void getPlacesByTrip(int tripId, CallBack callBack);//передавать ИД трипа
    void getPlacesByCity(int cityId, CallBack callBack);//передавать ИД города

    void getAllTrips(CallBack callBack);
    void getMyTrips(String token, CallBack callBack);
    void getTripsByCity(int cityId, CallBack callBack);
    void getTripById(int tripId, CallBack callBack);

    void createTrip(String token, String tripTitle, CallBack callBack);
    void signIn(String email, String password, CallBack callBack);
    void registration(String email, String password, CallBack callBack);

    void uploadPlace(String token, MultipartBody.Part body, RequestBody tripIdRequest, CallBack callBack);

    void getAllCities(CallBack callBack);

}
