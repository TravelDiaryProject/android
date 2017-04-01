package com.traveldiary.android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.traveldiary.android.network.CallBackInterface;
import com.traveldiary.android.model.City;
import com.traveldiary.android.model.Place;
import com.traveldiary.android.model.RegistrationResponse;
import com.traveldiary.android.model.Trip;
import com.traveldiary.android.network.Network;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity implements CallBackInterface{

    private EditText mEditEmail;
    private EditText mEditPassword;
    private Button mRegistrationButton;

    private Network network;

    private String TOKEN;
    public static StringBuilder TOKEN_TO_SEND = new StringBuilder("Bearer ");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mEditEmail = (EditText) findViewById(R.id.editEmailRegistration);
        mEditPassword = (EditText) findViewById(R.id.editPasswordRegistration);

        mRegistrationButton = (Button) findViewById(R.id.registerButton);
        mRegistrationButton.setOnClickListener(registerBtnClickListener());

        network = new Network(this);

    }

    public View.OnClickListener registerBtnClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = String.valueOf(mEditEmail.getText());
                String password = String.valueOf(mEditPassword.getText());

                if( isRegistrationValuesValid(email, password)) {

                    network.registration(email, password);

                }
            }
        };
    }

    public void badLogOrPass(){
        Toast.makeText(this, "BAD LOG OR PASS", Toast.LENGTH_LONG).show();
    }

    public boolean isRegistrationValuesValid( String email, String password)
    {
        return Validator.isEmailValid( this, email )
                && Validator.isPasswordValid( this, password );
    }


    @Override
    public void registration(Response<RegistrationResponse> response) {

        if (response.body() != null) {
            RegistrationResponse registrationResponse = response.body();

            TOKEN = registrationResponse.getToken();
            TOKEN_TO_SEND.append(TOKEN);

            Log.d("Token", " OK");

            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
            startActivity(intent);

        } else {
            Log.d("Token", " BAD");
            badLogOrPass();
        }
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
    public void createTrip(String info) {

    }

    @Override
    public void signIn(Response<RegistrationResponse> response) {

    }
}
