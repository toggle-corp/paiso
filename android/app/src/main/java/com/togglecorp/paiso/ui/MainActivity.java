package com.togglecorp.paiso.ui;

import android.Manifest;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.togglecorp.paiso.R;
import com.togglecorp.paiso.db.Contact;
import com.togglecorp.paiso.db.DbHelper;
import com.togglecorp.paiso.db.PaisoTransaction;
import com.togglecorp.paiso.db.TransactionData;
import com.togglecorp.paiso.db.User;
import com.togglecorp.paiso.helpers.AuthUser;
import com.togglecorp.paiso.helpers.PermissionListener;
import com.togglecorp.paiso.helpers.PermissionsManager;
import com.togglecorp.paiso.network.SyncListener;
import com.togglecorp.paiso.network.SyncManager;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SyncListener {
    private final static String TAG = "MainActivity";

    private AuthUser mAuthUser;
    private User mUser;
    private NavigationManager mNavigationManager = new NavigationManager();
    private DbHelper mDbHelper;
    private SyncManager mSyncManager;

    private boolean mContactsRead = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Theme
        String theme = PreferenceManager.getDefaultSharedPreferences(this).getString("theme", "green");
        if (theme.equals("green")) {
            setTheme(R.style.GreenTheme);
        } else {
            setTheme(R.style.RedTheme);
        }

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
        SyncManager.setUser(mUser);
        mSyncManager = SyncManager.get(mDbHelper);

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
        getMenuInflater().inflate(R.menu.menu_main, menu);

        boolean hasPending = false;
        List<PaisoTransaction> allTransactions = PaisoTransaction.getAll(PaisoTransaction.class, mDbHelper);
        for (PaisoTransaction transaction: allTransactions) {
            List<TransactionData> dataList = transaction.getPendingData(mDbHelper);
            if (dataList.size() > 0) {
                hasPending = true;
                break;
            }
        }
        menu.findItem(R.id.pending_transactions).setVisible(hasPending);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mNavigationManager.openDrawer();
                return true;
            case R.id.pending_transactions:
                showNotifications();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showNotifications() {
        startActivity(new Intent(this, PendingTransactionsActivity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults)
    {
        PermissionsManager.handleResult(requestCode, permissions, grantResults);

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
    public void onResume() {
        super.onResume();

        if (mSyncManager != null) {
            mSyncManager.addListener(this);
        }

        boolean phoneVerificationShown = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("phone_verification_shown", false);
        if (!phoneVerificationShown && (mUser.phone == null || mUser.phone.length() == 0)) {
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit().putBoolean("phone_verification_shown", true).apply();
            startActivityForResult(new Intent(this, PhoneVerificationActivity.class), PhoneVerificationActivity.REQUEST_CODE);
            return;
        }

        if (!mContactsRead) {
            mContactsRead = true;
            readContacts();
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
    public void onSync(boolean complete) {
        invalidateOptionsMenu();
    }

    public void readContacts() {
        // Check for contacts-read permission and read the contacts
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                PermissionsManager.check(
                        MainActivity.this, new String[]{ Manifest.permission.READ_CONTACTS },
                        new PermissionListener() {
                            @Override
                            public void onGranted() {
                                try {
                                    Contact.readContacts(mDbHelper);
                                }
                                catch (Exception ignored) {}
                                mSyncManager.requestSync();
                            }
                        }
                );
                return null;
            }
        }.execute();
    }
}
