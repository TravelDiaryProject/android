package com.traveldiary.android.data;

import com.traveldiary.android.callback.CallbackRegistration;
import com.traveldiary.android.model.Place;
import com.traveldiary.android.model.Trip;
import com.traveldiary.android.callback.SimpleCallBack;
import com.traveldiary.android.callback.CallbackCities;
import com.traveldiary.android.callback.CallbackCountries;
import com.traveldiary.android.callback.CallbackPlaces;
import com.traveldiary.android.callback.CallbackTrips;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;


interface DataInterface {

    void getTopPlacesOffset(int offset, CallbackPlaces callbackPlaces);
    void getPlacesByTrip(int tripId, CallbackPlaces callbackPlaces);
    void getPlacesByCity(int cityId, CallbackPlaces callbackPlaces);
    void getPlacesByCountry(int countryId, CallbackPlaces callbackPlaces);

    void getMyTrips(CallbackTrips callbackTrips);
    void getFutureTrips(CallbackTrips callbackTrips);

    void getTripById(int tripId, SimpleCallBack simpleCallBack);
    void addToFutureTrips(Place place, SimpleCallBack simpleCallBack);
    void likePlace(Place place, SimpleCallBack simpleCallBack);
    void unlikePlace(Place place, SimpleCallBack simpleCallBack);
    void createTrip(String tripTitle, SimpleCallBack simpleCallBack);

    void signIn(String email, String password, CallbackRegistration callbackRegistration);
    void registration(String email, String password, CallbackRegistration callbackRegistration);

    void uploadPlace(MultipartBody.Part body, RequestBody tripIdRequest, SimpleCallBack simpleCallBack);

    void getAllCities(CallbackCities callbackCities);
    void getAllCountries(CallbackCountries callbackCountries);

    void removePlace(Place place, SimpleCallBack simpleCallBack);
    void removeTrip(Trip trip, SimpleCallBack simpleCallBack);
    void removeAll();

}
