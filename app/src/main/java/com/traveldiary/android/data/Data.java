package com.traveldiary.android.data;

import android.util.Log;

import com.traveldiary.android.model.City;
import com.traveldiary.android.model.Country;
import com.traveldiary.android.model.Place;
import com.traveldiary.android.model.Trip;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

class Data {

    private static String TAG = "DATA";
    private Realm realm = Realm.getDefaultInstance();

    RealmResults<Trip> getMyTrips(){
        return realm.where(Trip.class).equalTo("isMine", 1).equalTo("isFuture", 0).findAll();
    }

    RealmResults<Trip> getFutureTrips(){
        Log.d(TAG, "prepareListFutureTrips");
        return realm.where(Trip.class).equalTo("isFuture", 1).equalTo("isMine", 1).findAll();
    }

    RealmResults<Place> getPlacesByTrip(int tripId){
        Log.d(TAG, "getPlacesByTrip");
        return realm.where(Place.class).equalTo("tripId", tripId).findAllSorted("shootedAt", Sort.ASCENDING);
    }

    RealmResults<City> getAllCities(){
        return realm.where(City.class).findAll();
    }

    RealmResults<Country> getAllCountries(){
        return realm.where(Country.class).findAll();
    }

    Trip getTripById(int tripId){
        return realm.where(Trip.class).equalTo("id", tripId).findFirst();
    }



    void addOrUpdateListPlaces(List<Place> placeList){
        Log.d(TAG, "addOrUpdateListPlaces");

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(placeList);
        realm.commitTransaction();
    }

    void addOrUpdateListTrips(List<Trip> tripList){
        Log.d(TAG, "addOrUpdateListTrips");

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(tripList);
        realm.commitTransaction();
    }

    void addOrUpdateListCities(List<City> cityList){
        Log.d(TAG, "addOrUpdateListCities");

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(cityList);
        realm.commitTransaction();
    }

    void addOrUpdateListCountries(List<Country> countryList){
        Log.d(TAG, "addOrUpdateListCountries");

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(countryList);
        realm.commitTransaction();
    }

    void changeFutureStatePlace(Place place){
        Log.d(TAG, "addOrUpdatePlaces");

        realm.beginTransaction();
        if (place.getIsInFutureTrips()==0)
            place.setIsInFutureTrips(1);
        else if (place.getIsInFutureTrips()==1)
            place.setIsInFutureTrips(0);
        realm.copyToRealmOrUpdate(place);
        realm.commitTransaction();

    }

    void changeLikeStatePlace(Place place){
        Log.d(TAG, "changeLikeStatePlace");

        realm.beginTransaction();
        if (place.getIsLiked()==0)
            place.setIsLiked(1);
        else if (place.getIsLiked()==1)
            place.setIsLiked(0);
        realm.copyToRealmOrUpdate(place);
        realm.commitTransaction();

    }

    void removePlace(Place place){
        Log.d(TAG, "deletePlace");

        realm.beginTransaction();
        Place place1 = realm.where(Place.class).equalTo("id", place.getId()).findFirst();
        place1.deleteFromRealm();
        realm.commitTransaction();
    }

    void removeTrip(int tripId){
        Log.d(TAG, "deleteTrip");

        realm.beginTransaction();
        Trip trip1 = realm.where(Trip.class).equalTo("id", tripId).findFirst();
        trip1.deleteFromRealm();
        realm.commitTransaction();
    }

    void removeMyTrips(){

        realm.beginTransaction();
        RealmResults<Trip> realmResults = realm.where(Trip.class).equalTo("isMine", 1).equalTo("isFuture", 0).findAll();
        realmResults.deleteAllFromRealm();
        realm.commitTransaction();

    }

    void removeFutureTrips(){

        realm.beginTransaction();
        RealmResults<Trip> realmResults = realm.where(Trip.class).equalTo("isMine", 1).equalTo("isFuture", 1).findAll();
        realmResults.deleteAllFromRealm();
        realm.commitTransaction();
    }

    void removeCities() {

        realm.beginTransaction();
        RealmResults<City> realmResult = realm.where(City.class).findAll();
        realmResult.deleteAllFromRealm();
        realm.commitTransaction();
    }

    void removeCountries() {

        realm.beginTransaction();
        RealmResults<Country> realmResult = realm.where(Country.class).findAll();
        realmResult.deleteAllFromRealm();
        realm.commitTransaction();
    }


    void removePlacesByTrip(int tripId){

        realm.beginTransaction();
        RealmResults<Place> realmResults = realm.where(Place.class).equalTo("tripId", tripId).findAll();
        realmResults.deleteAllFromRealm();
        realm.commitTransaction();

    }

    void addOrUpdateTrip(Trip trip){
        Log.d(TAG, "addOrUpdatePlace");

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(trip);
        realm.commitTransaction();
    }

    void removeAll(){
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
    }
}
