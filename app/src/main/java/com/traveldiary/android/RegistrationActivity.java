package com.traveldiary.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.traveldiary.android.data.DataService;
import com.traveldiary.android.network.CallBack;
import com.traveldiary.android.model.RegistrationResponse;

import retrofit2.Response;

import static com.traveldiary.android.App.network;
import static com.traveldiary.android.Constans.APP_PREFERENCES;
import static com.traveldiary.android.Constans.APP_PREFERENCES_EMAIL;
import static com.traveldiary.android.Constans.APP_PREFERENCES_TOKEN;
import static com.traveldiary.android.Constans.TOKEN_CONST;

public class RegistrationActivity extends AppCompatActivity {

    private EditText mEditEmail;
    private EditText mEditPassword;
    private Button mRegistrationButton;

    private ProgressBar mProgressBar;

    private StringBuilder tokenBuilder;
    private SharedPreferences mSharedPreferences;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mContext = this;

        mProgressBar = (ProgressBar) findViewById(R.id.registration_progress);

        mEditEmail = (EditText) findViewById(R.id.editEmailRegistration);
        mEditPassword = (EditText) findViewById(R.id.editPasswordRegistration);

        mRegistrationButton = (Button) findViewById(R.id.registerButton);
        mRegistrationButton.setOnClickListener(registerBtnClickListener());

    }

    public View.OnClickListener registerBtnClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mRegistrationButton.setClickable(false);
                mProgressBar.setVisibility(View.VISIBLE);

                String email = String.valueOf(mEditEmail.getText());
                String password = String.valueOf(mEditPassword.getText());

                if( isRegistrationValuesValid(email, password)) {
                    registration(email, password);
                }else {
                    mProgressBar.setVisibility(View.GONE);
                    mRegistrationButton.setClickable(true);
                }
            }
        };
    }

    public void registration(final String email, String password){
        DataService dataService = new DataService();
        dataService.registration(email, password, new CallBack() {
            @Override
            public void responseNetwork(Object o) {
                Response<RegistrationResponse> response = (Response<RegistrationResponse>) o;
                RegistrationResponse registrationResponse = response.body();

                tokenBuilder = new StringBuilder("Bearer ");
                tokenBuilder.append(registrationResponse.getToken());

                TOKEN_CONST = tokenBuilder.toString();

                mSharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString(APP_PREFERENCES_TOKEN, TOKEN_CONST);
                editor.putString(APP_PREFERENCES_EMAIL, email);
                editor.apply();

                mProgressBar.setVisibility(View.GONE);

                onBackPressed();
            }

            @Override
            public void failNetwork(Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                mRegistrationButton.setClickable(true);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean isRegistrationValuesValid( String email, String password)
    {
        return Validator.isEmailValid( this, email )
                && Validator.isPasswordValid( this, password );
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
