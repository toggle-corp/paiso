package com.togglecorp.paiso;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;


public class DashboardFragment extends Fragment {
    private static final String TAG = "Dashboard Fragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        ArrayList<DashboardTransaction> transactions = new ArrayList<>();
        transactions.add(new DashboardTransaction("Khatri", "khatri@noob.com", 20000));
        transactions.add(new DashboardTransaction("Ankit", "frozen@helium.com", 45000));

        RecyclerView recyclerTransactions =
                (RecyclerView) view.findViewById(R.id.recycler_transactions);

        recyclerTransactions.setClickable(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());;
        recyclerTransactions.setLayoutManager(layoutManager);
        final DashboardTransactionAdapter adapter = new DashboardTransactionAdapter(transactions);
        recyclerTransactions.setAdapter(adapter);

        view.findViewById(R.id.add_transaction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddTransactionActivity.class));
            }
        });

        return view;
    }

}
