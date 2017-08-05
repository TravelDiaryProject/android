package com.traveldiary.android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
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


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.traveldiary.android.R;
import com.traveldiary.android.Validator;
import com.traveldiary.android.callback.CallbackRegistration;
import com.traveldiary.android.model.RegistrationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.traveldiary.android.App.dataService;
import static com.traveldiary.android.Constans.APP_PREFERENCES;
import static com.traveldiary.android.Constans.APP_PREFERENCES_EMAIL;
import static com.traveldiary.android.Constans.APP_PREFERENCES_TOKEN;
import static com.traveldiary.android.Constans.TOKEN_CONST;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, FacebookCallback<LoginResult> {

    private EditText mEditEmail;
    private EditText mEditLoginPassword;
    private Button mLoginButton;
    private ProgressBar mProgressBar;
    private StringBuilder tokenBuilder;
    private Context mContext;
    private SharedPreferences mSharedPreferences;

    private GoogleApiClient mGoogleApiClient;
    private SignInButton mGoogleSignButton;

    private final int GOOGLE_CODE = 2;
    private final int FACEBOOK_CODE = 1;


    private static final String TAG = "LOGIN";

    private LoginButton mFacebookLoginButton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();

        mFacebookLoginButton = (LoginButton) findViewById(R.id.facebook_sign_in_button);
        mFacebookLoginButton.setReadPermissions("user_friends");
        mFacebookLoginButton.setReadPermissions("public_profile");
        mFacebookLoginButton.setReadPermissions("email");

        //mFacebookLoginButton.setReadPermissions("email");

        mFacebookLoginButton.registerCallback(callbackManager, this);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1099242893912-e82v8uf90foqhej7bmtdskogcrtcf9eq.apps.googleusercontent.com")
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleSignButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        mGoogleSignButton.setSize(SignInButton.SIZE_STANDARD);
        mGoogleSignButton.setOnClickListener(this);

        mContext = this;

        mProgressBar = (ProgressBar) findViewById(R.id.login_progress);

        mEditEmail = (EditText) findViewById(R.id.editEmail);
        mEditLoginPassword = (EditText) findViewById(R.id.editLoginPassword);

        mLoginButton = (Button) findViewById(R.id.loginButton);
        mLoginButton.setOnClickListener(this);

        makeRegistrationLink();
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

        dataService.signIn(email, password, new CallbackRegistration() {
            @Override
            public void response(RegistrationResponse registrationResponse) {
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
            public void fail(Throwable t) {
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
                mLoginButton.setClickable(true);
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.loginButton:
                emailLogin();
                break;

            case R.id.google_sign_in_button:
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, GOOGLE_CODE);
                break;

        }
    }

    private void emailLogin(){

        Log.d(TAG, mEditEmail.getText() + " - " + mEditLoginPassword);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case FACEBOOK_CODE:
                callbackManager.onActivityResult(requestCode, resultCode, data);
                break;
            case GOOGLE_CODE:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                Log.d(TAG, "- " + result.getStatus().getStatusCode());
                handleSignInResult(result);
                break;
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess() );
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            String idToken = acct.getIdToken();

            Log.d(TAG, "Google info: " + acct.getEmail() + " - idToken = " + idToken + " - " + acct.getFamilyName() + " - " + acct.getGivenName() + " - " + acct.getPhotoUrl());

        } else {
            // Signed out, show unauthenticated UI.
        }
    }


    /*Facebook callback*/
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "fail" + connectionResult.toString());
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        Log.d(TAG, "facebook success = " + loginResult.toString());

        AccessToken access_token = loginResult.getAccessToken();

        GraphRequestBatch batch = new GraphRequestBatch(
                GraphRequest.newMeRequest(
                        access_token,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject jsonObject,
                                    GraphResponse response) {
                                // Application code for user
                                try {
                                    System.out.println(" ------------------ " + jsonObject.getString("email"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }),
                GraphRequest.newMyFriendsRequest(
                        access_token,
                        new GraphRequest.GraphJSONArrayCallback() {
                            @Override
                            public void onCompleted(
                                    JSONArray jsonArray,
                                    GraphResponse response) {

                                System.out.println(" ================== " + jsonArray.toString());

                                // Application code for users friends
                            }
                        })
        );
        batch.addCallback(new GraphRequestBatch.Callback() {
            @Override
            public void onBatchCompleted(GraphRequestBatch graphRequests) {
                // Application code for when the batch finishes
            }
        });
        batch.executeAsync();




    }

    @Override
    public void onCancel() {
        Log.d(TAG, "facebook cancel ");
    }

    @Override
    public void onError(FacebookException error) {
        Log.d(TAG, "facebook error = " + error.toString());
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
