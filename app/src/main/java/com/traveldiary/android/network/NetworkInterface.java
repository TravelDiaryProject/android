package com.traveldiary.android.network;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public interface NetworkInterface {

    void getTopPlaces(CallBack callBack);
    void getPlacesByTrip(int tripId, CallBack callBack);//передавать ИД трипа
    void getPlacesByCity(int cityId, CallBack callBack);//передавать ИД города
    void getPlacesByCountry(int countryId, CallBack callBack);

    void getMyTrips(String token, CallBack callBack);
    void getTripsByCity(int cityId, CallBack callBack);
    void getFutureTrips(String token, CallBack callBack);

    void addToFutureTrips(String token, int placeId, CallBack callBack);
    void likePlace(String token, int placeId, CallBack callBack);

    void createTrip(String token, String tripTitle, CallBack callBack);
    void signIn(String email, String password, CallBack callBack);
    void registration(String email, String password, CallBack callBack);

    void uploadPlace(String token, MultipartBody.Part body, RequestBody tripIdRequest, CallBack callBack);

    void getAllCities(CallBack callBack);
    void getAllCountries(CallBack callBack);

}
