package com.togglecorp.paiso.expenses

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.togglecorp.paiso.R
import com.togglecorp.paiso.misc.DateFormatter
import kotlinx.android.synthetic.main.layout_expense.view.*

class ExpenseListAdapter(val context: Context, val expenses: List<Expense>)
    : RecyclerView.Adapter<ExpenseListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) =
        ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_expense, parent, false))

    override fun getItemCount() = expenses.size

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bind(context, expenses[position])
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        fun bind(context: Context, expense: Expense) {
            itemView.title.text = expense.title
            itemView.date.text = DateFormatter.getReadableTime(context, expense.editedAt)
            itemView.amount.text = expense.amount.toString()

            itemView.setOnClickListener {
                val intent = Intent(context, EditExpenseActivity::class.java)
                intent.putExtra("mode", "edit")
                intent.putExtra("id", expense._id)
                context.startActivity(intent)
            }
        }
    }

}