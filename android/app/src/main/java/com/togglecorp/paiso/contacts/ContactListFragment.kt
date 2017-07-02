package com.togglecorp.paiso.contacts

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import com.togglecorp.paiso.database.DatabaseContext
import kotlinx.android.synthetic.main.fragment_contact_list.view.*


class ContactListFragment : LifecycleFragment() {

    private var contactListAdapter: ContactListAdapter? = null
    private val contactList = mutableListOf<Contact>()

    override fun onCreateView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?,
                              savedInstanceState: android.os.Bundle?): android.view.View? {
        val view = inflater!!.inflate(com.togglecorp.paiso.R.layout.fragment_contact_list, container, false)


        contactListAdapter = ContactListAdapter(context, contactList)
        view.contactListView.layoutManager = LinearLayoutManager(context)
        view.contactListView.adapter = contactListAdapter

        view.addContact.setOnClickListener {
            val intent = Intent(context, EditContactActivity::class.java)
            intent.putExtra("mode", "add")
            startActivity(intent)
        }


        DatabaseContext.get(context).contactDao().getAll()
                .observe(this, Observer { refresh(it) })
        return view
    }

    private fun refresh(contacts: List<Contact>?) {
        contactList.clear()
        contacts?.forEach {
            if (!it.deleted) {
                contactList.add(it)
            }
        }
        contactListAdapter?.notifyDataSetChanged()
    }
}
