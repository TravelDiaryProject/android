package com.traveldiary.android.network;

import android.util.Log;

import com.traveldiary.android.model.City;
import com.traveldiary.android.model.Country;
import com.traveldiary.android.model.Place;
import com.traveldiary.android.model.RegistrationResponse;
import com.traveldiary.android.model.Trip;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.traveldiary.android.Constans.TOKEN_CONST;

public class Network {

    private static final TravelDiaryService travelDiaryService = Api.getTravelDiaryService();

    public Network() {
    }

    public void downloadTopPlaces(int offset, int limit, final CallbackPlaces callbackPlaces){

        if (TOKEN_CONST==null || TOKEN_CONST.equals("")) {
            travelDiaryService.listTopPlacesOffset(offset, limit).enqueue(new Callback<List<Place>>() {
                @Override
                public void onResponse(Call<List<Place>> call, retrofit2.Response<List<Place>> response) {
                    if (response.code() == 200) {
                        callbackPlaces.responseNetwork(response.body());
                    }else {
                        callbackPlaces.failNetwork(new Throwable(response.message()));
                    }
                }

                @Override
                public void onFailure(Call<List<Place>> call, Throwable t) {
                    callbackPlaces.failNetwork(t);
                }
            });
        }else {
            travelDiaryService.listTopPlacesOffset(offset, limit, TOKEN_CONST).enqueue(new Callback<List<Place>>() {
                @Override
                public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                    if (response.code() == 200) {
                        callbackPlaces.responseNetwork(response.body());
                    }else {
                        callbackPlaces.failNetwork(new Throwable(response.message()));
                    }
                }

                @Override
                public void onFailure(Call<List<Place>> call, Throwable t) {
                    callbackPlaces.failNetwork(t);
                }
            });
        }
    }

    public void downloadMyTrips(final CallbackTrips callbackTrips) {

        travelDiaryService.listMyTrips(TOKEN_CONST).enqueue(new Callback<List<Trip>>() {
            @Override
            public void onResponse(Call<List<Trip>> call, retrofit2.Response<List<Trip>> response) {
                if (response.code() == 200) {
                    callbackTrips.responseNetwork(response.body());
                } else {
                    callbackTrips.failNetwork(new Throwable(response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<Trip>> call, Throwable t) {
                callbackTrips.failNetwork(t);
            }
        });
    }

    public void downloadFutureTrips(final CallbackTrips callbackTrips) {

        travelDiaryService.listMyFutureTrips(TOKEN_CONST).enqueue(new Callback<List<Trip>>() {
            @Override
            public void onResponse(Call<List<Trip>> call, retrofit2.Response<List<Trip>> response) {
                if (response.code()==200){
                    callbackTrips.responseNetwork(response.body());
                }else {
                    callbackTrips.failNetwork(new Throwable(response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<Trip>> call, Throwable t) {
                callbackTrips.failNetwork(t);
            }
        });
    }

    public void downloadTripById(int tripId, final SimpleCallBack simpleCallBack) {

        if (TOKEN_CONST==null || TOKEN_CONST.equals("")) {
            travelDiaryService.getTripById(tripId).enqueue(new Callback<Trip>() {
                @Override
                public void onResponse(Call<Trip> call, Response<Trip> response) {
                    if (response.code()==200) {
                        simpleCallBack.response(response.body());
                    }else {
                        simpleCallBack.fail(new Throwable(response.message()));
                    }
                }

                @Override
                public void onFailure(Call<Trip> call, Throwable t) {
                    simpleCallBack.fail(t);
                }
            });
        }else {
            travelDiaryService.getTripById(TOKEN_CONST, tripId).enqueue(new Callback<Trip>() {
                @Override
                public void onResponse(Call<Trip> call, Response<Trip> response) {
                    if (response.code()==200) {
                        simpleCallBack.response(response.body());
                    }else {
                        simpleCallBack.fail(new Throwable(response.message()));
                    }
                }

                @Override
                public void onFailure(Call<Trip> call, Throwable t) {
                    simpleCallBack.fail(t);
                }
            });
        }
    }

    public void downloadPlacesByTripId(int tripId, final CallbackPlaces callbackPlaces) {

        if (TOKEN_CONST==null || TOKEN_CONST.equals("")) {
            travelDiaryService.listPlacesByTrip(tripId).enqueue(new Callback<List<Place>>() {
                @Override
                public void onResponse(Call<List<Place>> call, retrofit2.Response<List<Place>> response) {
                    if (response.code()==200){
                        callbackPlaces.responseNetwork(response.body());
                    }else {
                        callbackPlaces.failNetwork(new Throwable(response.message()));
                    }
                }

                @Override
                public void onFailure(Call<List<Place>> call, Throwable t) {
                    callbackPlaces.failNetwork(t);
                }
            });
        }else {
            travelDiaryService.listPlacesByTrip(TOKEN_CONST, tripId).enqueue(new Callback<List<Place>>() {
                @Override
                public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                    if (response.code()==200){
                        callbackPlaces.responseNetwork(response.body());
                    }else {
                        callbackPlaces.failNetwork(new Throwable(response.message()));
                    }
                }

                @Override
                public void onFailure(Call<List<Place>> call, Throwable t) {
                    callbackPlaces.failNetwork(t);
                }
            });
        }
    }

    public void downloadPlacesByCityId(int cityId, final CallbackPlaces callbackPlaces) {

        if (TOKEN_CONST==null || TOKEN_CONST.equals("")) {

            travelDiaryService.listPlacesByCity(cityId).enqueue(new Callback<List<Place>>() {
                @Override
                public void onResponse(Call<List<Place>> call, retrofit2.Response<List<Place>> response) {
                    if (response.code() == 200) {
                        callbackPlaces.responseNetwork(response.body());
                    } else
                        callbackPlaces.failNetwork(new Throwable(response.message()));
                }

                @Override
                public void onFailure(Call<List<Place>> call, Throwable t) {
                    callbackPlaces.failNetwork(t);
                }
            });
        } else {
            travelDiaryService.listPlacesByCity(TOKEN_CONST, cityId).enqueue(new Callback<List<Place>>() {
                @Override
                public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                    if (response.code() == 200) {
                        callbackPlaces.responseNetwork(response.body());
                    } else
                        callbackPlaces.failNetwork(new Throwable(response.message()));
                }

                @Override
                public void onFailure(Call<List<Place>> call, Throwable t) {
                    callbackPlaces.failNetwork(t);
                }
            });
        }
    }

    public void downloadPlacesByCountryId(int countryId, final CallbackPlaces callbackPlaces) {

        if (TOKEN_CONST==null || TOKEN_CONST.equals("")) {

            travelDiaryService.listPlacesByCountry(countryId).enqueue(new Callback<List<Place>>() {
                @Override
                public void onResponse(Call<List<Place>> call, retrofit2.Response<List<Place>> response) {
                    if (response.code() == 200) {
                        callbackPlaces.responseNetwork(response.body());
                    } else
                        callbackPlaces.failNetwork(new Throwable(response.message()));
                }

                @Override
                public void onFailure(Call<List<Place>> call, Throwable t) {
                    callbackPlaces.failNetwork(t);
                }
            });
        } else {
            travelDiaryService.listPlacesByCountry(TOKEN_CONST, countryId).enqueue(new Callback<List<Place>>() {
                @Override
                public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                    if (response.code() == 200) {
                        callbackPlaces.responseNetwork(response.body());
                    } else
                        callbackPlaces.failNetwork(new Throwable(response.message()));
                }

                @Override
                public void onFailure(Call<List<Place>> call, Throwable t) {
                    callbackPlaces.failNetwork(t);
                }
            });
        }
    }

    public void downloadAllCities(final CallbackCities callbackCities){

        travelDiaryService.listAllCities().enqueue(new Callback<List<City>>() {
            @Override
            public void onResponse(Call<List<City>> call, retrofit2.Response<List<City>> response) {
                if (response.code()==200) {
                    callbackCities.responseNetwork(response.body());
                }else {
                    callbackCities.failNetwork(new Throwable(response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<City>> call, Throwable t) {
                callbackCities.failNetwork(t);
            }
        });
    }

    public void downloadAllCountries(final CallbackCountries callbackCountries){

        travelDiaryService.listAllCountries().enqueue(new Callback<List<Country>>() {
            @Override
            public void onResponse(Call<List<Country>> call, retrofit2.Response<List<Country>> response) {
                if (response.code()==200) {
                    callbackCountries.responseNetwork(response.body());
                }else {
                    callbackCountries.failNetwork(new Throwable(response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<Country>> call, Throwable t) {
                callbackCountries.failNetwork(t);
            }
        });
    }

    public void addToFuture(int placeId, final SimpleCallBack simpleCallBack){

        travelDiaryService.addToFutureTrips(TOKEN_CONST, placeId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==201) {
                    simpleCallBack.response(response);
                }else {
                    simpleCallBack.fail(new Throwable(response.message()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                simpleCallBack.fail(t);
            }
        });

    }

    public void uploadLike(int placeId, final SimpleCallBack simpleCallBack){

        travelDiaryService.likePlace(TOKEN_CONST, placeId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==200 || response.code()==201)
                    simpleCallBack.response(response);
                else
                    simpleCallBack.fail(new Throwable(response.message()));
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                simpleCallBack.fail(t);
            }
        });

    }

    public void uploadUnlike(int placeId, final SimpleCallBack simpleCallBack){

        travelDiaryService.unlikePlace(TOKEN_CONST, placeId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==200 || response.code()==201)
                    simpleCallBack.response(response);
                else
                    simpleCallBack.fail(new Throwable(response.message()));
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                simpleCallBack.fail(t);
            }
        });

    }

    public void uploadNewTrip(String tripTitle, final SimpleCallBack simpleCallBack){

        travelDiaryService.createTrip(TOKEN_CONST, tripTitle).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {

                if (response.code()==201)
                    simpleCallBack.response(response);
                else
                    simpleCallBack.fail(new Throwable(response.message()));
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                simpleCallBack.fail(t);
            }
        });

    }

    public void login(String email, String password, final SimpleCallBack simpleCallBack){

        travelDiaryService.getToken(email, password).enqueue(new Callback<RegistrationResponse>() {
            @Override
            public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {

                if (response.code()==200)
                    simpleCallBack.response(response);
                else
                    simpleCallBack.fail(new Throwable(response.message()));
            }

            @Override
            public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                simpleCallBack.fail(t);
            }
        });
    }

    public void registration(String email, String password, final SimpleCallBack simpleCallBack){
        travelDiaryService.registration(email, password).enqueue(new Callback<RegistrationResponse>() {
            @Override
            public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                if (response.code()==201)
                    simpleCallBack.response(response);
                else
                    simpleCallBack.fail(new Throwable(response.message()));
            }

            @Override
            public void onFailure(Call<RegistrationResponse> call, Throwable t) { simpleCallBack.fail(t); }
        });
    }

    public void uploadImage(MultipartBody.Part body, RequestBody tripIdRequest, final SimpleCallBack simpleCallBack){
        travelDiaryService.postImage(TOKEN_CONST, body, tripIdRequest).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==201) {
                    simpleCallBack.response(response);
                }else {
                    simpleCallBack.fail(new Throwable(response.message()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                simpleCallBack.fail(t);
            }
        });
    }

    public void removePlace(int placeId, final SimpleCallBack simpleCallBack) {
        if (TOKEN_CONST!=null && !TOKEN_CONST.equals("")){
            RequestBody placeIdRequest = RequestBody.create(MediaType.parse("multipart/form-data"), Integer.toString(placeId));
            travelDiaryService.removePlace(TOKEN_CONST, placeIdRequest).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d("NETWORK", "Remove response = " + response.code() + response.message());
                    if (response.code()==201) {
                        simpleCallBack.response(response);
                    }else {
                        simpleCallBack.fail(new Throwable(response.message()));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    simpleCallBack.fail(t);
                }
            });
        }
    }

    public void removeTrip(int tripId, final SimpleCallBack simpleCallBack) {
        if (TOKEN_CONST!=null && !TOKEN_CONST.equals("")){
            RequestBody tripIdRequest = RequestBody.create(MediaType.parse("multipart/form-data"), Integer.toString(tripId));
            travelDiaryService.removeTrip(TOKEN_CONST, tripIdRequest).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d("NETWORK", "Remove response = " + response.code() + response.message());
                    if (response.code()==201) {
                        simpleCallBack.response(response);
                    }else {
                        simpleCallBack.fail(new Throwable(response.message()));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    simpleCallBack.fail(t);
                }
            });
        }
    }
}
