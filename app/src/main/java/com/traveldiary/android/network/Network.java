package com.traveldiary.android.network;

import com.traveldiary.android.Api;
import com.traveldiary.android.Interfaces.CallBackInterface;
import com.traveldiary.android.Interfaces.NetworkInterface;
import com.traveldiary.android.Interfaces.TravelDiaryService;
import com.traveldiary.android.essence.City;
import com.traveldiary.android.essence.Place;
import com.traveldiary.android.essence.RegistrationResponse;
import com.traveldiary.android.essence.Trip;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Cyborg on 3/30/2017.
 */

public class Network implements NetworkInterface{

    private static final TravelDiaryService travelDiaryService = Api.getTravelDiaryService();

    private List<Place> allPlaces = new ArrayList<>();
    private List<Place> myPlaces = new ArrayList<>();
    private List<Place> placesByTrip = new ArrayList<>();
    private List<Place> placesByCity = new ArrayList<>();

    private List<Trip> allTrips = new ArrayList<>();
    private List<Trip> myTrips = new ArrayList<>();
    private List<Trip> tripsByCity = new ArrayList<>();

    private List<City> allCities = new ArrayList<>();

    private CallBackInterface callBackInterface;

    public Network(CallBackInterface callBackInterface) {
        this.callBackInterface = callBackInterface;
    }

    /*
            PLACES
     */

    @Override
    public void getAllPlaces() {

        if (allPlaces.size() == 0) {
            downloadAllPlaces();
        }else {
            callBackInterface.getAllPlaces(allPlaces);
        }

    }

    @Override
    public void getMyPlaces(String token) {
        if (myPlaces.size() == 0){
            downloadMyPlaces(token);
        }else {
            callBackInterface.getMyPlaces(myPlaces);
        }
    }

    @Override
    public void getPlacesByTrip(int tripId) {

        downloadPlacesByTripId(tripId);

    }

    @Override
    public void getPlacesByCity(int cityId) {

        downloadPlacesByCityId(cityId);

    }

    /*
            TRIPS
     */
    @Override
    public void getAllTrips() {
        if (allTrips.size()==0){
            downloadAllTrips();
        }else {
            callBackInterface.getAllTrips(allTrips);
        }
    }

    @Override
    public void getMyTrips(String token) {
        if (myTrips.size()==0){
            downloadMyTrips(token);
        }else {
            callBackInterface.getMyTrips(myTrips);
        }

    }

    @Override
    public void getTripsByCity(int cityId) {
        downloadTripsByCityId(cityId);
    }

    /*
               Different
     */
    @Override
    public void createTrip(String token, String tripTitle) {
        uploadNewTrip(token, tripTitle);
    }

    @Override
    public void signIn(String email, String password) {
        login(email, password);
    }

    @Override
    public void registration(String email, String password) {
        reg(email, password);
    }

    @Override
    public void uploadPlace(String token, MultipartBody.Part body, RequestBody tripIdRequest) {
        uploadImage(token, body, tripIdRequest);
    }

    @Override
    public void getAllCities() {
        if (allCities.size()==0){
            downloadAllCities();
        }else {
            callBackInterface.getAllCities(allCities);
        }

    }


    private void downloadAllPlaces() {

        travelDiaryService.listAllPlaces().enqueue(new Callback<List<Place>>() {
            @Override
            public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {

                allPlaces.addAll(response.body());

                callBackInterface.getAllPlaces(allPlaces);

            }

            @Override
            public void onFailure(Call<List<Place>> call, Throwable t) {

            }
        });
    }

    private void downloadMyPlaces(String token) {

        travelDiaryService.listMyPlaces(token).enqueue(new Callback<List<Place>>() {
            @Override
            public void onResponse(Call<List<Place>> call, retrofit2.Response<List<Place>> response) {

                myPlaces.addAll(response.body());

                callBackInterface.getMyPlaces(myPlaces);
            }

            @Override
            public void onFailure(Call<List<Place>> call, Throwable t) {

            }
        });
    }

    private void downloadPlacesByTripId(int tripId) {

        travelDiaryService.listPlacesByTrip(tripId).enqueue(new Callback<List<Place>>() {
            @Override
            public void onResponse(Call<List<Place>> call, retrofit2.Response<List<Place>> response) {

                placesByTrip.addAll(response.body());

                callBackInterface.getPlacesByTrip(placesByTrip);
            }

            @Override
            public void onFailure(Call<List<Place>> call, Throwable t) {

            }
        });
    }

    private void downloadPlacesByCityId(int cityId) {

        travelDiaryService.listPlacesByCity(cityId).enqueue(new Callback<List<Place>>() {
            @Override
            public void onResponse(Call<List<Place>> call, retrofit2.Response<List<Place>> response) {

                placesByCity.addAll(response.body());


                callBackInterface.getPlacesByCity(placesByCity);
            }

            @Override
            public void onFailure(Call<List<Place>> call, Throwable t) {

            }
        });
    }

    private void downloadAllTrips() {

        travelDiaryService.listAllTrips().enqueue(new Callback<List<Trip>>() {
            @Override
            public void onResponse(Call<List<Trip>> call, retrofit2.Response<List<Trip>> response) {

                allTrips.addAll(response.body());

                callBackInterface.getAllTrips(allTrips);
            }

            @Override
            public void onFailure(Call<List<Trip>> call, Throwable t) {

            }
        });
    }

    private void downloadMyTrips(String token) {

        travelDiaryService.listMyTrips(token).enqueue(new Callback<List<Trip>>() {
            @Override
            public void onResponse(Call<List<Trip>> call, retrofit2.Response<List<Trip>> response) {

                myTrips.addAll(response.body());

                callBackInterface.getMyTrips(myTrips);

            }

            @Override
            public void onFailure(Call<List<Trip>> call, Throwable t) {

            }
        });
    }

    /*
                Не проверенно!!!!!!
     */
    private void downloadTripsByCityId(int cityId) {

        travelDiaryService.listTripsByCity(cityId).enqueue(new Callback<List<Trip>>() {
            @Override
            public void onResponse(Call<List<Trip>> call, retrofit2.Response<List<Trip>> response) {

                tripsByCity.addAll(response.body());

                callBackInterface.getPlacesByCity(placesByCity);
            }

            @Override
            public void onFailure(Call<List<Trip>> call, Throwable t) {

            }
        });
    }

    private void uploadNewTrip(String token, String tripTitle){

        travelDiaryService.createTrip(token, tripTitle).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                callBackInterface.createTrip(response.body().toString());

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                callBackInterface.createTrip(t.toString());

            }
        });

    }

    private void login(String email, String password){

        travelDiaryService.getToken(email, password).enqueue(new Callback<RegistrationResponse>() {
            @Override
            public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {

                callBackInterface.signIn(response);

            }

            @Override
            public void onFailure(Call<RegistrationResponse> call, Throwable t) {

            }
        });
    }

    private void reg(String email, String password){
        travelDiaryService.registration(email, password).enqueue(new Callback<RegistrationResponse>() {
            @Override
            public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {

                callBackInterface.registration(response);

            }

            @Override
            public void onFailure(Call<RegistrationResponse> call, Throwable t) {

            }
        });
    }

    private void uploadImage(String token, MultipartBody.Part body, RequestBody tripIdRequest){
        travelDiaryService.postImage(token, body, tripIdRequest).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                callBackInterface.uploadPlace(response);

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void downloadAllCities(){
        travelDiaryService.listAllCities().enqueue(new Callback<List<City>>() {
            @Override
            public void onResponse(Call<List<City>> call, retrofit2.Response<List<City>> response) {

                allCities.addAll(response.body());

                callBackInterface.getAllCities(allCities);

            }

            @Override
            public void onFailure(Call<List<City>> call, Throwable t) {

            }
        });
    }

}
