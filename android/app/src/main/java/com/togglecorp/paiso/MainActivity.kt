package com.togglecorp.paiso

import android.accounts.AccountManager
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.togglecorp.paiso.auth.ACCOUNT_TYPE
import com.togglecorp.paiso.auth.AUTH_TOKEN_TYPE
import com.togglecorp.paiso.auth.Auth
import com.togglecorp.paiso.auth.LoginActivity
import com.togglecorp.paiso.contacts.ContactListFragment
import com.togglecorp.paiso.containers.DashboardFragment
import com.togglecorp.paiso.containers.NotificationListFragment
import com.togglecorp.paiso.containers.SettingsFragment
import com.togglecorp.paiso.database.SyncManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkAuthenticated()

        val dashboardFragment = DashboardFragment()
        val contactListFragment = ContactListFragment()
        val notificationListFragment = NotificationListFragment()
        val settingsFragment = SettingsFragment()

        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.dashboard -> {
                    switchTo(dashboardFragment)
                    true
                }
                R.id.contacts -> {
                    switchTo(contactListFragment)
                    true
                }
                R.id.notifications -> {
                    switchTo(notificationListFragment)
                    true
                }
                R.id.settings -> {
                    switchTo(settingsFragment)
                    true
                }
                else -> false
            }
        }

        switchTo(dashboardFragment)
    }

    override fun onResume() {
        super.onResume()
        checkAuthenticated()

        SyncManager.fetch(this)
    }

    fun checkAuthenticated() {
        if (Auth.getToken(this) == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
    }

    override fun onPause() {
        super.onPause()
    }

    fun switchTo(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
    }
}
