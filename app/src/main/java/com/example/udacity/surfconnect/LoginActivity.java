package com.example.udacity.surfconnect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class LoginActivity extends AppCompatActivity {

    public static  int APP_REQUEST_CODE = 1;
    LoginButton loginButton;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FontHelper.setCustomTypeface(findViewById(R.id.view_root));

        loginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        loginButton.setReadPermissions("email");

        //Login Button Callback registration
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                //display error
                String toastMessage = error.getMessage();
                Toast.makeText(LoginActivity.this, toastMessage,Toast.LENGTH_LONG).show();
            }
        });

        //check for an existing access token. It is accessible when user logged in. If user is
        // logged in accessToken will not be null.
        AccessToken accessToken = AccountKit.getCurrentAccessToken();
        if(accessToken != null){
            //if previously logged in, proceed to the account activity
            //if null we will continue with login flow
            launchAccountActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //forward result to the callback manager for login button
        callbackManager.onActivityResult(requestCode, resultCode,data);

        //make sure that request code is the appropriate value by comparing it with the pass in
        // request code with app request code we use to launch the account kit activity.
        // if they are't same let the function continue until it finishes.
        if(requestCode == APP_REQUEST_CODE){
            //if it same we extract the login result from the intent ans continue the login flow.
            AccountKitLoginResult loginResult =
                    data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            //we want to handle the both cases where login success ans fails.
            if(loginResult.getError() != null){
                //if error in login result exists, display a toast with error message to the user.
                String toastMessage = loginResult.getError().getErrorType().getMessage();
                Toast.makeText(this, "2"+toastMessage, Toast.LENGTH_LONG).show();
            }else if(loginResult.getAccessToken() != null){
                //on successful login, proceed to the account activity.
                launchAccountActivity();
            }
        }
    }

    //This function will implement the token request
    private void onLogin(final LoginType loginType){
        //create intent for the Account Kit Activity
        final Intent intent = new Intent(this, AccountKitActivity.class);

        //configure login type and response type. ResponseType.TOKEN because we are using
        // client access token and ResponseType.CODE for authorization code
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(loginType,
                        AccountKitActivity.ResponseType.TOKEN);

        //build configuration then package it in our intent as an extra and launch the
        // AccountKitActivity
        final AccountKitConfiguration configuration = configurationBuilder.build();

        //launch the Account Kit Activity
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configuration);
        startActivityForResult(intent, APP_REQUEST_CODE);//when we request an authorisation token we
        //launch the account kit activity by calling this method(startActivityForResult), that allows
        //us to track the success of the login view onActivityResult().
    }

    public void onPhoneLogin(View view){
        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        logger.logEvent("onPhoneLogin");
        onLogin(LoginType.PHONE);
    }

    public void onEmailLogin(View view){
        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        logger.logEvent("onEmailLogin");
        onLogin(LoginType.EMAIL);
    }

    private void launchAccountActivity() {
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
        finish();
    }

}
