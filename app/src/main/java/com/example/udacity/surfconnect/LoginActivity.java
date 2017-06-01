package com.example.udacity.surfconnect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;

public class LoginActivity extends AppCompatActivity {

    public static  int APP_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FontHelper.setCustomTypeface(findViewById(R.id.view_root));

        //check for an existing access token. It is accessible when user logged in. If user is
        // logged in accessToken will not be null.
        AccessToken accessToken = AccountKit.getCurrentAccessToken();
        if(accessToken != null){
            //if previously logged in, proceed to the account activity
            //if null we will continue with login flow
            launchAccountActivity();
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
        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    public void onPhoneLogin(View view){
        onLogin(LoginType.PHONE);
    }

    public void onEmailLogin(View view){
        onLogin(LoginType.EMAIL);
    }

    private void launchAccountActivity() {
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
        finish();
    }

}
