package com.traveldiary.android.data;


import com.traveldiary.android.callback.CallbackRegistration;
import com.traveldiary.android.model.City;
import com.traveldiary.android.model.Country;
import com.traveldiary.android.model.Place;
import com.traveldiary.android.model.RegistrationResponse;
import com.traveldiary.android.model.Trip;
import com.traveldiary.android.callback.SimpleCallBack;
import com.traveldiary.android.callback.CallbackCities;
import com.traveldiary.android.callback.CallbackCountries;
import com.traveldiary.android.callback.CallbackPlaces;
import com.traveldiary.android.callback.CallbackTrips;

import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.traveldiary.android.App.network;

public class DataService implements DataInterface {

    private RealmResults<Trip> listMyTripDB;
    private RealmResults<Trip> listFutureTripDB;

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
    public void signIn(String email, String password, CallbackRegistration callbackRegistration) {
        login(email, password, callbackRegistration);
    }

    @Override
    public void registration(String email, String password, CallbackRegistration callbackRegistration) {
        registrationUser(email, password, callbackRegistration);
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
    public void removeTrip(int tripId, SimpleCallBack simpleCallBack) {
        deleteTrip(tripId, simpleCallBack);
    }

    @Override
    public void removeAll() {
        clearDB();
    }

    private void prepareListTopPlaces(int offset, final CallbackPlaces callbackPlaces){

        if (offset!=0) {
            offset--;
        }

        network.downloadTopPlaces(offset, COUNT_LIMIT, new CallbackPlaces() {
            @Override
            public void response(List<Place> placeList) {
                callbackPlaces.response(placeList);
            }

            @Override
            public void fail(Throwable t) {
                callbackPlaces.fail(t);
            }
        });
    }

    private void prepareListPlacesByTrip(final int tripId, final CallbackPlaces callbackPlaces){
        final RealmResults<Place> listPlacesDB = data.getPlacesByTrip(tripId);

        network.downloadPlacesByTripId(tripId, new CallbackPlaces() {
            @Override
            public void response(List<Place> listPlaceServer) {

                // need sorted list by data from server

//                if (listPlaceServer.size()==0){
//                    callbackPlaces.response(listPlaceServer);
//                } else if (listPlaceServer.size()==listPlacesDB.size()){
//                    for (int i = 0; i < listPlaceServer.size(); i++){
//                        if (!listPlaceServer.get(i).equals(listPlacesDB.get(i))){
//                            data.removePlacesByTrip(tripId);
//                            data.addOrUpdateListPlaces(listPlaceServer);
//                            break;
//                        }
//                    }
//                    callbackPlaces.response(listPlaceServer);
//                } else if (listPlacesDB.size()!=listPlaceServer.size()){
//                    data.removePlacesByTrip(tripId);
//                    data.addOrUpdateListPlaces(listPlaceServer);
//                    callbackPlaces.response(listPlaceServer);
//                }

                if (listPlacesDB.size() > listPlaceServer.size()) { // It is possible, but very rarely
                    data.removePlacesByTrip(tripId);
                }
                data.addOrUpdateListPlaces(listPlaceServer);
                callbackPlaces.response(listPlaceServer);  // List from server sorted by data
            }

            @Override
            public void fail(Throwable t) {
                callbackPlaces.response(listPlacesDB);
                callbackPlaces.fail(t);
            }
        });
    }

    private void prepareListPlacesByCity(final int cityId, final CallbackPlaces callbackPlaces){

        network.downloadPlacesByCityId(cityId, new CallbackPlaces() {
            @Override
            public void response(List<Place> listPlaceServer) {
                callbackPlaces.response(listPlaceServer);
            }

            @Override
            public void fail(Throwable t) {
                callbackPlaces.fail(t);
            }
        });
    }

    private void prepareListPlacesByCountry(final int countryId, final CallbackPlaces callbackPlaces){

        network.downloadPlacesByCountryId(countryId, new CallbackPlaces() {
            @Override
            public void response(List<Place> listPlaceServer) {
                callbackPlaces.response(listPlaceServer);
            }

            @Override
            public void fail(Throwable t) {
                callbackPlaces.fail(t);
            }
        });
    }

    private void prepareListMyTrips(final CallbackTrips callbackTripsl) {

        if (listMyTripDB==null){
            listMyTripDB = data.getMyTrips();
            callbackTripsl.response(listMyTripDB);
            listMyTripDB.addChangeListener(new RealmChangeListener<RealmResults<Trip>>() {
                @Override
                public void onChange(RealmResults<Trip> element) {
                    callbackTripsl.response(listMyTripDB);
                }
            });
        }

        network.downloadMyTrips(new CallbackTrips() {
            @Override
            public void response(List<Trip> listMyTripServer) {
                if (isTripListsEquals(listMyTripServer, listMyTripDB)){
                    callbackTripsl.neutral(listMyTripDB);
                }else {
                    data.removeMyTrips();
                    data.addOrUpdateListTrips(listMyTripServer);
                }
            }

            @Override
            public void neutral(List<Trip> listMyTripServer) {
            }

            @Override
            public void fail(Throwable t) {
                callbackTripsl.fail(t);
            }
        });
    }

    private void prepareListFutureTrips(final CallbackTrips callbackTrips){

        if (listFutureTripDB==null){
            listFutureTripDB = data.getFutureTrips();
            callbackTrips.response(listFutureTripDB);
            listFutureTripDB.addChangeListener(new RealmChangeListener<RealmResults<Trip>>() {
                @Override
                public void onChange(RealmResults<Trip> element) {
                    callbackTrips.response(listFutureTripDB);
                }
            });
        }

        network.downloadFutureTrips(new CallbackTrips() {
            @Override
            public void response(List<Trip> listFutureTripServer) {
                if (isTripListsEquals(listFutureTripServer, listFutureTripDB)){
                    callbackTrips.neutral(listFutureTripDB);
                }else {
                    data.removeFutureTrips();
                    data.addOrUpdateListTrips(listFutureTripServer);
                }
            }

            @Override
            public void neutral(List<Trip> listMyTripServer) {
            }

            @Override
            public void fail(Throwable t) {
                callbackTrips.fail(t);
            }
        });
    }

    private boolean isTripListsEquals(List<Trip> listServer, List<Trip> listDB){
        if (listServer.size() == listDB.size()) {
            for (int i = 0; i < listDB.size(); i++) {
                System.out.println("WWWWWWWWWWWWWWWWWWWWWwwwwwwwwwwwwwwwwwwwwwww = " + listServer.get(i).getTitle() + " db = " + listDB.get(i).getTitle());
                if (!listServer.get(i).equals(listDB.get(i))) {
                    return false;
                }
            }
            return true;
        }else {
            return false;
        }
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
            callbackCities.response(listCityDB);
        }
        listCityDB.addChangeListener(new RealmChangeListener<RealmResults<City>>() {
            @Override
            public void onChange(RealmResults<City> element) {
                callbackCities.response(listCityDB);
            }
        });

        network.downloadAllCities(new CallbackCities() {
            @Override
            public void response(List<City> listCityServer) {

                if (listCityDB.size() != listCityServer.size()) {  // если лист в базе пуст а лист с сервера нет..
                    data.removeCities();
                    data.addOrUpdateListCities(listCityServer);
                    // отсылаем в коллбек лист с сервера
                }else if (listCityDB.size()==0 && listCityServer.size()==0){
                    callbackCities.response(listCityDB);
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
            public void fail(Throwable t) {
                callbackCities.fail(t);
            }
        });
    }

    private void prepareListAllCountries(final CallbackCountries callbackCountries){
        final RealmResults<Country> listCountryDB = data.getAllCountries();
        if (listCountryDB.size()!=0){
            callbackCountries.response(listCountryDB);
        }
        listCountryDB.addChangeListener(new RealmChangeListener<RealmResults<Country>>() {
            @Override
            public void onChange(RealmResults<Country> element) {
                callbackCountries.response(listCountryDB);
            }
        });

        network.downloadAllCountries(new CallbackCountries() {
            @Override
            public void response(List<Country> listCountryServer) {

                if (listCountryDB.size() != listCountryServer.size()) {  // если лист в базе пуст а лист с сервера нет..
                    data.removeCountries();
                    data.addOrUpdateListCountries(listCountryServer);
                }else if (listCountryDB.size()==0 && listCountryServer.size()==0){
                    callbackCountries.response(listCountryDB);
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
            public void fail(Throwable t) {
                callbackCountries.fail(t);
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
                network.downloadMyTrips(new CallbackTrips() {
                    @Override
                    public void response(List<Trip> tripList) {
                        data.addOrUpdateListTrips(tripList);
                    }

                    @Override
                    public void neutral(List<Trip> listMyTripServer) {
                    }

                    @Override
                    public void fail(Throwable t) {
                        simpleCallBack.fail(t);
                    }
                });
            }

            @Override
            public void fail(Throwable t) {
                simpleCallBack.fail(t);
            }
        });
    }

    private void login(String email, String password, final CallbackRegistration callbackRegistration){

        network.login(email, password, new CallbackRegistration() {
            @Override
            public void response(RegistrationResponse registrationResponse) {
                callbackRegistration.response(registrationResponse);
            }

            @Override
            public void fail(Throwable t) {
                callbackRegistration.fail(t);
            }
        });
    }

    private void registrationUser(String email, String password, final CallbackRegistration callbackRegistration){

        network.registration(email, password, new CallbackRegistration() {
            @Override
            public void response(RegistrationResponse registrationResponse) {
                callbackRegistration.response(registrationResponse);
            }

            @Override
            public void fail(Throwable t) {
                callbackRegistration.fail(t);
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
            }

            @Override
            public void fail(Throwable t) {
                simpleCallBack.fail(t);
            }
        });
    }

    private void deleteTrip(final int tripId, final SimpleCallBack simpleCallBack){

        network.removeTrip(tripId, new SimpleCallBack() {
            @Override
            public void response(Object o) {
                data.removePlacesByTrip(tripId);
                data.removeTrip(tripId);
                simpleCallBack.response("removed");
            }

            @Override
            public void fail(Throwable t) {
                simpleCallBack.fail(t);
            }
        });
    }

    private void clearDB(){
        data.removeAll();
    }
}
