package com.togglecorp.paiso.contacts

import android.app.Activity
import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import com.togglecorp.paiso.R
import com.togglecorp.paiso.api.promise
import com.togglecorp.paiso.auth.Auth
import com.togglecorp.paiso.database.DatabaseContext
import com.togglecorp.paiso.database.SyncManager
import com.togglecorp.paiso.misc.Confirmation
import com.togglecorp.paiso.users.SEARCH_USER
import com.togglecorp.paiso.users.SearchUserActivity
import com.togglecorp.paiso.users.UserApi
import kotlinx.android.synthetic.main.activity_edit_contact.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import java.util.*


class EditContactActivity : LifecycleActivity() {

    private var mode = "add";
    private var contact = Contact()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_contact)

        setActionBar(toolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        if (PreferenceManager
                .getDefaultSharedPreferences(this)
                .getInt("myRemoteId", 0) == 0) {
            finish()
            return
        }

        linkButton.setOnClickListener {
            startActivityForResult(Intent(this, SearchUserActivity::class.java), SEARCH_USER)
        }

        if (intent.getStringExtra("mode") == "edit") {
            mode = "edit"
        }

        if (mode == "edit") {
            DatabaseContext.get(this).contactDao()
                    .findById(intent.getIntExtra("id", 0))
                    .observe(this, Observer {
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
            title = "Add Contact"
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
            if (contact._id == null) {
                contact.createdAt = Date()
            }

            contact.version += 1
            contact.name = contactName.text.toString()
            contact.user = contact.user
            contact.editedAt = Date()
            contact.sync = false

            async(CommonPool) {
                if (contact._id == null) {
                    DatabaseContext.get(this@EditContactActivity).contactDao().insert(contact)
                } else {
                    DatabaseContext.get(this@EditContactActivity).contactDao().update(contact)
                }
            }.await()
            SyncManager.sync(this@EditContactActivity)

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
                        contact.version += 1
                        contact.deleted = true
                        contact.sync = false

                        async(CommonPool) {
                            DatabaseContext.get(this@EditContactActivity).contactDao().update(contact)
                        }.await()
                        SyncManager.sync(this@EditContactActivity)

                        finish()
                    }
                }
    }
}

