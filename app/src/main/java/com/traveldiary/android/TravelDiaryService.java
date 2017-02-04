package com.traveldiary.android;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by Cyborg on 2/3/2017.
 */

public interface TravelDiaryService {
    @GET("/api/v1/trips")
    Call<List<Trip>> listAllTrips();

    @GET("/api/v1/trip/{id}/places")
    Call<List<Place>> listPlacesByTrip(@Path("id") int groupId);

    @Multipart
    @POST("/api/v1/place")
    Call<ResponseBody> postImage(@Part MultipartBody.Part image, @Part("tripId") RequestBody tripId);

    @FormUrlEncoded
    @POST("/api/v1/trip")
    Call<ResponseBody> createTrip(@Field("title") String tripTitle);

}
