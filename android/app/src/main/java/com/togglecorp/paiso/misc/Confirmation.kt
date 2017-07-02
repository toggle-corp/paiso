package com.togglecorp.paiso.misc

import android.app.AlertDialog
import android.content.Context
import com.togglecorp.paiso.promise.Promise

object Confirmation {
    fun show(context: Context, text: String, yes: String = "Yes", no: String = "No") : Promise<Nothing> {
        val promise = Promise<Nothing>()

        AlertDialog.Builder(context)
                .setMessage(text)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(yes, { _, _ -> promise.resolve(null) })
                .setNegativeButton(no, null)
                .show()

        return promise
    }
}