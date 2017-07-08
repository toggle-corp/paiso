package com.togglecorp.paiso.contacts

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.togglecorp.paiso.R
import kotlinx.android.synthetic.main.layout_contact.view.*

class ContactListAdapter(val context: Context, val contacts: List<Contact>)
    : RecyclerView.Adapter<ContactListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent?.context)
                .inflate(R.layout.layout_contact, parent, false))

    override fun getItemCount() = contacts.size

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bind(context, contacts[position])
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        fun bind(context: Context, contact: Contact) {
            itemView.name.text = contact.name
            itemView.setOnClickListener {
                val intent = Intent(context, ContactDetailsActivity::class.java)
                intent.putExtra("mode", "edit")
                intent.putExtra("id", contact._id)
                context.startActivity(intent)
            }
        }
    }

}

