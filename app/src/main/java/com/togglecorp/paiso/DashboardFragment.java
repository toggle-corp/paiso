package com.togglecorp.paiso;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;


public class DashboardFragment extends Fragment implements RefreshListener {
    private static final String TAG = "Dashboard Fragment";

    private ArrayList<DashboardTransaction> mTransactions = new ArrayList<>();
    private DashboardTransactionAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Intialize recycler view
        RecyclerView recyclerTransactions =
                (RecyclerView) view.findViewById(R.id.recycler_transactions);

        recyclerTransactions.setClickable(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());;
        recyclerTransactions.setLayoutManager(layoutManager);
        mAdapter = new DashboardTransactionAdapter(mTransactions);
        recyclerTransactions.setAdapter(mAdapter);

        // FAB button to add transaction
        view.findViewById(R.id.add_transaction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddTransactionActivity.class));
            }
        });

        // Add database refresh listener to populate the transactions
        Database.get().refreshListeners.add(this);

        // Refresh once
        refresh();

        return view;
    }

    @Override
    public void refresh() {
        // Get map of users and their summary info
        HashMap<String, Double> summary = new HashMap<>();
        for (String transactionId: Database.get().transactionIds) {
            // Get each transaction
            Transaction transaction = Database.get().transactions.get(transactionId);
            if (transaction != null) {
                if (!transaction.customUser) {
                    String user = null; Double amount = 0.0;

                    if (transaction.by.equals(Database.get().selfId)) {
                        user = transaction.to; amount = -transaction.amount;
                    }
                    else if (transaction.to.equals(Database.get().selfId)) {
                        user = transaction.by; amount = transaction.amount;
                    }

                    if (user != null) {
                        if (summary.containsKey(user))
                            summary.put(user, summary.get(user) + amount);
                        else
                            summary.put(user, amount);
                    }
                }
            }
        }

        // Now fill up the array for recycler view
        mTransactions.clear();
        for (HashMap.Entry<String, Double> summaryEntry: summary.entrySet()) {
            mTransactions.add(new DashboardTransaction(
                    Database.get().users.get(summaryEntry.getKey()).displayName,
                    Database.get().users.get(summaryEntry.getKey()).email,
                    summaryEntry.getValue()
            ));
        }
        mAdapter.notifyDataSetChanged();
    }
}
