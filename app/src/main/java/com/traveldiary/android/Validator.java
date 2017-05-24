package com.traveldiary.android;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.traveldiary.android.callback.SimpleCallBack;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;


public class Validator {

    public static void check(final Context context, final SimpleCallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (checkInternet(context)) {
                    callBack.response(true);

                } else {
                    callBack.fail(null);
                }
            }
        }).start();
    }

    private static boolean checkInternet(Context context) {

        ConnectivityManager
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm.getActiveNetworkInfo() != null) {
            try {
                InetAddress ipAddr = InetAddress.getByName("google.com");
                return !ipAddr.equals("");

            } catch (IOException e) {
                return false;
            }
        }
        return false;

    }

    public static boolean isNetworkAvailable(Context currentContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) currentContext.getSystemService(currentContext.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

//        if(activeNetworkInfo == null){
//            Toast.makeText(currentContext, currentContext.getString(R.string.check_network_connection), Toast.LENGTH_SHORT).show();
//        }
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
