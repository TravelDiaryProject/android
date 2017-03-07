package com.traveldiary.android;

import com.traveldiary.android.Interfaces.TravelDiaryService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.traveldiary.android.Constans.ROOT_URL;

/**
 * Created by Cyborg on 3/7/2017.
 */

public class Api {

    private static Api instance = null;

    private TravelDiaryService travelDiaryService;

    public static Api getInstance(){
        if (instance == null) {
            synchronized (Api.class) {
                if (instance == null) {
                    instance = new Api();
                }
            }
        }
        return instance;
    }

    private Api(){
        buildRetrofit();
    }

    private void buildRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();

        this.travelDiaryService = retrofit.create(TravelDiaryService.class);
    }
}
