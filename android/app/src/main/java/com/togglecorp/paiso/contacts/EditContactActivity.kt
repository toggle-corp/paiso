package com.togglecorp.paiso.contacts

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.*
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import com.togglecorp.paiso.R
import com.togglecorp.paiso.api.promise
import com.togglecorp.paiso.auth.Auth
import com.togglecorp.paiso.database.DatabaseContext
import com.togglecorp.paiso.misc.Confirmation
import com.togglecorp.paiso.transactions.PaisoTransaction
import com.togglecorp.paiso.users.SEARCH_USER
import com.togglecorp.paiso.users.SearchUserActivity
import com.togglecorp.paiso.users.UserApi
import kotlinx.android.synthetic.main.activity_edit_contact.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async


class ContactViewModel(application: Application?) : AndroidViewModel(application) {
    fun getContact(id: Int) : LiveData<Contact> {
        return DatabaseContext.get(getApplication()).contactDao().findById(id)
    }

    fun getContactByRemoteId(id: Int) : LiveData<Contact> {
        return DatabaseContext.get(getApplication()).contactDao().findLiveByRemoteId(id)
    }

    fun getTransactionList(contact: Contact) : LiveData<List<PaisoTransaction>> {
        return DatabaseContext.get(getApplication()).transactionDao().getFor(
                contact.remoteId, contact.user)
    }


}


class EditContactActivity : LifecycleActivity() {

    private var mode = "add";
    private var contact = Contact()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_contact)

        setActionBar(toolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        linkButton.setOnClickListener {
            startActivityForResult(Intent(this, SearchUserActivity::class.java), SEARCH_USER)
        }

        if (intent.getStringExtra("mode") == "edit") {
            mode = "edit"
        }

        if (mode == "edit") {
            ViewModelProviders.of(this).get(ContactViewModel::class.java)
                    .getContact(intent.getIntExtra("id", 0)).observe(this, Observer {
                if (it != null) {
                    contact = it
                    contactName.setText(contact.name)
                    if (contact.user != null) {
                        linkUser(contact.user!!)
                    }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SEARCH_USER) {
            if (resultCode == Activity.RESULT_OK) {
                val userRemoteId = data?.getIntExtra("userRemoteId", 0)
                if (userRemoteId != null) {
                    linkUser(userRemoteId)
                }
            }
            return
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun linkUser(userRemoteId: Int) {
        contact.user = userRemoteId
        linkButton.visibility = GONE
        UserApi.get(Auth.getHeader(this), userRemoteId).promise()
                .then {
                    runOnUiThread {
                        linkButton.text = "Linked with user: ${it?.body()?.getName()}"
                        linkButton.visibility = VISIBLE
                    }
                }
                .catch {
                    it?.printStackTrace()
                }
    }

    private fun save() {
        if (TextUtils.isEmpty(contactName.text)) {
            return
        }

        async(UI) {
            contact.name = contactName.text.toString()
            contact.user = contact.user
            contact.sync = false

            async(CommonPool) {
                if (contact._id == null) {
                    DatabaseContext.get(this@EditContactActivity).contactDao().insert(contact)
                } else {
                    DatabaseContext.get(this@EditContactActivity).contactDao().update(contact)
                }
            }.await()

            finish()
        }
    }

    private fun delete() {
        if (contact._id == null) {
            return
        }

        Confirmation.show(this, "Are you sure you want to delete this contact?")
                .then {
                    async(UI) {
                        contact.deleted = true
                        contact.sync = false

                        async(CommonPool) {
                            DatabaseContext.get(this@EditContactActivity).contactDao().update(contact)
                        }.await()

                        finish()
                    }
                }
    }
}

