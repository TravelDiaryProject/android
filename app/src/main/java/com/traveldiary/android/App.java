package com.traveldiary.android;

import android.app.Application;

import com.traveldiary.android.network.Network;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends Application {

    public static Network network;

    @Override
    public void onCreate() {

        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(realmConfiguration);

        network = new Network();

        super.onCreate();
    }
}
