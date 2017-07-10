package com.togglecorp.paiso.contacts

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.togglecorp.paiso.R
import com.togglecorp.paiso.database.DatabaseContext
import com.togglecorp.paiso.misc.AmountHeader
import com.togglecorp.paiso.transactions.EditTransactionActivity
import com.togglecorp.paiso.transactions.PaisoTransaction
import com.togglecorp.paiso.transactions.TransactionListAdapter
import kotlinx.android.synthetic.main.activity_contact_details.*
import kotlinx.android.synthetic.main.layout_amount_header.*
import kotlinx.android.synthetic.main.layout_amount_header.view.*

class ContactDetailsActivity : LifecycleActivity() {

    private var contact: Contact? = null

    private val transactionList = mutableListOf<PaisoTransaction>()
    private var transactionListAdapter: TransactionListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_details)

        setActionBar(toolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        transactionListAdapter = TransactionListAdapter(this, transactionList)
        transactionListView.layoutManager = LinearLayoutManager(this)
        transactionListView.adapter = transactionListAdapter

        AmountHeader(this, amountHeader, null, null, null)

        headerTotalAmount.text = "0.0"

        DatabaseContext.get(this).contactDao().findById(intent.getIntExtra("id", 0))
                .observe(this, Observer {
                    if (it != null && !it.deleted) {
                        contact = it
                        title = contact!!.name

                        DatabaseContext.get(this).transactionDao().getFor(
                                contact!!.remoteId, contact!!.user)
                                .observe(this, Observer {
                                    refresh(it!!)
                                })
                    } else {
                        finish()
                    }
                })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.contact_details_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.edit_contact -> {
                val intent = Intent(this, EditContactActivity::class.java)
                intent.putExtra("mode", "edit")
                intent.putExtra("id", contact!!._id)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun refresh(transactions: List<PaisoTransaction>) {
        transactionList.clear()
        transactions.forEach { transactionList.add(it) }
        transactionList.sortByDescending { it.editedAt }
        transactionListAdapter?.notifyDataSetChanged()

        val total = transactionList.fold<PaisoTransaction, Float>(0.0f) {
            total, next -> total + next.getSignedAmount(this)
        }
        headerTotalAmount.text = total.toString()

    }

    fun addTransaction(view: View) {
        val intent = Intent(this, EditTransactionActivity::class.java)
        intent.putExtra("mode", "add")
        intent.putExtra("contactId", contact!!.remoteId)
        startActivity(intent)
    }
}
