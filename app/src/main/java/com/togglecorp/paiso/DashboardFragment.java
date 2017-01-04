package com.togglecorp.paiso;


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
        transactions.add(new DashboardTransaction("Bibek", "bibek@toggle", 20000));
        transactions.add(new DashboardTransaction("Bibek 2", "bibek2@toggle", 20000));

        RecyclerView recyclerOweMe = (RecyclerView) container.findViewById(R.id.recycler_owe_me);
        RecyclerView recyclerOweThem = (RecyclerView) container.findViewById(R.id.recycler_owe_them);

        recyclerOweMe.setClickable(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());;
        recyclerOweMe.setLayoutManager(layoutManager);
        final DashboardTransactionAdapter adapter = new DashboardTransactionAdapter(transactions);
        recyclerOweMe.setAdapter(adapter);

        return view;
    }

}
