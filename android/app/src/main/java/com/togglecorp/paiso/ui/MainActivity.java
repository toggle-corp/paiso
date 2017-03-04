package com.togglecorp.paiso.ui;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.togglecorp.paiso.helpers.AuthUser;
import com.togglecorp.paiso.R;
import com.togglecorp.paiso.helpers.NavigationManager;
import com.togglecorp.paiso.helpers.PermissionListener;
import com.togglecorp.paiso.helpers.PermissionsManager;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    private AuthUser mAuthUser;
    private NavigationManager mNavigationManager = new NavigationManager();

    private static boolean mPhoneVerificationShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Get logged in user or start Login Activity
        mAuthUser = new AuthUser(this);
        if (mAuthUser.getUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Before anything, check for contacts-read permission
        PermissionsManager.check(this, new String[]{Manifest.permission.READ_CONTACTS},
                new PermissionListener() {
                    @Override
                    public void onGranted() {
                        // TODO: Read in contacts
                    }
                });

        // The toolbar and navigation
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        mNavigationManager.init(this, toolbar, mAuthUser);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mNavigationManager.openDrawer();
                return true;
            case R.id.add_transaction:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults)
    {
        PermissionsManager.handleResult(requestCode, permissions, grantResults);

    }

    @Override
    public void onStart() {
        super.onStart();

        if (!mPhoneVerificationShown) {
            mPhoneVerificationShown = true;

            // Check if we have registered phone number
            // Else ask for one and verify
            // TODO: Check existing
            startActivity(new Intent(this, PhoneVerificationActivity.class));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}
