package com.traveldiary.android.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.traveldiary.android.Constans.ROOT_URL;


class Api {

    private static Retrofit retrofit = null;

    private static TravelDiaryService travelDiaryService;

    private Api() {
    }

    static TravelDiaryService getTravelDiaryService(){
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
                    final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            .readTimeout(60, TimeUnit.SECONDS)
                            .connectTimeout(60, TimeUnit.SECONDS)
                            .build();


                    retrofit = new Retrofit.Builder()
                            .baseUrl(ROOT_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(okHttpClient)
                            .build();
                }
            }
        }
        return retrofit;
    }
}
