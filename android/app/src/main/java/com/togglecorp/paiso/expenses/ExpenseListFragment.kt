package com.togglecorp.paiso.expenses

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.togglecorp.paiso.R
import com.togglecorp.paiso.database.DatabaseContext
import kotlinx.android.synthetic.main.fragment_expense_list.view.*

class ExpenseListFragment : LifecycleFragment() {

    private var expenseListAdapter: ExpenseListAdapter? = null
    private val expenseList = mutableListOf<Expense>()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_expense_list, container, false)

        expenseListAdapter = ExpenseListAdapter(context, expenseList)
        view.expenseListView.layoutManager = LinearLayoutManager(context)
        view.expenseListView.adapter = expenseListAdapter
        view.expenseListView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        view.addExpense.setOnClickListener {
            val intent = Intent(context, EditExpenseActivity::class.java)
            intent.putExtra("mode", "add")
            startActivity(intent)
        }

        DatabaseContext.get(context).expenseDao().getAlive()
                .observe(this, Observer { refresh(it) })

        return view
    }

    private fun refresh(expenses: List<Expense>?) {
        expenseList.clear()
        expenses?.forEach {
            expenseList.add(it)
        }
        expenseListAdapter?.notifyDataSetChanged()
    }
}