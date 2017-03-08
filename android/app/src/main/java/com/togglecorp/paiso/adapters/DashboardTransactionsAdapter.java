package com.togglecorp.paiso.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.togglecorp.paiso.R;
import com.togglecorp.paiso.ui.ContactDetailsActivity;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardTransactionsAdapter extends RecyclerView.Adapter<DashboardTransactionsAdapter.ViewHolder>{
    public static class Item {
        public long id;
        public String username;
        public String userextra;
        public String photoUrl;
        public float amount;
    }

    private final Context mContext;
    private List<Item> mItems = new ArrayList<>();

    public DashboardTransactionsAdapter(Context context) {
        mContext = context;
    }

    public void setItems(List<Item> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    @Override
    public DashboardTransactionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_dashboard_transaction, parent, false
        ));
    }

    @Override
    public void onBindViewHolder(DashboardTransactionsAdapter.ViewHolder holder, int position) {
        Item item = mItems.get(position);

        holder.username.setText(item.username);
        holder.userextra.setText(item.userextra);

        if (item.photoUrl == null) {
            holder.avatar.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_avatar));
        } else {
            Picasso.with(mContext)
                    .load(item.photoUrl)
                    .into(holder.avatar);
        }

        holder.amount.setText(item.amount+"");
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {

        protected CircleImageView avatar;
        protected TextView username;
        protected TextView userextra;
        protected TextView amount;

        public ViewHolder(View itemView) {
            super(itemView);

            avatar = (CircleImageView) itemView.findViewById(R.id.avatar);
            username = (TextView) itemView.findViewById(R.id.username);
            userextra = (TextView) itemView.findViewById(R.id.userextra);
            amount = (TextView) itemView.findViewById(R.id.amount);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Item item = mItems.get(getAdapterPosition());
                    Intent intent = new Intent(mContext, ContactDetailsActivity.class);
                    intent.putExtra("id", item.id);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
