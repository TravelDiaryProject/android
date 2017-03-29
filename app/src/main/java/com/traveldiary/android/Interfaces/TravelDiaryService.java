package com.traveldiary.android.Interfaces;

import com.traveldiary.android.essence.City;
import com.traveldiary.android.essence.Place;
import com.traveldiary.android.essence.RegistrationResponse;
import com.traveldiary.android.essence.Trip;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Cyborg on 2/3/2017.
 */

public interface TravelDiaryService {

    @GET("/api/v1/trips")
    Call<List<Trip>> listAllTrips();

    @GET("/api/v1/places")
    Call<List<Place>> listAllPlaces();

    @GET("/api/v1/cities")
    Call<List<City>> listAllCities();

    @GET("/api/v1/my/trips")
    Call<List<Trip>> listMyTrips(@Header("Authorization") String token);

    @GET("/api/v1/my/places")
    Call<List<Place>> listMyPlaces(@Header("Authorization") String token);

    @GET("/api/v1/trip/{id}/places")
    Call<List<Place>> listPlacesByTrip(@Path("id") int groupId);

    /*@GET("/api/v1/places?city_id={id}")
    Call<List<Place>> listPlacesByCity(@Path("id") int cityId);*/

    @GET("/api/v1/places")
    Call<List<Place>> listPlacesByCity(@Query("city_id") int cityId);

    /*@GET("/api/v1/trip/{id}/places")
    Call<List<Place>> listPlacesByTrip(@Header("Authorization") String token, @Path("id") int groupId);*/


    /*@Multipart
    @POST("/api/v1/place")
    Call<ResponseBody> postImage(@Part MultipartBody.Part image, @Part("tripId") RequestBody tripId);*/

    @Multipart
    @POST("/api/v1/my/place")
    Call<ResponseBody> postImage(@Header("Authorization") String token, @Part MultipartBody.Part image, @Part("tripId") RequestBody tripId);



   /* @FormUrlEncoded
    @POST("/api/v1/trip")
    Call<ResponseBody> createTrip(@Field("title") String tripTitle);*/

    @FormUrlEncoded
    @POST("/api/v1/my/trip")
    Call<ResponseBody> createTrip(@Header("Authorization") String token, @Field("title") String tripTitle);



    @FormUrlEncoded
    @POST("/api/v1/login_check")
    Call<RegistrationResponse> getToken(@Field("_username") String username, @Field("_password") String password);

    @FormUrlEncoded
    @POST("/api/v1/register")
    Call<RegistrationResponse> registration(@Field("_email") String username, @Field("_password") String password);

}
