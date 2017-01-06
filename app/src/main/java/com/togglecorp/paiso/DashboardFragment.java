package com.togglecorp.paiso;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


public class DashboardFragment extends Fragment implements RefreshListener {
    private static final String TAG = "Dashboard Fragment";

    private ArrayList<DashboardTransaction> mTransactions = new ArrayList<>();
    private DashboardTransactionAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize recycler view
        RecyclerView recyclerTransactions =
                (RecyclerView) view.findViewById(R.id.recycler_transactions);

        recyclerTransactions.setClickable(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());;
        recyclerTransactions.setLayoutManager(layoutManager);
        mAdapter = new DashboardTransactionAdapter(getActivity(), mTransactions);
        recyclerTransactions.setAdapter(mAdapter);

        // FAB button to add transaction
        final FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.add_transaction);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddTransactionActivity.class));
            }
        });

        // Show hide fab on scroll
        recyclerTransactions.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

        return view;
    }

    @Override
    public void refresh() {
        double total = 0;

        // Get map of users and their summary info
        HashMap<String, Double> userSummary = new HashMap<>();
        HashMap<String, Double> customUserSummary = new HashMap<>();
        for (Transaction transaction: Database.get().transactions.values()) {
            // Get each transaction
            if (transaction != null) {
                String user = transaction.getOther(Database.get().selfId);
                Double amount = transaction.getSignedAmount(Database.get().selfId);

                HashMap<String, Double> summary =
                        transaction.customUser ? customUserSummary : userSummary;

                if (summary.containsKey(user))
                    summary.put(user, summary.get(user) + amount);
                else
                    summary.put(user, amount);

                total += amount;
            }
        }

        // Now fill up the array for recycler view
        mTransactions.clear();
        for (HashMap.Entry<String, Double> summaryEntry: userSummary.entrySet()) {
            if (Database.get().users.containsKey(summaryEntry.getKey())) {
                mTransactions.add(new DashboardTransaction(
                        summaryEntry.getKey(),
                        false,
                        Database.get().users.get(summaryEntry.getKey()).displayName,
                        Database.get().users.get(summaryEntry.getKey()).email,
                        summaryEntry.getValue()
                ));
            }
        }
        for (HashMap.Entry<String, Double> summaryEntry: customUserSummary.entrySet()) {
            if (Database.get().customUsers.containsKey(summaryEntry.getKey())) {
                mTransactions.add(new DashboardTransaction(
                        summaryEntry.getKey(),
                        true,
                        Database.get().customUsers.get(summaryEntry.getKey()),
                        "",
                        summaryEntry.getValue()
                ));
            }
        }

        mAdapter.notifyDataSetChanged();

        // Sum total
        ((TextView)getActivity().findViewById(R.id.amount))
                .setText(Utils.formatCurrency(total));
    }

    @Override
    public void onResume() {
        super.onResume();
        // Add database refresh listener to populate the transactions
        Database.get().refreshListeners.add(this);
        // Refresh once
        refresh();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Remove the database refresh listener
        Database.get().refreshListeners.remove(this);
    }
}
