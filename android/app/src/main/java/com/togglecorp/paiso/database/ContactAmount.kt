package com.togglecorp.paiso.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import android.content.Context
import android.preference.PreferenceManager

data class ContactAmount (
       var contactId: Int? = null,
       var contactName: String = "",
       var amount: Float = 0.0f,
       var user: Int? = null,
       var transactionType: String = "to"
) {
    fun isMy(context: Context) : Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("myRemoteId",
                0) == user
    }

    fun  getType(context: Context) : String {
        if (isMy(context)) {
            return transactionType
        } else {
            return if (transactionType == "to") "by" else "to"
        }
    }

    fun getSignedAmount(context: Context) : Float {
        return if (getType(context) == "to") amount else -amount
    }
}

@Dao
interface ContactAmountDao {
    @Query("SELECT contact._id as contactId, contact.name as contactName, paiso_transaction.amount as amount, paiso_transaction.user as user, paiso_transaction.transactionType as transactionType " +
            "FROM contact INNER JOIN paiso_transaction ON contact.remoteId = paiso_transaction.contact " +
            "WHERE contact.deleted = 0 AND paiso_transaction.deleted = 0 " +
            "UNION " +
            "SELECT contact._id as contactId, contact.name as contactName, paiso_transaction.amount as amount, paiso_transaction.user as user, paiso_transaction.transactionType as transactionType " +
            "FROM contact INNER JOIN paiso_transaction ON contact.user = paiso_transaction.user " +
            "WHERE contact.deleted = 0 AND paiso_transaction.deleted = 0 AND paiso_transaction.status = 'approved'")
    fun getAll() : LiveData<List<ContactAmount>>
}