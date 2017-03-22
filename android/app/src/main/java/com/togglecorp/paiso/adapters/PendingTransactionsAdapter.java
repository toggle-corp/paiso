package com.togglecorp.paiso.adapters;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.togglecorp.paiso.R;
import com.togglecorp.paiso.db.Contact;
import com.togglecorp.paiso.db.DbHelper;
import com.togglecorp.paiso.db.PaisoTransaction;
import com.togglecorp.paiso.db.TransactionData;
import com.togglecorp.paiso.helpers.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PendingTransactionsAdapter extends RecyclerView.Adapter<PendingTransactionsAdapter.ViewHolder> {

    private static class PendingTransaction {
        Contact contact;
        PaisoTransaction transaction;
        TransactionData data;
    }

    private final Activity mActivity;
    private final DbHelper mDbHelper;
    private List<PendingTransaction> mPendingTransactions = new ArrayList<>();

    public PendingTransactionsAdapter(Activity activity, DbHelper dbHelper) {
        mActivity = activity;
        mDbHelper = dbHelper;
        refresh();
    }

    public void refresh() {
        mPendingTransactions.clear();

        List<PaisoTransaction> allTransactions = PaisoTransaction.getAll(PaisoTransaction.class, mDbHelper);
        for (PaisoTransaction transaction: allTransactions) {
            List<TransactionData> dataList = transaction.getPendingData(mDbHelper);

            if (dataList.size() > 0) {
                for (TransactionData data: dataList) {
                    PendingTransaction pendingTransaction = new PendingTransaction();
                    pendingTransaction.contact = transaction.getContact(mDbHelper);
                    pendingTransaction.transaction = transaction;
                    pendingTransaction.data = data;
                    mPendingTransactions.add(pendingTransaction);
                }
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_pending_transaction, parent, false
        ));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PendingTransaction pendingTransaction = mPendingTransactions.get(position);

        holder.transactionName.setText(pendingTransaction.data.title);
        holder.contactName.setText(pendingTransaction.contact.displayName);
        holder.contactExtra.setText(DateTimeUtils.getFormattedDate(mActivity, pendingTransaction.data.timestamp));

        float amount = (pendingTransaction.transaction.transactionType.equals("to")) ? pendingTransaction.data.amount : -pendingTransaction.data.amount;
        holder.amount.setText(amount+"");
        if (pendingTransaction.contact.photoUrl == null) {
            holder.avatar.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_avatar));
        } else {
            Picasso.with(mActivity)
                    .load(pendingTransaction.contact.photoUrl)
                    .into(holder.avatar);
        }
    }

    @Override
    public int getItemCount() {
        return mPendingTransactions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView transactionName;
        protected CircleImageView avatar;
        protected TextView contactName;
        protected TextView contactExtra;
        protected TextView amount;

        protected ViewHolder(View itemView) {
            super(itemView);
            transactionName = (TextView) itemView.findViewById(R.id.transaction_name);
            avatar = (CircleImageView) itemView.findViewById(R.id.avatar);
            contactName = (TextView) itemView.findViewById(R.id.contact_name);
            contactExtra = (TextView) itemView.findViewById(R.id.contact_extra);
            amount = (TextView) itemView.findViewById(R.id.amount);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    new AlertDialog.Builder(mActivity)
//                            .setMessage("Approve of this transaction?")
//                            .setIcon(android.R.drawable.ic_dialog_alert)
//                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//
//                                public void onClick(DialogInterface dialog, int whichButton) {
//                                    approveSelected();
//                                }})
//                            .setNegativeButton(android.R.string.no, null).show();
//                }
//            });
        }

        public void approveSelected() {
            PendingTransaction pendingTransaction = mPendingTransactions.get(getAdapterPosition());
            pendingTransaction.data.approved = true;
            pendingTransaction.data.modified = true;
            pendingTransaction.data.save(mDbHelper);
            refresh();
        }

    }
}
