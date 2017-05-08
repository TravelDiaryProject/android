package com.traveldiary.android.network;


import com.traveldiary.android.model.Place;
import com.traveldiary.android.model.Trip;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public interface NetworkInterface {

    void getTopPlacesOffset(int offset, int limit, CallBack callBack);
    void getPlacesByTrip(int tripId, CallBack callBack);//передавать ИД трипа
    void getPlacesByCity(int cityId, CallBack callBack);//передавать ИД города
    void getPlacesByCountry(int countryId, CallBack callBack);

    void getMyTrips(CallBack callBack);
    //void getTripsByCity(int cityId, CallBack callBack);
    void getFutureTrips(CallBack callBack);

    void addToFutureTrips(int placeId, CallBack callBack);
    void likePlace(int placeId, CallBack callBack);
    void unlikePlace(int placeId, CallBack callBack);

    void createTrip(String tripTitle, CallBack callBack);
    void signIn(String email, String password, CallBack callBack);
    void registration(String email, String password, CallBack callBack);

    void uploadPlace(MultipartBody.Part body, RequestBody tripIdRequest, CallBack callBack);

    void getAllCities(CallBack callBack);
    void getAllCountries(CallBack callBack);

    void removePlace(int placeId, CallBack callBack);
    void removeTrip(int tripId, CallBack callBack);

}
