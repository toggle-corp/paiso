package com.togglecorp.paiso.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.togglecorp.paiso.R;
import com.togglecorp.paiso.db.Contact;
import com.togglecorp.paiso.ui.AddTransactionActivity;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SelectContactAdapter extends RecyclerView.Adapter<SelectContactAdapter.ViewHolder> {

    private Activity mActivity;
    private List<Contact> mContacts = new ArrayList<>();

    public SelectContactAdapter(Activity activity) {
        mActivity = activity;
    }

    public void setContacts(List<Contact> contacts) {
        mContacts = contacts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mActivity).inflate(
                R.layout.layout_select_contact, parent, false
        ));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Contact contact = mContacts.get(position);

        holder.name.setText(contact.displayName);
        holder.extra.setText((contact.email == null) ? (contact.phone == null ? "" : contact.phone) : contact.email);

        if (contact.photoUrl == null) {
            holder.avatar.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_avatar));
        } else {
            Picasso.with(mActivity)
                    .load(contact.photoUrl)
                    .into(holder.avatar);
        }
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        protected CircleImageView avatar;
        protected TextView name;
        protected TextView extra;

        protected ViewHolder(View itemView) {
            super(itemView);

            avatar = (CircleImageView) itemView.findViewById(R.id.avatar);
            name = (TextView) itemView.findViewById(R.id.contact_name);
            extra = (TextView) itemView.findViewById(R.id.contact_extra);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, AddTransactionActivity.class);
                    intent.putExtra("contactId", mContacts.get(getAdapterPosition())._id);
                    mActivity.startActivity(intent);
                    mActivity.finish();
                }
            });
        }
    }
}
