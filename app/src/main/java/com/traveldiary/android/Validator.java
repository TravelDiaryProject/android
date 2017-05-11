package com.traveldiary.android;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Patterns;
import android.widget.Toast;


public class Validator {

    public static boolean isNetworkAvailable(Context currentContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) currentContext.getSystemService(currentContext.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if(activeNetworkInfo == null){
            Toast.makeText(currentContext, currentContext.getString(R.string.check_network_connection), Toast.LENGTH_SHORT).show();
        }

//        if (activeNetwork != null && activeNetwork.isConnected()) {
//            try {
//                // тест доступности внешнего ресурса
//                URL url = new URL("http://www.google.com/");
//                HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
//                urlc.setRequestProperty("User-Agent", "test");
//                urlc.setRequestProperty("Connection", "close");
//                urlc.setConnectTimeout(1000); // Timeout в секундах
//                urlc.connect();
//                // статус ресурса OK
//                if (urlc.getResponseCode() == 200) {
//                    return true;
//                }
//                // иначе проверка провалилась
//                return false;
//
//            } catch (IOException e) {
//                Log.d("my_tag", "Ошибка проверки подключения к интернету", e);
//                return false;
//            }
//        }
//        return false;

        return activeNetworkInfo != null;
    }

    public static boolean isNameValid( Context currentContext, CharSequence name )
    {
        if( name.toString().isEmpty() )
        {
            Toast.makeText( currentContext, currentContext.getString( R.string.warning_name_with_space ), Toast.LENGTH_LONG ).show();
            return false;
        }

        for (int i = 0; i < name.length(); i++){
            if (Character.isSpaceChar(name.charAt(i))){
                Toast.makeText( currentContext, currentContext.getString( R.string.warning_name_with_space ), Toast.LENGTH_LONG ).show();
                return false;
            }
        }

        return true;
    }

    public static boolean isEmailValid( Context currentContext, CharSequence email )
    {
        if( email.toString().isEmpty() )
        {
            Toast.makeText( currentContext, currentContext.getString( R.string.warning_email_empty ), Toast.LENGTH_LONG ).show();
            return false;
        }

        if( !Patterns.EMAIL_ADDRESS.matcher( email ).matches() )
        {
            Toast.makeText( currentContext, currentContext.getString( R.string.warning_email_invalid ), Toast.LENGTH_LONG ).show();
            return false;
        }

        return true;
    }

    public static boolean isPasswordValid( Context currentContext, CharSequence password )
    {
        if( password.toString().isEmpty() )
        {
            Toast.makeText( currentContext, currentContext.getString( R.string.warning_password_empty ), Toast.LENGTH_LONG ).show();
            return false;
        }

        for (int i = 0; i < password.length(); i++){
            if (Character.isSpaceChar(password.charAt(i))){
                Toast.makeText( currentContext, currentContext.getString( R.string.warning_password_with_space ), Toast.LENGTH_LONG ).show();
                return false;
            }
        }

        return true;
    }
}
