package com.togglecorp.paiso.helpers;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.togglecorp.paiso.R;
import com.togglecorp.paiso.adapters.NavigationChangeListener;
import com.togglecorp.paiso.adapters.NavigationDrawerAdapter;
import com.togglecorp.paiso.ui.DashboardFragment;
import com.togglecorp.paiso.ui.LoginActivity;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class NavigationManager {

    private DrawerLayout mDrawerLayout;
    private NavigationDrawerAdapter mNavigationAdapter;
    private AppCompatActivity mActivity;

    public void init(AppCompatActivity activity, Toolbar toolbar, AuthUser authUser) {
        mActivity = activity;

        // Navigation item menu
        ArrayList<NavigationDrawerAdapter.Item> navItems = new ArrayList<>();
        navItems.add(new NavigationDrawerAdapter.Item("Dashboard",
                ContextCompat.getDrawable(activity, R.drawable.ic_home)));
        navItems.add(new NavigationDrawerAdapter.Item("Logout", null));

        mNavigationAdapter = new NavigationDrawerAdapter(activity, navItems, mNavigationListener);
        RecyclerView navigationRecyclerView =
                (RecyclerView) activity.findViewById(R.id.navigation_drawer_recycler_view);
        navigationRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        navigationRecyclerView.setAdapter(mNavigationAdapter);
        mNavigationAdapter.notifyDataSetChanged();

        // Navigation header
        mDrawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        View header = mDrawerLayout.findViewById(R.id.navigation_drawer_header);
        ((TextView)header.findViewById(R.id.display_name))
                .setText(authUser.getFbUser().getDisplayName());
        ((TextView)header.findViewById(R.id.email))
                .setText(authUser.getFbUser().getEmail());
        Picasso.with(activity)
                .load(authUser.getFbUser().getPhotoUrl())
                .into((CircleImageView)header.findViewById(R.id.avatar));



        // The hamburger icon
        ActionBarDrawerToggle drawerListener = new ActionBarDrawerToggle(activity, mDrawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
        };
        mDrawerLayout.addDrawerListener(drawerListener);
        drawerListener.syncState();

        mActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame, new DashboardFragment()).commit();
    }


    public NavigationChangeListener mNavigationListener = new NavigationChangeListener() {
        @Override
        public void onChange(int index) {
            switch (index) {
                case 0:
                    break;
                case 1:
                    FirebaseAuth.getInstance().signOut();
                    mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
                    mActivity.finish();
                    break;
            }
        }
    };

    public void openDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }
}
