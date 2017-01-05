package com.togglecorp.paiso;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
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

        // Initialize recycler view
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
        HashMap<String, Double> userSummary = new HashMap<>();
        HashMap<String, Double> customUserSummary = new HashMap<>();
        for (String transactionId: Database.get().transactionIds) {
            // Get each transaction
            Transaction transaction = Database.get().transactions.get(transactionId);
            if (transaction != null) {
                String user = transaction.getOther(Database.get().selfId);
                Double amount = transaction.getSignedAmount(Database.get().selfId);

                HashMap<String, Double> summary =
                        transaction.customUser ? customUserSummary : userSummary;

                if (summary.containsKey(user))
                    summary.put(user, summary.get(user) + amount);
                else
                    summary.put(user, amount);
            }
        }

        // Now fill up the array for recycler view
        mTransactions.clear();
        for (HashMap.Entry<String, Double> summaryEntry: userSummary.entrySet()) {
            mTransactions.add(new DashboardTransaction(
                    Database.get().users.get(summaryEntry.getKey()).displayName,
                    Database.get().users.get(summaryEntry.getKey()).email,
                    summaryEntry.getValue()
            ));
        }
        for (HashMap.Entry<String, Double> summaryEntry: customUserSummary.entrySet()) {
            mTransactions.add(new DashboardTransaction(
                    summaryEntry.getKey(),
                    "",
                    summaryEntry.getValue()
            ));
        }
        mAdapter.notifyDataSetChanged();
    }
}
