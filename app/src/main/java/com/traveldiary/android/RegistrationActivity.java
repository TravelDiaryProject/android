package com.traveldiary.android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.traveldiary.android.network.CallBack;
import com.traveldiary.android.model.RegistrationResponse;

import retrofit2.Response;

import static com.traveldiary.android.App.network;

public class RegistrationActivity extends AppCompatActivity {

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


                    network.registration(email, password, new CallBack() {
                        @Override
                        public void responseNetwork(Object o) {
                            Response<RegistrationResponse> response = (Response<RegistrationResponse>) o;
                            RegistrationResponse registrationResponse = response.body();

                            TOKEN = registrationResponse.getToken();
                            TOKEN_TO_SEND.append(TOKEN);

                            Log.d("Token", " OK");

                            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void failNetwork(Throwable t) {
                            Log.d("Token", " BAD");
                            badLogOrPass();
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
