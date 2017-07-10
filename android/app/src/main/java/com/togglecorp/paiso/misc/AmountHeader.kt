package com.togglecorp.paiso.misc

import android.app.DatePickerDialog
import android.content.Context
import android.view.View
import kotlinx.android.synthetic.main.layout_amount_header.view.*
import java.util.*


class AmountHeader(val context: Context, val view: View,
                   val startDate: Calendar?, val endDate: Calendar?,
                   val callback: (()->Unit)?) {

    init {
        if (callback == null || startDate == null || endDate == null) {
            view.dateContainer.visibility = View.GONE
        } else {
            refresh()
            view.dateContainer.visibility = View.VISIBLE

            val startDatePicker = DatePickerDialog(context,
                    DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                        startDate.set(year, month, dayOfMonth)
                        refresh()
                    }, startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH),
                    startDate.get(Calendar.DAY_OF_MONTH))

            val endDatePicker = DatePickerDialog(context,
                    DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                        endDate.set(year, month, dayOfMonth)
                        refresh()
                    }, endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH),
                    endDate.get(Calendar.DAY_OF_MONTH))

            view.startDate.setOnClickListener {
                startDatePicker.show()
            }

            view.endDate.setOnClickListener {
                endDatePicker.show()
            }

            refresh()
        }
    }

    private fun refresh() {
        view.startDate.text = DateFormatter.getReadableDate(context, startDate!!.time)
        view.endDate.text = DateFormatter.getReadableDate(context, endDate!!.time)
        callback!!()
    }
}