package com.togglecorp.paiso;

import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetailsActivity extends AppCompatActivity implements RefreshListener {

    private String mUserId;
    private boolean mCustomUser = true;

    private List<Pair<String, Transaction>> mTransactions = new ArrayList<>();
    private UserTransactionAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        // Get user id to display details for
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }

        mUserId = bundle.getString("user-id");
        if (mUserId == null) {
            finish();
            return;
        }
        mCustomUser = bundle.getBoolean("custom-user");

        // The toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        findViewById(R.id.avatar).setVisibility(View.INVISIBLE);
        if (mCustomUser) {
            setTitle(Database.get().contacts.get(mUserId).displayName);
        }
        else {
            User user = Database.get().users.get(mUserId);
            setTitle(user.displayName);

            // Avatar
            if (user.photoUrl != null && !user.photoUrl.equals("")) {
                findViewById(R.id.avatar).setVisibility(View.VISIBLE);
                Picasso.with(this)
                        .load(user.photoUrl)
                        .into((CircleImageView)findViewById(R.id.avatar));
            }
        }

        // Initialize recycler view
        RecyclerView recyclerTransactions =
                (RecyclerView) findViewById(R.id.recycler_transactions);

        recyclerTransactions.setClickable(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);;
        recyclerTransactions.setLayoutManager(layoutManager);
        mAdapter = new UserTransactionAdapter(this, mTransactions);
        recyclerTransactions.setAdapter(mAdapter);

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

    @Override
    public void onStart() {
        super.onStart();
        Database.get().startSync(this);
        // Add database refresh listener to populate the transactions
        Database.get().refreshListeners.add(this);
        refresh();
    }

    @Override
    public void onStop() {
        super.onStop();
        Database.get().stopSync();
        Database.get().refreshListeners.remove(this);
    }

    @Override
    public void refresh() {
        double total = 0;

        // Remove all items
        mTransactions.clear();

        // And add transactions involving this this user
        for (Map.Entry<String, Transaction> transaction: Database.get().transactions.entrySet()) {
            if ((transaction.getValue().deleted == null || !transaction.getValue().deleted) &&
                    transaction.getValue().customUser == mCustomUser &&
                    transaction.getValue().getOther(Database.get().selfId).equals(mUserId))
            {
                mTransactions.add(new Pair<>(transaction.getKey(), transaction.getValue()));
                total += transaction.getValue().getSignedAmount(Database.get().selfId);
            }
        }

        // Also sort by date: latest first
        Collections.sort(mTransactions, new Comparator<Pair<String, Transaction>>() {
            @Override
            public int compare(Pair<String, Transaction> t1, Pair<String, Transaction> t2) {
                return ((Long)t2.second.date).compareTo((Long)t1.second.date);
            }
        });

        mAdapter.notifyDataSetChanged();

        // Sum total
        Utils.setBalance(this, (TextView)findViewById(R.id.amount), total);
    }
}
