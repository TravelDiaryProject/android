package com.traveldiary.android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegistrationActivity extends AppCompatActivity {

    private EditText mEditName;
    private EditText mEditEmail;
    private EditText mEditPassword;
    private Button mRegistrationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mEditEmail = (EditText) findViewById(R.id.editEmailRegistration);
        mEditName = (EditText) findViewById(R.id.editNameRegistration);
        mEditPassword = (EditText) findViewById(R.id.editPasswordRegistration);

        mRegistrationButton = (Button) findViewById(R.id.registerButton);
        mRegistrationButton.setOnClickListener(registerBtnClickListener());

    }

    public View.OnClickListener registerBtnClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CharSequence name = mEditName.getText();
                CharSequence email = mEditEmail.getText();
                CharSequence password = mEditPassword.getText();

                if( isRegistrationValuesValid( name, email, password ) ) {
                    Log.d("All info VALID:", name.toString() + " - " + email.toString() + " - " + password.toString());
                    //All info VAlid we will send it so server
                }
            }
        };
    }

    public boolean isRegistrationValuesValid( CharSequence name, CharSequence email, CharSequence password)
    {
        return Validator.isNameValid( this, name )
                && Validator.isEmailValid( this, email )
                && Validator.isPasswordValid( this, password );
    }

}
