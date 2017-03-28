package com.togglecorp.paiso.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.togglecorp.paiso.R;
import com.togglecorp.paiso.adapters.ContactTransactionsAdapter;
import com.togglecorp.paiso.adapters.DashboardTransactionsAdapter;
import com.togglecorp.paiso.db.Contact;
import com.togglecorp.paiso.db.DbHelper;
import com.togglecorp.paiso.db.PaisoTransaction;
import com.togglecorp.paiso.db.TransactionData;
import com.togglecorp.paiso.network.SyncListener;
import com.togglecorp.paiso.network.SyncManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactDetailsActivity extends AppCompatActivity implements SyncListener {

    private static final String TAG = "Contact Details";
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
        // Theme
        String theme = getIntent().getStringExtra("theme");
        if (theme == null || theme.equals("green")) {
            setTheme(R.style.GreenTheme);
        } else {
            setTheme(R.style.RedTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        // Get contact contactId to display details for
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }

        long id = bundle.getLong("contactId", -1);
        if (id < 0) {
            finish();
            return;
        }

        mDbHelper = new DbHelper(this);
        mContact = Contact.get(Contact.class, mDbHelper, id);
        if (mContact == null) {
            finish();
            return;
        }

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);

        mTransactionsAdapter = new ContactTransactionsAdapter(this, mContact);
        mTransactionsRecyclerView = (RecyclerView) findViewById(R.id.transactions_recyclerview);
        mTransactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTransactionsRecyclerView.setAdapter(mTransactionsAdapter);

        mTotalTextView = (TextView) findViewById(R.id.total);

        mSyncManager = SyncManager.get(mDbHelper);
//        mRefreshLayout.setRefreshing(true);
//        mSyncManager.requestSync();


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

        // Show hide fab on scroll
        final FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.add_transaction);
        mTransactionsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 ||dy<0 && fab.isShown()) {
                    fab.hide();
                }
            }
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fab.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
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
                item.transactionId = paisoTransaction._id;
                item.editable = paisoTransaction.user.equals(SyncManager.getUser().userId);
                mItems.add(item);

                total += item.amount;
            }
        }


        Collections.sort(mItems, new Comparator<ContactTransactionsAdapter.Item>() {
            @Override
            public int compare(ContactTransactionsAdapter.Item item1, ContactTransactionsAdapter.Item item2) {
                return Long.valueOf(item2.timestamp).compareTo(item1.timestamp);
            }
        });

        mTransactionsAdapter.setItems(mItems);
        mTransactionsAdapter.notifyDataSetChanged();
        mTotalTextView.setText(total+"");

        mRefreshLayout.setRefreshing(false);

        // Change theme to render positive/negative total
        String currentTheme = getIntent().getStringExtra("theme");
        if (currentTheme == null || currentTheme.equals("green")) {
            if (total < 0) {
                getIntent().putExtra("theme", "red");
                recreate();
            }
        } else if (total >= 0) {
            getIntent().putExtra("theme", "green");
            recreate();
        }
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

        refresh();
        if (mSyncManager != null) {
            mSyncManager.addListener(this);
//            mRefreshLayout.setRefreshing(true);
//            mSyncManager.requestSync();
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
        try {
            if (complete) {
                refresh();
            }
        } catch (Exception ignored) {

        }
    }

    public void addTransaction(View view) {
        Intent intent = new Intent(this, AddTransactionActivity.class);
        intent.putExtra("contactId", mContact._id);
        startActivity(intent);
    }
}
