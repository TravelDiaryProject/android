package com.traveldiary.android.network;

import com.traveldiary.android.model.City;
import com.traveldiary.android.model.Country;
import com.traveldiary.android.model.Place;
import com.traveldiary.android.model.RegistrationResponse;
import com.traveldiary.android.model.Trip;

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


public interface TravelDiaryService {

//    @GET("/api/v1/top_places")
//    Call<List<Place>> listTopPlaces();
//    @GET("/api/v1/top_places")
//    Call<List<Place>> listTopPlaces(@Header("Authorization") String token);

    //http://188.166.77.89/api/v1/top_places?offset=0&limit=2
    @GET("/api/v1/top_places")
    Call<List<Place>> listTopPlacesOffset(@Query("offset") int offset, @Query("limit") int limit);
    @GET("/api/v1/top_places")
    Call<List<Place>> listTopPlacesOffset(@Query("offset") int offset, @Query("limit") int limit, @Header("Authorization") String token);

    @GET("/api/v1/trip/{id}/places")
    Call<List<Place>> listPlacesByTrip(@Path("id") int groupId);
    @GET("/api/v1/trip/{id}/places")
    Call<List<Place>> listPlacesByTrip(@Header("Authorization") String token, @Path("id") int groupId);

    @GET("/api/v1/top_places")
    Call<List<Place>> listPlacesByCity(@Query("city_id") int cityId);
    @GET("/api/v1/top_places")
    Call<List<Place>> listPlacesByCity(@Header("Authorization") String token, @Query("city_id") int cityId);

    @GET("/api/v1/top_places")
    Call<List<Place>> listPlacesByCountry(@Query("country_id") int countryId);
    @GET("/api/v1/top_places")
    Call<List<Place>> listPlacesByCountry(@Header("Authorization") String token, @Query("country_id") int countryId);


    @GET("/api/v1/cities")
    Call<List<City>> listAllCities();

    @GET("/api/v1/countries")
    Call<List<Country>> listAllCountries();

    @GET("/api/v1/my/trips")
    Call<List<Trip>> listMyTrips(@Header("Authorization") String token);

    @GET("/api/v1/my/future-trips")
    Call<List<Trip>> listMyFutureTrips(@Header("Authorization") String token);

    @GET("/api/v1/trip/{tripId}")
    Call<Trip> getTripById(@Path("tripId") int tripId);
    @GET("/api/v1/trip/{tripId}")
    Call<Trip> getTripById(@Header("Authorization") String token, @Path("tripId") int tripId);



    /*@GET("/api/v1/places?city_id={id}")
    Call<List<Place>> listPlacesByCity(@Path("id") int cityId);*/





    @GET("/api/v1/trips")
    Call<List<Trip>> listTripsByCity(@Query("city_id") int cityId);

    /*@GET("/api/v1/trip/{id}/places")
    Call<List<Place>> listPlacesByTrip(@Header("Authorization") String token, @Path("id") int groupId);*/


    /*@Multipart
    @POST("/api/v1/place")
    Call<ResponseBody> postImage(@Part MultipartBody.Part image, @Part("tripId") RequestBody tripId);*/

    @Multipart
    @POST("/api/v1/my/place")
    Call<ResponseBody> postImage(@Header("Authorization") String token, @Part MultipartBody.Part image, @Part("tripId") RequestBody tripId);

    @Multipart
    @POST("/api/v1/my/place/remove")
    Call<ResponseBody> removePlace(@Header("Authorization") String token, @Part("placeId") RequestBody placeId);

    @Multipart
    @POST("/api/v1/my/trip/remove")
    Call<ResponseBody> removeTrip(@Header("Authorization") String token, @Part("tripId") RequestBody tripId);



   /* @FormUrlEncoded
    @POST("/api/v1/trip")
    Call<ResponseBody> createTrip(@Field("title") String tripTitle);*/

    @FormUrlEncoded
    @POST("/api/v1/my/trip")
    Call<ResponseBody> createTrip(@Header("Authorization") String token, @Field("title") String tripTitle);

    @FormUrlEncoded
    @POST("/api/v1/my/add-place-to-future-trips")
    Call<ResponseBody> addToFutureTrips(@Header("Authorization") String token, @Field("placeId") int placeId);

    @FormUrlEncoded
    @POST("/api/v1/my/like")
    Call<ResponseBody> likePlace(@Header("Authorization") String token, @Field("placeId") int placeId);

    @FormUrlEncoded
    @POST("/api/v1/my/unlike")
    Call<ResponseBody> unlikePlace(@Header("Authorization") String token, @Field("placeId") int placeId);

    @FormUrlEncoded
    @POST("/api/v1/login_check")
    Call<RegistrationResponse> getToken(@Field("_username") String username, @Field("_password") String password);

    @FormUrlEncoded
    @POST("/api/v1/register")
    Call<RegistrationResponse> registration(@Field("_email") String username, @Field("_password") String password);

}
