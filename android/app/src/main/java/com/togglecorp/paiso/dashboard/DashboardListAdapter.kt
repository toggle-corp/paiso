package com.togglecorp.paiso.dashboard

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.togglecorp.paiso.R
import com.togglecorp.paiso.contacts.ContactDetailsActivity
import kotlinx.android.synthetic.main.layout_dashboard_transaction.view.*


data class DashboardItem(
        val contactId: Int,
        val contactName: String,
        var amount: Float = 0.0f
)


class DashboardListAdapter(val context: Context, val items: List<DashboardItem>) :
        RecyclerView.Adapter<DashboardListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_dashboard_transaction,
                parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bind(context, items[position])
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        fun bind(context: Context, item: DashboardItem) {
            itemView.contactName.text = item.contactName
            itemView.amount.text = item.amount.toString()

            itemView.setOnClickListener {
                val intent = Intent(context, ContactDetailsActivity::class.java)
                intent.putExtra("id", item.contactId)
                context.startActivity(intent)
            }
        }
    }

}