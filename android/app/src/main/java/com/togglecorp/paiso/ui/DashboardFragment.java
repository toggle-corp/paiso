package com.togglecorp.paiso.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
        mSyncManager = new SyncManager(mDbHelper);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSyncManager.requestSync();
            }
        });

        return view;
    }


    public void refresh() {

        List<PaisoTransaction> transactions = PaisoTransaction.getAll(PaisoTransaction.class, mDbHelper);
        HashMap<Contact, Float> contactTransactions = new HashMap<>();

        for (PaisoTransaction transaction: transactions) {
            Contact contact = transaction.getContact(mDbHelper);
            TransactionData transactionData = transaction.getLatestApproved(mDbHelper);

            if (contact != null && transactionData != null) {
                if (!contactTransactions.containsKey(contact)) {
                    contactTransactions.put(contact, transactionData.amount);
                } else {
                    contactTransactions.put(contact, contactTransactions.get(contact) + transactionData.amount);
                }
            }
        }

        float total = 0;
        mItems.clear();
        for (HashMap.Entry<Contact, Float> entry: contactTransactions.entrySet()) {
            Contact contact = entry.getKey();

            DashboardTransactionsAdapter.Item item = new DashboardTransactionsAdapter.Item();

            item.id = contact._id;
            item.username = contact.displayName;
            item.userextra = "";
            if (contact.email != null) { item.userextra = contact.email; }
            else if (contact.phone != null) { item.userextra = contact.phone; }
            item.photoUrl = contact.photoUrl;
            item.amount = entry.getValue();

            total += item.amount;

            mItems.add(item);
        }

        mTransactionsAdapter.setItems(mItems);
        mTransactionsAdapter.notifyDataSetChanged();
        mTotalTextView.setText(total+"");

        mRefreshLayout.setRefreshing(false);
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
