package com.togglecorp.paiso.notifications

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.preference.PreferenceManager
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.togglecorp.paiso.contacts.Contact
import com.togglecorp.paiso.database.DatabaseContext
import com.togglecorp.paiso.transactions.PaisoTransaction
import kotlinx.android.synthetic.main.fragment_notification_list.view.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async


class NotificationListFragment : LifecycleFragment() {

    var notificationListAdapter: NotificationListAdapter? = null
    val notificationList = mutableListOf<PaisoTransaction>()
    val contactList = mutableListOf<Contact?>()

    override fun onCreateView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?,
                              savedInstanceState: android.os.Bundle?): android.view.View? {
        val view = inflater!!.inflate(com.togglecorp.paiso.R.layout.fragment_notification_list, container, false)

        notificationListAdapter = NotificationListAdapter(context, notificationList, contactList)
        view.notificationListView.layoutManager = LinearLayoutManager(context)
        view.notificationListView.adapter = notificationListAdapter
        view.notificationListView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        DatabaseContext.get(context).transactionDao().getNotifiable(
                PreferenceManager.getDefaultSharedPreferences(context).getInt(
                        "myRemoteId", 0)
        ).observe(this, Observer { refresh(it) })

        return view
    }

    private fun refresh(notifications: List<PaisoTransaction>?) {
        async(UI) {
            notificationList.clear()
            notifications?.forEach {
                notificationList.add(it)
            }
            notificationList.sortByDescending { it.editedAt }

            contactList.clear()
            async(CommonPool) {
                notificationList.forEach {
                    contactList.add(DatabaseContext.get(getContext()).contactDao()
                            .findByUserId(it.contact))
                }
            }.await()

            notificationListAdapter?.notifyDataSetChanged()
        }
    }

}
