package com.togglecorp.paiso.containers

import android.content.Intent
import com.togglecorp.paiso.auth.Auth
import com.togglecorp.paiso.auth.LoginActivity
import kotlinx.android.synthetic.main.fragment_settings.view.*


class SettingsFragment : android.support.v4.app.Fragment() {

    override fun onCreateView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?,
                              savedInstanceState: android.os.Bundle?): android.view.View? {
        val view = inflater!!.inflate(com.togglecorp.paiso.R.layout.fragment_settings, container, false)

        view.logout.setOnClickListener { logout() }
        return view
    }

    private fun logout() {
        Auth.logout(context)
        startActivity(Intent(context, LoginActivity::class.java))
        activity.finish()
    }
}
