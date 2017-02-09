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
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private EditText mEditEmail;
    private EditText mEditPassword;
    private Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEditEmail = (EditText) findViewById(R.id.editEmail);
        mEditPassword = (EditText) findViewById(R.id.editPassword);

        mLoginButton = (Button) findViewById(R.id.loginButton);
        mLoginButton.setOnClickListener( buttonClickListener() );


        makeRegistrationLink();
    }

    public View.OnClickListener buttonClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CharSequence email = mEditEmail.getText();
                CharSequence password = mEditPassword.getText();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

                if( isLoginValuesValid( email, password ) ) {
                    Log.d("LOG and PASS are VALID:", email.toString() + password.toString());
                    //emeil and password are Valid! we can send they to server
                }
            }
        };
    }

    public boolean isLoginValuesValid( CharSequence email, CharSequence password )
    {
        return Validator.isEmailValid( this, email ) && Validator.isPasswordValid( this, password );
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
