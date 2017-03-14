package com.traveldiary.android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.traveldiary.android.Interfaces.TravelDiaryService;
import com.traveldiary.android.essence.RegistrationResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.traveldiary.android.Constans.ROOT_URL;

public class RegistrationActivity extends AppCompatActivity {

    private TravelDiaryService travelDiaryService;
    //private EditText mEditName;
    private EditText mEditEmail;
    private EditText mEditPassword;
    private Button mRegistrationButton;

    private String TOKEN;
    public static StringBuilder TOKEN_TO_SEND = new StringBuilder("Bearer ");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mEditEmail = (EditText) findViewById(R.id.editEmailRegistration);
        //mEditName = (EditText) findViewById(R.id.editNameRegistration);
        mEditPassword = (EditText) findViewById(R.id.editPasswordRegistration);

        mRegistrationButton = (Button) findViewById(R.id.registerButton);
        mRegistrationButton.setOnClickListener(registerBtnClickListener());

    }

    public View.OnClickListener registerBtnClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = String.valueOf(mEditEmail.getText());
                String password = String.valueOf(mEditPassword.getText());

                if( isRegistrationValuesValid(email, password)) {
                    Log.d("All info VALID:", /*name.toString() + " - " +*/ email + " - " + password);
                    //All info VAlid we will send it so server

                    travelDiaryService = Api.getTravelDiaryService();

                    travelDiaryService.registration(email, password).enqueue(new Callback<RegistrationResponse>() {
                        @Override
                        public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {

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
                        public void onFailure(Call<RegistrationResponse> call, Throwable t) {

                            Log.d("Token", t.getMessage());

                        }
                    });

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

}
