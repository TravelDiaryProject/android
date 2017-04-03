package com.traveldiary.android;

import android.content.Intent;
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


import com.traveldiary.android.network.CallBackInterface;
import com.traveldiary.android.network.TravelDiaryService;
import com.traveldiary.android.model.City;
import com.traveldiary.android.model.Place;
import com.traveldiary.android.model.RegistrationResponse;
import com.traveldiary.android.model.Trip;
import com.traveldiary.android.network.Network;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

import static com.traveldiary.android.App.network;

public class LoginActivity extends AppCompatActivity implements CallBackInterface {

    private TravelDiaryService travelDiaryService;
    private EditText mEditEmail;
    private EditText mEditLoginPassword;
    private Button mLoginButton;

    private ProgressBar mProgressBar;

    private String TOKEN;
    public static StringBuilder TOKEN_TO_SEND;

    //private SharedPreferences mLoginSetting;
    //public static final String SAVED_TOKEN = "saved_token";
    //public static final String APP_PREFERENCES = "mLoginSetting";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mProgressBar = (ProgressBar) findViewById(R.id.login_progress);

        mEditEmail = (EditText) findViewById(R.id.editEmail);
        mEditLoginPassword = (EditText) findViewById(R.id.editLoginPassword);

        mLoginButton = (Button) findViewById(R.id.loginButton);
        mLoginButton.setOnClickListener( buttonClickListener() );

        //network = new Network(this);
        network.setCallBackInterface(this);

        makeRegistrationLink();
    }

    public View.OnClickListener buttonClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mLoginButton.setClickable(false);
                mProgressBar.setVisibility(View.VISIBLE);

                String email = String.valueOf(mEditEmail.getText());
                String password = String.valueOf(mEditLoginPassword.getText());

                if (isLoginValuesValid(email, password) && isNetworkValid()) {
                    Log.d("LOG and PASS are", "VALID");
                    network.signIn(email, password);

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
        return Validator.isEmailValid( this, name ) && Validator.isPasswordValid( this, password );
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


    @Override
    public void signIn(Response<RegistrationResponse> response) {
        if (response.body() != null) {
            RegistrationResponse registrationResponse = response.body();

            TOKEN = registrationResponse.getToken();
            TOKEN_TO_SEND = new StringBuilder("Bearer ");
            TOKEN_TO_SEND.append(TOKEN);

            //SharedPreferences.Editor editor = mLoginSetting.edit();
            //editor.putString(SAVED_TOKEN, TOKEN);
            //editor.apply();

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
    public void registration(Response<RegistrationResponse> response) {

    }

    @Override
    public void uploadPlace(Response<ResponseBody> response) {

    }

    @Override
    public void getAllCities(List<City> allCities) {

    }

    @Override
    public void getAllPlaces(List<Place> allPlaces) {

    }

    @Override
    public void getMyPlaces(List<Place> myPlaces) {

    }

    @Override
    public void getPlacesByTrip(List<Place> placesByTrip) {

    }

    @Override
    public void getPlacesByCity(List<Place> placesByCity) {

    }

    @Override
    public void getAllTrips(List<Trip> allTrips) {

    }

    @Override
    public void getMyTrips(List<Trip> myTrips) {

    }

    @Override
    public void getTripsByCity(List<Trip> tripsByCity) {

    }

    @Override
    public void getTripById(Trip trip) {

    }

    @Override
    public void createTrip(String info) {

    }
}
