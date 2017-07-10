package com.togglecorp.paiso.notifications

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.togglecorp.paiso.R
import com.togglecorp.paiso.contacts.Contact
import com.togglecorp.paiso.database.DatabaseContext
import com.togglecorp.paiso.misc.Confirmation
import com.togglecorp.paiso.misc.DateFormatter
import com.togglecorp.paiso.transactions.PaisoTransaction
import kotlinx.android.synthetic.main.layout_transaction_notification.view.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import java.util.*

class NotificationListAdapter(val context: Context, val transactions: List<PaisoTransaction>,
                              val contacts: List<Contact?>)
    : RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_transaction_notification, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bind(context, transactions[position], contacts[position])
    }

    override fun getItemCount(): Int {
        return transactions.size
    }


    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        fun bind(context: Context, transaction: PaisoTransaction, contact: Contact?) {
            itemView.transactionTitle.text = transaction.title
            itemView.userName.text = contact?.name.orEmpty()
            itemView.transactionAmount.text = transaction.getSignedAmount(context).toString()
            itemView.actionName.text = if (transaction.acknowledgedAt == null) "Added by" else "Edited by"

            if (transaction.deleted) {
                itemView.actionName.text = "Deleted by"
            }

            itemView.notificationDate.text = DateFormatter.getReadableTime(context, transaction.editedAt)

            if (transaction.acknowledgedAt == null) {
                itemView.acceptAction.text = "Accept"
                itemView.rejectAction.visibility = View.VISIBLE

                itemView.acceptAction.setOnClickListener {
                    transaction.status = "approved"
                    transaction.acknowledgedAt = Date()
                    transaction.sync = false
                    async(CommonPool) {
                        DatabaseContext.get(context).transactionDao().update(transaction)
                    }
                }

                itemView.rejectAction.setOnClickListener {
                    Confirmation.show(context, "Are you sure you want to permanently reject this transaction?")
                            .then {
                                transaction.status = "rejected"
                                transaction.acknowledgedAt = Date()
                                transaction.sync = false
                                async(CommonPool) {
                                    DatabaseContext.get(context).transactionDao().update(transaction)
                                }
                            }
                }
            } else {
                itemView.acceptAction.text = "Dismiss"
                itemView.rejectAction.visibility = View.GONE

                itemView.acceptAction.setOnClickListener {
                    transaction.acknowledgedAt = Date()
                    transaction.sync = false
                    async(CommonPool) {
                        DatabaseContext.get(context).transactionDao().update(transaction)
                    }
                }
            }
        }
    }

}