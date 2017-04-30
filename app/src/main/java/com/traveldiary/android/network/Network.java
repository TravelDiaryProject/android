package com.traveldiary.android.network;

import android.util.Log;

import com.traveldiary.android.data.Data;
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

    private List<Trip> mTripsByCity;

    // TODO: 4/3/2017 refresh list after adding new trip or place!!!!

    public Network() {
    }



    /*
                PLACES
         */

    @Override
    public void getTopPlacesOffset(int offset, int limit, CallBack callBack) {
            downloadTopPlaces(offset, limit, callBack);
    }

    @Override
    public void getMyTrips(CallBack callBack) {
        downloadMyTrips(callBack);
    }

    @Override
    public void getFutureTrips(CallBack callBack) {
        downloadFutureTrips(callBack);
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

    @Override
    public void getAllCities(CallBack callBack) {
        downloadAllCities(callBack);
    }

    @Override
    public void getAllCountries(CallBack callBack) {
        downloadAllCountries(callBack);
    }

    @Override
    public void addToFutureTrips(int placeId, CallBack callBack) {
        addToFuture(placeId, callBack);
    }

    @Override
    public void likePlace(int placeId, CallBack callBack) {
        uploadLike(placeId, callBack);
    }

    @Override
    public void createTrip(String tripTitle, CallBack callBack) {
        uploadNewTrip(tripTitle, callBack);
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
    public void uploadPlace(MultipartBody.Part body, RequestBody tripIdRequest, CallBack callBack) {
        uploadImage(body, tripIdRequest, callBack);
    }

    @Override
    public void removePlace(int placeId, CallBack callBack) {
        removePlaceFromServer(placeId, callBack);
    }

    @Override
    public void removeTrip(int tripId, CallBack callBack) {
        removeTripFromSerer(tripId, callBack);
    }


    /*
                           TRIPS
    */



    /*@Override
    public void getTripsByCity(int cityId, CallBack callBack) {
        downloadTripsByCityId(cityId, callBack);
    }*/







    /*
               Different
     */



















    /*
                    Realization
     */

    private void downloadTopPlaces(int offset, int limit, final CallBack callBack){

        if (TOKEN_CONST==null || TOKEN_CONST.equals("")) {
            travelDiaryService.listTopPlacesOffset(offset, limit).enqueue(new Callback<List<Place>>() {
                @Override
                public void onResponse(Call<List<Place>> call, retrofit2.Response<List<Place>> response) {
                    if (response.code() == 200) {
                        callBack.responseNetwork(response.body());
                    }else {
                        callBack.failNetwork(new Throwable(response.message()));
                    }
                }

                @Override
                public void onFailure(Call<List<Place>> call, Throwable t) {
                    callBack.failNetwork(t);
                }
            });
        }else {
            travelDiaryService.listTopPlacesOffset(offset, limit, TOKEN_CONST).enqueue(new Callback<List<Place>>() {
                @Override
                public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                    if (response.code() == 200) {
                        callBack.responseNetwork(response.body());
                    }else {
                        callBack.failNetwork(new Throwable(response.message()));
                    }
                }

                @Override
                public void onFailure(Call<List<Place>> call, Throwable t) {
                    callBack.failNetwork(t);
                }
            });
        }
    }

    private void downloadMyTrips(final CallBack callBack) {

        travelDiaryService.listMyTrips(TOKEN_CONST).enqueue(new Callback<List<Trip>>() {
            @Override
            public void onResponse(Call<List<Trip>> call, retrofit2.Response<List<Trip>> response) {
                if (response.code()==200){
                    System.out.println("QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ m.s = " + response.body().size());
                    callBack.responseNetwork(response.body());
                }else {
                    callBack.failNetwork(new Throwable(response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<Trip>> call, Throwable t) {
                callBack.failNetwork(t);
            }
        });
    }

    private void downloadFutureTrips(final CallBack callBack) {

        travelDiaryService.listMyFutureTrips(TOKEN_CONST).enqueue(new Callback<List<Trip>>() {
            @Override
            public void onResponse(Call<List<Trip>> call, retrofit2.Response<List<Trip>> response) {
                if (response.code()==200){
                    callBack.responseNetwork(response.body());
                }else {
                    callBack.failNetwork(new Throwable(response.message()));
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

    private void downloadPlacesByTripId(int tripId, final CallBack callBack) {

        if (TOKEN_CONST==null || TOKEN_CONST.equals("")) {
            travelDiaryService.listPlacesByTrip(tripId).enqueue(new Callback<List<Place>>() {
                @Override
                public void onResponse(Call<List<Place>> call, retrofit2.Response<List<Place>> response) {
                    if (response.code()==200){
                        callBack.responseNetwork(response.body());
                    }else {
                        callBack.failNetwork(new Throwable(response.message()));
                    }
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
                    if (response.code()==200){
                        callBack.responseNetwork(response.body());
                    }else {
                        callBack.failNetwork(new Throwable(response.message()));
                    }
                }

                @Override
                public void onFailure(Call<List<Place>> call, Throwable t) {
                    callBack.failNetwork(t);
                }
            });
        }
    }

    private void downloadPlacesByCityId(int cityId, final CallBack callBack) {

        if (TOKEN_CONST==null || TOKEN_CONST.equals("")) {

            travelDiaryService.listPlacesByCity(cityId).enqueue(new Callback<List<Place>>() {
                @Override
                public void onResponse(Call<List<Place>> call, retrofit2.Response<List<Place>> response) {
                    if (response.code() == 200) {
                        callBack.responseNetwork(response.body());
                    } else
                        callBack.failNetwork(new Throwable(response.message()));
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
                        callBack.responseNetwork(response.body());
                    } else
                        callBack.failNetwork(new Throwable(response.message()));
                }

                @Override
                public void onFailure(Call<List<Place>> call, Throwable t) {
                    callBack.failNetwork(t);
                }
            });
        }
    }

    private void downloadPlacesByCountryId(int countryId, final CallBack callBack) {

        if (TOKEN_CONST==null || TOKEN_CONST.equals("")) {

            travelDiaryService.listPlacesByCountry(countryId).enqueue(new Callback<List<Place>>() {
                @Override
                public void onResponse(Call<List<Place>> call, retrofit2.Response<List<Place>> response) {
                    if (response.code() == 200) {
                        callBack.responseNetwork(response.body());
                    } else
                        callBack.failNetwork(new Throwable(response.message()));
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
                        callBack.responseNetwork(response.body());
                    } else
                        callBack.failNetwork(new Throwable(response.message()));
                }

                @Override
                public void onFailure(Call<List<Place>> call, Throwable t) {
                    callBack.failNetwork(t);
                }
            });
        }
    }

    private void downloadAllCities(final CallBack callBack){

        travelDiaryService.listAllCities().enqueue(new Callback<List<City>>() {
            @Override
            public void onResponse(Call<List<City>> call, retrofit2.Response<List<City>> response) {
                if (response.code()==200) {
                    callBack.responseNetwork(response.body());
                }else {
                    callBack.failNetwork(new Throwable(response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<City>> call, Throwable t) {
                callBack.failNetwork(t);
            }
        });
    }

    private void downloadAllCountries(final CallBack callBack){

        travelDiaryService.listAllCountries().enqueue(new Callback<List<Country>>() {
            @Override
            public void onResponse(Call<List<Country>> call, retrofit2.Response<List<Country>> response) {
                if (response.code()==200) {
                    callBack.responseNetwork(response.body());
                }else {
                    callBack.failNetwork(new Throwable(response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<Country>> call, Throwable t) {
                callBack.failNetwork(t);
            }
        });
    }

    private void addToFuture(int placeId, final CallBack callBack){

        travelDiaryService.addToFutureTrips(TOKEN_CONST, placeId).enqueue(new Callback<ResponseBody>() {
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

    private void uploadLike(int placeId, final CallBack callBack){

        travelDiaryService.likePlace(TOKEN_CONST, placeId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==200 || response.code()==201)
                    callBack.responseNetwork(response);
                else
                    callBack.failNetwork(new Throwable(response.message()));
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callBack.failNetwork(t);
            }
        });

    }

    private void uploadNewTrip(String tripTitle, final CallBack callBack){

        travelDiaryService.createTrip(TOKEN_CONST, tripTitle).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {

                if (response.code()==201)
                    callBack.responseNetwork(response);
                else
                    callBack.failNetwork(new Throwable(response.message()));
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

                if (response.code()==200)
                    callBack.responseNetwork(response);
                else
                    callBack.failNetwork(new Throwable(response.message()));
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
                if (response.code()==201)
                    callBack.responseNetwork(response);
                else
                    callBack.failNetwork(new Throwable(response.message()));
            }

            @Override
            public void onFailure(Call<RegistrationResponse> call, Throwable t) { callBack.failNetwork(t); }
        });
    }

    private void uploadImage(MultipartBody.Part body, RequestBody tripIdRequest, final CallBack callBack){
        travelDiaryService.postImage(TOKEN_CONST, body, tripIdRequest).enqueue(new Callback<ResponseBody>() {
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

    private void removePlaceFromServer(int placeId, final CallBack callBack) {
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

    private void removeTripFromSerer(int tripId, final CallBack callBack) {
        if (TOKEN_CONST!=null && !TOKEN_CONST.equals("")){
            RequestBody tripIdRequest = RequestBody.create(MediaType.parse("multipart/form-data"), Integer.toString(tripId));
            travelDiaryService.removeTrip(TOKEN_CONST, tripIdRequest).enqueue(new Callback<ResponseBody>() {
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
                Не проверенно!!!!!!
     */























}
