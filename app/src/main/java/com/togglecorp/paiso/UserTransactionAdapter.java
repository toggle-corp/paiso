package com.togglecorp.paiso;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class UserTransactionAdapter extends RecyclerView.Adapter<UserTransactionAdapter.UserTransactionViewHolder> {

    private List<Transaction> mTransactions;
    private Context mContext;

    UserTransactionAdapter(Context context, List<Transaction> transactions){
        mContext = context;
        mTransactions = transactions;
    }

    @Override
    public UserTransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserTransactionViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_transaction, parent, false));
    }

    @Override
    public void onBindViewHolder(UserTransactionViewHolder holder, int position) {
        holder.name.setText(mTransactions.get(position).title);
        holder.amount.setText(Utils.formatCurrency(
                mTransactions.get(position).getSignedAmount(Database.get().selfId)));
        holder.extra.setText(Utils.formatDate(mContext, (Long)mTransactions.get(position).date));
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
