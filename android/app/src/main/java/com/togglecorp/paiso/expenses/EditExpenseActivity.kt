package com.togglecorp.paiso.expenses

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import com.togglecorp.paiso.R
import com.togglecorp.paiso.database.DatabaseContext
import com.togglecorp.paiso.misc.Confirmation
import kotlinx.android.synthetic.main.activity_edit_expense.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import java.util.*

class EditExpenseActivity : LifecycleActivity() {
    private var mode = "add"
    private var expense = Expense()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_expense)

        setActionBar(toolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        if (PreferenceManager
                .getDefaultSharedPreferences(this)
                .getInt("myRemoteId", 0) == 0) {
            finish()
            return
        }

        if (intent.getStringExtra("mode") == "edit") {
            mode = "edit"

            DatabaseContext.get(this).expenseDao()
                    .findById(intent.getIntExtra("id", 0))
                    .observe(this, Observer {
                        if (it != null) {
                            expense = it
                            expenseTitle.setText(expense.title)
                            expenseAmount.setText(expense.amount.toString())
                        }
                    })

            title = "Edit Expense"
        } else {
            title = "Add Expense"
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
        if (TextUtils.isEmpty(expenseTitle.text) || TextUtils.isEmpty(expenseAmount.text)) {
            return
        }

        async(UI) {
            if (expense._id == null) {
                expense.createdAt = Date()
            }

            expense.title = expenseTitle.text.toString()
            expense.amount = expenseAmount.text.toString().toFloat()
            expense.user = PreferenceManager
                    .getDefaultSharedPreferences(this@EditExpenseActivity)
                    .getInt("myRemoteId", 0)
            expense.editedAt = Date()
            expense.sync = false

            async(CommonPool) {
                if (expense._id == null) {
                    DatabaseContext.get(this@EditExpenseActivity).expenseDao().insert(expense)
                } else {
                    DatabaseContext.get(this@EditExpenseActivity).expenseDao().update(expense)
                }
            }.await()

            finish()
        }
    }

    private fun delete() {
        if (expense._id == null) {
            return
        }

        Confirmation.show(this, "Are you sure you want to delete this expense record?")
                .then {
                    async(UI) {
                        expense.deleted = true
                        expense.sync = false

                        async(CommonPool) {
                            DatabaseContext.get(this@EditExpenseActivity).expenseDao().update(expense)
                        }.await()

                        finish()
                    }
                }
    }
}