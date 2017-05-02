package com.traveldiary.android.data;


/*write and get to realmDB*/

import android.util.Log;

import com.traveldiary.android.model.City;
import com.traveldiary.android.model.Country;
import com.traveldiary.android.model.Place;
import com.traveldiary.android.model.Trip;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

public class Data {

    private static String TAG = "DATA";
    private Realm realm = Realm.getDefaultInstance();

    public RealmResults<Trip> getMyTrips(){
        Log.d(TAG, "getMyTrips");
        return realm.where(Trip.class).equalTo("isMine", 1).equalTo("isFuture", 0).findAll();
    }

    public RealmResults<Trip> getFutureTrips(){
        Log.d(TAG, "getFutureTrips");
        return realm.where(Trip.class).equalTo("isFuture", 1).equalTo("isMine", 1).findAll();
    }

    public RealmResults<Place> getPlacesByTrip(int tripId){
        Log.d(TAG, "getPlacesByTrip");
        return realm.where(Place.class).equalTo("tripId", tripId).findAll();
    }

    public RealmResults<Place> getPlacesByCity(int cityId){
        Log.d(TAG, "getPlacesByCity");
        return realm.where(Place.class).equalTo("cityId", cityId).findAll();
    }

    public RealmResults<Place> getPlacesByCountry(int countryId){
        Log.d(TAG, "getPlacesByCountry");
        return realm.where(Place.class).equalTo("countryId", countryId).findAll();
    }

    public RealmResults<City> getAllCities(){
        return realm.where(City.class).findAll();
    }

    public RealmResults<Country> getAllCountries(){
        return realm.where(Country.class).findAll();
    }

    public Trip getTripById(int tripId){
        return realm.where(Trip.class).equalTo("id", tripId).findFirst();
    }







    public void addOrUpdateListPlaces(List<Place> placeList){
        Log.d(TAG, "addOrUpdateListPlaces");

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(placeList);
        realm.commitTransaction();
    }

    public void addOrUpdateListTrips(List<Trip> tripList){
        Log.d(TAG, "addOrUpdateListTrips");

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(tripList);
        realm.commitTransaction();
    }

    public void addOrUpdateListCities(List<City> cityList){
        Log.d(TAG, "addOrUpdateListCities");

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(cityList);
        realm.commitTransaction();
    }

    public void addOrUpdateListCountries(List<Country> countryList){
        Log.d(TAG, "addOrUpdateListCountries");

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(countryList);
        realm.commitTransaction();
    }

    public void changeFutureStatePlace(Place place){
        Log.d(TAG, "addOrUpdatePlaces");

        realm.beginTransaction();
        if (place.getIsInFutureTrips()==0)
            place.setIsInFutureTrips(1);
        else if (place.getIsInFutureTrips()==1)
            place.setIsInFutureTrips(0);
        realm.copyToRealmOrUpdate(place);
        realm.commitTransaction();

    }

    public void changeLikeStatePlace(Place place){
        Log.d(TAG, "changeLikeStatePlace");

        realm.beginTransaction();
        if (place.getIsLiked()==0)
            place.setIsLiked(1);
        else if (place.getIsLiked()==1)
            place.setIsLiked(0);
        realm.copyToRealmOrUpdate(place);
        realm.commitTransaction();

    }

    public void removePlace(Place place){
        Log.d(TAG, "removePlace");

        realm.beginTransaction();
        Place place1 = realm.where(Place.class).equalTo("id", place.getId()).findFirst();
        place1.deleteFromRealm();
        realm.commitTransaction();
    }

    public void removeMyTrips(){

        realm.beginTransaction();
        RealmResults<Trip> realmResults = realm.where(Trip.class).equalTo("isMine", 1).equalTo("isFuture", 0).findAll();
        realmResults.deleteAllFromRealm();
        realm.commitTransaction();

    }

    public void removeFutureTrips(){

        realm.beginTransaction();
        RealmResults<Trip> realmResults = realm.where(Trip.class).equalTo("isMine", 1).equalTo("isFuture", 1).findAll();
        realmResults.deleteAllFromRealm();
        realm.commitTransaction();
    }

    public void removeCities() {

        realm.beginTransaction();
        RealmResults<City> realmResult = realm.where(City.class).findAll();
        realmResult.deleteAllFromRealm();
        realm.commitTransaction();
    }

    public void removeCountries() {

        realm.beginTransaction();
        RealmResults<Country> realmResult = realm.where(Country.class).findAll();
        realmResult.deleteAllFromRealm();
        realm.commitTransaction();
    }


    public void removePlacesByTrip(int tripId){

        realm.beginTransaction();
        RealmResults<Place> realmResults = realm.where(Place.class).equalTo("tripId", tripId).findAll();
        realmResults.deleteAllFromRealm();
        realm.commitTransaction();

    }

    public void removePlacesByCity(int cityId) {
        realm.beginTransaction();
        RealmResults<Place> realmResults = realm.where(Place.class).equalTo("cityId", cityId).findAll();
        realmResults.deleteAllFromRealm();
        realm.commitTransaction();
    }

    public void removePlacesByCountry(int countryId) {
        realm.beginTransaction();
        RealmResults<Place> realmResults = realm.where(Place.class).equalTo("countryId", countryId).findAll();
        realmResults.deleteAllFromRealm();
        realm.commitTransaction();
    }


    public void removeTrip(Trip trip){
        Log.d(TAG, "removeTrip");

        realm.beginTransaction();
        trip.deleteFromRealm();
        realm.commitTransaction();
    }

    public void addOrUpdatePlace(Place place){
        Log.d(TAG, "addOrUpdatePlace");

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(place);
        realm.commitTransaction();
    }

    public void addOrUpdateTrip(Trip trip){
        Log.d(TAG, "addOrUpdatePlace");

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(trip);
        realm.commitTransaction();
    }






    public void removeAll(){
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
    }



}
