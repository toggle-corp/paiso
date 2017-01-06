package com.togglecorp.paiso;

import android.content.Context;
import android.content.Intent;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class UserTransactionAdapter extends RecyclerView.Adapter<UserTransactionAdapter.UserTransactionViewHolder> {

    private List<Pair<String, Transaction>> mTransactions;
    private Context mContext;

    UserTransactionAdapter(Context context, List<Pair<String, Transaction>> transactions){
        mContext = context;
        mTransactions = transactions;
    }

    @Override
    public UserTransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserTransactionViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_transaction, parent, false));
    }

    @Override
    public void onBindViewHolder(final UserTransactionViewHolder holder, int position) {
        holder.name.setText(mTransactions.get(position).second.title);
        holder.amount.setText(Utils.formatCurrency(
                mTransactions.get(position).second.getSignedAmount(Database.get().selfId)));
        holder.extra.setText(
                Utils.formatDate(mContext,
                        (Long)mTransactions.get(position).second.date)
                + (mTransactions.get(position).second.added_by.equals(Database.get().selfId) ? ", by me" : ", by them")
        );

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Only allow editing, if it was added by self
                if (mTransactions.get(holder.getAdapterPosition())
                        .second.added_by.equals(Database.get().selfId))
                {
                    Intent intent = new Intent(mContext, AddTransactionActivity.class);
                    intent.putExtra("transaction-id",
                            mTransactions.get(holder.getAdapterPosition()).first);
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTransactions.size();
    }

    protected class UserTransactionViewHolder extends RecyclerView.ViewHolder{
        protected TextView name;
        protected TextView amount;
        protected TextView extra;
        protected View root;

        public UserTransactionViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.name);
            amount = (TextView)itemView.findViewById(R.id.amount);
            extra = (TextView)itemView.findViewById(R.id.extra);
            root = itemView;
        }
    }
}
