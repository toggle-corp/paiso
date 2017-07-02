package com.togglecorp.paiso.users

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.togglecorp.paiso.R
import kotlinx.android.synthetic.main.layout_user.view.*

class UserListAdapter(val context: Context, val userList: List<User>, val onSelected: (User) -> Unit)
    : RecyclerView.Adapter<UserListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_user, parent, false))
    }

    override fun getItemCount() = userList.size

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bind(userList[position], onSelected)
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        fun bind(user: User, onSelected: (User) -> Unit) {
            itemView.name.text = user.getName()
            itemView.username.text = user.username

            itemView.setOnClickListener {
                onSelected(user)
            }
        }
    }
}

