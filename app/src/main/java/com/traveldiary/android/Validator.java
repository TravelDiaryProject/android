package com.traveldiary.android;

import android.content.Context;
import android.util.Patterns;
import android.widget.Toast;

/**
 * Created by Cyborg on 2/9/2017.
 */

public class Validator {

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
