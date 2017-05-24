package com.traveldiary.android;

import android.app.Application;

import com.traveldiary.android.data.Data;
import com.traveldiary.android.data.DataService;
import com.traveldiary.android.network.Network;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends Application {

    public static Network network;
    public static Data data;
    public static DataService dataService;

    @Override
    public void onCreate() {

        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(realmConfiguration);

        data = new Data();
        network = new Network();
        dataService = new DataService();

        super.onCreate();
    }
}
