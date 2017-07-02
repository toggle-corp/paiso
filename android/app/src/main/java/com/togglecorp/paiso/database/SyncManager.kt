package com.togglecorp.paiso.database

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ProcessLifecycleOwner
import android.content.Context
import com.togglecorp.paiso.api.promise
import com.togglecorp.paiso.auth.Auth
import com.togglecorp.paiso.contacts.Contact
import com.togglecorp.paiso.contacts.ContactApi
import com.togglecorp.paiso.promise.Promise
import com.togglecorp.paiso.transaction.PaisoTransaction
import com.togglecorp.paiso.transaction.TransactionApi
import com.togglecorp.paiso.users.User
import com.togglecorp.paiso.users.UserApi


object SyncManager {
    fun startPushing(context: Context) {
        val cxt = context.applicationContext
        DatabaseContext.get(cxt).contactDao().getModified()
                .observe(ProcessLifecycleOwner.get(), Observer {
                    pushContacts(context, it)
                            .catch { it?.printStackTrace(); null }
                })

        DatabaseContext.get(cxt).transactionDao().getModified()
                .observe(ProcessLifecycleOwner.get(), Observer {
                    pushTransactions(context, it)
                            .catch { it?.printStackTrace(); null }
                })
    }

    fun fetch(context: Context) : Promise<Unit?> {
        val cxt = context.applicationContext
        return fetchSelf(cxt)
                .thenPromise { fetchContacts(cxt) }
                .thenPromise { fetchTransactions(cxt) }
                .catch {
                    it?.printStackTrace()
                    null
                }.then { null }
    }

    fun fetchSelf(context: Context) : Promise<User?> {
        return UserApi.getMe(Auth.getHeader(context)).promise()
                .then {
                    val user = it?.body()
                    if (user != null) {
                        DatabaseContext.get(context).userDao().apply {
                            delete(user.username)
                            insert(user)
                        }
                    }

                    user
                }
                .then { it }
    }

    fun pushContacts(context: Context, contactList: List<Contact>?) : Promise<List<Contact?>?> {
        val promises = mutableListOf<Promise<Contact?>>()

        contactList?.forEach {
            val that = it
            if (it.remoteId == null && !it.deleted) {
                promises.add(
                        ContactApi.post(Auth.getHeader(context), it)
                                .promise()
                                .then {
                                    that.remoteId = it?.body()?.remoteId
                                    that.saveAsSynchronized(context)
                                    it?.body()
                                }
                )
            }
            else if (it.deleted) {
                promises.add(ContactApi.delete(Auth.getHeader(context), it.remoteId!!)
                        .promise().then { /* TODO delete that*/that.saveAsSynchronized(context); Contact() })
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

    fun fetchContacts(context: Context) : Promise<List<Contact>?> {
        return ContactApi.get(Auth.getHeader(context)).promise()
                .then {
                    val contacts = it?.body()
                    val contactDao = DatabaseContext.get(context).contactDao()

                    contacts?.forEach {
                        it.sync = true

                        val existing = contactDao.findByRemoteId(it.remoteId)
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

    fun pushTransactions(context: Context, transactionList: List<PaisoTransaction>?) : Promise<List<PaisoTransaction?>?> {
        val promises = mutableListOf<Promise<PaisoTransaction?>>()

        transactionList?.forEach {
            val that = it
            if (it.remoteId == null) {
                promises.add(
                        TransactionApi.post(Auth.getHeader(context), it)
                                .promise()
                                .then {
                                    that.remoteId = it?.body()?.remoteId
                                    that.saveAsSynchronized(context);
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

    fun fetchTransactions(context: Context) : Promise<List<PaisoTransaction>?> {
        return TransactionApi.get(Auth.getHeader(context)).promise()
                .then {
                    val transactions = it?.body()
                    val transactionDao = DatabaseContext.get(context).transactionDao()

                    transactions?.forEach {
                        it.sync = true

                        val existing = transactionDao.findByRemoteId(it.remoteId)
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
}

