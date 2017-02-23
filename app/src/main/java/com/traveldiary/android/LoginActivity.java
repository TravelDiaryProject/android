package com.traveldiary.android;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.traveldiary.android.Interfaces.TravelDiaryService;
import com.traveldiary.android.essence.RegistrationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.traveldiary.android.Constans.ROOT_URL;

public class LoginActivity extends AppCompatActivity {

    private EditText mEditLoginName;
    private EditText mEditLoginPassword;
    private Button mLoginButton;

    private ProgressBar mProgressBar;

    private String TOKEN;
    public static StringBuilder TOKEN_TO_SEND = new StringBuilder("Bearer ");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mProgressBar = (ProgressBar) findViewById(R.id.login_progress);

        mEditLoginName = (EditText) findViewById(R.id.editLoginName);
        mEditLoginPassword = (EditText) findViewById(R.id.editLoginPassword);

        mLoginButton = (Button) findViewById(R.id.loginButton);
        mLoginButton.setOnClickListener( buttonClickListener() );

        makeRegistrationLink();
    }

    public View.OnClickListener buttonClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mLoginButton.setClickable(false);
                mProgressBar.setVisibility(View.VISIBLE);

                String name = String.valueOf(mEditLoginName.getText());
                String password = String.valueOf(mEditLoginPassword.getText());

                if (isLoginValuesValid(name, password) && isNetworkValid()) {

                    Log.d("LOG and PASS are", "VALID");

                    TravelDiaryService travelDiaryService;

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(ROOT_URL).addConverterFactory(GsonConverterFactory.create()).build();
                    travelDiaryService = retrofit.create(TravelDiaryService.class);

                    travelDiaryService.getToken(name, password).enqueue(new Callback<RegistrationResponse>() {
                        @Override
                        public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {

                            if (response.body() != null) {
                                RegistrationResponse registrationResponse = response.body();

                                TOKEN = registrationResponse.getToken();
                                TOKEN_TO_SEND.append(TOKEN);

                                Log.d("Token", " OK");

                                mProgressBar.setVisibility(View.GONE);

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);

                            } else {
                                Log.d("Token", " BAD");
                                mProgressBar.setVisibility(View.GONE);
                                mLoginButton.setClickable(true);
                                badLogOrPass();
                            }
                        }


                        @Override
                        public void onFailure(Call<RegistrationResponse> call, Throwable t) {

                            mProgressBar.setVisibility(View.GONE);
                            mLoginButton.setClickable(true);
                            Log.d("Token", t.getMessage());

                        }
                    });

                } else {
                    mProgressBar.setVisibility(View.GONE);
                    mLoginButton.setClickable(true);
                }
            }
        };
    }

    public void badLogOrPass(){
        Toast.makeText(this, "BAD LOG OR PASS", Toast.LENGTH_LONG).show();
    }

    private boolean isNetworkValid() {
        return Validator.isNetworkAvailable(this);
    }

    public boolean isLoginValuesValid( CharSequence name, CharSequence password )
    {
        return Validator.isNameValid( this, name ) && Validator.isPasswordValid( this, password );
    }

    public void makeRegistrationLink()
    {
        SpannableString registrationPrompt = new SpannableString( getString( R.string.register_prompt ) );

        ClickableSpan clickableSpan = new ClickableSpan()
        {
            @Override
            public void onClick( View widget )
            {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        };

        String registrationText = getString( R.string.register_link );
        int linkStartIndex = registrationPrompt.toString().indexOf( registrationText );
        int linkEndIndex = linkStartIndex + registrationText.length();
        registrationPrompt.setSpan( clickableSpan, linkStartIndex, linkEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );

        TextView registerPromptView = (TextView) findViewById( R.id.registerPromptText );
        registerPromptView.setText( registrationPrompt );
        registerPromptView.setMovementMethod( LinkMovementMethod.getInstance() );
    }
}
