package com.togglecorp.paiso;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DashboardFragment extends Fragment implements RefreshListener {
    private static final String TAG = "Dashboard Fragment";

    private List<DashboardTransaction> mTransactions = new ArrayList<>();
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
//                startActivity(new Intent(getActivity(), AddTransactionActivity.class));
                startActivityForResult(new Intent(getActivity(), SelectContactActivity.class), 1);

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
            if (transaction != null && !(transaction.deleted != null && transaction.deleted)) {

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
            if (Database.get().contacts.containsKey(summaryEntry.getKey())) {
                mTransactions.add(new DashboardTransaction(
                        summaryEntry.getKey(),
                        true,
                        Database.get().contacts.get(summaryEntry.getKey()).displayName,
                        Database.get().contacts.get(summaryEntry.getKey()).data,
                        summaryEntry.getValue()
                ));
            }
        }

        mAdapter.notifyDataSetChanged();

        // Sum total
        Utils.setBalance(getActivity(), (TextView)getActivity().findViewById(R.id.amount), total);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {

            Intent intent = new Intent(getActivity(), AddTransactionActivity.class);
            intent.putExtra("contact-id", data.getStringExtra("contact-id"));
            startActivity(intent);
        }
    }
}