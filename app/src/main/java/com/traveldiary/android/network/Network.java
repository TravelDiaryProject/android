package com.traveldiary.android.network;

import android.util.Log;

import com.traveldiary.android.model.City;
import com.traveldiary.android.model.Country;
import com.traveldiary.android.model.Place;
import com.traveldiary.android.model.RegistrationResponse;
import com.traveldiary.android.model.Trip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.traveldiary.android.Constans.TOKEN_CONST;

public class Network implements NetworkInterface{

    private static final TravelDiaryService travelDiaryService = Api.getTravelDiaryService();

    private List<Place> mTopPlaces;
    private List<Place> mPlacesByTrip;
    private List<Place> mPlacesByCity;
    private List<Place> mPlacesByCountry;

    private List<Trip> mMyTrips;
    private List<Trip> mFutureTrips;
    private List<Trip> mTripsByCity;

    private List<City> mAllCities;
    private List<Country> mAllCountries;

    // TODO: 4/3/2017 refresh list after adding new trip or place!!!!

    public Network() {
    }



    /*
                PLACES
         */

    @Override
    public void getTopPlaces(CallBack callBack) {
            downloadTopPlaces(callBack);
    }

    @Override
    public void getPlacesByTrip(int tripId, CallBack callBack) {
        downloadPlacesByTripId(tripId, callBack);
    }

    @Override
    public void getPlacesByCity(int cityId, CallBack callBack) {
        downloadPlacesByCityId(cityId, callBack);
    }

    @Override
    public void getPlacesByCountry(int countryId, CallBack callBack) {
        downloadPlacesByCountryId(countryId, callBack);
    }

    /*
                           TRIPS
    */

    @Override
    public void getMyTrips(String token, CallBack callBack) {
        Log.d("NETWORK", " getMyTrips");
        if (mMyTrips !=null) {
            Log.d("NETWORK", " mMyTrips != null - callback(mMyTrips)");
            callBack.responseNetwork(mMyTrips);
        }
        Log.d("NETWORK", " download MyTrips");
        downloadMyTrips(token, callBack);
    }

    @Override
    public void getTripsByCity(int cityId, CallBack callBack) {
        downloadTripsByCityId(cityId, callBack);
    }

    @Override
    public void getFutureTrips(String token, CallBack callBack) {

        if (mFutureTrips !=null){
            callBack.responseNetwork(mFutureTrips);
        }
        downloadFutureTrips(token, callBack);
    }

    @Override
    public void addToFutureTrips(String token, Place place, CallBack callBack) {

        // TODO: 4/10/2017 если ответ с сервера положителен - изменить состояние place

        addToFuture(token, place, callBack);
        //downloadFutureTrips(token, null);
    }

    @Override
    public void likePlace(String token, int placeId, CallBack callBack) {
        uploadLike(token, placeId, callBack);
    }

    /*
               Different
     */
    @Override
    public void createTrip(String token, String tripTitle, CallBack callBack) {
        uploadNewTrip(token, tripTitle, callBack);
    }

    @Override
    public void signIn(String email, String password, CallBack callBack) {
        login(email, password, callBack);
    }

    @Override
    public void registration(String email, String password, CallBack callBack) {
        reg(email, password, callBack);
    }

    @Override
    public void uploadPlace(String token, MultipartBody.Part body, RequestBody tripIdRequest, CallBack callBack) {
        uploadImage(token, body, tripIdRequest, callBack);
    }

    @Override
    public void getAllCities(CallBack callBack) {
        if (mAllCities ==null)
            downloadAllCities(callBack);
        else
            callBack.responseNetwork(mAllCities);
    }

    @Override
    public void getAllCountries(CallBack callBack) {
        if (mAllCountries ==null)
            downloadAllCountries(callBack);
        else
            callBack.responseNetwork(mAllCountries);
    }

    @Override
    public void removePlace(String token, Place place, CallBack callBack) {
        removePlaceFromServer(token, place, callBack);
    }

    @Override
    public void removeTrip(String token, Trip trip, CallBack callBack) {
        removeTripFromSerer(token, trip, callBack);
    }

    private void removeTripFromSerer(String token, final Trip trip, final CallBack callBack) {
        if (TOKEN_CONST!=null && !TOKEN_CONST.equals("")){
            RequestBody tripIdRequest = RequestBody.create(MediaType.parse("multipart/form-data"), Integer.toString(trip.getId()));
            travelDiaryService.removeTrip(TOKEN_CONST, tripIdRequest).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d("NETWORK", "Remove response = " + response.code() + response.message());
                    if (response.code()==201) {
                        mFutureTrips.remove(trip);
                        callBack.responseNetwork(response);
                    }else {
                        callBack.failNetwork(new Throwable(response.message()));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    callBack.failNetwork(t);
                }
            });
        }
    }

    private void removePlaceFromServer(String token, final Place place, final CallBack callBack) {
        if (TOKEN_CONST!=null && !TOKEN_CONST.equals("")){
            RequestBody placeIdRequest = RequestBody.create(MediaType.parse("multipart/form-data"), Integer.toString(place.getId()));
            travelDiaryService.removePlace(TOKEN_CONST, placeIdRequest).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d("NETWORK", "Remove response = " + response.code() + response.message());
                    if (response.code()==201) {
                        mTopPlaces.remove(place); //remove from top places... (now in top places all places!!!!!!!!!!!!!!)
                        callBack.responseNetwork(response);
                    }else {
                        callBack.failNetwork(new Throwable(response.message()));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    callBack.failNetwork(t);
                }
            });
        }
    }


    /*
                    Realization
     */

    private void downloadTopPlaces(final CallBack callBack){

        mTopPlaces = new ArrayList<>();

        if (TOKEN_CONST==null || TOKEN_CONST.equals("")) {
            travelDiaryService.listTopPlaces().enqueue(new Callback<List<Place>>() {
                @Override
                public void onResponse(Call<List<Place>> call, retrofit2.Response<List<Place>> response) {
                    if (response.code() == 200) {
                        mTopPlaces.addAll(response.body());
                    }
                    callBack.responseNetwork(mTopPlaces);
                }

                @Override
                public void onFailure(Call<List<Place>> call, Throwable t) {
                    callBack.failNetwork(t);
                }
            });
        }else {
            travelDiaryService.listTopPlaces(TOKEN_CONST).enqueue(new Callback<List<Place>>() {
                @Override
                public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                    if (response.code() == 200) {
                        mTopPlaces.addAll(response.body());
                    }
                    callBack.responseNetwork(mTopPlaces);
                }

                @Override
                public void onFailure(Call<List<Place>> call, Throwable t) {
                    callBack.failNetwork(t);
                }
            });
        }

    }

    private void downloadPlacesByTripId(int tripId, final CallBack callBack) {

        mPlacesByTrip = new ArrayList<>();

        if (TOKEN_CONST==null || TOKEN_CONST.equals("")) {
            travelDiaryService.listPlacesByTrip(tripId).enqueue(new Callback<List<Place>>() {
                @Override
                public void onResponse(Call<List<Place>> call, retrofit2.Response<List<Place>> response) {
                    mPlacesByTrip.addAll(response.body());
                    callBack.responseNetwork(mPlacesByTrip);

                }

                @Override
                public void onFailure(Call<List<Place>> call, Throwable t) {
                    callBack.failNetwork(t);
                }
            });
        }else {
            travelDiaryService.listPlacesByTrip(TOKEN_CONST, tripId).enqueue(new Callback<List<Place>>() {
                @Override
                public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                    mPlacesByTrip.addAll(response.body());
                    callBack.responseNetwork(mPlacesByTrip);

                }

                @Override
                public void onFailure(Call<List<Place>> call, Throwable t) {
                    callBack.failNetwork(t);
                }
            });
        }
    }

    private void downloadPlacesByCityId(int cityId, final CallBack callBack) {

        mPlacesByCity = new ArrayList<>();

        if (TOKEN_CONST==null || TOKEN_CONST.equals("")) {

            travelDiaryService.listPlacesByCity(cityId).enqueue(new Callback<List<Place>>() {
                @Override
                public void onResponse(Call<List<Place>> call, retrofit2.Response<List<Place>> response) {
                    if (response.code() == 200) {
                        mPlacesByCity.addAll(response.body());
                        callBack.responseNetwork(mPlacesByCity);
                    } else
                        callBack.failNetwork(new Throwable("Response = " + response.code()));
                }

                @Override
                public void onFailure(Call<List<Place>> call, Throwable t) {
                    callBack.failNetwork(t);
                }
            });
        } else {
            travelDiaryService.listPlacesByCity(TOKEN_CONST, cityId).enqueue(new Callback<List<Place>>() {
                @Override
                public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                    if (response.code() == 200) {
                        mPlacesByCity.addAll(response.body());
                        callBack.responseNetwork(mPlacesByCity);
                    } else
                        callBack.failNetwork(new Throwable("Response = " + response.code()));
                }

                @Override
                public void onFailure(Call<List<Place>> call, Throwable t) {
                    callBack.failNetwork(t);
                }
            });
        }
    }

    private void downloadPlacesByCountryId(int countryId, final CallBack callBack) {

        mPlacesByCountry = new ArrayList<>();

        if (TOKEN_CONST==null || TOKEN_CONST.equals("")) {

            travelDiaryService.listPlacesByCountry(countryId).enqueue(new Callback<List<Place>>() {
                @Override
                public void onResponse(Call<List<Place>> call, retrofit2.Response<List<Place>> response) {
                    if (response.code() == 200) {
                        mPlacesByCountry.addAll(response.body());
                        callBack.responseNetwork(mPlacesByCountry);
                    } else
                        callBack.failNetwork(new Throwable("Response = " + response.code()));
                }

                @Override
                public void onFailure(Call<List<Place>> call, Throwable t) {
                    callBack.failNetwork(t);
                }
            });
        } else {
            travelDiaryService.listPlacesByCountry(TOKEN_CONST, countryId).enqueue(new Callback<List<Place>>() {
                @Override
                public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                    if (response.code() == 200) {
                        mPlacesByCountry.addAll(response.body());
                        callBack.responseNetwork(mPlacesByCountry);
                    } else
                        callBack.failNetwork(new Throwable("Response = " + response.code()));
                }

                @Override
                public void onFailure(Call<List<Place>> call, Throwable t) {
                    callBack.failNetwork(t);
                }
            });
        }
    }

    private void downloadMyTrips(String token, final CallBack callBack) {

        if (mMyTrips ==null) {
            Log.d("NETWORK", " mMyTrips == null creaate new ArrrayList");
            mMyTrips = new ArrayList<>();
        }

        travelDiaryService.listMyTrips(token).enqueue(new Callback<List<Trip>>() {
            @Override
            public void onResponse(Call<List<Trip>> call, retrofit2.Response<List<Trip>> response) {

                Log.d("NETWORK", " downloadMyTrips response = " + response.code());

                if (response.code()==200){


                    List<Trip> test = new ArrayList<Trip>();
                    test.addAll(response.body());
                    for (int i = 0; i < test.size(); i++) {
                        Log.d("NETWORK", " downloadMyTrips response = " + test.get(i).getTitle());
                    }

                    if (response.body().size()!= mMyTrips.size()) {
                        mMyTrips.clear();
                        mMyTrips.addAll(response.body());
                        Collections.reverse(mMyTrips);
                        if (callBack!=null) {
                            Log.d("NETWORK", " callback with new mMyTrips");
                            callBack.responseNetwork(mMyTrips);
                        }
                    }else if (response.body().size()==0){
                        callBack.responseNetwork(mMyTrips);
                    }
                }else {
                    callBack.failNetwork(new Throwable("Response = " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<Trip>> call, Throwable t) {
                if (callBack!=null) {
                    callBack.failNetwork(t);
                }
            }
        });
    }

    private void downloadFutureTrips(String token, final CallBack callBack) {

        if (mFutureTrips ==null) {
            mFutureTrips = new ArrayList<>();
        }

        travelDiaryService.listMyFutureTrips(token).enqueue(new Callback<List<Trip>>() {
            @Override
            public void onResponse(Call<List<Trip>> call, retrofit2.Response<List<Trip>> response) {

                if (response.code()==200){
                    if (response.body().size()!= mFutureTrips.size()){
                        mFutureTrips.clear();
                        mFutureTrips.addAll(response.body());
                        Collections.reverse(mFutureTrips);
                        if (callBack!=null) {
                            callBack.responseNetwork(mFutureTrips);
                        }
                    }else if (response.body().size()==0){
                        callBack.responseNetwork(mFutureTrips);
                    }
                }else {
                    callBack.failNetwork(new Throwable("Response = " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<Trip>> call, Throwable t) {
                if (callBack!=null) {
                    callBack.failNetwork(t);
                }
            }
        });
    }

    /*
                Не проверенно!!!!!!
     */
    private void downloadTripsByCityId(int cityId, final CallBack callBack) {

        mTripsByCity = new ArrayList<>();

        travelDiaryService.listTripsByCity(cityId).enqueue(new Callback<List<Trip>>() {
            @Override
            public void onResponse(Call<List<Trip>> call, retrofit2.Response<List<Trip>> response) {
                mTripsByCity.addAll(response.body());
                callBack.responseNetwork(mTripsByCity);
            }

            @Override
            public void onFailure(Call<List<Trip>> call, Throwable t) {
                callBack.failNetwork(t);
            }
        });
    }


    private void addToFuture(String token, Place place, final CallBack callBack){

        travelDiaryService.addToFutureTrips(token, place.getId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==201) {
                    callBack.responseNetwork(response);
                }else {
                    callBack.failNetwork(new Throwable(response.message()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callBack.failNetwork(t);
            }
        });

    }

    private void uploadLike(String token, int placeId, final CallBack callBack){

        travelDiaryService.likePlace(token, placeId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                callBack.responseNetwork(response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callBack.failNetwork(t);
            }
        });

    }


    private void uploadNewTrip(final String token, String tripTitle, final CallBack callBack){

        travelDiaryService.createTrip(token, tripTitle).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response!=null && response.code()==201){
                    downloadMyTrips(token, new CallBack() {
                        @Override
                        public void responseNetwork(Object o) {
                            callBack.responseNetwork(response);
                        }

                        @Override
                        public void failNetwork(Throwable t) {

                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callBack.failNetwork(t);
            }
        });

    }

    private void login(String email, String password, final CallBack callBack){

        travelDiaryService.getToken(email, password).enqueue(new Callback<RegistrationResponse>() {
            @Override
            public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                callBack.responseNetwork(response);
            }

            @Override
            public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                callBack.failNetwork(t);
            }
        });
    }

    private void reg(String email, String password, final CallBack callBack){
        travelDiaryService.registration(email, password).enqueue(new Callback<RegistrationResponse>() {
            @Override
            public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                callBack.responseNetwork(response);
            }

            @Override
            public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                callBack.failNetwork(t);
            }
        });
    }

    private void uploadImage(String token, MultipartBody.Part body, RequestBody tripIdRequest, final CallBack callBack){
        travelDiaryService.postImage(token, body, tripIdRequest).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==201) {
                    callBack.responseNetwork(response);
                }else {
                    callBack.failNetwork(new Throwable(response.message()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callBack.failNetwork(t);
            }
        });
    }

    private void downloadAllCities(final CallBack callBack){

        mAllCities = new ArrayList<>();

        travelDiaryService.listAllCities().enqueue(new Callback<List<City>>() {
            @Override
            public void onResponse(Call<List<City>> call, retrofit2.Response<List<City>> response) {
                if (response.code()==200) {
                    mAllCities.addAll(response.body());
                }
                callBack.responseNetwork(mAllCities);
            }

            @Override
            public void onFailure(Call<List<City>> call, Throwable t) {
                callBack.failNetwork(t);
            }
        });
    }

    private void downloadAllCountries(final CallBack callBack){

        mAllCountries = new ArrayList<>();

        travelDiaryService.listAllCountries().enqueue(new Callback<List<Country>>() {
            @Override
            public void onResponse(Call<List<Country>> call, retrofit2.Response<List<Country>> response) {
                if (response.code()==200) {
                    mAllCountries.addAll(response.body());
                }
                callBack.responseNetwork(mAllCountries);
            }

            @Override
            public void onFailure(Call<List<Country>> call, Throwable t) {
                callBack.failNetwork(t);
            }
        });
    }
}
