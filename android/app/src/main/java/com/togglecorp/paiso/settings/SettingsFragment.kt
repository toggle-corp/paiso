package com.togglecorp.paiso.settings

import android.content.Intent
import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.PreferenceManager
import android.util.Log
import com.togglecorp.paiso.R
import com.togglecorp.paiso.auth.Auth
import com.togglecorp.paiso.auth.LoginActivity
import com.togglecorp.paiso.database.DatabaseContext
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async


class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

        findPreference("pref_logout").setOnPreferenceClickListener {
            logout()
            true
        }

        async(UI) {
            val namePreference = findPreference("pref_username")
            var name = ""
            val username = Auth.getUsername(activity)

            async(CommonPool) {
                if (username != null) {
                    name = DatabaseContext.get(activity).userDao().findByUserName(username)
                            ?.getName().orEmpty()
                }
            }.await()

            namePreference.title = name
            namePreference.summary = username
        }
    }

    private fun logout() {
        Auth.logout(activity)
        startActivity(Intent(activity, LoginActivity::class.java))
        activity.finish()
    }
}
