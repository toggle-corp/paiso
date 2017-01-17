package com.togglecorp.paiso;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class SelectContactActivity extends AppCompatActivity {

    private ContactAdapter mAdapter;
    private List<Pair<String, Contact>> mContacts = new ArrayList<>();
    private List<Pair<String, Contact>> mFilteredContacts = new ArrayList<>();
    private String mQueryString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        // The toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        setTitle("Select contact");

        // Initialize recycler view
        mAdapter = new ContactAdapter(this, new ContactAdapter.SelectionListener() {
            @Override
            public void onSelect(String contactId) {
                Intent result = new Intent();
                result.putExtra("contact-id", contactId);
                setResult(Activity.RESULT_OK, result);
                finish();
            }
        }, mFilteredContacts);

        RecyclerView recyclerContacts = (RecyclerView) findViewById(R.id.recycler_contacts);
        recyclerContacts.setLayoutManager(new LinearLayoutManager(this));
        recyclerContacts.setAdapter(mAdapter);

        // Refresh contact list
        showRecentContacts();

        // Set tab listeners
        ((TabLayout)findViewById(R.id.contact_tabs)).addOnTabSelectedListener(
                new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        if (tab.getPosition() == 0)
                            showRecentContacts();
                        else
                            showAllContacts();
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                }
        );
    }

    private void showAllContacts() {
        mContacts.clear();
        for (Map.Entry<String, Contact> contact: Database.get().contacts.entrySet()) {
            mContacts.add(new Pair<>(contact.getKey(), contact.getValue()));
        }

        Collections.sort(mContacts, new Comparator<Pair<String, Contact> >() {
            @Override
            public int compare(Pair<String, Contact> c1, Pair<String, Contact>  c2) {
                return c1.second.displayName.compareTo(c2.second.displayName);
            }
        });

        filterContacts();
    }

    private void showRecentContacts() {
        mContacts.clear();

        // First get all transactions and sort them
        ArrayList<Transaction> transactions = new ArrayList<>();
        for (Transaction transaction: Database.get().transactions.values())
                transactions.add(transaction);
        Collections.sort(transactions, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction t1, Transaction t2) {
                return ((Long)t2.date).compareTo((Long)t1.date);
            }
        });

        // Then get all contacts for those transactions
        ArrayList<String> added = new ArrayList<>();
        for (Transaction transaction: transactions) {
            String cid = transaction.getOtherContactId(Database.get().selfId);
            if (Database.get().contacts.containsKey(cid) && !added.contains(cid)) {
                added.add(cid);
                mContacts.add(new Pair<>(cid, Database.get().contacts.get(cid)));
            }
        }

        Collections.sort(mContacts, new Comparator<Pair<String, Contact> >() {
            @Override
            public int compare(Pair<String, Contact> c1, Pair<String, Contact>  c2) {
                return c1.second.displayName.compareTo(c2.second.displayName);
            }
        });

        filterContacts();

    }

    private void filterContacts() {
        mFilteredContacts.clear();
        for (Pair<String, Contact> contact: mContacts) {
            if (mQueryString == null || contact.second.displayName.toLowerCase()
                    .contains(mQueryString.toLowerCase()))
            {
                mFilteredContacts.add(contact);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_contact, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        ((SearchView) MenuItemCompat.getActionView(searchItem)).setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        mQueryString = query;
                        filterContacts();
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        mQueryString = newText;
                        filterContacts();
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
}
