package com.togglecorp.paiso.transactions

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.togglecorp.paiso.R
import com.togglecorp.paiso.contacts.Contact
import com.togglecorp.paiso.database.DatabaseContext
import com.togglecorp.paiso.misc.Confirmation
import kotlinx.android.synthetic.main.activity_edit_transaction.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import java.util.*


class EditTransactionActivity : LifecycleActivity() {
    private var mode = "add"
    private var transaction = PaisoTransaction()
    private var transactionType = "to"
    private var contact: Contact? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_transaction)

        setActionBar(toolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        if (intent.getStringExtra("mode") == "edit") {
            mode = "edit"
        }

        DatabaseContext.get(this).contactDao()
                .findLiveByRemoteId(intent.getIntExtra("contactId", 0))
                .observe(this,
                        Observer {
                            if (it == null) {
                                finish()
                            }
                            contact = it
                        })

        if (mode == "edit") {
            DatabaseContext.get(this).transactionDao()
                    .findById(intent.getIntExtra("id", 0))
                    .observe(this, Observer {
                        if (it != null) {
                            transaction = it
                            transactionTitle.setText(transaction.title)
                            transactionAmount.setText(transaction.amount.toString())
                            transactionType = transaction.getType(this)

                            if (transactionType == "to")
                                transactionArrow.setImageResource(R.drawable.ic_arrow_long_right)
                            else
                                transactionArrow.setImageResource(R.drawable.ic_arrow_long_left)
                        }
                    })

            title = "Edit Contact"
        } else {
            title = "Add contact"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.done_delete_menu, menu)
        menu?.findItem(R.id.delete)?.isVisible = mode == "edit"
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.done -> {
                save()
                return true
            }
            R.id.delete -> {
                delete()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun save() {
        if (TextUtils.isEmpty(transactionTitle.text) || TextUtils.isEmpty(transactionAmount.text)) {
            return
        }

        async(UI) {
            if (transaction._id == null) {
                transaction.createdAt = Date()
            }

            transaction.title = transactionTitle.text.toString()
            transaction.amount = transactionAmount.text.toString().toFloat()
            transaction.user = PreferenceManager
                    .getDefaultSharedPreferences(this@EditTransactionActivity)
                    .getInt("myRemoteId", 0)
            transaction.contact = contact!!.remoteId
            transaction.transactionType = transactionType
            transaction.editedAt = Date()

            transaction.sync = false

            async(CommonPool) {
                if (transaction._id == null) {
                    DatabaseContext.get(this@EditTransactionActivity).transactionDao().insert(transaction)
                } else {
                    DatabaseContext.get(this@EditTransactionActivity).transactionDao().update(transaction)
                }
            }.await()

            finish()
        }
    }

    private fun delete() {
        if (transaction._id == null) {
            return
        }

        Confirmation.show(this, "Are you sure you want to delete this transaction?")
                .then {
                    async(UI) {
                        transaction.deleted = true
                        transaction.editedAt = Date()
                        transaction.sync = false

                        async(CommonPool) {
                            DatabaseContext.get(this@EditTransactionActivity).transactionDao().update(transaction)
                        }.await()

                        finish()
                    }
                }
    }

    fun toggleArrow(view: View) {
        if (transactionType == "to") {
            transactionArrow.setImageResource(R.drawable.ic_arrow_long_left)
            transactionType = "by"
        } else {
            transactionArrow.setImageResource(R.drawable.ic_arrow_long_right)
            transactionType = "to"
        }
    }
}