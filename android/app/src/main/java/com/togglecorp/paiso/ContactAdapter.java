package com.togglecorp.paiso;

import android.content.Context;
import android.content.Intent;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private List<Pair<String, Contact>> mContacts;
    private Context mContext;
    private SelectionListener mSelectionListener;

    public interface SelectionListener {
        void onSelect(String contactId);
    }

    public ContactAdapter(Context context, SelectionListener selectionListener, List<Pair<String, Contact>> contacts) {
        mContext = context;
        mSelectionListener = selectionListener;
        mContacts = contacts;
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(
                R.layout.layout_contact, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Contact contact = mContacts.get(position).second;
        holder.username.setText(contact.displayName);
        holder.details.setText(contact.data);

        if (contact.photoUrl != null && !contact.photoUrl.equals("")) {
            Picasso.with(mContext)
                    .load(contact.photoUrl)
                    .into(holder.avatar);
        }
        else if (contact.userId != null && Database.get().users.get(contact.userId) != null &&
                Database.get().users.get(contact.userId).photoUrl != null &&
                !Database.get().users.get(contact.userId).photoUrl.equals(""))
        {
            Picasso.with(mContext)
                    .load(Database.get().users.get(contact.userId).photoUrl)
                    .into(holder.avatar);
        }
        else {
            holder.avatar.setImageResource(R.drawable.ic_avatar);
        }

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectionListener.onSelect(mContacts.get(holder.getAdapterPosition()).first);
            }
        });
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        protected View root;
        protected TextView username;
        protected TextView details;
        protected CircleImageView avatar;

        protected ViewHolder(View rootView) {
            super(rootView);

            root = rootView;
            username = (TextView) root.findViewById(R.id.username);
            details = (TextView) root.findViewById(R.id.details);
            avatar = (CircleImageView) root.findViewById(R.id.avatar);
        }
    }
}
