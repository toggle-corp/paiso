package com.togglecorp.paiso.transaction

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.togglecorp.paiso.R
import kotlinx.android.synthetic.main.layout_contact_transaction.view.*

class TransactionListAdapter(val context: Context, val transactionList: List<PaisoTransaction>)
    : RecyclerView.Adapter<TransactionListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_contact_transaction, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bind(transactionList[position])
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }


    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        fun bind(transaction: PaisoTransaction) {
            itemView.name.text = transaction.title
            itemView.info.text = transaction.editedAt.toString()
            itemView.amount.text = transaction.amount.toString()
        }
    }

}

