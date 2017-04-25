package com.traveldiary.android;

import android.app.Application;

import com.traveldiary.android.network.Network;

public class App extends Application {

    public static Network network;

    @Override
    public void onCreate() {
        network = new Network();
        super.onCreate();
    }
}
