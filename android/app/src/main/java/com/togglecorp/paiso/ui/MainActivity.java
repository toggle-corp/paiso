package com.togglecorp.paiso.ui;

import android.Manifest;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.togglecorp.paiso.db.Contact;
import com.togglecorp.paiso.db.DbHelper;
import com.togglecorp.paiso.db.User;
import com.togglecorp.paiso.helpers.AuthUser;
import com.togglecorp.paiso.R;
import com.togglecorp.paiso.helpers.NavigationManager;
import com.togglecorp.paiso.helpers.PermissionListener;
import com.togglecorp.paiso.helpers.PermissionsManager;
import com.togglecorp.paiso.network.SyncListener;
import com.togglecorp.paiso.network.SyncManager;

public class MainActivity extends AppCompatActivity implements SyncListener {
    private final static String TAG = "MainActivity";

    private AuthUser mAuthUser;
    private User mUser;
    private NavigationManager mNavigationManager = new NavigationManager();
    private DbHelper mDbHelper;
    private SyncManager mSyncManager;

    private boolean mPhoneVerificationShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Get logged in user or start Login Activity
        mAuthUser = new AuthUser(this);
        if (mAuthUser.getFbUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        mDbHelper = new DbHelper(this);
        mUser = mAuthUser.getUser(mDbHelper);
        mSyncManager = new SyncManager(mDbHelper, mUser);

        // Check for contacts-read permission and read the contacts
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                PermissionsManager.check(
                        MainActivity.this, new String[]{ Manifest.permission.READ_CONTACTS },
                        new PermissionListener() {
                            @Override
                            public void onGranted() {
                                Contact.readContacts(mDbHelper);
                                mSyncManager.requestSync();
                            }
                        }
                );
                return null;
            }
        }.execute();

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
        // Inflate the menu; this adds items transactionTo the action bar if it is present.
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


        if (!mPhoneVerificationShown && (mUser.phone == null || mUser.phone.length() == 0)) {
            mPhoneVerificationShown = true;
            startActivityForResult(new Intent(this, PhoneVerificationActivity.class), PhoneVerificationActivity.REQUEST_CODE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onResume() {
        super.onResume();

        if (mSyncManager != null) {
            mSyncManager.addListener(this);
            mSyncManager.requestSync();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mSyncManager != null) {
            mSyncManager.removeListener(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PhoneVerificationActivity.REQUEST_CODE) {
            if (resultCode == RESULT_OK && data.hasExtra("phone") && data.getStringExtra("phone").length() > 0) {

                if (mUser != null) {
                    mUser.phone = data.getStringExtra("phone");
                    mUser.modified = true;
                }

                if (mSyncManager != null) {
                    mSyncManager.requestSync();
                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onSync() {
        Log.d(TAG, "Synchronised");
    }
}
