package com.example.udacity.surfconnect;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.AccessToken;
import com.facebook.ProfileTracker;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.login.LoginManager;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Locale;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;

public class AccountActivity extends AppCompatActivity {

    ProfileTracker profileTracker;
    ImageView profilePic;
    TextView id;
    TextView infoLabel;
    TextView info;
    Button family, tagged_place, tagged_photo, uploaded_photo, git_hub;
    public static final String EXTRA_MESSAGE = "com.example.udacity.surfconnect";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        FontHelper.setCustomTypeface(findViewById(R.id.view_root));

        id = (TextView) findViewById(R.id.id);
        infoLabel = (TextView) findViewById(R.id.info_label);
        info = (TextView) findViewById(R.id.info);
        profilePic = (ImageView) findViewById(R.id.profile_image);
        family = (Button) findViewById(R.id.family);
        tagged_photo = (Button) findViewById(R.id.tagged_photos);
        tagged_place = (Button) findViewById(R.id.tagged_places);
        uploaded_photo = (Button) findViewById(R.id.uploaded_photos);
        git_hub = (Button) findViewById(R.id.git_hub);

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                if (currentProfile != null) {
                    displayProfileInfo(currentProfile);
                }
            }
        };

        git_hub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this,DisplayActivity.class);
                String name = git_hub.getText().toString();
                intent.putExtra(EXTRA_MESSAGE,name);
                startActivity(intent);
            }
        });

        tagged_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this,DisplayActivity.class);
                String name = tagged_photo.getText().toString();
                intent.putExtra(EXTRA_MESSAGE,name);
                startActivity(intent);
            }
        });

        tagged_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this,DisplayActivity.class);
                String name = tagged_place.getText().toString();
                intent.putExtra(EXTRA_MESSAGE,name);
                startActivity(intent);
            }
        });

        uploaded_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this,DisplayActivity.class);
                String name = uploaded_photo.getText().toString();
                intent.putExtra(EXTRA_MESSAGE,name);
                startActivity(intent);
            }
        });

        family.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this,DisplayActivity.class);
                String name = family.getText().toString();
                intent.putExtra(EXTRA_MESSAGE,name);
                startActivity(intent);
            }
        });

        if (AccessToken.getCurrentAccessToken() != null) {
            git_hub.setVisibility(View.INVISIBLE);
            // If there is an access token then Login Button was used
            // Check if the profile has already been fetched
            Profile currentProfile = Profile.getCurrentProfile();
            if (currentProfile != null) {
                displayProfileInfo(currentProfile);
            } else {
                // Fetch the profile, which will trigger the onCurrentProfileChanged receiver
                Profile.fetchProfileForCurrentAccessToken();
            }
        } else {
            family.setVisibility(View.INVISIBLE);
            tagged_place.setVisibility(View.INVISIBLE);
            tagged_photo.setVisibility(View.INVISIBLE);
            uploaded_photo.setVisibility(View.INVISIBLE);
            //once we are logged in via an account kit token, we can grab account information that we
            // might be interested in such as account id, phone number or email. We can do this with
            // call to getCurrentAccount(), in this we pass in anonymous inner class AccountKitCallback
            // and make sure return type is account.
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(Account account) {
                    //pass in account to grab all the information.
                    //Get Account Kit ID
                    String accountKitId = account.getId();
                    id.setText(accountKitId);

                    //Get Phone number and the check is the phone number is null.
                    PhoneNumber phonenumber = account.getPhoneNumber();
                    if (account.getPhoneNumber() != null) {
                        //if the phone number is available, display it.
                        String formattedPhoneNumber = formatPhoneNumber(phonenumber.toString());
                        info.setText(formattedPhoneNumber);
                        infoLabel.setText(R.string.phone_label);
                    } else {
                        //if the email address is available, display it.
                        String emailString = account.getEmail();
                        info.setText(emailString);
                        infoLabel.setText(R.string.email_label);
                    }
                }

                @Override
                public void onError(AccountKitError accountKitError) {
                    //display error
                    String toastMessage = accountKitError.getErrorType().getMessage();
                    Toast.makeText(AccountActivity.this, "1" + toastMessage, Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //unregister the profile tracker receiver
        profileTracker.stopTracking();
    }

    public void onLogout(View view) {
        //logout of Account kit.
        AccountKit.logOut();
        //logout of login button
        LoginManager.getInstance().logOut();
        //next start new login activity, so that it take back to login window.
        launchLoginActivity();
    }

    private void displayProfileInfo(Profile profile) {
        // get Profile ID
        String profileId = profile.getId();
        id.setText(profileId);

        // display the Profile name
        String name = profile.getName();
        info.setText(name);
        infoLabel.setText(R.string.name_label);

        // display the profile picture
        Uri profilePicUri = profile.getProfilePictureUri(100, 100);
        displayProfilePic(profilePicUri);
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private String formatPhoneNumber(String phoneNumber) {
        // helper method to format the phone number for display
        try {
            PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber pn = pnu.parse(phoneNumber, Locale.getDefault().getCountry());
            phoneNumber = pnu.format(pn, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
        return phoneNumber;
    }

    private void displayProfilePic(Uri uri) {
        // helper method to load the profile pic in a circular image view
        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(30)
                .oval(false)
                .build();
        Picasso.with(AccountActivity.this)
                .load(uri)
                .transform(transformation)
                .into(profilePic);
    }
}
