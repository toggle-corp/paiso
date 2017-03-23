package com.togglecorp.paiso.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.togglecorp.paiso.R;
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
import java.util.HashMap;
import java.util.List;

public class DashboardFragment extends Fragment implements SyncListener {
    private static final String TAG = "DashboardFragment";

    private DbHelper mDbHelper;
    private SyncManager mSyncManager;
    private List<DashboardTransactionsAdapter.Item> mItems = new ArrayList<>();

    private TextView mTotalTextView;
    private DashboardTransactionsAdapter mTransactionsAdapter;
    private RecyclerView mTransactionsRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    private FloatingActionButton mFab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mTransactionsAdapter = new DashboardTransactionsAdapter(getActivity());
        mTransactionsRecyclerView = (RecyclerView) view.findViewById(R.id.transactions_recyclerview);
        mTransactionsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTransactionsRecyclerView.setAdapter(mTransactionsAdapter);

        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        mTotalTextView = (TextView) view.findViewById(R.id.total);

        mDbHelper = new DbHelper(getActivity());
        mSyncManager = SyncManager.get(mDbHelper);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSyncManager.requestSync();
            }
        });

        mSyncManager.addListener(this);
        mRefreshLayout.setRefreshing(true);
        mSyncManager.requestSync();


        // Floating action button

        // Show hide fab on scroll
        mFab = (FloatingActionButton) view.findViewById(R.id.add_transaction);
        mTransactionsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 ||dy<0 && mFab.isShown()) {
                    mFab.hide();
                }
            }
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mFab.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        mFab.hide();

        // Select contact to add transaction on click
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SelectContactActivity.class));
            }
        });

        return view;
    }

    public void refresh() {
        mFab.show();

        List<PaisoTransaction> transactions = PaisoTransaction.getAll(PaisoTransaction.class, mDbHelper);
        HashMap<Contact, Pair<Long, Float>> contactTransactions = new HashMap<>();

        for (PaisoTransaction transaction: transactions) {
            Contact contact = transaction.getContact(mDbHelper);
            TransactionData transactionData = transaction.getLatestApproved(mDbHelper);

            if (contact != null && transactionData != null) {
                float amount = transaction.transactionType.equals("to") ? transactionData.amount : -transactionData.amount;

                if (!contactTransactions.containsKey(contact)) {
                    contactTransactions.put(contact, new Pair<>(transactionData.timestamp, amount));
                } else {
                    contactTransactions.put(contact, new Pair<>(
                            contactTransactions.get(contact).first > transactionData.timestamp ? contactTransactions.get(contact).first : transactionData.timestamp,
                            contactTransactions.get(contact).second + amount)
                    );
                }
            }
        }

        float total = 0;
        mItems.clear();
        for (HashMap.Entry<Contact, Pair<Long, Float>> entry: contactTransactions.entrySet()) {
            Contact contact = entry.getKey();

            DashboardTransactionsAdapter.Item item = new DashboardTransactionsAdapter.Item();

            item.contactId = contact._id;
            item.username = contact.displayName;
            item.userextra = "";

            if (contact.email != null) { item.userextra = contact.email; }
            else if (contact.phone != null) { item.userextra = contact.phone; }

            item.photoUrl = contact.photoUrl;
            item.amount = entry.getValue().second;
            item.timestamp = entry.getValue().first;

            total += item.amount;

            mItems.add(item);
        }

        // Sort by date
        Collections.sort(mItems, new Comparator<DashboardTransactionsAdapter.Item>() {
            @Override
            public int compare(DashboardTransactionsAdapter.Item item1, DashboardTransactionsAdapter.Item item2) {
                return (int)(item1.timestamp - item2.timestamp);
            }
        });

        mTransactionsAdapter.setItems(mItems);
        mTransactionsAdapter.notifyDataSetChanged();
        mTotalTextView.setText(total+"");

        mRefreshLayout.setRefreshing(false);

        // Change theme to render positive/negative total
        if (getActivity() != null && !getActivity().isFinishing()) {
            String currentTheme = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("theme", "green");
            if (currentTheme.equals("green")) {
                if (total < 0) {
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("theme", "red").apply();
                    getActivity().recreate();
                }
            } else if (total >= 0) {
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("theme", "green").apply();
                getActivity().recreate();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        refresh();
        if (mSyncManager != null) {
            mSyncManager.addListener(this);
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
                mRefreshLayout.setRefreshing(false);
                refresh();
            }
        } catch (Exception ignored) {

        }
    }
}
