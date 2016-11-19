package com.togglecorp.paiso;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    private AuthUser mAuthUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get logged in user or start Login Activity
        mAuthUser = new AuthUser(this);
        if (mAuthUser.getFbUser() == null) {
            // Not signed in, launch the Login activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Before anything, check for contacts read permission
        getContactsReadPermission();

        // Set active user id to the database
        Database.get().selfId = mAuthUser.getFbUser().getUid();
        Database.get().self = mAuthUser.getUser();
    }

    public void getContactsReadPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
    }



    @Override
    public void onStart() {
        super.onStart();
        Database.get().startSync(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Database.get().stopSync();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults)
    {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getSharedPreferences("com.toggle.paiso.defaults", 0)
                        .edit().putBoolean("CAN_READ_CONTACTS", true).apply();

                Database.get().stopSync();
                Database.get().startSync(this);
            }
        }
    }
}
