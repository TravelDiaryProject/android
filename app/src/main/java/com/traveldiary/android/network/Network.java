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

    private List<Place> topPlaces;
    private List<Place> placesByTrip;
    private List<Place> placesByCity;
    private List<Place> placesByCountry;

    private List<Trip> myTrips;
    private List<Trip> futureTrips;
    private List<Trip> tripsByCity;

    private List<City> allCities;
    private List<Country> allCounties;

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
        if (myTrips!=null) {
            Log.d("NETWORK", " myTrips != null - callback(myTrips)");
            callBack.responseNetwork(myTrips);
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

        if (futureTrips!=null){
            callBack.responseNetwork(futureTrips);
        }
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

    @Override
    public void getAllCountries(CallBack callBack) {
        if (allCounties==null)
            downloadAllCountries(callBack);
        else
            callBack.responseNetwork(allCounties);
    }

    @Override
    public void removePlace(String token, int placeId, CallBack callBack) {
        removePlaceFromServer(token, placeId, callBack);
    }

    private void removePlaceFromServer(String token, int placeId, final CallBack callBack) {
        if (TOKEN_CONST!=null && !TOKEN_CONST.equals("")){
            RequestBody placeIdRequest = RequestBody.create(MediaType.parse("multipart/form-data"), Integer.toString(placeId));
            travelDiaryService.removePlace(TOKEN_CONST, placeIdRequest).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d("NETWORK", "Remove response = " + response.code() + response.message());
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
    }


    /*
                    Realization
     */

    private void downloadTopPlaces(final CallBack callBack){

        topPlaces = new ArrayList<>();

        if (TOKEN_CONST==null || TOKEN_CONST.equals("")) {
            travelDiaryService.listTopPlaces().enqueue(new Callback<List<Place>>() {
                @Override
                public void onResponse(Call<List<Place>> call, retrofit2.Response<List<Place>> response) {
                    if (response.code() == 200) {
                        topPlaces.addAll(response.body());
                    }
                    callBack.responseNetwork(topPlaces);
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
                        topPlaces.addAll(response.body());
                    }
                    callBack.responseNetwork(topPlaces);
                }

                @Override
                public void onFailure(Call<List<Place>> call, Throwable t) {
                    callBack.failNetwork(t);
                }
            });
        }

    }

    private void downloadPlacesByTripId(int tripId, final CallBack callBack) {

        placesByTrip = new ArrayList<>();

        if (TOKEN_CONST==null || TOKEN_CONST.equals("")) {
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
        }else {
            travelDiaryService.listPlacesByTrip(TOKEN_CONST, tripId).enqueue(new Callback<List<Place>>() {
                @Override
                public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                    placesByTrip.addAll(response.body());
                    callBack.responseNetwork(placesByTrip);

                }

                @Override
                public void onFailure(Call<List<Place>> call, Throwable t) {
                    callBack.failNetwork(t);
                }
            });
        }
    }

    private void downloadPlacesByCityId(int cityId, final CallBack callBack) {

        placesByCity = new ArrayList<>();

        if (TOKEN_CONST==null || TOKEN_CONST.equals("")) {

            travelDiaryService.listPlacesByCity(cityId).enqueue(new Callback<List<Place>>() {
                @Override
                public void onResponse(Call<List<Place>> call, retrofit2.Response<List<Place>> response) {
                    if (response.code() == 200) {
                        placesByCity.addAll(response.body());
                        callBack.responseNetwork(placesByCity);
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
                        placesByCity.addAll(response.body());
                        callBack.responseNetwork(placesByCity);
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

        placesByCountry = new ArrayList<>();

        if (TOKEN_CONST==null || TOKEN_CONST.equals("")) {

            travelDiaryService.listPlacesByCountry(countryId).enqueue(new Callback<List<Place>>() {
                @Override
                public void onResponse(Call<List<Place>> call, retrofit2.Response<List<Place>> response) {
                    if (response.code() == 200) {
                        placesByCountry.addAll(response.body());
                        callBack.responseNetwork(placesByCountry);
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
                        placesByCountry.addAll(response.body());
                        callBack.responseNetwork(placesByCountry);
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

        if (myTrips==null) {
            Log.d("NETWORK", " myTrips == null creaate new ArrrayList");
            myTrips = new ArrayList<>();
        }

        travelDiaryService.listMyTrips(token).enqueue(new Callback<List<Trip>>() {
            @Override
            public void onResponse(Call<List<Trip>> call, retrofit2.Response<List<Trip>> response) {

                Log.d("NETWORK", " downloadMyTrips response = " + response.code());

                if (response.code()==200){
                    if (response.body().size()!=myTrips.size()) {
                        myTrips.clear();
                        myTrips.addAll(response.body());
                        Collections.reverse(myTrips);
                        if (callBack!=null) {
                            Log.d("NETWORK", " callback with new myTrips");
                            callBack.responseNetwork(myTrips);
                        }
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

        if (futureTrips==null) {
            futureTrips = new ArrayList<>();
        }

        travelDiaryService.listMyFutureTrips(token).enqueue(new Callback<List<Trip>>() {
            @Override
            public void onResponse(Call<List<Trip>> call, retrofit2.Response<List<Trip>> response) {

                if (response.code()==200){
                    if (response.body().size()!=futureTrips.size()){
                        futureTrips.clear();
                        futureTrips.addAll(response.body());
                        Collections.reverse(futureTrips);
                        if (callBack!=null) {
                            callBack.responseNetwork(futureTrips);
                        }
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

        allCities = new ArrayList<>();

        travelDiaryService.listAllCities().enqueue(new Callback<List<City>>() {
            @Override
            public void onResponse(Call<List<City>> call, retrofit2.Response<List<City>> response) {
                if (response.code()==200) {
                    allCities.addAll(response.body());
                }
                callBack.responseNetwork(allCities);
            }

            @Override
            public void onFailure(Call<List<City>> call, Throwable t) {
                callBack.failNetwork(t);
            }
        });
    }

    private void downloadAllCountries(final CallBack callBack){

        allCounties = new ArrayList<>();

        travelDiaryService.listAllCountries().enqueue(new Callback<List<Country>>() {
            @Override
            public void onResponse(Call<List<Country>> call, retrofit2.Response<List<Country>> response) {
                if (response.code()==200) {
                    allCounties.addAll(response.body());
                }
                callBack.responseNetwork(allCounties);
            }

            @Override
            public void onFailure(Call<List<Country>> call, Throwable t) {
                callBack.failNetwork(t);
            }
        });
    }
}
