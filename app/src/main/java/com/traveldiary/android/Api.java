package com.traveldiary.android;

import com.traveldiary.android.Interfaces.TravelDiaryService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.traveldiary.android.Constans.ROOT_URL;


public class Api {

    private static Retrofit retrofit = null;

    private static TravelDiaryService travelDiaryService;

    private Api() {
    }

    public static TravelDiaryService getTravelDiaryService(){
        return initTravelDiaryService();
    }

    private static TravelDiaryService initTravelDiaryService(){
        if (travelDiaryService == null) {
            synchronized (Api.class) {
                if (travelDiaryService == null) {
                    travelDiaryService = getRetrofit().create(TravelDiaryService.class);
                }
            }
        }
        return travelDiaryService;
    }

    private synchronized static Retrofit getRetrofit() {
        if (retrofit == null) {
            synchronized (Api.class) {
                if (retrofit == null) {
                    retrofit = new Retrofit.Builder()
                            .baseUrl(ROOT_URL)
                            .addConverterFactory(GsonConverterFactory.create()).build();
                }
            }
        }
        return retrofit;
    }
}
