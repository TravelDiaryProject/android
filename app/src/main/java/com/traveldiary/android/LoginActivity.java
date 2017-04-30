package com.traveldiary.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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


import com.traveldiary.android.data.DataService;
import com.traveldiary.android.network.CallBack;
import com.traveldiary.android.model.RegistrationResponse;

import retrofit2.Response;

import static com.traveldiary.android.App.network;
import static com.traveldiary.android.Constans.APP_PREFERENCES;
import static com.traveldiary.android.Constans.APP_PREFERENCES_EMAIL;
import static com.traveldiary.android.Constans.APP_PREFERENCES_TOKEN;
import static com.traveldiary.android.Constans.TOKEN_CONST;

public class LoginActivity extends AppCompatActivity {

    private EditText mEditEmail;
    private EditText mEditLoginPassword;
    private Button mLoginButton;

    private ProgressBar mProgressBar;

    private StringBuilder tokenBuilder;
    private Context mContext;

    private SharedPreferences mSharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;

        mProgressBar = (ProgressBar) findViewById(R.id.login_progress);

        mEditEmail = (EditText) findViewById(R.id.editEmail);
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

                String email = String.valueOf(mEditEmail.getText());
                String password = String.valueOf(mEditLoginPassword.getText());

                if (isLoginValuesValid(email, password) && isNetworkValid()) {
                    sign(email, password);
                } else {
                    mProgressBar.setVisibility(View.GONE);
                    mLoginButton.setClickable(true);
                }
            }
        };
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


    public void sign(final String email, String password){
        DataService dataService = new DataService();
        dataService.login(email, password, new CallBack() {
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
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
                mLoginButton.setClickable(true);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
