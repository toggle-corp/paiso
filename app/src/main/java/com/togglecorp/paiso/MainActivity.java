package com.togglecorp.paiso;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    private AuthUser mAuthUser;

    private DrawerLayout mDrawerLayout;
    private NavigationDrawerAdapter mNavigationAdapter;

    DashboardFragment mDashboardFragment = new DashboardFragment();

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

        // Before anything, check for contacts-read permission
        getContactsReadPermission();

        // Set active user id to the database
        Database.get().selfId = mAuthUser.getFbUser().getUid();
        Database.get().self = mAuthUser.getUser();


        // Navigation item menu
        ArrayList<NavigationDrawerAdapter.Item> navItems = new ArrayList<>();
        navItems.add(new NavigationDrawerAdapter.Item("Dashboard",
                ContextCompat.getDrawable(this, R.drawable.ic_home)));
//        navItems.add(new NavigationDrawerAdapter.Item("Transactions",
//                ContextCompat.getDrawable(this, R.drawable.ic_contents)));
//        navItems.add(new NavigationDrawerAdapter.Item("People",
//                ContextCompat.getDrawable(this, R.drawable.ic_people)));

        mNavigationAdapter = new NavigationDrawerAdapter(this, navItems, mNavigationListener);
        RecyclerView navigationRecyclerView =
                (RecyclerView)findViewById(R.id.navigation_drawer_recycler_view);
        navigationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        navigationRecyclerView.setAdapter(mNavigationAdapter);
        mNavigationAdapter.notifyDataSetChanged();

        // Navigation header
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        View header = mDrawerLayout.findViewById(R.id.navigation_drawer_header);
        ((TextView)header.findViewById(R.id.display_name))
                .setText(mAuthUser.getFbUser().getDisplayName());
        ((TextView)header.findViewById(R.id.email))
                .setText(mAuthUser.getFbUser().getEmail());
        Picasso.with(this)
                .load(mAuthUser.getFbUser().getPhotoUrl())
                .into((CircleImageView)header.findViewById(R.id.avatar));

        // The toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        // The hamburger icon
        ActionBarDrawerToggle drawerListener = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
        };
        mDrawerLayout.addDrawerListener(drawerListener);
        drawerListener.syncState();


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame, mDashboardFragment).commit();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.add_transaction:
                startActivity(new Intent(this, AddTransactionActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public ChangeListener mNavigationListener = new ChangeListener() {
        @Override
        public void onChange(int index) {
            switch (index) {
                case 0:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame, mDashboardFragment).commit();
                    break;
            }
        }
    };

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
