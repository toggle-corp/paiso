package com.togglecorp.paiso.contacts

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.togglecorp.paiso.R
import com.togglecorp.paiso.transaction.PaisoTransaction
import com.togglecorp.paiso.transaction.TransactionListAdapter
import kotlinx.android.synthetic.main.activity_contact_details.*

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

        val viewModel = ViewModelProviders.of(this).get(ContactViewModel::class.java)
        viewModel.getContact(intent.getIntExtra("id", 0)).observe(this, Observer {
            if (it != null) {
                contact = it
                title = contact!!.name

                viewModel.getTransactionList(contact!!).observe(this, Observer {
                    refresh(it!!)
                })
            } else {
                finish()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun refresh(transactions: List<PaisoTransaction>) {
        transactionList.clear()
        transactions.forEach { transactionList.add(it) }
        transactionListAdapter?.notifyDataSetChanged()
    }

}
