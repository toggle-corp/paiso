package com.togglecorp.paiso.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.togglecorp.paiso.R;
import com.togglecorp.paiso.adapters.ContactTransactionsAdapter;
import com.togglecorp.paiso.db.Contact;
import com.togglecorp.paiso.db.DbHelper;
import com.togglecorp.paiso.db.PaisoTransaction;
import com.togglecorp.paiso.db.TransactionData;
import com.togglecorp.paiso.network.SyncListener;
import com.togglecorp.paiso.network.SyncManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactDetailsActivity extends AppCompatActivity implements SyncListener {

    private Contact mContact;

    private DbHelper mDbHelper;
    private SyncManager mSyncManager;

    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mTransactionsRecyclerView;
    private ContactTransactionsAdapter mTransactionsAdapter;
    private TextView mTotalTextView;

    private List<ContactTransactionsAdapter.Item> mItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        // Get contact id to display details for
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }

        long id = bundle.getLong("id", -1);
        if (id < 0) {
            finish();
            return;
        }

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);

        mTransactionsAdapter = new ContactTransactionsAdapter(this);
        mTransactionsRecyclerView = (RecyclerView) findViewById(R.id.transactions_recyclerview);
        mTransactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTransactionsRecyclerView.setAdapter(mTransactionsAdapter);

        mTotalTextView = (TextView) findViewById(R.id.total);

        mDbHelper = new DbHelper(this);
        mSyncManager = new SyncManager(mDbHelper);

        mContact = Contact.get(Contact.class, mDbHelper, id);
        if (mContact == null) {
            finish();
            return;
        }

        // The toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        setTitle(mContact.displayName);
        CircleImageView avatar = (CircleImageView) findViewById(R.id.avatar);
        if (mContact.photoUrl != null) {
            Picasso.with(this)
                    .load(mContact.photoUrl)
                    .into(avatar);
        } else {
            avatar.setImageDrawable(null);
        }

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSyncManager.requestSync();
            }
        });
    }

    private void refresh() {
        List<PaisoTransaction> paisoTransactions = mContact.getAllTransactions(mDbHelper);

        float total = 0;
        mItems.clear();
        for (PaisoTransaction paisoTransaction: paisoTransactions) {
            TransactionData data = paisoTransaction.getLatestApproved(mDbHelper);
            if (data != null) {

                ContactTransactionsAdapter.Item item = new ContactTransactionsAdapter.Item();
                item.title = data.title;
                item.amount = (paisoTransaction.transactionType.equals("to")) ? data.amount : -data.amount;
                item.timestamp = data.timestamp;
                mItems.add(item);

                total += item.amount;
            }
        }

        Collections.sort(mItems, new Comparator<ContactTransactionsAdapter.Item>() {
            @Override
            public int compare(ContactTransactionsAdapter.Item item1, ContactTransactionsAdapter.Item item2) {
                return (int)(item2.timestamp - item1.timestamp);
            }
        });

        mTransactionsAdapter.setItems(mItems);
        mTransactionsAdapter.notifyDataSetChanged();
        mTotalTextView.setText(total+"");

        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mSyncManager != null) {
            mSyncManager.addListener(this);
            mRefreshLayout.setRefreshing(true);
            mSyncManager.requestSync();
        }
        refresh();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mSyncManager != null) {
            mSyncManager.removeListener(this);
        }
    }


    @Override
    public void onSync() {
        refresh();
    }
}
