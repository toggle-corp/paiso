package com.togglecorp.paiso.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.togglecorp.paiso.R;
import com.togglecorp.paiso.helpers.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactTransactionsAdapter extends RecyclerView.Adapter<ContactTransactionsAdapter.ViewHolder>{
    public static class Item {
        public String title;
        public long timestamp;
        public float amount;
    }

    private final Context mContext;
    private List<Item> mItems = new ArrayList<>();

    public ContactTransactionsAdapter(Context context) {
        mContext = context;
    }

    public void setItems(List<Item> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    @Override
    public ContactTransactionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_contact_transaction, parent, false
        ));
    }

    @Override
    public void onBindViewHolder(ContactTransactionsAdapter.ViewHolder holder, int position) {
        Item item = mItems.get(position);

        holder.title.setText(item.title);
        holder.extra.setText(DateTimeUtils.getFormattedDate(mContext, item.timestamp));
        holder.amount.setText(item.amount+"");
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView title;
        protected TextView extra;
        protected TextView amount;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            extra = (TextView) itemView.findViewById(R.id.extra);
            amount = (TextView) itemView.findViewById(R.id.amount);
        }
    }
}
