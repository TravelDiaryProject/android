package com.traveldiary.android.data;


import android.util.Log;

import com.traveldiary.android.model.City;
import com.traveldiary.android.model.Country;
import com.traveldiary.android.model.Place;
import com.traveldiary.android.model.Trip;
import com.traveldiary.android.network.SimpleCallBack;
import com.traveldiary.android.network.CallbackCities;
import com.traveldiary.android.network.CallbackCountries;
import com.traveldiary.android.network.CallbackPlaces;
import com.traveldiary.android.network.CallbackTrips;

import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.traveldiary.android.App.network;

public class DataService implements DataInterface {

    private Data data = new Data();
    private static final int COUNT_LIMIT = 5;

    @Override
    public void getTopPlacesOffset(int offset, CallbackPlaces callbackPlaces) {
        prepareListTopPlaces(offset, callbackPlaces);
    }

    @Override
    public void getPlacesByTrip(int tripId, CallbackPlaces callbackPlaces) {
        prepareListPlacesByTrip(tripId, callbackPlaces);
    }

    @Override
    public void getPlacesByCity(int cityId, CallbackPlaces callbackPlaces) {
        prepareListPlacesByCity(cityId, callbackPlaces);
    }

    @Override
    public void getPlacesByCountry(int countryId, CallbackPlaces callbackPlaces) {
        prepareListPlacesByCountry(countryId, callbackPlaces);
    }

    @Override
    public void getMyTrips(CallbackTrips callbackTrips) {
        prepareListMyTrips(callbackTrips);
    }

    @Override
    public void getFutureTrips(CallbackTrips callbackTrips) {
        prepareListFutureTrips(callbackTrips);
    }

    @Override
    public void getTripById(int tripId, SimpleCallBack simpleCallBack) {
        prepareTripById(tripId, simpleCallBack);
    }

    @Override
    public void addToFutureTrips(Place place, SimpleCallBack simpleCallBack) {
        addToFuture(place, simpleCallBack);
    }

    @Override
    public void likePlace(Place place, SimpleCallBack simpleCallBack) {
        addLike(place, simpleCallBack);
    }

    @Override
    public void unlikePlace(Place place, SimpleCallBack simpleCallBack) {
        removeLike(place, simpleCallBack);
    }

    @Override
    public void createTrip(String tripTitle, SimpleCallBack simpleCallBack) {
        createNewTrip(tripTitle, simpleCallBack);
    }

    @Override
    public void signIn(String email, String password, SimpleCallBack simpleCallBack) {
        login(email, password, simpleCallBack);
    }

    @Override
    public void registration(String email, String password, SimpleCallBack simpleCallBack) {
        registrationUser(email, password, simpleCallBack);
    }

    @Override
    public void uploadPlace(MultipartBody.Part body, RequestBody tripIdRequest, SimpleCallBack simpleCallBack) {
        uploadImage(body, tripIdRequest, simpleCallBack);
    }

    @Override
    public void getAllCities(CallbackCities callbackCities) {
        prepareListAllCities(callbackCities);
    }

    @Override
    public void getAllCountries(CallbackCountries callbackCountries) {
        prepareListAllCountries(callbackCountries);
    }

    @Override
    public void removePlace(Place place, SimpleCallBack simpleCallBack) {
        deletePlace(place, simpleCallBack);
    }

    @Override
    public void removeTrip(Trip trip, SimpleCallBack simpleCallBack) {
        deleteTrip(trip, simpleCallBack);
    }

    @Override
    public void removeAll() {
        clearDB();
    }


    private void prepareListTopPlaces(int offset, final CallbackPlaces callbackPlaces){

        if (offset!=0)
            offset--;

        network.downloadTopPlaces(offset, COUNT_LIMIT, new CallbackPlaces() {
            @Override
            public void responseNetwork(List<Place> placeList) {
                callbackPlaces.responseNetwork(placeList);
            }

            @Override
            public void failNetwork(Throwable t) {
                callbackPlaces.failNetwork(t);
            }
        });
    }

    private void prepareListPlacesByTrip(final int tripId, final CallbackPlaces callbackPlaces){
        final RealmResults<Place> listPlacesDB = data.getPlacesByTrip(tripId);

        network.downloadPlacesByTripId(tripId, new CallbackPlaces() {
            @Override
            public void responseNetwork(List<Place> listPlaceServer) {

                if (listPlacesDB.size() > listPlaceServer.size()) {
                    data.removePlacesByTrip(tripId);
                }
                data.addOrUpdateListPlaces(listPlaceServer);
                callbackPlaces.responseNetwork(listPlaceServer);
            }

            @Override
            public void failNetwork(Throwable t) {
                callbackPlaces.responseNetwork(listPlacesDB);
                callbackPlaces.failNetwork(t);
            }
        });
    }

    private void prepareListPlacesByCity(final int cityId, final CallbackPlaces callbackPlaces){
        final RealmResults<Place> listPlacesDB = data.getPlacesByCity(cityId);
        if (listPlacesDB.size()!=0){
            callbackPlaces.responseNetwork(listPlacesDB);
        }
        listPlacesDB.addChangeListener(new RealmChangeListener<RealmResults<Place>>() {
            @Override
            public void onChange(RealmResults<Place> element) {
                callbackPlaces.responseNetwork(listPlacesDB);
            }
        });

        network.downloadPlacesByCityId(cityId, new CallbackPlaces() {
            @Override
            public void responseNetwork(List<Place> listPlaceServer) {

                if (listPlacesDB.size() > listPlaceServer.size()) {  // если лист в базе пуст а лист с сервера нет..
                    data.removePlacesByCity(cityId);
                }else if (listPlacesDB.size()==0 && listPlaceServer.size()==0){
                    callbackPlaces.responseNetwork(listPlacesDB);
                }
                data.addOrUpdateListPlaces(listPlaceServer);
            }

            @Override
            public void failNetwork(Throwable t) {
                callbackPlaces.failNetwork(t);
            }
        });
    }

    private void prepareListPlacesByCountry(final int countryId, final CallbackPlaces callbackPlaces){
        final RealmResults<Place> listPlacesDB = data.getPlacesByCountry(countryId);
        if (listPlacesDB.size()!=0){
            callbackPlaces.responseNetwork(listPlacesDB);
            Log.d("PlacesFragment","size = " + listPlacesDB.size());
        }
        listPlacesDB.addChangeListener(new RealmChangeListener<RealmResults<Place>>() {
            @Override
            public void onChange(RealmResults<Place> element) {
                callbackPlaces.responseNetwork(listPlacesDB);
            }
        });

        network.downloadPlacesByCountryId(countryId, new CallbackPlaces() {
            @Override
            public void responseNetwork(List<Place> listPlaceServer) {
                Log.d("PlacesFragment","size2 = " + listPlaceServer.size());

                if (listPlacesDB.size() > listPlaceServer.size()) {  // если лист в базе пуст а лист с сервера нет..
                    data.removePlacesByCountry(countryId);                      // отсылаем в коллбек лист с сервера
                }else if (listPlacesDB.size()==0 && listPlaceServer.size()==0){
                    callbackPlaces.responseNetwork(listPlacesDB);
                }
                data.addOrUpdateListPlaces(listPlaceServer);
            }

            @Override
            public void failNetwork(Throwable t) {
                callbackPlaces.failNetwork(t);
            }
        });
    }

    private void prepareListMyTrips(final CallbackTrips callbackTripsl) {

        final RealmResults<Trip> listMyTripDB = data.getMyTrips();

        network.downloadMyTrips(new CallbackTrips() {
            @Override
            public void responseNetwork(List<Trip> listMyTripServer) {
                if (listMyTripDB.size() > listMyTripServer.size()) {
                    data.removeMyTrips();
                }
                data.addOrUpdateListTrips(listMyTripServer);
                callbackTripsl.responseNetwork(listMyTripServer);
            }

            @Override
            public void failNetwork(Throwable t) {
                callbackTripsl.responseNetwork(listMyTripDB);
                callbackTripsl.failNetwork(t);
            }
        });
    }

    private void prepareListFutureTrips(final CallbackTrips callbackTrips){

        final RealmResults<Trip> listFutureDB = data.getFutureTrips();

        network.downloadFutureTrips(new CallbackTrips() {
            @Override
            public void responseNetwork(List<Trip> listFutureTripServer) {

                if (listFutureDB.size() > listFutureTripServer.size()) {
                    data.removeFutureTrips();
                }
                data.addOrUpdateListTrips(listFutureTripServer);
                callbackTrips.responseNetwork(listFutureTripServer);
            }

            @Override
            public void failNetwork(Throwable t) {
                callbackTrips.responseNetwork(listFutureDB);
                callbackTrips.failNetwork(t);
            }
        });
    }

    private void prepareTripById(int tripId, final SimpleCallBack simpleCallBack){

        final Trip trip = data.getTripById(tripId);

        network.downloadTripById(tripId, new SimpleCallBack() {
            @Override
            public void response(Object o) {
                Trip tripServer = (Trip) o;
                data.addOrUpdateTrip(tripServer);
                simpleCallBack.response(tripServer);
            }

            @Override
            public void fail(Throwable t) {
                if (trip!=null)
                    simpleCallBack.response(trip);
                simpleCallBack.fail(t);
            }
        });
    }

    private void prepareListAllCities(final CallbackCities callbackCities){
        final RealmResults<City> listCityDB = data.getAllCities();
        if (listCityDB.size()!=0){
            callbackCities.responseNetwork(listCityDB);
        }
        listCityDB.addChangeListener(new RealmChangeListener<RealmResults<City>>() {
            @Override
            public void onChange(RealmResults<City> element) {
                callbackCities.responseNetwork(listCityDB);
            }
        });

        network.downloadAllCities(new CallbackCities() {
            @Override
            public void responseNetwork(List<City> listCityServer) {

                if (listCityDB.size() != listCityServer.size()) {  // если лист в базе пуст а лист с сервера нет..
                    data.removeCities();
                    data.addOrUpdateListCities(listCityServer);
                    // отсылаем в коллбек лист с сервера
                }else if (listCityDB.size()==0 && listCityServer.size()==0){
                    callbackCities.responseNetwork(listCityDB);
                }

                if (listCityDB.size() == listCityServer.size()){
                    for (int i = 0; i < listCityServer.size(); i++){
                        if (listCityDB.get(i).getId()!=listCityServer.get(i).getId()){
                            data.addOrUpdateListCities(listCityServer);
                        }
                    }
                }
            }

            @Override
            public void failNetwork(Throwable t) {
                callbackCities.failNetwork(t);
            }
        });
    }

    private void prepareListAllCountries(final CallbackCountries callbackCountries){
        final RealmResults<Country> listCountryDB = data.getAllCountries();
        if (listCountryDB.size()!=0){
            callbackCountries.responseNetwork(listCountryDB);
        }
        listCountryDB.addChangeListener(new RealmChangeListener<RealmResults<Country>>() {
            @Override
            public void onChange(RealmResults<Country> element) {
                callbackCountries.responseNetwork(listCountryDB);
            }
        });

        network.downloadAllCountries(new CallbackCountries() {
            @Override
            public void responseNetwork(List<Country> listCountryServer) {

                if (listCountryDB.size() != listCountryServer.size()) {  // если лист в базе пуст а лист с сервера нет..
                    data.removeCountries();
                    data.addOrUpdateListCountries(listCountryServer);
                }else if (listCountryDB.size()==0 && listCountryServer.size()==0){
                    callbackCountries.responseNetwork(listCountryDB);
                }

                if (listCountryDB.size() == listCountryServer.size()){
                    for (int i = 0; i < listCountryServer.size(); i++){
                        if (listCountryDB.get(i).getId()!=listCountryServer.get(i).getId()){
                            data.addOrUpdateListCountries(listCountryServer);
                        }
                    }
                }
            }

            @Override
            public void failNetwork(Throwable t) {
                callbackCountries.failNetwork(t);
            }
        });
    }

    private void addToFuture(final Place place, final SimpleCallBack simpleCallBack){

        data.changeFutureStatePlace(place);
        simpleCallBack.response("changeFutureStatePlace");

        network.addToFuture(place.getId(), new SimpleCallBack() {
            @Override
            public void response(Object o) {
                // изменения загружены на сервер
                // все ок
            }

            @Override
            public void fail(Throwable t) {
                data.changeFutureStatePlace(place);// откатываем изменения
                simpleCallBack.fail(t);
            }
        });
    }

    private void addLike(final Place place, final SimpleCallBack simpleCallBack){

        data.changeLikeStatePlace(place);
        simpleCallBack.response("changeLikeStatePlace");

        network.uploadLike(place.getId(), new SimpleCallBack() {
            @Override
            public void response(Object o) {
                // изменения загружены на сервер
                // все ок
            }

            @Override
            public void fail(Throwable t) {
                data.changeFutureStatePlace(place);// откатываем изменения
                simpleCallBack.fail(t);
            }
        });
    }

    private void removeLike(final Place place, final SimpleCallBack simpleCallBack){

        data.changeLikeStatePlace(place);
        simpleCallBack.response("changeLikeStatePlace");

        network.uploadUnlike(place.getId(), new SimpleCallBack() {
            @Override
            public void response(Object o) {
                // изменения загружены на сервер
                // все ок
            }

            @Override
            public void fail(Throwable t) {
                data.changeFutureStatePlace(place);// откатываем изменения
                simpleCallBack.fail(t);
            }
        });
    }

    private void createNewTrip(String tripTitle, final SimpleCallBack simpleCallBack){

        network.uploadNewTrip(tripTitle, new SimpleCallBack() {
            @Override
            public void response(Object o) {
                simpleCallBack.response("Created");
            }

            @Override
            public void fail(Throwable t) {
                simpleCallBack.fail(t);
            }
        });
    }

    private void login(String email, String password, final SimpleCallBack simpleCallBack){

        network.login(email, password, new SimpleCallBack() {
            @Override
            public void response(Object o) {
                simpleCallBack.response(o);
            }

            @Override
            public void fail(Throwable t) {
                simpleCallBack.fail(t);
            }
        });
    }

    private void registrationUser(String email, String password, final SimpleCallBack simpleCallBack){

        network.registration(email, password, new SimpleCallBack() {
            @Override
            public void response(Object o) {
                simpleCallBack.response(o);
            }

            @Override
            public void fail(Throwable t) {
                simpleCallBack.fail(t);
            }
        });
    }

    private void uploadImage(MultipartBody.Part body, RequestBody tripIdRequest, final SimpleCallBack simpleCallBack){

        network.uploadImage(body, tripIdRequest, new SimpleCallBack() {
            @Override
            public void response(Object o) {
                simpleCallBack.response(o);
            }

            @Override
            public void fail(Throwable t) {
                simpleCallBack.fail(t);
            }
        });
    }

    private void deletePlace(final Place place, final SimpleCallBack simpleCallBack){

        network.removePlace(place.getId(), new SimpleCallBack() {
            @Override
            public void response(Object o) {
                data.removePlace(place);
                simpleCallBack.response("removed");
                // removed from server - OK
            }

            @Override
            public void fail(Throwable t) {
                // не удален с сервера
                // добавить в список не синхронизированных объектов
                simpleCallBack.fail(t);
            }
        });
    }

    private void deleteTrip(final Trip trip, final SimpleCallBack simpleCallBack){

        network.removeTrip(trip.getId(), new SimpleCallBack() {
            @Override
            public void response(Object o) {
                data.removePlacesByTrip(trip.getId());
                data.removeTrip(trip);
                simpleCallBack.response("removed");
                // removed from server - OK
            }

            @Override
            public void fail(Throwable t) {
                // не удален с сервера
                // добавить в список не синхронизированных объектов
                simpleCallBack.fail(t);
            }
        });
    }

    private void clearDB(){
        data.removeAll();
    }
}
