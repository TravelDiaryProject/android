package com.traveldiary.android.network;

import android.content.SharedPreferences;
import android.util.Log;

import com.traveldiary.android.model.City;
import com.traveldiary.android.model.Place;
import com.traveldiary.android.model.RegistrationResponse;
import com.traveldiary.android.model.Trip;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.traveldiary.android.Constans.TOKEN_CONST;

public class Network implements NetworkInterface{

    private static final TravelDiaryService travelDiaryService = Api.getTravelDiaryService();

    private List<Place> topPlaces;
    private List<Place> myPlaces;
    private List<Place> placesByTrip;
    private List<Place> placesByCity;

    private List<Trip> myTrips;
    private List<Trip> futureTrips;
    private List<Trip> tripsByCity;

    private List<City> allCities;

    // TODO: 4/3/2017 refresh list after adding new trip or place!!!!

    public Network() {
    }



    /*
                PLACES
         */

    @Override
    public void getTopPlaces(CallBack callBack) {
        if (topPlaces==null){
            downloadTopPlaces(callBack);
        }else {
            callBack.responseNetwork(topPlaces);
        }
    }

    @Override
    public void getMyPlaces(String token, CallBack callBack) {
        if (myPlaces==null){
            downloadMyPlaces(token, callBack);
        }else {
            callBack.responseNetwork(myPlaces);
        }
    }

    @Override
    public void getPlacesByTrip(int tripId, CallBack callBack) {
        downloadPlacesByTripId(tripId, callBack);
    }

    @Override
    public void getPlacesByCity(int cityId, CallBack callBack) {
        downloadPlacesByCityId(cityId, callBack);
    }

    /*
                           TRIPS
    */

    @Override
    public void getMyTrips(String token, CallBack callBack) {
        if (myTrips==null)
            downloadMyTrips(token, callBack);
        else
            callBack.responseNetwork(myTrips);
    }

    @Override
    public void getTripsByCity(int cityId, CallBack callBack) {
        downloadTripsByCityId(cityId, callBack);
    }

    @Override
    public void getFutureTrips(String token, CallBack callBack) {
        /*if (futureTrips==null || futureTrips.size()==0){
            downloadFutureTrips(token, callBack);
        }else {
            callBack.responseNetwork(futureTrips);
        }*/
        downloadFutureTrips(token, callBack);
    }

    @Override
    public void addToFutureTrips(String token, int placeId, CallBack callBack) {

        // TODO: 4/10/2017 если ответ с сервера положителен - изменить состояние place

        addToFuture(token, placeId, callBack);
        downloadFutureTrips(token, null);
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
        if (allCities==null)
            downloadAllCities(callBack);
        else
            callBack.responseNetwork(allCities);
    }


    /*
                    Realization
     */

    private void downloadTopPlaces(final CallBack callBack){

        topPlaces = new ArrayList<>();

        travelDiaryService.listTopPlaces().enqueue(new Callback<List<Place>>() {
            @Override
            public void onResponse(Call<List<Place>> call, retrofit2.Response<List<Place>> response) {
                topPlaces.addAll(response.body());
                callBack.responseNetwork(topPlaces);
            }

            @Override
            public void onFailure(Call<List<Place>> call, Throwable t) {
                callBack.failNetwork(t);
            }
        });

    }

    private void downloadMyPlaces(String token, final CallBack callBack) {

        myPlaces = new ArrayList<>();

        travelDiaryService.listMyPlaces(token).enqueue(new Callback<List<Place>>() {
            @Override
            public void onResponse(Call<List<Place>> call, retrofit2.Response<List<Place>> response) {
                myPlaces.addAll(response.body());
                callBack.responseNetwork(myPlaces);
            }

            @Override
            public void onFailure(Call<List<Place>> call, Throwable t) {
                callBack.failNetwork(t);
            }
        });
    }

    private void downloadPlacesByTripId(int tripId, final CallBack callBack) {

        placesByTrip = new ArrayList<>();

        travelDiaryService.listPlacesByTrip(tripId).enqueue(new Callback<List<Place>>() {
            @Override
            public void onResponse(Call<List<Place>> call, retrofit2.Response<List<Place>> response) {
                placesByTrip.addAll(response.body());
                callBack.responseNetwork(placesByTrip);
            }

            @Override
            public void onFailure(Call<List<Place>> call, Throwable t) {
                callBack.failNetwork(t);
            }
        });
    }

    private void downloadPlacesByCityId(int cityId, final CallBack callBack) {

        placesByCity = new ArrayList<>();

        travelDiaryService.listPlacesByCity(cityId).enqueue(new Callback<List<Place>>() {
            @Override
            public void onResponse(Call<List<Place>> call, retrofit2.Response<List<Place>> response) {
                placesByCity.addAll(response.body());
                callBack.responseNetwork(placesByCity);
            }

            @Override
            public void onFailure(Call<List<Place>> call, Throwable t) {
                callBack.failNetwork(t);
            }
        });
    }

    private void downloadMyTrips(String token, final CallBack callBack) {

        myTrips = new ArrayList<>();

        travelDiaryService.listMyTrips(token).enqueue(new Callback<List<Trip>>() {
            @Override
            public void onResponse(Call<List<Trip>> call, retrofit2.Response<List<Trip>> response) {

                Log.d("NETWORK", " downloadMyTrips response = " + response.code());

                if (response.code()==200){
                    myTrips.addAll(response.body());
                }
                if (callBack!=null) {
                    callBack.responseNetwork(myTrips);
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

        futureTrips = new ArrayList<>();

        travelDiaryService.listMyFutureTrips(token).enqueue(new Callback<List<Trip>>() {
            @Override
            public void onResponse(Call<List<Trip>> call, retrofit2.Response<List<Trip>> response) {
                if (response.body()!=null) {
                    futureTrips.addAll(response.body());
                    if (callBack!=null) {
                        callBack.responseNetwork(futureTrips);
                    }
                }else {
                    if (callBack!=null) {
                        callBack.responseNetwork(futureTrips);
                    }
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

        tripsByCity = new ArrayList<>();

        travelDiaryService.listTripsByCity(cityId).enqueue(new Callback<List<Trip>>() {
            @Override
            public void onResponse(Call<List<Trip>> call, retrofit2.Response<List<Trip>> response) {
                tripsByCity.addAll(response.body());
                callBack.responseNetwork(tripsByCity);
            }

            @Override
            public void onFailure(Call<List<Trip>> call, Throwable t) {
                callBack.failNetwork(t);
            }
        });
    }


    private void addToFuture(String token, int placeId, final CallBack callBack){

        travelDiaryService.addToFutureTrips(token, placeId).enqueue(new Callback<ResponseBody>() {
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
                callBack.responseNetwork(response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callBack.failNetwork(t);
            }
        });
    }

    private void downloadAllCities(final CallBack callBack){

        allCities = new ArrayList<>();

        travelDiaryService.listAllCities().enqueue(new Callback<List<City>>() {
            @Override
            public void onResponse(Call<List<City>> call, retrofit2.Response<List<City>> response) {
                allCities.addAll(response.body());
                callBack.responseNetwork(allCities);
            }

            @Override
            public void onFailure(Call<List<City>> call, Throwable t) {
                callBack.failNetwork(t);
            }
        });
    }
}
