package com.togglecorp.paiso.database

import android.content.Context
import android.preference.PreferenceManager
import com.togglecorp.paiso.api.promise
import com.togglecorp.paiso.auth.Auth
import com.togglecorp.paiso.contacts.Contact
import com.togglecorp.paiso.contacts.ContactApi
import com.togglecorp.paiso.expenses.Expense
import com.togglecorp.paiso.expenses.ExpenseApi
import com.togglecorp.paiso.promise.Promise
import com.togglecorp.paiso.transactions.PaisoTransaction
import com.togglecorp.paiso.transactions.TransactionApi
import com.togglecorp.paiso.users.User
import com.togglecorp.paiso.users.UserApi
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import java.util.concurrent.atomic.AtomicBoolean


object SyncManager {
    private val SYNC_LOCK = AtomicBoolean(false)

    fun sync(context: Context) : Promise<Unit?> {
        val cxt = context.applicationContext

        val contactDao = DatabaseContext.get(cxt).contactDao()
        val transactionDao = DatabaseContext.get(cxt).transactionDao()
        val expenseDao = DatabaseContext.get(cxt).expenseDao()

        val promise = Promise<Unit?>()

        async(CommonPool) {
            synchronized(SYNC_LOCK) {
                while (SYNC_LOCK.get()) {
                    (SYNC_LOCK as java.lang.Object).wait()
                }
                SYNC_LOCK.set(true)
            }

            pushContacts(context, contactDao.getModifiedList())
                    .then {
                        pushTransactions(context, transactionDao.getModifiedList())
                    }
                    .then {
                        pushExpenses(context, expenseDao.getModifiedList())
                    }
                    .then {
                        synchronized(SYNC_LOCK) {
                            SYNC_LOCK.set(false)
                            (SYNC_LOCK as java.lang.Object).notifyAll()
                        }
                    }
                    .catch {
                        synchronized(SYNC_LOCK) {
                            SYNC_LOCK.set(false)
                            (SYNC_LOCK as java.lang.Object).notifyAll()
                        }
                        it?.printStackTrace(); null
                    }
                    .then {
                        fetch(context)
                        promise.resolve(null)
                    }
                    .catch {
                        promise.resolve(null)
                    }
        }

        return promise
    }

    fun fetch(context: Context) : Promise<Unit?> {
        val cxt = context.applicationContext
        return fetchSelf(cxt)
                .thenPromise { fetchContacts(cxt) }
                .thenPromise { fetchTransactions(cxt) }
                .thenPromise { fetchExpenses(cxt) }
                .catch {
                    it?.printStackTrace()
                    null
                }.then { null }
    }

    private fun fetchSelf(context: Context) : Promise<User?> {
        return UserApi.getMe(Auth.getHeader(context)).promise()
                .then {
                    val user = it?.body()
                    if (user != null) {
                        DatabaseContext.get(context).userDao().apply {
                            delete(user.username)
                            insert(user)

                            PreferenceManager.getDefaultSharedPreferences(context).edit()
                                    .putInt("myRemoteId", user.remoteId!!).apply()
                        }
                    }

                    user
                }
                .then { it }
    }

    private fun pushContacts(context: Context, contactList: List<Contact>?) : Promise<List<Contact?>?> {
        val promises = mutableListOf<Promise<Contact?>>()

        contactList?.forEach {
            val that = it
            if (it.remoteId == null && !it.deleted) {
                promises.add(
                        ContactApi.post(Auth.getHeader(context), it)
                                .promise()
                                .then {
                                    if (it?.body() != null) {
                                        that.remoteId = it.body().remoteId
                                        that.saveAsSynchronized(context)
                                    }
                                    it?.body()
                                }
                )
            }
            else if (it.deleted) {
                promises.add(ContactApi.delete(Auth.getHeader(context), it.remoteId!!)
                        .promise()
                        .then {
                            that.saveAsSynchronized(context);

                            DatabaseContext.get(context).transactionDao().deleteFor(that.remoteId, that.user)
                            /* TODO delete that*/

                            Contact()
                        })
            }
            else {
                promises.add(
                        ContactApi.put(Auth.getHeader(context), it.remoteId!!, it)
                                .promise().then { that.saveAsSynchronized(context); it?.body() }
                )
            }
        }

        return Promise.all(promises).then { it }
    }

    private fun fetchContacts(context: Context) : Promise<List<Contact>?> {
        return ContactApi.get(Auth.getHeader(context)).promise()
                .then {
                    val contacts = it?.body()
                    val contactDao = DatabaseContext.get(context).contactDao()

                    contacts?.forEach {
                        it.sync = true

                        val existing = contactDao.findByUuid(it.uuid)
                        if (existing == null) {
                            contactDao.insert(it)
                        } else {
                            it._id = existing._id
                            contactDao.update(it)
                        }
                    }

                    contacts
                }
    }

    private fun pushTransactions(context: Context, transactionList: List<PaisoTransaction>?) : Promise<List<PaisoTransaction?>?> {
        val promises = mutableListOf<Promise<PaisoTransaction?>>()

        transactionList?.forEach {
            val that = it
            if (it.remoteId == null) {
                promises.add(
                        TransactionApi.post(Auth.getHeader(context), it)
                                .promise()
                                .then {
                                    if (it?.body() != null) {
                                        that.remoteId = it.body().remoteId
                                        that.saveAsSynchronized(context);
                                    }
                                    it?.body()
                                }
                )
            } else {
                promises.add(
                        TransactionApi.put(Auth.getHeader(context), it.remoteId!!, it)
                                .promise().then { that.saveAsSynchronized(context); it?.body() }
                )
            }
        }

        return Promise.all(promises).then { it }
    }

    private fun fetchTransactions(context: Context) : Promise<List<PaisoTransaction>?> {
        return TransactionApi.get(Auth.getHeader(context)).promise()
                .then {
                    val transactions = it?.body()
                    val transactionDao = DatabaseContext.get(context).transactionDao()

                    transactions?.forEach {
                        it.sync = true

                        val existing = transactionDao.findByUuid(it.uuid)
                        if (existing == null) {
                            transactionDao.insert(it)
                        } else {
                            it._id = existing._id
                            transactionDao.update(it)
                        }
                    }

                    transactions
                }
    }

    private fun pushExpenses(context: Context, expenseList: List<Expense>?) : Promise<List<Expense?>?> {
        val promises = mutableListOf<Promise<Expense?>>()

        expenseList?.forEach {
            val that = it
            if (it.remoteId == null) {
                promises.add(
                        ExpenseApi.post(Auth.getHeader(context), it)
                                .promise()
                                .then {
                                    that.remoteId = it?.body()?.remoteId
                                    that.saveAsSynchronized(context);
                                    it?.body()
                                }
                )
            } else {
                promises.add(
                        ExpenseApi.put(Auth.getHeader(context), it.remoteId!!, it)
                                .promise().then { that.saveAsSynchronized(context); it?.body() }
                )
            }
        }

        return Promise.all(promises).then { it }
    }

    private fun fetchExpenses(context: Context) : Promise<List<Expense>?> {
        return ExpenseApi.get(Auth.getHeader(context)).promise()
                .then {
                    val expenses = it?.body()
                    val expenseDao = DatabaseContext.get(context).expenseDao()

                    expenses?.forEach {
                        it.sync = true

                        val existing = expenseDao.findByUuid(it.uuid)
                        if (existing == null) {
                            expenseDao.insert(it)
                        } else {
                            it._id = existing._id
                            expenseDao.update(it)
                        }
                    }

                    expenses
                }
    }
}

