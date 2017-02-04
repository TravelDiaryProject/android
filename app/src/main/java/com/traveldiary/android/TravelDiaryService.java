package com.traveldiary.android;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Cyborg on 2/3/2017.
 */

public interface TravelDiaryService {
    @GET("/api/v1/trips")
    Call<List<Trip>> listAllTrips();

    @GET("/api/v1/trip/{id}/places")
    Call<List<Place>> listPlacesByTrip(@Path("id") int groupId);

}
