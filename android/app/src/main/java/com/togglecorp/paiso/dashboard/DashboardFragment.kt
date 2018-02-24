package com.togglecorp.paiso.dashboard

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.togglecorp.paiso.R
import com.togglecorp.paiso.database.ContactAmount
import com.togglecorp.paiso.database.DatabaseContext
import com.togglecorp.paiso.fcm.sendRegistrationToServer
import com.togglecorp.paiso.misc.AmountHeader
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import kotlinx.android.synthetic.main.layout_amount_header.*
import kotlinx.android.synthetic.main.layout_amount_header.view.*


class DashboardFragment : LifecycleFragment() {

    private var dashboardAdapter: DashboardListAdapter? = null
    private val dashboardList = mutableListOf<DashboardItem>()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_dashboard, container, false)

        sendRegistrationToServer(context)

        dashboardAdapter = DashboardListAdapter(context, dashboardList)
        view.dashboardListView.layoutManager = LinearLayoutManager(context)
        view.dashboardListView.adapter = dashboardAdapter
        view.dashboardListView.addItemDecoration(DividerItemDecoration(context,  DividerItemDecoration.VERTICAL))
        view.headerTotalAmount.text = "0.0"

        AmountHeader(context, view.amountHeader, null, null, null)

        DatabaseContext.get(context).contactAmountDao().getAll()
                .observe(this, Observer { refresh(it) })

        return view
    }

    private fun refresh(contacts: List<ContactAmount>?) {
        dashboardList.clear()

        contacts?.forEach {contact ->
            val existing = dashboardList.find { it.contactId == contact.contactId }
            if (existing != null) {
                existing.amount += contact.getSignedAmount(context)
            } else {
                dashboardList.add(DashboardItem(contact.contactId!!,
                        contact.contactName, contact.getSignedAmount(context)))
            }
        }
        dashboardList.sortBy { it.contactName }
        dashboardAdapter?.notifyDataSetChanged()

        val total = dashboardList.fold<DashboardItem, Float>(0.0f) {
            total, next -> total + next.amount
        }
        headerTotalAmount.text = total.toString()
    }
}
