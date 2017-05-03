package com.traveldiary.android.data;


import com.traveldiary.android.model.City;
import com.traveldiary.android.model.Country;
import com.traveldiary.android.model.Place;
import com.traveldiary.android.model.Trip;
import com.traveldiary.android.network.CallBack;

import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.traveldiary.android.App.network;

public class DataService {

    private Data data = new Data();
    private static final int COUNT_LIMIT = 5;

    public void getTopPlaces(int offset, final CallBack callBack){

        if (offset!=0)
            offset--;

        network.getTopPlacesOffset(offset, COUNT_LIMIT, new CallBack() {
            @Override
            public void responseNetwork(Object o) {
                callBack.responseNetwork(o);
            }

            @Override
            public void failNetwork(Throwable t) {
                callBack.failNetwork(t);
            }
        });
    }

    public void getMyTrips(final CallBack callBack){

        final RealmResults<Trip> listMyTripDB = data.getMyTrips();
        if (listMyTripDB.size()!=0){
            callBack.responseNetwork(listMyTripDB);
        }

        listMyTripDB.addChangeListener(new RealmChangeListener<RealmResults<Trip>>() {
            @Override
            public void onChange(RealmResults<Trip> element) {
                callBack.responseNetwork(listMyTripDB);
            }
        });

        network.getMyTrips(new CallBack() {
            @Override
            public void responseNetwork(Object o) {
                List<Trip> listMyTripServer = (List<Trip>) o;

                if (listMyTripDB.size() > listMyTripServer.size()) {
                    data.removeMyTrips();
                }else if (listMyTripDB.size()==0 && listMyTripServer.size()==0){
                    callBack.responseNetwork(listMyTripDB);
                }
                data.addOrUpdateListTrips(listMyTripServer);
            }

            @Override
            public void failNetwork(Throwable t) {
                callBack.failNetwork(t);
            }
        });
    }

    public void getFutureTrips(final CallBack callBack){

        final RealmResults<Trip> listFutureDB = data.getFutureTrips();
        if (listFutureDB.size()!=0){
            callBack.responseNetwork(listFutureDB);
        }
        listFutureDB.addChangeListener(new RealmChangeListener<RealmResults<Trip>>() {
            @Override
            public void onChange(RealmResults<Trip> element) {
                callBack.responseNetwork(listFutureDB);
            }
        });

        network.getFutureTrips(new CallBack() {
            @Override
            public void responseNetwork(Object o) {
                List<Trip> listFutureTripServer = (List<Trip>) o;

                if (listFutureDB.size() > listFutureTripServer.size()) {
                    data.removeFutureTrips();
                }else if (listFutureDB.size()==0 && listFutureTripServer.size()==0){
                    callBack.responseNetwork(listFutureDB);
                }
                data.addOrUpdateListTrips(listFutureTripServer);
            }

            @Override
            public void failNetwork(Throwable t) {
                callBack.failNetwork(t);
            }
        });
    }

    public void getTripById(int tripId, final CallBack callBack){

        Trip trip = data.getTripById(tripId);
        if (trip!=null){
            callBack.responseNetwork(trip);
        }else {
            callBack.failNetwork(new Throwable("Not found"));
        }

    }


    public void getPLacesByTrip(final int tripId, final CallBack callBack){
        final RealmResults<Place> listPlacesDB = data.getPlacesByTrip(tripId);
        if (listPlacesDB.size()!=0){
            callBack.responseNetwork(listPlacesDB);
        }
        listPlacesDB.addChangeListener(new RealmChangeListener<RealmResults<Place>>() {
            @Override
            public void onChange(RealmResults<Place> element) {
                callBack.responseNetwork(listPlacesDB);
            }
        });

        network.getPlacesByTrip(tripId, new CallBack() {
            @Override
            public void responseNetwork(Object o) {
                List<Place> listPlaceServer = (List<Place>) o;

                if (listPlacesDB.size() > listPlaceServer.size()) {
                    data.removePlacesByTrip(tripId);
                }else if (listPlacesDB.size()==0 && listPlaceServer.size()==0){
                    callBack.responseNetwork(listPlacesDB);
                }
                data.addOrUpdateListPlaces(listPlaceServer);
            }

            @Override
            public void failNetwork(Throwable t) {
                callBack.failNetwork(t);
            }
        });
    }

    public void getPlacesByCity(final int cityId, final CallBack callBack){
        final RealmResults<Place> listPlacesDB = data.getPlacesByCity(cityId);
        if (listPlacesDB.size()!=0){
            callBack.responseNetwork(listPlacesDB);
        }
        listPlacesDB.addChangeListener(new RealmChangeListener<RealmResults<Place>>() {
            @Override
            public void onChange(RealmResults<Place> element) {
                callBack.responseNetwork(listPlacesDB);
            }
        });

        network.getPlacesByCity(cityId, new CallBack() {
            @Override
            public void responseNetwork(Object o) {
                List<Place> listPlaceServer = (List<Place>) o;

                if (listPlacesDB.size() > listPlaceServer.size()) {  // если лист в базе пуст а лист с сервера нет..
                    data.removePlacesByCity(cityId);
                }else if (listPlacesDB.size()==0 && listPlaceServer.size()==0){
                    callBack.responseNetwork(listPlacesDB);
                }
                data.addOrUpdateListPlaces(listPlaceServer);
            }

            @Override
            public void failNetwork(Throwable t) {
                callBack.failNetwork(t);
            }
        });
    }

    public void getPlacesByCountry(final int countryId, final CallBack callBack){
        final RealmResults<Place> listPlacesDB = data.getPlacesByCountry(countryId);
        if (listPlacesDB.size()!=0){
            callBack.responseNetwork(listPlacesDB);
        }
        listPlacesDB.addChangeListener(new RealmChangeListener<RealmResults<Place>>() {
            @Override
            public void onChange(RealmResults<Place> element) {
                callBack.responseNetwork(listPlacesDB);
            }
        });

        network.getPlacesByCountry(countryId, new CallBack() {
            @Override
            public void responseNetwork(Object o) {
                List<Place> listPlaceServer = (List<Place>) o;

                if (listPlacesDB.size() > listPlaceServer.size()) {  // если лист в базе пуст а лист с сервера нет..
                    data.removePlacesByCountry(countryId);                      // отсылаем в коллбек лист с сервера
                }else if (listPlacesDB.size()==0 && listPlaceServer.size()==0){
                    callBack.responseNetwork(listPlacesDB);
                }
                data.addOrUpdateListPlaces(listPlaceServer);
            }

            @Override
            public void failNetwork(Throwable t) {
                callBack.failNetwork(t);
            }
        });
    }

    public void getAllCities(final CallBack callBack){
        final RealmResults<City> listCityDB = data.getAllCities();
        if (listCityDB.size()!=0){
            callBack.responseNetwork(listCityDB);
        }
        listCityDB.addChangeListener(new RealmChangeListener<RealmResults<City>>() {
            @Override
            public void onChange(RealmResults<City> element) {
                callBack.responseNetwork(listCityDB);
            }
        });

        network.getAllCities(new CallBack() {
            @Override
            public void responseNetwork(Object o) {
                List<City> listCityServer = (List<City>) o;

                if (listCityDB.size() != listCityServer.size()) {  // если лист в базе пуст а лист с сервера нет..
                    data.removeCities();
                    data.addOrUpdateListCities(listCityServer);
                    // отсылаем в коллбек лист с сервера
                }else if (listCityDB.size()==0 && listCityServer.size()==0){
                    callBack.responseNetwork(listCityDB);
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
                callBack.failNetwork(t);
            }
        });
    }

    public void getAllCountries(final CallBack callBack){
        final RealmResults<Country> listCountryDB = data.getAllCountries();
        if (listCountryDB.size()!=0){
            callBack.responseNetwork(listCountryDB);
        }
        listCountryDB.addChangeListener(new RealmChangeListener<RealmResults<Country>>() {
            @Override
            public void onChange(RealmResults<Country> element) {
                callBack.responseNetwork(listCountryDB);
            }
        });

        network.getAllCountries(new CallBack() {
            @Override
            public void responseNetwork(Object o) {
                List<Country> listCountryServer = (List<Country>) o;

                if (listCountryDB.size() != listCountryServer.size()) {  // если лист в базе пуст а лист с сервера нет..
                    data.removeCountries();
                    data.addOrUpdateListCountries(listCountryServer);
                }else if (listCountryDB.size()==0 && listCountryServer.size()==0){
                    callBack.responseNetwork(listCountryDB);
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
                callBack.failNetwork(t);
            }
        });
    }

    public void addToFuture(final Place place, final CallBack callBack){

        data.changeFutureStatePlace(place);
        callBack.responseNetwork("changeFutureStatePlace");

        network.addToFutureTrips(place.getId(), new CallBack() {
            @Override
            public void responseNetwork(Object o) {
                // изменения загружены на сервер
                // все ок
            }

            @Override
            public void failNetwork(Throwable t) {
                data.changeFutureStatePlace(place);// откатываем изменения
                callBack.failNetwork(t);
            }
        });
    }

    public void addLike(final Place place, final CallBack callBack){

        data.changeLikeStatePlace(place);
        callBack.responseNetwork("changeLikeStatePlace");

        network.likePlace(place.getId(), new CallBack() {
            @Override
            public void responseNetwork(Object o) {
                // изменения загружены на сервер
                // все ок
            }

            @Override
            public void failNetwork(Throwable t) {
                data.changeFutureStatePlace(place);// откатываем изменения
                callBack.failNetwork(t);
            }
        });
    }

    public void createNewTrip(String tripTitle, final CallBack callBack){

        network.createTrip(tripTitle, new CallBack() {
            @Override
            public void responseNetwork(Object o) {
                callBack.responseNetwork("Created");
            }

            @Override
            public void failNetwork(Throwable t) {
                callBack.failNetwork(t);
            }
        });

    }

    public void login(String email, String password, final CallBack callBack){

        network.signIn(email, password, new CallBack() {
            @Override
            public void responseNetwork(Object o) {
                callBack.responseNetwork(o);
            }

            @Override
            public void failNetwork(Throwable t) {
                callBack.failNetwork(t);
            }
        });
    }

    public void registration(String email, String password, final CallBack callBack){

        network.registration(email, password, new CallBack() {
            @Override
            public void responseNetwork(Object o) {
                callBack.responseNetwork(o);
            }

            @Override
            public void failNetwork(Throwable t) {
                callBack.failNetwork(t);
            }
        });
    }

    public void uploadImage(MultipartBody.Part body, RequestBody tripIdRequest, final CallBack callBack){

        network.uploadPlace(body, tripIdRequest, new CallBack() {
            @Override
            public void responseNetwork(Object o) {
                callBack.responseNetwork(o);
            }

            @Override
            public void failNetwork(Throwable t) {
                callBack.failNetwork(t);
            }
        });
    }

    public void removePlace(final Place place, final CallBack callBack){

        network.removePlace(place.getId(), new CallBack() {
            @Override
            public void responseNetwork(Object o) {
                data.removePlace(place);
                callBack.responseNetwork("removed");
                // removed from server - OK
            }

            @Override
            public void failNetwork(Throwable t) {
                // не удален с сервера
                // добавить в список не синхронизированных объектов
                callBack.failNetwork(t);
            }
        });


    }

    public void removeTrip(final Trip trip, final CallBack callBack){

        network.removeTrip(trip.getId(), new CallBack() {
            @Override
            public void responseNetwork(Object o) {
                data.removeTrip(trip);
                //callBack.responseNetwork("removed");
                // removed from server - OK
            }

            @Override
            public void failNetwork(Throwable t) {
                // не удален с сервера
                // добавить в список не синхронизированных объектов
                callBack.failNetwork(t);
            }
        });
    }

}
