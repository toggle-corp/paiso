package com.togglecorp.paiso.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.togglecorp.paiso.R;
import com.togglecorp.paiso.adapters.SelectContactAdapter;
import com.togglecorp.paiso.db.Contact;
import com.togglecorp.paiso.db.DbHelper;
import com.togglecorp.paiso.db.PaisoTransaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SelectContactActivity extends AppCompatActivity {

    private DbHelper mDbHelper;

    private String mQueryString = "";
    private boolean mRecentTab = true;

    private RecyclerView mContactsRecyclerView;
    private SelectContactAdapter mContactsAdapter;

    private List<Contact> mContacts = new ArrayList<>();
    private List<Contact> mRecentContacts = new ArrayList<>();
    private List<Contact> mFilteredContacts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.GreenTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        mDbHelper = new DbHelper(this);

        // The toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        setTitle("Select contact");

        // Recycler view and adapter
        mContactsAdapter = new SelectContactAdapter(this);
        mContactsAdapter.setContacts(mFilteredContacts);

        mContactsRecyclerView = (RecyclerView) findViewById(R.id.contacts_recyclerview);
        mContactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mContactsRecyclerView.setAdapter(mContactsAdapter);

        // Set tab listeners
        ((TabLayout)findViewById(R.id.contact_tabs)).addOnTabSelectedListener(
                new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        mRecentTab = tab.getPosition() == 0;
                        refresh();
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                }
        );

        // Read in all contacts
        mContacts = Contact.getAll(Contact.class, mDbHelper);
        Collections.sort(mContacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact c1, Contact c2) {
                return c1.displayName.compareTo(c2.displayName);
            }
        });

        // Read in recent contacts
        for (int i=0; i<mContacts.size(); i++) {
            Contact contact = mContacts.get(i);
            List<PaisoTransaction> transactions = contact.getAllTransactions(mDbHelper);
            if (transactions.size() > 0) {
                mRecentContacts.add(contact);
            }
        }
        Collections.sort(mRecentContacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact c1, Contact c2) {
                return (int)(c2.getLatestTransactionTime(mDbHelper) - c1.getLatestTransactionTime(mDbHelper));
            }
        });

        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_contact, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        ((SearchView) MenuItemCompat.getActionView(searchItem)).setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        mQueryString = query;
                        refresh();
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        mQueryString = newText;
                        refresh();
                        return false;
                    }
                }
        );

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Back button
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void refresh() {
        mFilteredContacts.clear();

        if (mRecentTab) {
            // Recent contacts only
            for (int i=0; i<mRecentContacts.size(); i++) {
                Contact contact = mRecentContacts.get(i);
                if (mQueryString == null || contact.displayName.toLowerCase()
                        .contains(mQueryString.toLowerCase())) {

                    mFilteredContacts.add(contact);
                }
            }

        }
        else {
            // All contacts
            for (int i=0; i<mContacts.size(); i++) {
                Contact contact = mContacts.get(i);
                if (mQueryString == null || contact.displayName.toLowerCase()
                        .contains(mQueryString.toLowerCase())) {

                    mFilteredContacts.add(contact);
                }
            }
        }

        mContactsAdapter.notifyDataSetChanged();
    }
}
