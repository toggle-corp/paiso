package com.togglecorp.paiso.helpers;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.togglecorp.paiso.R;
import com.togglecorp.paiso.common.ChangeListener;
import com.togglecorp.paiso.ui.NavigationDrawerAdapter;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class NavigationManager {

    private DrawerLayout mDrawerLayout;
    private NavigationDrawerAdapter mNavigationAdapter;

    public void init(Activity activity, Toolbar toolbar, AuthUser authUser) {

        // Navigation item menu
        ArrayList<NavigationDrawerAdapter.Item> navItems = new ArrayList<>();
        navItems.add(new NavigationDrawerAdapter.Item("Dashboard",
                ContextCompat.getDrawable(activity, R.drawable.ic_home)));

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
                .setText(authUser.getUser().getDisplayName());
        ((TextView)header.findViewById(R.id.email))
                .setText(authUser.getUser().getEmail());
        Picasso.with(activity)
                .load(authUser.getUser().getPhotoUrl())
                .into((CircleImageView)header.findViewById(R.id.avatar));



        // The hamburger icon
        ActionBarDrawerToggle drawerListener = new ActionBarDrawerToggle(activity, mDrawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
        };
        mDrawerLayout.addDrawerListener(drawerListener);
        drawerListener.syncState();
    }


    public ChangeListener mNavigationListener = new ChangeListener() {
        @Override
        public void onChange(int index) {
            switch (index) {
                case 0:
                    break;
            }
        }
    };

    public void openDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }
}
