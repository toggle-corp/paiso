package com.togglecorp.paiso;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class DashboardTransactionAdapter extends RecyclerView.Adapter<DashboardTransactionAdapter.DashboardTransactionViewHolder> {

    private List<DashboardTransaction> mTransactions;
    private Context mContext;

    DashboardTransactionAdapter(Context context, List<DashboardTransaction> transactions){
        mContext = context;
        mTransactions = transactions;
    }

    @Override
    public DashboardTransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DashboardTransactionViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.layout_dashboard_transaction, parent, false));
    }

    @Override
    public void onBindViewHolder(final DashboardTransactionViewHolder holder, int position) {
        holder.name.setText(mTransactions.get(position).name);
        holder.amount.setText(Utils.formatCurrency(mTransactions.get(position).amount));
        holder.extra.setText(mTransactions.get(position).extra);

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserDetailsActivity.class);
                intent.putExtra("user-id",
                        mTransactions.get(holder.getAdapterPosition()).userId);
                intent.putExtra("custom-user",
                        mTransactions.get(holder.getAdapterPosition()).customUser);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTransactions.size();
    }

    protected class DashboardTransactionViewHolder extends RecyclerView.ViewHolder{
        protected TextView name;
        protected TextView amount;
        protected TextView extra;
        protected View root;

        public DashboardTransactionViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.name);
            amount = (TextView)itemView.findViewById(R.id.amount);
            extra = (TextView)itemView.findViewById(R.id.extra);
            root = itemView;
        }
    }
}
