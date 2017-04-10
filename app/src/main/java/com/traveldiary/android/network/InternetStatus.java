package com.traveldiary.android.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


public class InternetStatus {

    public void check(final Context context, final CallBack callBack){

        new Thread(new Runnable() {
            @Override
            public void run(){
                if (checkInternet(context)) {
                    Log.d("my_tag", "Есть подключение");
                    callBack.responseNetwork(true);
                } else {
                    Log.d("my_tag", "Нет подключения");
                    callBack.failNetwork(null);
                }
            }
        }).start();

    }

    private boolean checkInternet(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        // проверка подключения
        if (activeNetwork != null && activeNetwork.isConnected()) {
            try {
                // тест доступности внешнего ресурса
                URL url = new URL("http://www.google.com/");
                HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
                urlc.setRequestProperty("User-Agent", "test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1000); // Timeout в секундах
                urlc.connect();
                // статус ресурса OK
                if (urlc.getResponseCode() == 200) {
                    return true;
                }
                // иначе проверка провалилась
                return false;

            } catch (IOException e) {
                Log.d("my_tag", "Ошибка проверки подключения к интернету", e);
                return false;
            }
        }
        return false;
    }
}
