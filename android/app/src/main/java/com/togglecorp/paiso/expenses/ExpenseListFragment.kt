package com.togglecorp.paiso.expenses

import android.arch.lifecycle.*
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.togglecorp.paiso.R
import com.togglecorp.paiso.database.DatabaseContext
import com.togglecorp.paiso.misc.AmountHeader
import kotlinx.android.synthetic.main.fragment_expense_list.view.*
import kotlinx.android.synthetic.main.layout_amount_header.*
import kotlinx.android.synthetic.main.layout_amount_header.view.*
import java.util.*


class ExpenseListViewModel : ViewModel() {
    val startDate: Calendar = Calendar.getInstance()
    val endDate: Calendar = Calendar.getInstance()

    var liveData: LiveData<List<Expense>>? = null
    var observer: Observer<List<Expense>>? = null

    init {
        startDate.add(Calendar.DAY_OF_MONTH, -30);
        startDate.set(Calendar.HOUR_OF_DAY, 0)
        startDate.set(Calendar.MINUTE, 0)
        startDate.set(Calendar.SECOND, 0)

        endDate.set(Calendar.HOUR_OF_DAY, 23)
        endDate.set(Calendar.MINUTE, 59)
        endDate.set(Calendar.SECOND, 59)
    }
}


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
        view.headerTotalAmount.text = "0.0"

        val model = ViewModelProviders.of(activity).get(ExpenseListViewModel::class.java)
        model.observer = Observer<List<Expense>> { refresh(it) }

        AmountHeader(context, view.amountHeader, model.startDate, model.endDate, {
            model.liveData?.removeObserver(model.observer)
            model.liveData = DatabaseContext.get(context)
                    .expenseDao()
                    .getExisting(model.startDate.time, model.endDate.time)
            model.liveData!!.observe(this, model.observer)
        })

        view.addExpense.setOnClickListener {
            val intent = Intent(context, EditExpenseActivity::class.java)
            intent.putExtra("mode", "add")
            startActivity(intent)
        }

        return view
    }

    private fun refresh(expenses: List<Expense>?) {
        expenseList.clear()
        expenses?.forEach {
            expenseList.add(it)
        }
        expenseList.sortByDescending { it.editedAt }
        expenseListAdapter?.notifyDataSetChanged()

        val total = expenseList.fold<Expense, Float>(0.0f) {
            total, next -> total + next.amount
        }
        headerTotalAmount.text = total.toString()
    }
}