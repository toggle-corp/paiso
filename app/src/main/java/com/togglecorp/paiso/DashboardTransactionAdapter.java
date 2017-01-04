package com.togglecorp.paiso;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class DashboardTransactionAdapter extends RecyclerView.Adapter<DashboardTransactionAdapter.DashboardTransactionViewHolder> {

    List<DashboardTransaction> mTransactions;

    DashboardTransactionAdapter(List<DashboardTransaction> transactions){
        mTransactions = transactions;
    }

    @Override
    public DashboardTransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DashboardTransactionViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_dashboard_transaction, parent, false));
    }

    @Override
    public void onBindViewHolder(DashboardTransactionViewHolder holder, int position) {
        holder.name.setText(mTransactions.get(position).name);
        holder.amount.setText(mTransactions.get(position).amount+"");
        holder.extra.setText(mTransactions.get(position).extra);
    }

    @Override
    public int getItemCount() {
        return mTransactions.size();
    }

    protected class DashboardTransactionViewHolder extends RecyclerView.ViewHolder{
        protected TextView name;
        protected TextView amount;
        protected TextView extra;

        public DashboardTransactionViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.name);
            amount = (TextView)itemView.findViewById(R.id.amount);
            extra = (TextView)itemView.findViewById(R.id.extra);
        }
    }
}
